package cricket.merstham.website.frontend.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentServiceManager {
    private final Map<String, PaymentService> paymentServiceMap;

    @Autowired
    public PaymentServiceManager(List<PaymentService> paymentServices) {
        this.paymentServiceMap =
                paymentServices.stream()
                        .collect(Collectors.toMap(PaymentService::getName, Function.identity()));
    }

    public PaymentService getServiceByName(String name) {
        return paymentServiceMap.get(name);
    }

    public List<String> getAvailableServices() {
        return paymentServiceMap.keySet().stream().sorted().collect(Collectors.toList());
    }
}
