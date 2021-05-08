package cricket.merstham.website.frontend.configuration;

import java.util.List;

public class CookieConfiguration {
    private String apiKey;
    private String productCode;
    private List<CookieCategory> optionalCookiesCategories;

    public String getApiKey() {
        return apiKey;
    }

    public CookieConfiguration setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public String getProductCode() {
        return productCode;
    }

    public CookieConfiguration setProductCode(String productCode) {
        this.productCode = productCode;
        return this;
    }

    public List<CookieCategory> getOptionalCookiesCategories() {
        return optionalCookiesCategories;
    }

    public CookieConfiguration setOptionalCookiesCategories(List<CookieCategory> optionalCookiesCategories) {
        this.optionalCookiesCategories = optionalCookiesCategories;
        return this;
    }

    public static class CookieCategory {
        private String name;
        private String label;
        private boolean enabled;
        private String description;
        private List<String> cookieNames;

        public String getName() {
            return name;
        }

        public CookieCategory setName(String name) {
            this.name = name;
            return this;
        }

        public String getLabel() {
            return label;
        }

        public CookieCategory setLabel(String label) {
            this.label = label;
            return this;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public CookieCategory setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public CookieCategory setDescription(String description) {
            this.description = description;
            return this;
        }

        public List<String> getCookieNames() {
            return cookieNames;
        }

        public CookieCategory setCookieNames(List<String> cookieNames) {
            this.cookieNames = cookieNames;
            return this;
        }
    }
}
