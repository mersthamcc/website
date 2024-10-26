package cricket.merstham.graphql.controllers;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import io.sentry.Sentry;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NoSuchElementException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message("Record not found")
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else if (ex instanceof AccessDeniedException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.UNAUTHORIZED)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        } else {
            Sentry.captureException(ex);
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }
    }
}
