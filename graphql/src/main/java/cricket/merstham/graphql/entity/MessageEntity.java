package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "message")
public class MessageEntity {
    @Id
    @Size(max = 32)
    @Column(name = "key", nullable = false, length = 32)
    private String key;

    @Size(max = 64)
    @NotNull
    @Column(name = "class", nullable = false, length = 64)
    private String messageClass;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String messageText;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;
}
