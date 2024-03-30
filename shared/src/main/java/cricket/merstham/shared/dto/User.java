package cricket.merstham.shared.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = 8364481698860946275L;

    private String username;
    private String subjectId;
    private String email;
    private String givenName;
    private String familyName;
    private String phoneNumber;
    private List<String> roles;

    @Transient
    public String getFullName() {
        return givenName + " " + familyName;
    }
}
