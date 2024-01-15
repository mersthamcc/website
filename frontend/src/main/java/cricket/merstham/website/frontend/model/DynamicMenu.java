package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.shared.dto.Venue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
@Accessors(chain = true)
public class DynamicMenu {
    private List<Venue> venues;
    private List<Integer> seasons;
    private List<ContactCategory> contactCategories;
}
