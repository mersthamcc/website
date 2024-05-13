package cricket.merstham.graphql.helpers;

import java.time.LocalDate;
import java.util.List;

import static java.time.DayOfWeek.SUNDAY;

public class SelectionHelper {

    public static List<LocalDate> getThisWeekendsDates(LocalDate now) {
        if (now.getDayOfWeek().equals(SUNDAY)) {
            return List.of(now, now.minusDays(1));
        }
        var sunday = now.plusDays((long) SUNDAY.getValue() - now.getDayOfWeek().getValue());
        return getThisWeekendsDates(sunday);
    }
}
