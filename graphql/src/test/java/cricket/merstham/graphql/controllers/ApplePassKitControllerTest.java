package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.graphql.repository.PassKitDeviceRegistrationEntityRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ApplePassKitControllerTest {

    private static String REGISTERED_PASS_TYPE_ID = "org.example.testing";

    private MemberEntityRepository memberEntityRepository = mock(MemberEntityRepository.class);
    private PassKitDeviceRegistrationEntityRepository passKitDeviceRegistrationEntityRepository =
            mock(PassKitDeviceRegistrationEntityRepository.class);

    private ApplePassKitController applePassKitController =
            new ApplePassKitController(
                    memberEntityRepository,
                    passKitDeviceRegistrationEntityRepository,
                    REGISTERED_PASS_TYPE_ID);

    @Test
    void parseSerialNumber() {
        var result =
                applePassKitController.parseSerialNumber(
                        "ca182b98-606a-46c9-85ab-f50bcd5b612a--20107");

        assertThat(result).isEqualTo("20107");
    }
}
