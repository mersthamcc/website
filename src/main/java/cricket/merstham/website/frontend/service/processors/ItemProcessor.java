package cricket.merstham.website.frontend.service.processors;

import java.util.List;

public interface ItemProcessor<T> {

    default void postOpen(T item) {}

    default List<String> preSave(T item) { return List.of(); }

    default void postSave(T item) {}
}
