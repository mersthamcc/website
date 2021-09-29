package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.List;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeDefinition implements Serializable {

    private static final long serialVersionUID = 20210522173300L;

    @JsonProperty private String section;

    @JsonProperty private String key;

    @JsonProperty private String type;

    @JsonProperty private boolean mandatory;

    @JsonProperty private List<String> choices;

    public String getSection() {
        return section;
    }

    public AttributeDefinition setSection(String section) {
        this.section = section;
        return this;
    }

    public String getType() {
        return type;
    }

    public AttributeDefinition setType(String type) {
        this.type = type;
        return this;
    }

    public String getKey() {
        return key;
    }

    public AttributeDefinition setKey(String key) {
        this.key = key;
        return this;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public AttributeDefinition setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        return this;
    }

    public List<String> getChoices() {
        return choices;
    }

    public AttributeDefinition setChoices(List<String> choices) {
        this.choices = choices;
        return this;
    }
}
