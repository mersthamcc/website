package cricket.merstham.graphql.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.entity.PricelistItemInfoEntity;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.PricelistItemInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.jackson.JsonNodeValueReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                                var iterator = context.getSource().elements();
                                while (iterator.hasNext()) {
                                    result.add(iterator.next().asText());
                                }
                                return result;
                            }
                            return null;
                        });
        modelMapper
                .createTypeMap(List.class, Map.class)
                .setConverter(
                        context -> {
                            if (nonNull(context.getSource())) {
                                List<KeyValuePair> source = context.getSource();

                                return source.stream()
                                        .collect(
                                                Collectors.toMap(
                                                        KeyValuePair::getKey,
                                                        KeyValuePair::getValue));
                            }
                            return context.getDestination();
                        });
        modelMapper
                .createTypeMap(Map.class, List.class)
                .setConverter(
                        context -> {
                            if (nonNull(context.getSource())) {
                                Map<String, String> source = context.getSource();

                                return source.entrySet().stream()
                                        .map(
                                                a ->
                                                        KeyValuePair.builder()
                                                                .key(a.getKey())
                                                                .value(a.getValue())
                                                                .build())
                                        .collect(Collectors.toCollection(LinkedList::new));
                            }
                            return List.of();
                        });
        modelMapper
                .createTypeMap(PricelistItemInfoEntity.class, PricelistItemInfo.class)
                .setConverter(
                        context -> {
                            if (nonNull(context.getSource())) {
                                PricelistItemInfoEntity source = context.getSource();
                                return PricelistItemInfo.builder()
                                        .pricelistItemId(source.getId().getPricelistItemId())
                                        .key(source.getId().getKey())
                                        .icon(source.getIcon())
                                        .description(source.getDescription())
                                        .build();
                            }
                            return null;
                        });
        return modelMapper;
    }
}
