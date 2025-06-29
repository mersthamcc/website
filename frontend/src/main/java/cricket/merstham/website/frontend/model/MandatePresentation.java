package cricket.merstham.website.frontend.model;

import com.gocardless.resources.CustomerBankAccount;
import com.gocardless.resources.Mandate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MandatePresentation {
    private Mandate mandate;
    private CustomerBankAccount customerBankAccount;
}
