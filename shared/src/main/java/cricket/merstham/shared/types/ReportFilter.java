package cricket.merstham.shared.types;

public enum ReportFilter {
    ALL("ALL"),
    UNPAID("UNPAID"),
    OPENAGE("OPENAGE");

    private final String value;

    ReportFilter(String value) {
        this.value = value;
    }

    public String asText() {
        return value;
    }
}
