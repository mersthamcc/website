package cricket.merstham.website.frontend.service.account;

import cricket.merstham.shared.dto.MemberSummary;
import de.brendamour.jpasskit.PKBarcode;
import de.brendamour.jpasskit.PKField;
import de.brendamour.jpasskit.PKLocation;
import de.brendamour.jpasskit.PKPass;
import de.brendamour.jpasskit.enums.PKBarcodeFormat;
import de.brendamour.jpasskit.enums.PKPassType;
import de.brendamour.jpasskit.passes.PKGenericPass;
import de.brendamour.jpasskit.signing.PKInMemorySigningUtil;
import de.brendamour.jpasskit.signing.PKPassTemplateInMemory;
import de.brendamour.jpasskit.signing.PKSigningException;
import de.brendamour.jpasskit.signing.PKSigningInformation;
import jakarta.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class PassGeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(PassGeneratorService.class);

    private final PKSigningInformation signingInformation;
    private final PKInMemorySigningUtil signingUtil;
    private final String clubName;
    private final String backgroundColourHex;
    private final String foregroundColourHex;
    private final String logoUrl;
    private final String applePassIdentifier;
    private final String barcodePattern;
    private final String appleTeamid;
    private final Double locationLatitude;
    private final Double locationLongitude;
    private final String locationPrompt;
    private final String appleWalletDescription;

    @Autowired
    public PassGeneratorService(
            @Named("appleSigningCertificate") X509Certificate appleSigningCertificate,
            @Named("appleSigningKey") PrivateKey appleSigningKey,
            @Named("appleIntermediaryCertificate") X509Certificate appleIntermediaryCertificate,
            @Value("${club.club-name}") String clubName,
            @Value("${wallet.background-colour}") String backgroundColourHex,
            @Value("${wallet.foreground-colour}") String foregroundColourHex,
            @Value("${wallet.wallet-logo}") String logoUrl,
            @Value("${wallet.apple.pass-identifier}") String applePassIdentifier,
            @Value("${wallet.barcode-pattern}") String barcodePattern,
            @Value("${wallet.apple.team-id}") String appleTeamid,
            @Value("${wallet.location-latitude}") Double locationLatitude,
            @Value("${wallet.location-longitude}") Double locationLongitude,
            @Value("${wallet.location-prompt}") String locationPrompt,
            @Value("${wallet.apple.pass-description}") String appleWalletDescription) {
        this.clubName = clubName;
        this.backgroundColourHex = backgroundColourHex;
        this.foregroundColourHex = foregroundColourHex;
        this.logoUrl = logoUrl;
        this.applePassIdentifier = applePassIdentifier;
        this.barcodePattern = barcodePattern;
        this.appleTeamid = appleTeamid;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.locationPrompt = locationPrompt;
        this.appleWalletDescription = appleWalletDescription;
        this.signingInformation =
                new PKSigningInformation(
                        appleSigningCertificate, appleSigningKey, appleIntermediaryCertificate);
        signingUtil = new PKInMemorySigningUtil();
    }

    public byte[] createAppleWalletPass(MemberSummary member, String serialNumber)
            throws IOException {
        var pass =
                PKPass.builder()
                        .formatVersion(1)
                        .teamIdentifier(appleTeamid)
                        .organizationName(clubName)
                        .description(appleWalletDescription)
                        .serialNumber(format("{0}-{1}", member.getUuid(), serialNumber))
                        .backgroundColor(backgroundColourHex)
                        .foregroundColor(foregroundColourHex)
                        .labelColor(foregroundColourHex)
                        .logoText(clubName)
                        .sharingProhibited(false)
                        .passTypeIdentifier(applePassIdentifier)
                        .locations(
                                List.of(
                                        PKLocation.builder()
                                                .latitude(locationLatitude)
                                                .longitude(locationLongitude)
                                                .relevantText(locationPrompt)
                                                .build()))
                        .pass(buildGenericPass(member))
                        .barcodes(
                                List.of(
                                        PKBarcode.builder()
                                                .format(PKBarcodeFormat.PKBarcodeFormatCode128)
                                                .message(format(barcodePattern, member.getId()))
                                                .build()))
                        .build();

        var template = getTemplate();

        try {
            return signingUtil.createSignedAndZippedPkPassArchive(
                    pass, template, signingInformation);
        } catch (PKSigningException e) {
            throw new RuntimeException(e);
        }
    }

    private static PKGenericPass buildGenericPass(MemberSummary member) {
        return PKGenericPass.builder()
                .passType(PKPassType.PKGenericPass)
                .primaryField(
                        PKField.builder()
                                .label("Name")
                                .key("name")
                                .value(
                                        format(
                                                "{0} {1}",
                                                member.getGivenName(), member.getFamilyName()))
                                .build())
                .auxiliaryField(
                        PKField.builder()
                                .label("Category")
                                .key("category")
                                .value(member.getDescription())
                                .build())
                .auxiliaryField(
                        PKField.builder()
                                .label("Year")
                                .key("year")
                                .value(member.getLastSubsYear())
                                .build())
                .build();
    }

    @NotNull
    private PKPassTemplateInMemory getTemplate() throws IOException {
        PKPassTemplateInMemory template = new PKPassTemplateInMemory();
        template.addFile(PKPassTemplateInMemory.PK_LOGO, new URL(logoUrl));
        template.addFile(PKPassTemplateInMemory.PK_LOGO_RETINA, new URL(logoUrl));
        template.addFile(PKPassTemplateInMemory.PK_ICON, new URL(logoUrl));
        template.addFile(PKPassTemplateInMemory.PK_ICON_RETINA, new URL(logoUrl));
        return template;
    }
}
