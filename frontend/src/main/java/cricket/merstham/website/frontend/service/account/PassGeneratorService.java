package cricket.merstham.website.frontend.service.account;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.walletobjects.Walletobjects;
import com.google.api.services.walletobjects.model.Barcode;
import com.google.api.services.walletobjects.model.GenericObject;
import com.google.api.services.walletobjects.model.Image;
import com.google.api.services.walletobjects.model.ImageUri;
import com.google.api.services.walletobjects.model.LocalizedString;
import com.google.api.services.walletobjects.model.TextModuleData;
import com.google.api.services.walletobjects.model.TranslatedString;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
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
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cricket.merstham.website.frontend.configuration.WalletConfiguration.CATEGORY;
import static cricket.merstham.website.frontend.configuration.WalletConfiguration.YEAR;
import static java.text.MessageFormat.format;

@Service
public class PassGeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(PassGeneratorService.class);
    public static final String LANGUAGE = "en-US";

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
    private final String walletDescription;
    private final Walletobjects walletobjects;
    private final String googleIssuerId;
    private final String googleWalletClass;
    private final GoogleCredentials googleCredentials;

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
            @Value("${wallet.pass-description}") String walletDescription,
            Walletobjects walletobjects,
            @Value("${wallet.google.issuer}") String googleIssuerId,
            String googleWalletClass,
            GoogleCredentials googleCredentials) {
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
        this.walletDescription = walletDescription;
        this.walletobjects = walletobjects;
        this.googleIssuerId = googleIssuerId;
        this.googleCredentials = googleCredentials;
        this.signingInformation =
                new PKSigningInformation(
                        appleSigningCertificate, appleSigningKey, appleIntermediaryCertificate);
        signingUtil = new PKInMemorySigningUtil();
        this.googleWalletClass = googleWalletClass;
    }

    public String createGoogleWalletPass(MemberSummary member, String serialNumber) {
        var passObject = getGooglePassObject(member, serialNumber);

        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", ((ServiceAccountCredentials) googleCredentials).getClientEmail());
        claims.put("aud", "google");
        claims.put("origins", List.of("www.mersthamcc.co.uk"));
        claims.put("typ", "savetowallet");
        claims.put("iat", Instant.now().getEpochSecond());

        HashMap<String, Object> payload = new HashMap<>();
        payload.put("genericObjects", List.of(passObject));

        claims.put("payload", payload);
        Algorithm algorithm =
                Algorithm.RSA256(
                        null,
                        (RSAPrivateKey)
                                ((ServiceAccountCredentials) googleCredentials).getPrivateKey());
        String token = JWT.create().withPayload(claims).sign(algorithm);

        return format("https://pay.google.com/gp/v/save/{0}", token);
    }

    public byte[] createAppleWalletPass(MemberSummary member, String serialNumber)
            throws IOException {
        var pass =
                PKPass.builder()
                        .formatVersion(1)
                        .teamIdentifier(appleTeamid)
                        .organizationName(clubName)
                        .description(walletDescription)
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

    private GenericObject getGooglePassObject(MemberSummary member, String serialNumber) {
        try {
            var cardId = format("{0}.{1}-{2}", googleIssuerId, member.getUuid(), serialNumber);
            try {
                walletobjects.genericobject().get(cardId).execute();
                return walletobjects
                        .genericobject()
                        .update(cardId, buildGooglePass(member, cardId))
                        .execute();
            } catch (GoogleJsonResponseException ex) {
                if (ex.getStatusCode() != 404) {
                    throw new RuntimeException("Unknown error getting Google Pass");
                }
            }
            GenericObject response =
                    walletobjects.genericobject().insert(buildGooglePass(member, cardId)).execute();

            return response;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private GenericObject buildGooglePass(MemberSummary member, String cardId) {
        return new GenericObject()
                .setId(cardId)
                .setClassId(googleWalletClass)
                .setState("ACTIVE")
                .setTextModulesData(
                        List.of(
                                new TextModuleData()
                                        .setHeader("Category")
                                        .setBody(member.getDescription())
                                        .setId(CATEGORY),
                                new TextModuleData()
                                        .setHeader("Year")
                                        .setBody(member.getLastSubsYear())
                                        .setId(YEAR)))
                .setBarcode(
                        new Barcode()
                                .setType("CODE_128")
                                .setValue(format(barcodePattern, member.getId())))
                .setCardTitle(
                        new LocalizedString()
                                .setDefaultValue(
                                        new TranslatedString()
                                                .setLanguage(LANGUAGE)
                                                .setValue(clubName)))
                .setHeader(
                        new LocalizedString()
                                .setDefaultValue(
                                        new TranslatedString()
                                                .setLanguage(LANGUAGE)
                                                .setValue(
                                                        format(
                                                                "{0} {1}",
                                                                member.getGivenName(),
                                                                member.getFamilyName()))))
                .setSubheader(
                        new LocalizedString()
                                .setDefaultValue(
                                        new TranslatedString()
                                                .setLanguage(LANGUAGE)
                                                .setValue("Name")))
                .setHexBackgroundColor(backgroundColourHex)
                .setLogo(
                        new Image()
                                .setSourceUri(new ImageUri().setUri(logoUrl))
                                .setContentDescription(
                                        new LocalizedString()
                                                .setDefaultValue(
                                                        new TranslatedString()
                                                                .setLanguage(LANGUAGE)
                                                                .setValue(clubName))));
    }
}
