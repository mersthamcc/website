package cricket.merstham.website.frontend.templates;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.text.MessageFormat.format;

public class PhoneNumberFormatter implements TemplateMethodModelEx {

    public static final String DEFAULT_REGION = "GB";
    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 1) {
            throw new TemplateModelException("Wrong arguments");
        }
        try {
            var phoneNumber = phoneUtil.parse(((SimpleScalar) args.get(0)).getAsString(), DEFAULT_REGION);
            var areaCodeLength = phoneUtil.getLengthOfNationalDestinationCode(phoneNumber);
            var fullNumber = Long.toString(phoneNumber.getNationalNumber());
            return PhoneNumberWrapper
                    .builder()
                    .countryCode(format("+{0}", phoneNumber.getCountryCode()))
                    .areaCode(fullNumber.substring(0, areaCodeLength))
                    .localNumber(fullNumber.substring(areaCodeLength))
                    .formatted(phoneUtil.formatNumberForMobileDialing(
                            phoneNumber,
                            DEFAULT_REGION, true))
                    .build();
//            return new SimpleHash(
//                    Map.of(
//                            "country_code",
//                                    new SimpleScalar(format("+{0}", phoneNumber.getCountryCode())),
//                            "area_code", new SimpleScalar(fullNumber.substring(0, areaCodeLength)),
//                            "local_number", new SimpleScalar(fullNumber.substring(areaCodeLength)),
//                            "formatted",
//                                    new SimpleScalar(
//                                            phoneUtil.formatNumberForMobileDialing(
//                                                    phoneNumber,
//                                                    DEFAULT_REGION, true))));
        } catch (NumberParseException e) {
            throw new TemplateModelException("Cannot parse phone number", e);
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PhoneNumberWrapper {
        private String countryCode;
        private String areaCode;
        private String localNumber;
        private String formatted;
    }
}
