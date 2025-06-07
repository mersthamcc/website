package cricket.merstham.graphql.configuration.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import cricket.merstham.graphql.configuration.ApiKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationPropertiesBinding
@Component
public class ApiKeysConvertor implements Converter<String, List<ApiKey>> {

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @Override
    public List<ApiKey> convert(@NotNull String source) {
        try {
            return JSON_MAPPER.readValue(source, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new ConversionFailedException(
                    TypeDescriptor.valueOf(String.class),
                    TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(ApiKey.class)),
                    source,
                    e);
        }
    }
}
