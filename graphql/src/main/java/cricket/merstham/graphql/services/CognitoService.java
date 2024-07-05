package cricket.merstham.graphql.services;

import cricket.merstham.shared.dto.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.util.List;

@Service
public class CognitoService {

    private static final Logger LOG = LogManager.getLogger(CognitoService.class);
    private static final String COGNITO_USERNAME_CLAIM = "cognito:username";
    private static final String GIVEN_NAME_CLAIM = "given_name";
    private static final String FAMILY_NAME_CLAIM = "family_name";
    private static final String EMAIL_CLAIM = "email";
    public static final String PHONE_NUMBER_CLAIM = "phone_number";
    private static final String EMAIL_VERIFIED_CLAIM = "email_verified";

    private final CognitoIdentityProviderClient client;
    private final String userPoolId;

    @Autowired
    public CognitoService(
            CognitoIdentityProviderClient client,
            @Value("${configuration.cognito.user-pool-id:#{null}}") String userPoolId) {
        this.client = client;
        this.userPoolId = userPoolId;
    }

    public User getUserDetails(String username) {
        var user =
                client.adminGetUser(
                        AdminGetUserRequest.builder()
                                .userPoolId(userPoolId)
                                .username(username)
                                .build());

        return User.builder()
                .subjectId(user.username())
                .username(getUserAttribute(user.userAttributes(), COGNITO_USERNAME_CLAIM))
                .givenName(getUserAttribute(user.userAttributes(), GIVEN_NAME_CLAIM))
                .familyName(getUserAttribute(user.userAttributes(), FAMILY_NAME_CLAIM))
                .email(getUserAttribute(user.userAttributes(), EMAIL_CLAIM))
                .phoneNumber(getUserAttribute(user.userAttributes(), PHONE_NUMBER_CLAIM))
                .verified(
                        Boolean.parseBoolean(
                                getUserAttribute(user.userAttributes(), EMAIL_VERIFIED_CLAIM)))
                .enabled(user.enabled())
                .build();
    }

    private String getUserAttribute(List<AttributeType> attributes, String name) {
        return attributes.stream()
                .filter(a -> a.name().equals(name))
                .findFirst()
                .map(a -> a.value())
                .orElse("");
    }
}
