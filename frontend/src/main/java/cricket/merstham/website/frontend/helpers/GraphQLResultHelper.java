package cricket.merstham.website.frontend.helpers;

import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

public class GraphQLResultHelper {

    private GraphQLResultHelper() {}

    public static <T, R> R requireGraphData(Response<T> result, Function<T, R> function) {
        return requireGraphData(result, function, () -> "Error getting data");
    }

    public static <T, R> R requireGraphData(
            Response<T> result, Function<T, R> function, Supplier<String> errorMessage) {
        if (result.hasErrors()) {
            throw new GraphException(errorMessage.get(), result.getErrors());
        }
        var data = function.apply(result.getData());
        if (isNull(data)) {
            throw new ResourceNotFoundException(errorMessage.get());
        }
        return data;
    }
}
