package cricket.merstham.website.frontend.configuration;

import cricket.merstham.shared.types.AttributeType;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper
                .createTypeMap(
                        cricket.merstham.website.graph.type.AttributeType.class,
                        AttributeType.class)
                .setConverter(context -> AttributeType.valueOf(context.getSource().rawValue()));
        return modelMapper;
    }
}
