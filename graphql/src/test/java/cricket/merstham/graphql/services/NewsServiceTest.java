package cricket.merstham.graphql.services;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import cricket.merstham.graphql.config.ModelMapperConfiguration;
import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.helpers.RandomDateTimes;
import cricket.merstham.graphql.repository.NewsEntityRepository;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.extensions.StringExtensions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NewsServiceTest {

    private static final int NEWS_STORY_COUNT = 55;
    private static final Lorem LOREM = LoremIpsum.getInstance();
    private static final List<NewsEntity> NEWS_ENTITIES =
            IntStream.range(0, NEWS_STORY_COUNT)
                    .mapToObj(
                            i -> {
                                var created = RandomDateTimes.between(Instant.EPOCH, Instant.now());
                                var title = LOREM.getTitle(2, 10);
                                return NewsEntity.builder()
                                        .title(title)
                                        .id(i)
                                        .body(LOREM.getHtmlParagraphs(2, 8))
                                        .author(LOREM.getNameMale())
                                        .uuid(UUID.randomUUID().toString())
                                        .socialSummary(LOREM.getWords(2, 15))
                                        .path(
                                                format(
                                                        "/news/{0}/{1}/{2}/{3}",
                                                        created.atZone(UTC).getYear(),
                                                        created.atZone(UTC).format(ofPattern("MM")),
                                                        created.atZone(UTC).format(ofPattern("dd")),
                                                        StringExtensions.toSlug(title)))
                                        .createdDate(created)
                                        .publishDate(created)
                                        .publishDate(
                                                RandomDateTimes.between(
                                                        Instant.EPOCH, Instant.now()))
                                        .draft(false)
                                        .attributes(
                                                new HashMap<>(
                                                        Map.of(
                                                                "ATTRIBUTE_ONE", LOREM.getWords(1),
                                                                "ATTRIBUTE_TWO",
                                                                        LOREM.getWords(1))))
                                        .build();
                            })
                    .sorted(Comparator.comparing(NewsEntity::getCreatedDate))
                    .collect(Collectors.toList());
    private NewsEntityRepository repository = mock(NewsEntityRepository.class);

    private NewsService service =
            new NewsService(repository, new ModelMapperConfiguration().modelMapper());

    public static Stream<Integer> validIds() {
        return IntStream.range(0, NEWS_STORY_COUNT).boxed();
    }

    public static Stream<String> validPaths() {
        return NEWS_ENTITIES.stream().map(n -> n.getPath());
    }

    @BeforeEach
    void setup() {
        when(repository.saveAndFlush(any(NewsEntity.class))).then(returnsFirstArg());

        for (int i = 0; i < Math.ceil(NEWS_STORY_COUNT / 10.0); i++) {
            when(repository.findAll(
                            eq(PageRequest.of(i + 1, 10, Sort.by("publishDate").descending()))))
                    .thenReturn(
                            new PageImpl<>(
                                    NEWS_ENTITIES.stream()
                                            .skip(i * 10)
                                            .limit(10)
                                            .collect(Collectors.toList())));
        }
        when(repository.count()).thenReturn((long) NEWS_STORY_COUNT);
        for (int i = 0; i < NEWS_STORY_COUNT; i++) {
            when(repository.findById(i)).thenReturn(Optional.of(NEWS_ENTITIES.get(i)));
        }

        NEWS_ENTITIES.forEach(
                n -> {
                    var example = Example.of(NewsEntity.builder().path(n.getPath()).build());
                    when(repository.findOne(example)).thenReturn(Optional.of(n));
                });

        when(repository.adminSearch(eq(1), eq(25), eq("test search")))
                .thenReturn(NEWS_ENTITIES.subList(0, 25));
        when(repository.adminSearch(eq(1), eq(25), eq("bad search"))).thenReturn(List.of());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void shouldReturnRequestedPageCorrectly(int page) {
        var pageRequest = ArgumentCaptor.forClass(PageRequest.class);

        var result = service.getNewsFeed(page);

        verify(repository).findAll(pageRequest.capture());

        assertThat(pageRequest.getValue().getPageNumber(), equalTo(page));
        assertThat(pageRequest.getValue().getPageSize(), equalTo(10));

        assertThat(result.size(), equalTo(10));

        var entity = NEWS_ENTITIES.get((page - 1) * 10);
        assertThat(result.get(0).getTitle(), equalTo(entity.getTitle()));
        assertThat(result.get(0).getBody(), equalTo(entity.getBody()));
        assertThat(result.get(0).getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.get(0).getUuid(), equalTo(entity.getUuid()));
        assertThat(result.get(0).getPath(), equalTo(entity.getPath()));
        assertThat(result.get(0).getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.get(0).getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.get(0).getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.get(0).isDraft(), equalTo(entity.getDraft()));
        assertThat(result.get(0).getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.get(0).getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldReturnLastPageCorrectly() {
        var pageRequest = ArgumentCaptor.forClass(PageRequest.class);

        var result = service.getNewsFeed(6);

        verify(repository).findAll(pageRequest.capture());

        assertThat(pageRequest.getValue().getPageNumber(), equalTo(6));
        assertThat(pageRequest.getValue().getPageSize(), equalTo(10));

        assertThat(result.size(), equalTo(5));

        var entity = NEWS_ENTITIES.get(NEWS_STORY_COUNT - 1);
        assertThat(result.get(4).getTitle(), equalTo(entity.getTitle()));
        assertThat(result.get(4).getBody(), equalTo(entity.getBody()));
        assertThat(result.get(4).getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.get(4).getUuid(), equalTo(entity.getUuid()));
        assertThat(result.get(4).getPath(), equalTo(entity.getPath()));
        assertThat(result.get(4).getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.get(4).getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.get(4).getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.get(4).isDraft(), equalTo(entity.getDraft()));
        assertThat(result.get(4).getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.get(4).getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldReturnCorrectTotals() {
        var result = service.getNewsFeedTotals();

        assertThat(result.getTotalRecords(), equalTo((long) NEWS_STORY_COUNT));
        assertThat(result.getTotalMatching(), equalTo((long) NEWS_STORY_COUNT));
    }

    @ParameterizedTest
    @MethodSource("validIds")
    void shouldReturnCorrectItemUsingId(int id) {
        var result = service.getNewsItemById(id);

        var entity = NEWS_ENTITIES.get(id);
        assertThat(result.getTitle(), equalTo(entity.getTitle()));
        assertThat(result.getBody(), equalTo(entity.getBody()));
        assertThat(result.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.getUuid(), equalTo(entity.getUuid()));
        assertThat(result.getPath(), equalTo(entity.getPath()));
        assertThat(result.getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(entity.getDraft()));
        assertThat(result.getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldThrowForInvalidId() {
        assertThrows(
                NoSuchElementException.class, () -> service.getNewsItemById(NEWS_STORY_COUNT + 1));
    }

    @ParameterizedTest
    @MethodSource("validPaths")
    void shouldReturnCorrectItemUsingId(String path) {
        var result = service.getNewsItemByPath(path);

        var entity = NEWS_ENTITIES.stream().filter(n -> n.getPath().equals(path)).findFirst().get();
        assertThat(result.getTitle(), equalTo(entity.getTitle()));
        assertThat(result.getBody(), equalTo(entity.getBody()));
        assertThat(result.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.getUuid(), equalTo(entity.getUuid()));
        assertThat(result.getPath(), equalTo(entity.getPath()));
        assertThat(result.getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(entity.getDraft()));
        assertThat(result.getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldThrowForInvalidPath() {
        assertThrows(
                NoSuchElementException.class, () -> service.getNewsItemByPath("/invalid-path"));
    }

    @Test
    void shouldReturnCorrectValuesForAdminListWhemMatches() {
        var result = service.getAdminNewsList(1, 25, "test search");

        assertThat(result.size(), equalTo(25));

        for (int i = 0; i < result.size(); i++) {
            var entity = NEWS_ENTITIES.get(i);
            assertThat(result.get(i).getTitle(), equalTo(entity.getTitle()));
            assertThat(result.get(i).getBody(), equalTo(entity.getBody()));
            assertThat(result.get(i).getAuthor(), equalTo(entity.getAuthor()));
            assertThat(result.get(i).getUuid(), equalTo(entity.getUuid()));
            assertThat(result.get(i).getPath(), equalTo(entity.getPath()));
            assertThat(result.get(i).getSocialSummary(), equalTo(entity.getSocialSummary()));
            assertThat(result.get(i).getPublishDate(), equalTo(entity.getPublishDate()));
            assertThat(result.get(i).getCreatedDate(), equalTo(entity.getCreatedDate()));
            assertThat(result.get(i).isDraft(), equalTo(entity.getDraft()));
            assertThat(result.get(i).getAttributeMap(), equalTo(entity.getAttributes()));
            assertThat(result.get(i).getId(), equalTo(entity.getId()));
        }
    }

    @Test
    void shouldReturnEmptyListForAdminListWhenNoMatch() {
        var result = service.getAdminNewsList(1, 25, "bad search");

        assertThat(result.size(), equalTo(0));
    }

    @Test
    void shouldCorrectlyUpdateExistingRecord() {
        var news =
                News.builder()
                        .id(1)
                        .title("a new title")
                        .body("a new body")
                        .socialSummary("a social summary")
                        .build();

        var result = service.save(news);

        var entity = NEWS_ENTITIES.get(1);
        assertThat(result.getTitle(), equalTo("a new title"));
        assertThat(result.getBody(), equalTo("a new body"));
        assertThat(result.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.getUuid(), equalTo(entity.getUuid()));
        assertThat(result.getPath(), equalTo(entity.getPath()));
        assertThat(result.getSocialSummary(), equalTo("a social summary"));
        assertThat(result.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(entity.getDraft()));
        assertThat(result.getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldCorrectlyCreateNewRecord() {
        var created = RandomDateTimes.between(Instant.EPOCH, Instant.now());
        var title = LOREM.getTitle(2, 10);

        var news =
                News.builder()
                        .title(title)
                        .id(-1)
                        .body(LOREM.getHtmlParagraphs(2, 8))
                        .author(LOREM.getNameMale())
                        .uuid(UUID.randomUUID().toString())
                        .socialSummary(LOREM.getWords(2, 15))
                        .path(
                                format(
                                        "/news/{0}/{1}/{2}/{3}",
                                        created.atZone(UTC).format(ofPattern("yyyy")),
                                        created.atZone(UTC).format(ofPattern("MM")),
                                        created.atZone(UTC).format(ofPattern("dd")),
                                        StringExtensions.toSlug(title)))
                        .createdDate(created)
                        .publishDate(created)
                        .publishDate(RandomDateTimes.between(Instant.EPOCH, Instant.now()))
                        .draft(false)
                        .attributes(
                                List.of(
                                        KeyValuePair.builder()
                                                .key("ATTRIBUTE_ONE")
                                                .value(LOREM.getWords(1))
                                                .build(),
                                        KeyValuePair.builder()
                                                .key("ATTRIBUTE_TWO")
                                                .value(LOREM.getWords(1))
                                                .build()))
                        .build();

        var result = service.save(news);

        assertThat(result.getTitle(), equalTo(title));
        assertThat(result.getBody(), equalTo(news.getBody()));
        assertThat(result.getAuthor(), equalTo(news.getAuthor()));
        assertThat(result.getUuid(), equalTo(news.getUuid()));
        assertThat(result.getPath(), equalTo(news.getPath()));
        assertThat(result.getSocialSummary(), equalTo(news.getSocialSummary()));
        assertThat(result.getPublishDate(), equalTo(news.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(news.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(news.isDraft()));
        assertThat(result.getAttributes(), equalTo(news.getAttributes()));
        assertThat(result.getId(), equalTo(news.getId()));
    }

    @Test
    void shouldCorrectlyUpdateNewsAttributes() {
        var originalAttributeOne = NEWS_ENTITIES.get(0).getAttributes().get("ATTRIBUTE_ONE");

        var result =
                service.saveAttributes(
                        0,
                        List.of(
                                KeyValuePair.builder()
                                        .key("ATTRIBUTE_TWO")
                                        .value("a new value")
                                        .build(),
                                KeyValuePair.builder()
                                        .key("ATTRIBUTE_THREE")
                                        .value("a new entry")
                                        .build()));

        var entity = NEWS_ENTITIES.get(0);
        assertThat(result.getTitle(), equalTo(entity.getTitle()));
        assertThat(result.getBody(), equalTo(entity.getBody()));
        assertThat(result.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.getUuid(), equalTo(entity.getUuid()));
        assertThat(result.getPath(), equalTo(entity.getPath()));
        assertThat(result.getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(entity.getDraft()));
        assertThat(
                result.getAttributeMap(),
                equalTo(
                        Map.of(
                                "ATTRIBUTE_ONE", originalAttributeOne,
                                "ATTRIBUTE_TWO", "a new value",
                                "ATTRIBUTE_THREE", "a new entry")));
        assertThat(result.getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldThrowExceptionOnUpdateNewsAttributesWhenBadIdSpecified() {
        assertThrows(
                NoSuchElementException.class,
                () ->
                        service.saveAttributes(
                                NEWS_STORY_COUNT,
                                List.of(
                                        KeyValuePair.builder()
                                                .key("ATTRIBUTE_TWO")
                                                .value("a new value")
                                                .build(),
                                        KeyValuePair.builder()
                                                .key("ATTRIBUTE_THREE")
                                                .value("a new entry")
                                                .build())));
    }

    @Test
    void shouldReturnDeletedEntityOnDelete() {
        var result = service.delete(10);

        var entity = NEWS_ENTITIES.get(10);
        assertThat(result.getTitle(), equalTo(entity.getTitle()));
        assertThat(result.getBody(), equalTo(entity.getBody()));
        assertThat(result.getAuthor(), equalTo(entity.getAuthor()));
        assertThat(result.getUuid(), equalTo(entity.getUuid()));
        assertThat(result.getPath(), equalTo(entity.getPath()));
        assertThat(result.getSocialSummary(), equalTo(entity.getSocialSummary()));
        assertThat(result.getPublishDate(), equalTo(entity.getPublishDate()));
        assertThat(result.getCreatedDate(), equalTo(entity.getCreatedDate()));
        assertThat(result.isDraft(), equalTo(entity.getDraft()));
        assertThat(result.getAttributeMap(), equalTo(entity.getAttributes()));
        assertThat(result.getId(), equalTo(entity.getId()));
    }

    @Test
    void shouldThrowExceptionOnDeleteWhenBadIdSpecified() {
        assertThrows(NoSuchElementException.class, () -> service.delete(NEWS_STORY_COUNT));
    }
}
