package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SiblingDiscountTest {

    private static final int CURRENT_YEAR = LocalDate.now().getYear();
    private SiblingDiscount discount =
            new SiblingDiscount("junior", new BigDecimal("10.00"), CURRENT_YEAR);

    @Test
    void noItemsReturnsZeroDiscount() {
        var basket = new RegistrationBasket(List.of());

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(0);
    }

    @Test
    void noMatchingItemsReturnsZeroDiscount() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(0);
    }

    @Test
    void singleMatchingItemsReturnsZeroDiscount() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(0);
    }

    @Test
    void twoMatchingItemsReturnsDiscountTimeOne() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(10.00);
    }

    @Test
    void threeMatchingItemsReturnsDiscountTimesTwo() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(20.00);
    }

    @Test
    void singleNewMatchingItemAndHistoricMatchingItemsReturnsNoDiscount() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR - 1));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR - 1));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(0.00);
    }

    @Test
    void singleNewMatchingItemAndSinglePreviouslyRegisteredMatchingItemsReturnsSingleDiscount() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR - 1));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(10.00);
    }

    @Test
    void singleNewMatchingItemAndMultiplePreviouslyRegisteredMatchingItemsReturnsSingleDiscount() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR));
        basket.putSubscription(
                UUID.randomUUID(),
                createMemberSubscription("junior", RegistrationAction.NONE, CURRENT_YEAR));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(10.00);
    }

    private MemberSubscription createMemberSubscription(String category) {
        return createMemberSubscription(category, RegistrationAction.NEW, CURRENT_YEAR);
    }

    private MemberSubscription createMemberSubscription(
            String category, RegistrationAction action, int registrationYear) {
        return MemberSubscription.builder()
                .category(category)
                .action(action)
                .year(registrationYear)
                .price(BigDecimal.TEN)
                .build();
    }
}
