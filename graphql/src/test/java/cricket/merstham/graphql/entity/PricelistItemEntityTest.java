package cricket.merstham.graphql.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PricelistItemEntityTest {

    @Test
    void getCurrentPrice() {
        var item =
                PricelistItemEntity.builder()
                        .description("Test Item")
                        .priceList(
                                List.of(
                                        PricelistEntity.builder()
                                                .primaryKey(
                                                        PricelistEntityId.builder()
                                                                .dateFrom(
                                                                        LocalDate.now()
                                                                                .minus(
                                                                                        10,
                                                                                        ChronoUnit
                                                                                                .MONTHS))
                                                                .dateTo(LocalDate.now())
                                                                .build())
                                                .price(BigDecimal.TEN)
                                                .build(),
                                        PricelistEntity.builder()
                                                .primaryKey(
                                                        PricelistEntityId.builder()
                                                                .dateFrom(
                                                                        LocalDate.now()
                                                                                .plus(
                                                                                        1,
                                                                                        ChronoUnit
                                                                                                .DAYS))
                                                                .dateTo(
                                                                        LocalDate.now()
                                                                                .plus(
                                                                                        10,
                                                                                        ChronoUnit
                                                                                                .MONTHS))
                                                                .build())
                                                .price(BigDecimal.valueOf(20.0))
                                                .build()))
                        .build();
        var item2 =
                PricelistItemEntity.builder()
                        .description("Test Item")
                        .priceList(
                                List.of(
                                        PricelistEntity.builder()
                                                .primaryKey(
                                                        PricelistEntityId.builder()
                                                                .dateFrom(
                                                                        LocalDate.now()
                                                                                .minus(
                                                                                        10,
                                                                                        ChronoUnit
                                                                                                .MONTHS))
                                                                .dateTo(
                                                                        LocalDate.now()
                                                                                .minus(
                                                                                        1,
                                                                                        ChronoUnit
                                                                                                .DAYS))
                                                                .build())
                                                .price(BigDecimal.TEN)
                                                .build(),
                                        PricelistEntity.builder()
                                                .primaryKey(
                                                        PricelistEntityId.builder()
                                                                .dateFrom(LocalDate.now())
                                                                .dateTo(
                                                                        LocalDate.now()
                                                                                .plus(
                                                                                        10,
                                                                                        ChronoUnit
                                                                                                .MONTHS))
                                                                .build())
                                                .price(BigDecimal.valueOf(20.0))
                                                .build()))
                        .build();

        assertThat(item.getCurrentPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(item2.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(20.0));
    }
}
