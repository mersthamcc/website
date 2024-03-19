package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JpaJsonbToStringListConverter extends JpaJsonbToListConverter<String> {

    public JpaJsonbToStringListConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
