package cricket.merstham.website.frontend.model.discounts;

import cricket.merstham.website.frontend.model.RegistrationBasket;

import java.math.BigDecimal;

public interface Discount {
    String getDiscountName();

    BigDecimal apply(RegistrationBasket basket);
}
