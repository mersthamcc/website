package cricket.merstham.website.frontend.helpers;

import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.service.GraphService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class GraphServiceMockHelper {

    public static <T extends Mutation, R extends Mutation.Data> void mockMutation(
            GraphService mock,
            ArgumentCaptor<T> mutation,
            OAuth2AccessToken token,
            Function<T, R>... data)
            throws IOException {
        when(mock.executeMutation(mutation.capture(), eq(token)))
                .thenAnswer(new SequencedAnswer<>(List.of(data)));
    }

    public static <T extends Query, R extends Query.Data> void mockQuery(
            GraphService mock,
            ArgumentCaptor<T> query,
            OAuth2AccessToken token,
            Function<T, R>... data)
            throws IOException {
        when(mock.executeQuery(query.capture(), eq(token)))
                .thenAnswer(new SequencedAnswer<>(List.of(data)));
    }

    public static <T extends Query, R extends Query.Data> void mockQuery(
            GraphService mock, ArgumentCaptor<T> query, Function<T, R>... data) throws IOException {
        when(mock.executeQuery(query.capture())).thenAnswer(new SequencedAnswer<>(List.of(data)));
    }

    private static class SequencedAnswer<T extends Operation, R extends Operation.Data>
            implements Answer<Response<Operation.Data>> {

        private static final Logger LOG = LogManager.getLogger(SequencedAnswer.class);
        private int answerIndex = -1;
        private final List<Function<T, R>> dataSuppliers;

        private SequencedAnswer(List<Function<T, R>> dataSuppliers) {
            this.dataSuppliers = dataSuppliers;
        }

        @Override
        public Response<Operation.Data> answer(InvocationOnMock invocation) throws Throwable {
            answerIndex++;
            T argument = invocation.getArgument(0);
            LOG.debug(
                    "Providing answer {} to call with argument of type {}",
                    answerIndex,
                    argument.getClass().getSimpleName());
            return Response.<T.Data>builder(argument)
                    .data(dataSuppliers.get(answerIndex).apply(invocation.getArgument(0)))
                    .build();
        }
    }
}
