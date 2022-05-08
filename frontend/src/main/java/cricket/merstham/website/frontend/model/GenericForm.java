package cricket.merstham.website.frontend.model;

import java.util.Map;

public class GenericForm {
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public GenericForm setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}
