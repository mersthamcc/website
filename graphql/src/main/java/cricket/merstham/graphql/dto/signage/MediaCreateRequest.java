package cricket.merstham.graphql.dto.signage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class MediaCreateRequest implements Serializable {
    @Serial private static final long serialVersionUID = -2196043573630487892L;

    @JsonProperty private String name;

    @JsonProperty("media_origin")
    private MediaOrigin mediaOrigin;

    @JsonProperty private String description;
    @JsonProperty private List<String> tags;
    @JsonProperty private Arguments arguments;
    @JsonProperty private int workspace;

    @JsonProperty("parent_folder")
    private int parentFolder;

    // {
    //  "name": "name_example",
    //  "media_origin": {
    //    "type": "image",
    //    "source": "local",
    //    "format": null
    //  },
    //  "description": "a short description",
    //  "default_duration": 20,
    //  "tags": [
    //    "Europe",
    //    "Athens"
    //  ],
    //  "availability_schedule": {
    //    "enable": true,
    //    "available_after": "2024-05-20T12:38:00Z",
    //    "available_before": "2024-06-21T12:38:00Z",
    //    "availability_slots": [
    //      {
    //        "start": "10:00:00",
    //        "end": "12:00:00",
    //        "days_of_week": "0101011"
    //      }
    //    ]
    //  },
    //  "arguments": {
    //    "download_from_url": "https://app.yodeck.com/123Fdj1432"
    //  },
    //  "workspace": 123,
    //  "parent_folder": 123
    // }
}
