package cricket.merstham.graphql.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static cricket.merstham.graphql.helpers.SelectionHelper.getThisWeekendsDates;
import static org.assertj.core.api.Assertions.assertThat;

class SelectionHelperTest {

    @Test
    void getThisWeekendsDatesShouldReturnTodayAndTomorrowWhenDayIsSunday() {
        var today = LocalDate.of(2024, 5, 12);

        var result = getThisWeekendsDates(today);
        assertThat(result)
                .hasSize(2)
                .satisfiesExactly(sat -> sat.equals(today.minusDays(1)), sun -> sun.equals(today));
    }

    @ParameterizedTest
    @MethodSource("testDates")
    void getThisWeekendsDatesShouldReturnCorrectDatesWhenTodayIsAWeekday(LocalDate today) {
        var result = getThisWeekendsDates(today);
        assertThat(result)
                .hasSize(2)
                .satisfiesExactly(
                        sat -> sat.equals(LocalDate.of(2024, 05, 18)),
                        sun -> sun.equals(LocalDate.of(2024, 05, 19)));
    }

    static Stream<Arguments> testDates() {
        return Stream.of(
                Arguments.of(LocalDate.of(2024, 5, 13)),
                Arguments.of(LocalDate.of(2024, 5, 14)),
                Arguments.of(LocalDate.of(2024, 5, 15)),
                Arguments.of(LocalDate.of(2024, 5, 16)),
                Arguments.of(LocalDate.of(2024, 5, 17)),
                Arguments.of(LocalDate.of(2024, 5, 18)),
                Arguments.of(LocalDate.of(2024, 5, 19)));
    }
}
