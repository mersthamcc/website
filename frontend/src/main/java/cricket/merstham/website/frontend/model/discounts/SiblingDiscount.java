package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.website.frontend.model.RegistrationBasket;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class SiblingDiscount implements Discount, Serializable {
    @Serial private static final long serialVersionUID = 20240126192000L;

    private static final String DISCOUNT_NAME = "discounts.sibling";
    private final String categoryName;
    private final BigDecimal discountAmount;

    public SiblingDiscount(String categoryName, BigDecimal discountAmount) {
        this.categoryName = categoryName;
        this.discountAmount = discountAmount;
    }

    @Override
    public String getDiscountName() {
        return DISCOUNT_NAME;
    }

    @Override
    public BigDecimal apply(RegistrationBasket basket) {
        var matchingItems =
                basket.getSubscriptions().values().stream()
                        .filter(s -> Objects.equals(s.getCategory(), categoryName))
                        .count();

        return matchingItems == 0
                ? BigDecimal.ZERO
                : discountAmount.multiply(BigDecimal.valueOf(matchingItems - 1));
    }
}
