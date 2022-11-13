package cricket.merstham.website.frontend.configuration;

import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.types.AttributeType;
import org.junit.jupiter.api.Test;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelMapperConfigurationTest {

    private static final ModelMapperConfiguration modelMapperConfiguration =
            new ModelMapperConfiguration();
    private static final ModelMapper modelMapper = modelMapperConfiguration.modelMapper();

    @Test
    void testNewsDtoToGraphQlMapping() {
        var now = Instant.now();
        News news =
                News.builder()
                        .id(1)
                        .draft(false)
                        .author("Test Author")
                        .body("Test Body")
                        .title("Test Title")
                        .path("/news/test-title")
                        .publishDate(now)
                        .createdDate(now)
                        .uuid(UUID.randomUUID().toString())
                        .attributes(List.of())
                        .socialSummary("A summary")
                        .build();

        News mapped = new News();

        modelMapper.map(news, mapped);

        assertThat(mapped.getId(), equalTo(news.getId()));
        assertThat(mapped.getTitle(), equalTo(news.getTitle()));
        assertThat(mapped.getBody(), equalTo(news.getBody()));
        assertThat(mapped.getAuthor(), equalTo(news.getAuthor()));
        assertThat(mapped.getUuid(), equalTo(news.getUuid()));
        assertThat(mapped.getCreatedDate(), equalTo(news.getCreatedDate()));
        assertThat(mapped.getPublishDate(), equalTo(news.getPublishDate()));
        // assertThat(mapped.getAttributes(), equalTo(news.getAttributes()));
        // assertThat(mapped.getPath(), equalTo(news.getPath()));
        assertThat(mapped.getSocialSummary(), equalTo(news.getSocialSummary()));
        assertThat(mapped.isDraft(), equalTo(news.isDraft()));
    }

    @Test
    void shouldCorrectlyMapAttributeTypeEnum() {
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.DATE,
                        AttributeType.class),
                equalTo(AttributeType.Date));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.STRING,
                        AttributeType.class),
                equalTo(AttributeType.String));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.LIST,
                        AttributeType.class),
                equalTo(AttributeType.List));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.EMAIL,
                        AttributeType.class),
                equalTo(AttributeType.Email));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.BOOLEAN,
                        AttributeType.class),
                equalTo(AttributeType.Boolean));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.NUMBER,
                        AttributeType.class),
                equalTo(AttributeType.Number));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.OPTION,
                        AttributeType.class),
                equalTo(AttributeType.Option));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.TIME,
                        AttributeType.class),
                equalTo(AttributeType.Time));
        assertThat(
                modelMapper.map(
                        cricket.merstham.website.graph.type.AttributeType.TIMESTAMP,
                        AttributeType.class),
                equalTo(AttributeType.Timestamp));
        assertThrows(
                MappingException.class,
                () ->
                        modelMapper.map(
                                cricket.merstham.website.graph.type.AttributeType.$UNKNOWN,
                                AttributeType.class));
    }
}
