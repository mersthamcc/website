package cricket.merstham.website.frontend.model;

import java.net.URI;

public class DataTableValue {
    private Object value;
    private URI link;

    public Object getValue() {
        return value;
    }

    public DataTableValue setValue(Object value) {
        this.value = value;
        return this;
    }

    public URI getLink() {
        return link;
    }

    public DataTableValue setLink(URI link) {
        this.link = link;
        return this;
    }
}
