package cricket.merstham.website.frontend.model;

import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.website.frontend.model.discounts.Discount;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistrationBasketTest {

    @Test
    void emptyBasketResultsInTotalOfZero() {
        var basket = new RegistrationBasket(List.of());

        var total = basket.getBasketTotal();

        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void basketWithSingleItemResultsInCorrectTotal() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));

        var total = basket.getBasketTotal();

        assertThat(total.doubleValue()).isEqualTo(100);
    }

    @Test
    void basketWithMultipleItemsResultsInCorrectTotal() {
        var basket = new RegistrationBasket(List.of());
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior", 50));

        var total = basket.getBasketTotal();

        assertThat(total.doubleValue()).isEqualTo(250);
    }

    @Test
    void basketWithMultipleItemsAndMatchingsDiscountsInCorrectTotalAndDiscounts() {
        var siblingDiscount = mock(Discount.class);
        when(siblingDiscount.apply(any())).thenReturn(BigDecimal.valueOf(10.00));
        when(siblingDiscount.getDiscountName()).thenReturn("Sibling Discount");
        var unusedDiscount = mock(Discount.class);
        when(unusedDiscount.apply(any())).thenReturn(BigDecimal.ZERO);
        when(unusedDiscount.getDiscountName()).thenReturn("Discount Not Matched");

        var basket = new RegistrationBasket(List.of(siblingDiscount, unusedDiscount));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior", 50));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior", 50));

        var discounts = basket.getDiscounts();
        var total = basket.getBasketTotal();

        assertThat(discounts)
                .containsEntry("Sibling Discount", BigDecimal.valueOf(10.00))
                .doesNotContainKey("Discount Not Matched");

        assertThat(total.doubleValue()).isEqualTo(190);
    }

    @Test
    void basketWithMultipleItemsAndNoMatchingsDiscountsInCorrectTotalAndEmptyDiscounts() {
        var siblingDiscount = mock(Discount.class);
        when(siblingDiscount.apply(any())).thenReturn(BigDecimal.ZERO);
        when(siblingDiscount.getDiscountName()).thenReturn("Sibling Discount");
        var unusedDiscount = mock(Discount.class);
        when(unusedDiscount.apply(any())).thenReturn(BigDecimal.ZERO);
        when(unusedDiscount.getDiscountName()).thenReturn("Discount Not Matched");

        var basket = new RegistrationBasket(List.of(siblingDiscount, unusedDiscount));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("adult", 100));
        basket.putSubscription(UUID.randomUUID(), createMemberSubscription("junior", 50));

        var discounts = basket.getDiscounts();
        var total = basket.getBasketTotal();

        assertThat(discounts).isEmpty();

        assertThat(total.doubleValue()).isEqualTo(250);
    }

    private MemberSubscription createMemberSubscription(String category, double price) {
        return createMemberSubscription(category, price, RegistrationAction.NEW);
    }

    private MemberSubscription createMemberSubscription(
            String category, double price, RegistrationAction action) {
        return MemberSubscription.builder()
                .category(category)
                .action(action)
                .price(BigDecimal.valueOf(price))
                .build();
    }
}
