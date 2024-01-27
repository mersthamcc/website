package cricket.merstham.website.frontend.configuration;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDefaults {
    private String category;
    private String nameField;
    private String emailField;
    private List<String> persistFields;
}
