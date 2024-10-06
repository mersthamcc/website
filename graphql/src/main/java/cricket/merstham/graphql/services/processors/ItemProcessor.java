package cricket.merstham.graphql.services.processors;

public interface ItemProcessor<R, E> {

    default void postOpen(R response, E entity) {}

    default void preSave(R request, E entity) {}

    default void postSave(R request, E entity) {}
}
