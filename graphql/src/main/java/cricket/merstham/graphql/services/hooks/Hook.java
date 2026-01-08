package cricket.merstham.graphql.services.hooks;

import cricket.merstham.shared.dto.User;

public interface Hook<T> {
    void onConfirm(T data, String paymentType, User user);
}
