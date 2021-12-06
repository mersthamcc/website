package cricket.merstham.website.frontend.service.processors;

public interface ItemProcessor<T> {
    default void preSave(T item) {};
    default void postProcessing(T item) {};
}
