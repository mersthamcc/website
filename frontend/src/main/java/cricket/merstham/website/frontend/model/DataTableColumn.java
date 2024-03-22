package cricket.merstham.website.frontend.model;

public class DataTableColumn {
    private String key;
    private String fieldName;
    private String functionName;
    private boolean function = false;
    private boolean sortable = true;
    private boolean display = true;

    public String getKey() {
        return key;
    }

    public DataTableColumn setKey(String key) {
        this.key = key;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public DataTableColumn setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public boolean isSortable() {
        return sortable;
    }

    public DataTableColumn setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public boolean isDisplay() {
        return display;
    }

    public DataTableColumn setDisplay(boolean display) {
        this.display = display;
        return this;
    }

    public String getFunctionName() {
        return functionName;
    }

    public DataTableColumn setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public boolean isFunction() {
        return function;
    }

    public DataTableColumn setFunction(boolean function) {
        this.function = function;
        return this;
    }
}
