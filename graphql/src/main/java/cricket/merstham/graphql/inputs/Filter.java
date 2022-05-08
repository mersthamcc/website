package cricket.merstham.graphql.inputs;

public interface Filter<T> {
    boolean matches(T value);
}
