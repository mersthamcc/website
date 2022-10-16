package cricket.merstham.website.frontend.helpers;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class RoutesHelper {

    public static final String ADMIN_MEMBER_BASE = "/administration/membership";

    public static final String ADMIN_MEMBER_EDIT_ROUTE = ADMIN_MEMBER_BASE + "/edit/{id}";
    public static final String ADMIN_NEWS_BASE = "/administration/news";
    public static final String ADMIN_NEWS_SAVE_ROUTE = ADMIN_NEWS_BASE + "/save";
    public static final String ADMIN_NEWS_NEW_ROUTE = ADMIN_NEWS_BASE + "/new";
    public static final String ADMIN_NEWS_EDIT_ROUTE = ADMIN_NEWS_BASE + "/edit/{id}";
    public static final String ADMIN_NEWS_DELETE_ROUTE = ADMIN_NEWS_BASE + "/delete/{id}";
    public static final String ADMIN_NEWS_AJAX_ROUTE = ADMIN_NEWS_BASE + "/get-data";
    public static final String NEWS_HOME_ROUTE = "/news";
    public static final String NEWS_ITEM_LEGACY_ROUTE = "/news/{id}";
    public static final String NEWS_ROUTE_TEMPLATE = "/{year}/{month}/{day}/{slug}";
    public static final String NEWS_ITEM_ROUTE = "/news" + NEWS_ROUTE_TEMPLATE;

    public static final String LEGACY_RESOURCES = "/resources/**";

    public static URI buildRoute(String template, Map<String, Object> parameters) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance().path(template).build();
        return uriComponents.expand(parameters).toUri();
    }
}
