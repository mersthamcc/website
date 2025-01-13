package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.website.frontend.model.RegistrationBasket;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class CouponDiscount implements Discount, Serializable {
    @Serial private static final long serialVersionUID = 972421413123722530L;
    private static final String DISCOUNT_NAME = "discounts.coupons";

    @Override
    public String getDiscountName() {
        return DISCOUNT_NAME;
    }

    @Override
    public BigDecimal apply(RegistrationBasket basket) {
        return basket.getAppliedCoupons().stream()
                .map(c -> c.getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
