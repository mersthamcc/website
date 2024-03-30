package cricket.merstham.website.frontend.model;

import cricket.merstham.website.frontend.validators.Password;
import cricket.merstham.website.frontend.validators.PasswordsMatch;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@PasswordsMatch(message = "passwordsDoNotMatch")
public class ChangePassword implements Serializable {

    @Serial private static final long serialVersionUID = -530430891948199568L;

    private String currentPassword;

    @NotEmpty(message = "passwordNotProvided")
    @Length(min = 8, message = "passwordNotLongEnough")
    @Password(message = "passwordNotStrongEnough")
    private String password;

    @NotEmpty(message = "confirmationPasswordNotProvided")
    private String confirmPassword;
}
