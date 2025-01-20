package cricket.merstham.graphql.services.hooks;

public interface Hook<T> {
    void onConfirm(T data);
}
