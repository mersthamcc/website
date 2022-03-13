package cricket.merstham.graphql.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class Member implements Serializable {
    private static final long serialVersionUID = -5200325799993222375L;

    private Integer id;
    private String type;
    private Instant registrationDate;
    private String ownerUserId;
    private List<MemberAttribute> attributes = new ArrayList<>();
    private List<MemberSubscription> subscription = new ArrayList<>();
}