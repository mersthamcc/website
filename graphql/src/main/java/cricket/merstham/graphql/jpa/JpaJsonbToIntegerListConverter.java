package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JpaJsonbToIntegerListConverter extends JpaJsonbToListConverter<Integer> {

    public JpaJsonbToIntegerListConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
