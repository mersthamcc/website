package cricket.merstham.graphql.configuration;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class CacheConfiguration {

    public static final String MEMBER_SUMMARY_CACHE = "member_summary";
    public static final String NEWS_SUMMARY_CACHE = "news_feed";
    public static final String NEWS_SUMMARY_TOTAL_CACHE = "news_feed_totals";
    public static final String NEWS_ITEM_BY_ID_CACHE = "news_item_by_id";
    public static final String NEWS_ITEM_BY_PATH_CACHE = "news_item_by_path";
    public static final String EVENT_SUMMARY_CACHE = "event_feed";
    public static final String EVENT_SUMMARY_TOTAL_CACHE = "event_feed_totals";
    public static final String EVENT_ITEM_BY_ID_CACHE = "event_item_by_id";
    public static final String EVENT_ITEM_BY_PATH_CACHE = "event_item_by_path";
    public static final String CONTACT_SUMMARY_CACHE = "contact_feed";
    public static final String CONTACT_CATEGORY_SUMMARY_CACHE = "contact_category_feed";
    public static final String CONTACT_SUMMARY_TOTAL_CACHE = "contact_feed_totals";
    public static final String CONTACT_CATEGORY_SUMMARY_TOTAL_CACHE =
            "contact_category_feed_totals";
    public static final String CONTACT_ITEM_BY_ID_CACHE = "contact_item_by_id";
    public static final String CONTACT_ITEM_BY_PATH_CACHE = "contact_item_by_path";
    public static final String TEAM_CACHE = "team";
    public static final String ACTIVE_TEAM_CACHE = "active_team";
    public static final String FIXTURE_CACHE = "fixture";
    public static final String PAGE_SUMMARY_TOTAL_CACHE = "page_feed_totals";
    public static final String PAGE_ITEM_BY_ID_CACHE = "page_item_by_id";

    public static final String VENUE_SUMMARY_TOTAL_CACHE = "venue_feed_totals";
    public static final String VENUES_FOR_MENU_CACHE = "venue_for_menu_cache";
    public static final String VENUE_ITEM_BY_ID_CACHE = "venue_item_by_id";

    public Set<String> getCacheNames() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .map(f -> f.getName())
                .collect(Collectors.toSet());
    }
}
