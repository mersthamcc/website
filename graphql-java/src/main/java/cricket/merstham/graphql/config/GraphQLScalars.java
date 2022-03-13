package cricket.merstham.graphql.config;

import cricket.merstham.graphql.scalars.DateCoercing;
import cricket.merstham.graphql.scalars.DateTimeCoercing;
import cricket.merstham.graphql.scalars.JsonCoercing;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLScalars implements RuntimeWiringConfigurer {

    @Override
    public void configure(RuntimeWiring.Builder builder) {
        builder.scalar(dateTimeScalar());
        builder.scalar(dateScalar());
        builder.scalar(jsonScalar());
    }

    private GraphQLScalarType jsonScalar() {
        return GraphQLScalarType.newScalar()
                .name("Json")
                .description("JsonNode scalar.")
                .coercing(new JsonCoercing())
                .build();
    }

    private GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("Java Instant as scalar.")
                .coercing(new DateTimeCoercing())
                .build();
    }

    private GraphQLScalarType dateScalar() {
        return GraphQLScalarType.newScalar()
                .name("Date")
                .description("Java Instant as date only scalar.")
                .coercing(new DateCoercing())
                .build();
    }
}
