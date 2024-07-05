package cricket.merstham.shared.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
public class User implements Serializable {
    @Serial private static final long serialVersionUID = 8364481698860946275L;

    private String username;
    private String subjectId;
    private String email;
    private String givenName;
    private String familyName;
    private String phoneNumber;
    private List<String> roles;
    private boolean enabled;
    private boolean verified;

    @Transient
    public String getFullName() {
        return givenName + " " + familyName;
    }
}
