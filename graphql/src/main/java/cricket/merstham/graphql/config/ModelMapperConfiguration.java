package cricket.merstham.graphql.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JsonNodeValueReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setSkipNullEnabled(true)
                .addValueReader(new JsonNodeValueReader());
        modelMapper
                .createTypeMap(JsonNode.class, List.class)
                .setConverter(
                        context -> {
                            if (nonNull(context.getSource()) && context.getSource().isArray()) {
                                var result = new ArrayList<String>();
                                var iterator = ((ArrayNode) context.getSource()).elements();
                                while (iterator.hasNext()) {
                                    result.add(iterator.next().asText());
                                }
                                return result;
                            }
                            return null;
                        });
        return modelMapper;
    }
}
