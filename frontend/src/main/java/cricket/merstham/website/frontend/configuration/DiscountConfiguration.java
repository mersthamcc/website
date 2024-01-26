package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.model.discounts.Discount;
import cricket.merstham.website.frontend.model.discounts.SiblingDiscount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DiscountConfiguration {

    private List<Discount> activeDiscounts =
            List.of(new SiblingDiscount("junior", BigDecimal.valueOf(10.00)));

    @Bean
    public List<Discount> getActiveDiscounts() {
        return activeDiscounts;
    }
}
