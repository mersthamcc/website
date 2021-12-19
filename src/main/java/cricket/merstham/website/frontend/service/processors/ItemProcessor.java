package cricket.merstham.website.frontend.service.processors;

public interface ItemProcessor<T> {

    default void postOpen(T item) {}

    default void preSave(T item) {}

    default void postSave(T item) {}
}
