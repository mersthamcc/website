package uk.co.mersthamcc.keycloak.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.models.UserModel;
import uk.co.mersthamcc.keycloak.ConditionalOtpConstants;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.co.mersthamcc.keycloak.ConditionalOtpConstants.MOBILE_PHONE_ATTR;
import static uk.co.mersthamcc.keycloak.ConditionalOtpConstants.PHONE_NUMBER_FIELD;
import static uk.co.mersthamcc.keycloak.TestHelpers.MOBILE_NUMBER;

class ConditionalOtpSmsHelperTest {


    private MultivaluedMap<String, String> updateForm(String mobileNumber) {
        MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
        if (mobileNumber!=null) form.put(PHONE_NUMBER_FIELD, List.of(mobileNumber));
        return form;
    }

    @Test
    void processUpdateSuccess() {
        UserModel user = mock(UserModel.class);

        assertThat(ConditionalOtpSmsHelper.processUpdate(user, updateForm(MOBILE_NUMBER)), equalTo(true));
        verify(user).setAttribute(eq(MOBILE_PHONE_ATTR), eq(List.of(MOBILE_NUMBER)));
    }

    @Test
    void processUpdateFailWithEmptyString() {
        UserModel user = mock(UserModel.class);

        assertThat(ConditionalOtpSmsHelper.processUpdate(user, updateForm("")), equalTo(false));
    }

    @Test
    void processUpdateFailWithBlankString() {
        UserModel user = mock(UserModel.class);

        assertThat(ConditionalOtpSmsHelper.processUpdate(user, updateForm("    ")), equalTo(false));
    }

    @Test
    void processUpdateFailWithNullString() {
        UserModel user = mock(UserModel.class);

        assertThat(ConditionalOtpSmsHelper.processUpdate(user, updateForm(null)), equalTo(false));
    }

    @ParameterizedTest
    @ValueSource(strings = {MOBILE_NUMBER, "07777123456", "+44 7777 123 456", "(07777) 123456", "+44-7777-123456", "07777-123456", "  0777 7123 456  "})
    void normalisePhoneNumberTests(String input) {
        assertThat(ConditionalOtpSmsHelper.normalisePhoneNumber(input), equalTo(MOBILE_NUMBER));
    }

    @Test
    void normalisePhoneNumberTestInternational() {
        assertThat(ConditionalOtpSmsHelper.normalisePhoneNumber("+1 222 333 4444"), equalTo("+12223334444"));
        assertThat(ConditionalOtpSmsHelper.normalisePhoneNumber("00 1 222 333 4444"), equalTo("+12223334444"));
    }

}