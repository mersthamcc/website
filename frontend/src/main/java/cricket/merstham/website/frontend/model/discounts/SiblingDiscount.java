package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.shared.dto.RegistrationAction;
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
    private final int registrationYear;

    public SiblingDiscount(String categoryName, BigDecimal discountAmount, int registrationYear) {
        this.categoryName = categoryName;
        this.discountAmount = discountAmount;
        this.registrationYear = registrationYear;
    }

    @Override
    public String getDiscountName() {
        return DISCOUNT_NAME;
    }

    @Override
    public BigDecimal apply(RegistrationBasket basket) {
        var matchingItems =
                basket.getChargeableSubscriptions().stream()
                        .filter(s -> Objects.equals(s.getCategory(), categoryName))
                        .count();
        var alreadyRegisteredItems =
                basket.getSubscriptions().values().stream()
                        .filter(
                                s ->
                                        s.getYear() == registrationYear
                                                && s.getCategory().equals(categoryName)
                                                && s.getAction().equals(RegistrationAction.NONE))
                        .count();
        long discountFactor = matchingItems;
        if (alreadyRegisteredItems == 0) {
            discountFactor = discountFactor - 1;
        }
        return matchingItems == 0 && alreadyRegisteredItems == 0
                ? BigDecimal.ZERO
                : discountAmount.multiply(BigDecimal.valueOf(discountFactor));
    }
}
