package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.configuration.ModelMapperConfiguration;
import cricket.merstham.website.graph.AdminNewsQuery;
import cricket.merstham.website.graph.NewsFeedQuery;
import cricket.merstham.website.graph.SaveNewsAttributesMutation;
import cricket.merstham.website.graph.SaveNewsMutation;
import cricket.merstham.website.graph.type.KeyValuePairInput;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class NewsServiceTest {

    private static final Lorem LOREM = LoremIpsum.getInstance();

    private final GraphService graphService = mock(GraphService.class);
    private final NewsService service =
            new NewsService(graphService, List.of(), new ModelMapperConfiguration().modelMapper());

    @Test
    void shouldReturnCorrectlyFormattedSspResponseForGetItems() throws IOException {
        var news =
                IntStream.range(0, 20)
                        .mapToObj(
                                i ->
                                        new AdminNewsQuery.New(
                                                "News",
                                                i,
                                                UUID.randomUUID().toString(),
                                                false,
                                                LOREM.getTitle(2, 8),
                                                LOREM.getNameFemale(),
                                                Instant.now(),
                                                Instant.now(),
                                                "/image.png",
                                                LOREM.getHtmlParagraphs(2, 5),
                                                LOREM.getWords(2, 8),
                                                "/news/test"))
                        .toList();

        var data = mock(AdminNewsQuery.Data.class);
        when(data.getNews()).thenReturn(news);
        when(data.getNewsTotals()).thenReturn(new AdminNewsQuery.NewsTotals("NewsTotals", 20, 30));

        var response = mock(Response.class);
        when(response.getData()).thenReturn(data);
        when(graphService.<AdminNewsQuery, AdminNewsQuery.Data>executeQuery(
                        any(AdminNewsQuery.class), any(OAuth2AccessToken.class)))
                .thenReturn(response);

        var accessToken = mock(OAuth2AccessToken.class);
        var result = service.getItems(accessToken, 1, 20, "search string");

        assertThat(result.getData().size(), equalTo(20));
        assertThat(result.getRecordsTotal(), equalTo(20));
        assertThat(result.getRecordsFiltered(), equalTo(30));

        for (int i = 0; i < news.size(); i++) {
            var item = result.getData().get(i).getData();
            assertThat(item.getTitle(), equalTo(news.get(i).getTitle()));
            assertThat(item.getPath(), equalTo(news.get(i).getPath()));
            assertThat(item.getBody(), equalTo(news.get(i).getBody()));
            assertThat(item.getAuthor(), equalTo(news.get(i).getAuthor()));
            assertThat(item.getUuid(), equalTo(news.get(i).getUuid()));
            assertThat(item.getId(), equalTo(news.get(i).getId()));
            assertThat(item.getPublishDate(), equalTo(news.get(i).getPublishDate()));
            assertThat(item.getCreatedDate(), equalTo(news.get(i).getCreatedDate()));
            assertThat(item.isDraft(), equalTo(news.get(i).getDraft()));
            assertThat(item.getSocialSummary(), equalTo(news.get(i).getSocialSummary()));
        }
    }

    @Test
    void shouldReturnCorrectlyFormattedFeed() throws IOException {
        var news =
                IntStream.range(0, 20)
                        .mapToObj(
                                i ->
                                        new NewsFeedQuery.Feed(
                                                "News",
                                                i,
                                                UUID.randomUUID().toString(),
                                                false,
                                                LOREM.getTitle(2, 8),
                                                LOREM.getNameFemale(),
                                                "/news/test",
                                                LOREM.getHtmlParagraphs(2, 5),
                                                Instant.now(),
                                                Instant.now(),
                                                "/image.png",
                                                List.of(
                                                        new NewsFeedQuery.Attribute(
                                                                "Attribute",
                                                                "ATTRIBUTE_ONE",
                                                                LOREM.getWords(1)),
                                                        new NewsFeedQuery.Attribute(
                                                                "Attribute",
                                                                "ATTRIBUTE_TWO",
                                                                LOREM.getWords(1)))))
                        .toList();

        var data = mock(NewsFeedQuery.Data.class);
        when(data.getFeed()).thenReturn(news);
        when(data.getNewsTotals()).thenReturn(new NewsFeedQuery.NewsTotals("NewsTotals", 20));

        var response = mock(Response.class);
        when(response.getData()).thenReturn(data);
        when(graphService.<NewsFeedQuery, NewsFeedQuery.Data>executeQuery(any(NewsFeedQuery.class)))
                .thenReturn(response);

        var result = service.feed(1);

        assertThat(result.getData().size(), equalTo(20));
        assertThat(result.getRecordsTotal(), equalTo(20));

        for (int i = 0; i < news.size(); i++) {
            var item = result.getData().get(i);
            assertThat(item.getTitle(), equalTo(news.get(i).getTitle()));
            assertThat(item.getPath(), equalTo(news.get(i).getPath()));
            assertThat(item.getBody(), equalTo(news.get(i).getBody()));
            assertThat(item.getAuthor(), equalTo(news.get(i).getAuthor()));
            assertThat(item.getUuid(), equalTo(news.get(i).getUuid()));
            assertThat(item.getId(), equalTo(news.get(i).getId()));
            assertThat(item.getPublishDate(), equalTo(news.get(i).getPublishDate()));
            assertThat(item.getCreatedDate(), equalTo(news.get(i).getCreatedDate()));
            assertThat(item.isDraft(), equalTo(news.get(i).getDraft()));
        }
    }

    @Test
    void shouldSuccessfullyMakeSaveNewsMutationRequest() throws IOException {
        var serviceSpy = spy(service);
        var requestCaptor = ArgumentCaptor.forClass(SaveNewsMutation.class);
        var accessToken = mock(OAuth2AccessToken.class);
        var news =
                News.builder()
                        .title(LOREM.getTitle(2, 10))
                        .body(LOREM.getHtmlParagraphs(2, 5))
                        .author(LOREM.getName())
                        .publishDate(Instant.now())
                        .createdDate(Instant.now())
                        .socialSummary(LOREM.getWords(2, 8))
                        .publishToFacebook(true)
                        .publishToTwitter(true)
                        .uuid(UUID.randomUUID().toString())
                        .path("/news/1")
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
        var newsCaptor = ArgumentCaptor.forClass(News.class);
        doReturn(news)
                .when(serviceSpy)
                .saveAttributes(any(OAuth2AccessToken.class), newsCaptor.capture());

        var data = mock(SaveNewsMutation.Data.class);
        when(data.getSaveNews())
                .thenReturn(
                        new SaveNewsMutation.SaveNews(
                                "News",
                                1,
                                news.getUuid(),
                                news.isDraft(),
                                news.getTitle(),
                                news.getBody(),
                                news.getAuthor(),
                                news.getPath(),
                                news.getCreatedDate(),
                                news.getPublishDate(),
                                news.getFeatureImageUrl(),
                                news.getSocialSummary(),
                                news.getAttributes().stream()
                                        .map(
                                                a ->
                                                        new SaveNewsMutation.Attribute(
                                                                "Attribute",
                                                                a.getKey(),
                                                                a.getValue()))
                                        .toList(),
                                news.isPublishToFacebook(),
                                news.isPublishToTwitter()));
        var response = mock(Response.class);
        when(response.getData()).thenReturn(data);

        when(graphService.executeMutation(requestCaptor.capture(), any(OAuth2AccessToken.class)))
                .thenReturn(response);

        var result = serviceSpy.saveNewsItem(accessToken, news);

        var input = requestCaptor.getValue().variables().news();
        assertThat(input.title(), equalTo(news.getTitle()));
        assertThat(input.uuid(), equalTo(news.getUuid()));
        assertThat(input.path(), equalTo(news.getPath()));
        assertThat(input.body(), equalTo(news.getBody()));
        assertThat(input.author(), equalTo(news.getAuthor()));
        assertThat(input.socialSummary(), equalTo(news.getSocialSummary()));
        assertThat(input.createdDate(), equalTo(news.getCreatedDate()));
        assertThat(input.publishDate(), equalTo(news.getPublishDate()));
        assertThat(input.draft(), equalTo(news.isDraft()));
        assertThat(input.attributes(), equalTo(null));

        assertThat(newsCaptor.getValue().getId(), equalTo(1));
        assertThat(
                newsCaptor.getValue().getAttributes(),
                equalTo(
                        List.of(
                                KeyValuePair.builder()
                                        .key("ATTRIBUTE_ONE")
                                        .value(news.getAttribute("ATTRIBUTE_ONE"))
                                        .build(),
                                KeyValuePair.builder()
                                        .key("ATTRIBUTE_TWO")
                                        .value(news.getAttribute("ATTRIBUTE_TWO"))
                                        .build())));

        assertThat(result.getId(), equalTo(1));
        assertThat(result.getTitle(), equalTo(news.getTitle()));
        assertThat(result.getUuid(), equalTo(news.getUuid()));
        assertThat(result.getPath(), equalTo(news.getPath()));
        assertThat(result.getBody(), equalTo(news.getBody()));
        assertThat(result.getAuthor(), equalTo(news.getAuthor()));
        assertThat(result.getSocialSummary(), equalTo(news.getSocialSummary()));
        assertThat(result.getCreatedDate(), equalTo(news.getCreatedDate()));
        assertThat(result.getPublishDate(), equalTo(news.getPublishDate()));
        assertThat(result.isDraft(), equalTo(news.isDraft()));
        assertThat(result.getAttributes(), equalTo(news.getAttributes()));
    }

    @Test
    void shouldSuccessfullySendSaveNewsAttributesRequest() throws IOException {
        var requestCaptor = ArgumentCaptor.forClass(SaveNewsAttributesMutation.class);
        var accessToken = mock(OAuth2AccessToken.class);
        var news =
                News.builder()
                        .id(10)
                        .title(LOREM.getTitle(2, 10))
                        .body(LOREM.getHtmlParagraphs(2, 5))
                        .author(LOREM.getName())
                        .publishDate(Instant.now())
                        .createdDate(Instant.now())
                        .socialSummary(LOREM.getWords(2, 8))
                        .publishToFacebook(true)
                        .publishToTwitter(true)
                        .uuid(UUID.randomUUID().toString())
                        .path("/news/1")
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

        var data = mock(SaveNewsAttributesMutation.Data.class);
        when(data.getSaveNewsAttributes())
                .thenReturn(
                        new SaveNewsAttributesMutation.SaveNewsAttributes(
                                "News",
                                1,
                                news.getUuid(),
                                news.isDraft(),
                                news.getTitle(),
                                news.getBody(),
                                news.getAuthor(),
                                news.getPath(),
                                news.getCreatedDate(),
                                news.getPublishDate(),
                                news.getSocialSummary(),
                                news.getFeatureImageUrl(),
                                news.getAttributes().stream()
                                        .map(
                                                a ->
                                                        new SaveNewsAttributesMutation.Attribute(
                                                                "Attribute",
                                                                a.getKey(),
                                                                a.getValue()))
                                        .toList(),
                                news.isPublishToFacebook(),
                                news.isPublishToTwitter()));
        var response = mock(Response.class);
        when(response.getData()).thenReturn(data);

        when(graphService.executeMutation(requestCaptor.capture(), any(OAuth2AccessToken.class)))
                .thenReturn(response);

        var result = service.saveAttributes(accessToken, news);

        var input = requestCaptor.getValue().variables();

        assertThat(input.id(), equalTo(10));
        assertThat(
                input.attributes(),
                equalTo(
                        List.of(
                                KeyValuePairInput.builder()
                                        .key("ATTRIBUTE_ONE")
                                        .value(news.getAttribute("ATTRIBUTE_ONE"))
                                        .build(),
                                KeyValuePairInput.builder()
                                        .key("ATTRIBUTE_TWO")
                                        .value(news.getAttribute("ATTRIBUTE_TWO"))
                                        .build())));

        assertThat(result.getId(), equalTo(1));
        assertThat(result.getTitle(), equalTo(news.getTitle()));
        assertThat(result.getUuid(), equalTo(news.getUuid()));
        assertThat(result.getPath(), equalTo(news.getPath()));
        assertThat(result.getBody(), equalTo(news.getBody()));
        assertThat(result.getAuthor(), equalTo(news.getAuthor()));
        assertThat(result.getSocialSummary(), equalTo(news.getSocialSummary()));
        assertThat(result.getCreatedDate(), equalTo(news.getCreatedDate()));
        assertThat(result.getPublishDate(), equalTo(news.getPublishDate()));
        assertThat(result.isDraft(), equalTo(news.isDraft()));
        assertThat(result.getAttributes(), equalTo(news.getAttributes()));
    }

    @Test
    void get() {}

    @Test
    void testGet() {}

    @Test
    void testGet1() {}

    @Test
    void delete() {}
}
