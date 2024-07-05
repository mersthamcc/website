package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.UserPaymentMethodEntity;
import cricket.merstham.graphql.repository.UserPaymentMethodRepository;
import cricket.merstham.shared.dto.UserPaymentMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final UserPaymentMethodRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentMethodService(UserPaymentMethodRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("isAuthenticated()")
    public List<UserPaymentMethod> getPaymentMethods(String userId) {
        return repository.findAllByUserId(userId).stream()
                .map(p -> modelMapper.map(p, UserPaymentMethod.class))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public UserPaymentMethod savePaymentMethod(UserPaymentMethod paymentMethod) {
        var entity = UserPaymentMethodEntity.builder().build();
        modelMapper.map(entity, paymentMethod);

        return modelMapper.map(repository.saveAndFlush(entity), UserPaymentMethod.class);
    }
}
