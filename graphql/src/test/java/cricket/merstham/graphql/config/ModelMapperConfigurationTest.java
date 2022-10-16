package cricket.merstham.graphql.config;

import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.shared.dto.News;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ModelMapperConfigurationTest {

    private static ModelMapperConfiguration modelMapperConfiguration =
            new ModelMapperConfiguration();
    private static ModelMapper modelMapper = modelMapperConfiguration.modelMapper();

    @Test
    void shouldMapNewsEntityToDto() {
        NewsEntity entity =
                NewsEntity.builder()
                        .id(1)
                        .path("/news/2022/01/01/this-is-a-test")
                        .draft(false)
                        .title("This is a test")
                        .uuid(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .publishDate(Instant.now().plus(1, HOURS))
                        .author("Test Author")
                        .body("This is the body")
                        .socialSummary("Social body")
                        .attributes(Map.of("facebook_id", "123456", "twitter_id", "654321"))
                        .build();

        News dto = News.builder().build();
        modelMapper.map(entity, dto);

        assertThat(dto.getId(), equalTo(entity.getId()));
        assertThat(dto.getTitle(), equalTo(entity.getTitle()));
        assertThat(dto.getPath(), equalTo(entity.getPath()));
        assertThat(dto.isDraft(), equalTo(entity.getDraft()));
        assertThat(dto.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(dto.getBody(), equalTo(entity.getBody()));
        assertThat(dto.getUuid(), equalTo(entity.getUuid()));
        assertThat(dto.getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(dto.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(dto.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(dto.getAttributes(), equalTo(entity.getAttributes()));
    }

    @Test
    void shouldMapNewsDtoToEntity() {
        News dto =
                News.builder()
                        .id(1)
                        .path("/news/2022/01/01/this-is-a-test")
                        .draft(false)
                        .title("This is a test")
                        .uuid(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .publishDate(Instant.now().plus(1, HOURS))
                        .author("Test Author")
                        .body("This is the body")
                        .socialSummary("Social body")
                        .attributes(Map.of("facebook_id", "123456", "twitter_id", "654321"))
                        .build();
        NewsEntity entity = NewsEntity.builder().build();

        modelMapper.map(dto, entity);

        assertThat(entity.getId(), equalTo(dto.getId()));
        assertThat(entity.getTitle(), equalTo(dto.getTitle()));
        assertThat(entity.getPath(), equalTo(dto.getPath()));
        assertThat(entity.getDraft(), equalTo(dto.isDraft()));
        assertThat(entity.getAuthor(), equalTo(dto.getAuthor()));
        assertThat(entity.getBody(), equalTo(dto.getBody()));
        assertThat(entity.getUuid(), equalTo(dto.getUuid()));
        assertThat(entity.getSocialSummary(), equalTo(dto.getSocialSummary()));
        assertThat(entity.getCreatedDate(), equalTo(dto.getCreatedDate()));
        assertThat(entity.getPublishDate(), equalTo(dto.getPublishDate()));
        assertThat(entity.getAttributes(), equalTo(dto.getAttributes()));
    }
}
