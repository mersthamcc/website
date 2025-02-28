package cricket.merstham.graphql.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import cricket.merstham.graphql.extensions.MemberSummaryGoogleSheets;
import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.shared.dto.ReportExport;
import cricket.merstham.shared.types.ReportFilter;
import jakarta.inject.Named;
import lombok.experimental.ExtensionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;
import static java.text.MessageFormat.format;

@Service
@ExtensionMethod({MemberSummaryGoogleSheets.class})
public class GoogleSheetsService {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleSheetsService.class);

    private final GoogleCredentials googleCredentials;
    private final CognitoService cognitoService;
    private final String applicationName;

    @Autowired
    public GoogleSheetsService(
            @Named("SheetsCredentials") GoogleCredentials googleCredentials,
            @Value("${configuration.google.application-name}") String applicationName,
            CognitoService cognitoService) {
        this.googleCredentials = googleCredentials;
        this.applicationName = applicationName;
        this.cognitoService = cognitoService;
    }

    public ReportExport exportMemberSummary(
            Principal principal, List<MemberSummary> members, ReportFilter filter) {
        try {
            var subject = getSubject(principal);
            LOG.info("Getting user info for subject {}", subject);
            var emailAddress = cognitoService.getUserBySubjectId(subject).getEmail();
            LOG.info("Creating delegated credentials for user {}", emailAddress);
            var credentials = googleCredentials.createDelegated(emailAddress);
            credentials.refresh();
            LOG.info("Initiating export of {} members for {}", filter.asText(), emailAddress);
            var drive =
                    new Drive.Builder(
                                    GoogleNetHttpTransport.newTrustedTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    new HttpCredentialsAdapter(credentials))
                            .setApplicationName(applicationName)
                            .build();
            var sheets =
                    new Sheets.Builder(
                                    GoogleNetHttpTransport.newTrustedTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    new HttpCredentialsAdapter(credentials))
                            .setApplicationName(applicationName)
                            .build();

            var file = createExportSheet(drive, filter);
            LOG.info("Sheet {} created", file.getId());
            var spreadsheet = sheets.spreadsheets().get(file.getId()).execute();

            addReportData(sheets, spreadsheet, members, filter);
            LOG.info("Exported {} rows to {}", members.size(), file.getId());

            return ReportExport.builder()
                    .type("MemberSummary-" + filter.asText())
                    .location(spreadsheet.getSpreadsheetUrl())
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private void addReportData(
            Sheets sheets,
            Spreadsheet spreadsheet,
            List<MemberSummary> members,
            ReportFilter filter)
            throws IOException {
        var sheet = spreadsheet.getSheets().get(0);

        sheets.spreadsheets()
                .batchUpdate(
                        spreadsheet.getSpreadsheetId(),
                        new BatchUpdateSpreadsheetRequest()
                                .setRequests(
                                        List.of(
                                                new Request()
                                                        .setUpdateSpreadsheetProperties(
                                                                new UpdateSpreadsheetPropertiesRequest()
                                                                        .setFields("*")
                                                                        .setProperties(
                                                                                spreadsheet
                                                                                        .getProperties()
                                                                                        .setLocale(
                                                                                                "en_GB"))),
                                                new Request()
                                                        .setUpdateSheetProperties(
                                                                new UpdateSheetPropertiesRequest()
                                                                        .setFields("*")
                                                                        .setProperties(
                                                                                sheet.getProperties()
                                                                                        .setTitle(
                                                                                                filter
                                                                                                        .asText()))),
                                                new Request()
                                                        .setUpdateCells(
                                                                new UpdateCellsRequest()
                                                                        .setRows(
                                                                                addMembersToTable(
                                                                                        members))
                                                                        .setFields("*")
                                                                        .setRange(
                                                                                new GridRange()
                                                                                        .setSheetId(
                                                                                                sheet.getProperties()
                                                                                                        .getSheetId())
                                                                                        .setStartRowIndex(
                                                                                                0)
                                                                                        .setStartColumnIndex(
                                                                                                0))))))
                .execute();
    }

    private List<RowData> addMembersToTable(List<MemberSummary> members) {
        var data = new ArrayList<>(List.of(MemberSummaryGoogleSheets.getHeaders()));
        members.forEach(m -> data.add(m.getSheetsRowData()));
        return data;
    }

    private File createExportSheet(Drive drive, ReportFilter filter) {
        try {
            var folder = getUserFolder(drive);
            var now = LocalDateTime.now();

            var sheet = new File();
            sheet.setParents(List.of(folder.getId()));
            sheet.setMimeType("application/vnd.google-apps.spreadsheet");
            sheet.setName(
                    format(
                            "{0}-{1} Member Summary Export",
                            now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            filter.asText()));

            return drive.files().create(sheet).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getUserFolder(Drive drive) {
        try {
            var result =
                    drive.files()
                            .list()
                            .setQ(
                                    "mimeType = 'application/vnd.google-apps.folder' and name = 'website-exports'")
                            .execute();

            if (result.getFiles().isEmpty()) {
                return createUserExportFolder(drive);
            }
            return result.getFiles().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createUserExportFolder(Drive drive) {
        try {
            var fileMetadata = new File();
            fileMetadata.setName("website-exports");
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            return drive.files().create(fileMetadata).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
