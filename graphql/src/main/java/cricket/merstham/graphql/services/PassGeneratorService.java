package cricket.merstham.graphql.services;

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
import cricket.merstham.graphql.configuration.WalletConfiguration;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.shared.dto.Pass;
import cricket.merstham.shared.extensions.StringExtensions;
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
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;
import static cricket.merstham.shared.IdentifierConstants.APPLE_PASS_SERIAL;
import static cricket.merstham.shared.IdentifierConstants.GOOGLE_PASS_SERIAL;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Service
@ExtensionMethod({StringExtensions.class})
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
    private final String apiBaseUrl;
    private final MemberEntityRepository memberEntityRepository;

    @Autowired
    public PassGeneratorService(
            @Named("appleSigningCertificate") X509Certificate appleSigningCertificate,
            @Named("appleSigningKey") PrivateKey appleSigningKey,
            @Named("appleIntermediaryCertificate") X509Certificate appleIntermediaryCertificate,
            @Value("${configuration.wallet.club-name}") String clubName,
            @Value("${configuration.wallet.background-colour}") String backgroundColourHex,
            @Value("${configuration.wallet.foreground-colour}") String foregroundColourHex,
            @Value("${configuration.wallet.wallet-logo}") String logoUrl,
            @Value("${configuration.wallet.apple.pass-identifier}") String applePassIdentifier,
            @Value("${configuration.wallet.barcode-pattern}") String barcodePattern,
            @Value("${configuration.wallet.apple.team-id}") String appleTeamid,
            @Value("${configuration.wallet.location-latitude}") Double locationLatitude,
            @Value("${configuration.wallet.location-longitude}") Double locationLongitude,
            @Value("${configuration.wallet.location-prompt}") String locationPrompt,
            @Value("${configuration.wallet.pass-description}") String walletDescription,
            Walletobjects walletobjects,
            @Value("${configuration.wallet.google.issuer}") String googleIssuerId,
            String googleWalletClass,
            @Named("WalletCredentials") GoogleCredentials googleCredentials,
            @Value("${configuration.api-url}") String apiBaseUrl,
            MemberEntityRepository memberEntityRepository) {
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
        this.apiBaseUrl = apiBaseUrl;
        this.memberEntityRepository = memberEntityRepository;
        this.signingInformation =
                new PKSigningInformation(
                        appleSigningCertificate, appleSigningKey, appleIntermediaryCertificate);
        signingUtil = new PKInMemorySigningUtil();
        this.googleWalletClass = googleWalletClass;
    }

    @PreAuthorize("isAuthenticated()")
    public Pass getPassData(String memberUuid, String type, Principal principal)
            throws IOException {
        var member =
                memberEntityRepository
                        .findByUuidAndOwnerUserId(memberUuid, getSubject(principal))
                        .orElseThrow();
        String serialNumber;
        String content;
        switch (type) {
            case "apple":
                serialNumber = member.getIdentifiers().get(APPLE_PASS_SERIAL);
                if (isNull(serialNumber) || !serialNumber.isNumeric()) {
                    serialNumber = Long.toString(member.getPassUpdateEpochSecond());
                }
                content =
                        Base64.getEncoder()
                                .encodeToString(createAppleWalletPass(member, serialNumber));
                member.getIdentifiers().put(APPLE_PASS_SERIAL, serialNumber);
                break;
            case "google":
                serialNumber = member.getIdentifiers().get(GOOGLE_PASS_SERIAL);
                if (isNull(serialNumber)) {
                    serialNumber = UUID.randomUUID().toString();
                }
                content = createGoogleWalletPass(member, serialNumber);
                member.getIdentifiers().put(GOOGLE_PASS_SERIAL, serialNumber);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        memberEntityRepository.saveAndFlush(member);
        return Pass.builder().serialNumber(serialNumber).content(content).type(type).build();
    }

    public String createGoogleWalletPass(MemberEntity member, String serialNumber) {
        var passObject = createOrUpdateGooglePassObject(member, serialNumber);

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

    public byte[] createAppleWalletPass(MemberEntity member, String serialNumber)
            throws IOException {
        var pass =
                PKPass.builder()
                        .formatVersion(1)
                        .teamIdentifier(appleTeamid)
                        .organizationName(clubName)
                        .description(walletDescription)
                        .serialNumber(
                                serialNumber.startsWith(member.getUuid() + "--")
                                        ? serialNumber
                                        : format("{0}--{1}", member.getUuid(), serialNumber))
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
                                                .format(PKBarcodeFormat.PKBarcodeFormatQR)
                                                .message(format(barcodePattern, member.getId()))
                                                .build()))
                        .webServiceURL(new URL(new URL(apiBaseUrl), "/passkit"))
                        .authenticationToken(member.getUuid())
                        .build();

        var template = getTemplate();

        try {
            return signingUtil.createSignedAndZippedPkPassArchive(
                    pass, template, signingInformation);
        } catch (PKSigningException e) {
            throw new RuntimeException(e);
        }
    }

    private static PKGenericPass buildGenericPass(MemberEntity member) {
        return PKGenericPass.builder()
                .passType(PKPassType.PKGenericPass)
                .primaryField(
                        PKField.builder()
                                .label("Name")
                                .key("name")
                                .value(member.getFullName())
                                .build())
                .auxiliaryField(
                        PKField.builder()
                                .label("Category")
                                .key("category")
                                .value(
                                        member.getMostRecentSubscription()
                                                .getPricelistItem()
                                                .getDescription())
                                .build())
                .auxiliaryField(
                        PKField.builder()
                                .label("Year")
                                .key("year")
                                .value(
                                        Integer.toString(
                                                member.getMostRecentSubscription().getYear()))
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

    public GenericObject createOrUpdateGooglePassObject(MemberEntity member, String serialNumber) {
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

    private GenericObject buildGooglePass(MemberEntity member, String cardId) {
        return new GenericObject()
                .setId(cardId)
                .setClassId(googleWalletClass)
                .setState("ACTIVE")
                .setTextModulesData(
                        List.of(
                                new TextModuleData()
                                        .setHeader("Category")
                                        .setBody(
                                                member.getMostRecentSubscription()
                                                        .getPricelistItem()
                                                        .getDescription())
                                        .setId(WalletConfiguration.CATEGORY),
                                new TextModuleData()
                                        .setHeader("Year")
                                        .setBody(
                                                Integer.toString(
                                                        member.getMostRecentSubscription()
                                                                .getYear()))
                                        .setId(WalletConfiguration.YEAR)))
                .setBarcode(
                        new Barcode()
                                .setType("QR_CODE")
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
                                                .setValue(member.getFullName())))
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
