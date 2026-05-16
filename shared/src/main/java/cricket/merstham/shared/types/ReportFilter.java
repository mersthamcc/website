package cricket.merstham.shared.types;

public enum ReportFilter {
    ALL("ALL"),
    UNPAID("UNPAID"),
    OPENAGE("OPENAGE"),
    NO_PHOTOS_COACHING("NO_PHOTOS_COACHING"),
    NO_PHOTOS_MEDIA("NO_PHOTOS_MEDIA"),
    NOT_THIS_YEAR("NOT_THIS_YEAR"),
    HAS_MEDICAL_CONDITIONS("HAS_MEDICAL_CONDITIONS");

    private final String value;

    ReportFilter(String value) {
        this.value = value;
    }

    public String asText() {
        return value;
    }
}
