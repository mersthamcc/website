package cricket.merstham.graphql.extensions;

import com.google.api.services.sheets.v4.model.Border;
import com.google.api.services.sheets.v4.model.Borders;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.Color;
import com.google.api.services.sheets.v4.model.ColorStyle;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.NumberFormat;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.TextFormat;
import cricket.merstham.shared.dto.MemberSummary;

import java.util.List;

import static java.util.Objects.isNull;

public class MemberSummaryGoogleSheets {
    private static final CellFormat HEADER_FORMAT =
            new CellFormat()
                    .setBackgroundColorStyle(
                            new ColorStyle()
                                    .setRgbColor(
                                            new Color()
                                                    .setRed(152.0f / 255)
                                                    .setGreen(184.0f / 255)
                                                    .setBlue(244.0f / 255)))
                    .setBorders(new Borders().setBottom(new Border().setWidth(2).setStyle("solid")))
                    .setTextFormat(new TextFormat().setBold(true));

    private static final CellFormat DATE_FORMAT =
            new CellFormat().setNumberFormat(new NumberFormat().setType("DATE"));

    private static final CellFormat CURRENCY_FORMAT =
            new CellFormat()
                    .setNumberFormat(
                            new NumberFormat()
                                    .setType("CURRENCY")
                                    .setPattern(
                                            "_([$£-809]* #,##0.00_);_([$£-809]* \\(#,##0.00\\);_([$£-809]* \"-\"??_);_(@_)"));

    private static final RowData HEADERS =
            new RowData()
                    .setValues(
                            List.of(
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue().setStringValue("Id"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Family Name"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Given Name"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue().setStringValue("Gender"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Date of Birth"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue().setStringValue("Category"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Description"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Last Registered"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Age Group (if applicable)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue().setStringValue("Price"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Received (so far)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Payment Type(s)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Open Age Allowed"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Photos (Marketing)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Photos (Coaching)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Email Address"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Phone Number"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Parent Name (1)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Parent Email Address (1)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Parent Phone Number (1)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Parent Name (2)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Parent Email Address (2)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Parent Phone Number (2)"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue("Emergency Contact"))
                                            .setUserEnteredFormat(HEADER_FORMAT),
                                    new CellData()
                                            .setUserEnteredValue(
                                                    new ExtendedValue()
                                                            .setStringValue(
                                                                    "Emergency Contact Number"))
                                            .setUserEnteredFormat(HEADER_FORMAT)));

    public static RowData getSheetsRowData(MemberSummary member) {
        RowData rowData = new RowData();

        rowData.setValues(
                List.of(
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setNumberValue(member.getId().doubleValue())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue().setStringValue(member.getGivenName())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue().setStringValue(member.getFamilyName())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue().setStringValue(member.getGender())),
                        new CellData()
                                .setUserEnteredFormat(DATE_FORMAT)
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getDob())
                                                                ? ""
                                                                : member.getDob().toString())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(member.getLastSubsCategory())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(member.getDescription())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setNumberValue(
                                                        member.getMostRecentSubscription()
                                                                .doubleValue())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue().setStringValue(member.getAgeGroup())),
                        new CellData()
                                .setUserEnteredFormat(CURRENCY_FORMAT)
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setNumberValue(
                                                        isNull(member.getLastSubsPrice())
                                                                ? 0
                                                                : member.getLastSubsPrice()
                                                                        .doubleValue())),
                        new CellData()
                                .setUserEnteredFormat(CURRENCY_FORMAT)
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setNumberValue(
                                                        isNull(member.getReceived())
                                                                ? 0
                                                                : member.getReceived()
                                                                        .doubleValue())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(member.getPaymentTypes())),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setBoolValue(
                                                        isNull(member.getDeclarations())
                                                                ? null
                                                                : member.getDeclarations()
                                                                        .contains("OPENAGE"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setBoolValue(
                                                        isNull(member.getDeclarations())
                                                                ? null
                                                                : member.getDeclarations()
                                                                        .contains(
                                                                                "PHOTOS-MARKETING"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setBoolValue(
                                                        isNull(member.getDeclarations())
                                                                ? null
                                                                : member.getDeclarations()
                                                                        .contains(
                                                                                "PHOTOS-MARKETING"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("email"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("phone"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-name-1"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-email-1"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-number-1"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-name-2"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-email-2"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get("parent-number-2"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get(
                                                                                "emergency-contact-name"))),
                        new CellData()
                                .setUserEnteredValue(
                                        new ExtendedValue()
                                                .setStringValue(
                                                        isNull(member.getAttributes())
                                                                ? null
                                                                : member.getAttributes()
                                                                        .get(
                                                                                "emergency-contact-phone")))));

        return rowData;
    }

    public static RowData getHeaders() {
        return HEADERS;
    }
}
