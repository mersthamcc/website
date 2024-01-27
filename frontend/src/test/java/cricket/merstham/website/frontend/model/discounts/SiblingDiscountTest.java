package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SiblingDiscountTest {

    private SiblingDiscount discount = new SiblingDiscount("junior", new BigDecimal("10.00"));

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
    void threeMatchingItemsReturnsDiscountTimeTwo() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior"));

        var result = discount.apply(basket);

        assertThat(result.doubleValue()).isEqualTo(20.00);
    }

    private MemberSubscription createMemberSubscription(String category) {
        return MemberSubscription.builder().category(category).build();
    }
}
