package cricket.merstham.website.frontend.menu;

import cricket.merstham.website.frontend.configuration.ViewConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MenuTest {

    private static final List<Menu> menuTree =
            List.of(
                    new Menu("test-item-1", null, null, null, null),
                    new Menu(
                            "test-item-2",
                            new LinkedHashMap<>(),
                            null,
                            null,
                            () ->
                                    List.of(
                                            new Menu("sub-item-1", null, null, null, null),
                                            new Menu("sub-item-2", null, null, null, null))));

    @Test
    void getArgumentValuesWhenNoPathVariables() {
        Menu menuItem = new Menu("test-item", null, null, null, null);

        assertThat(menuItem.getArgumentValues()).isEmpty();
    }

    @Test
    void getArgumentValuesWithOnePathVariable() {
        var params = new LinkedHashMap<String, String>();
        params.put("id", "1");
        Menu menuItem = new Menu("test-item", params, null, null, null);

        assertThat(menuItem.getArgumentValues()).isEqualTo(new String[] {"1"});
    }

    @Test
    void getArgumentValuesWithMultiplePathVariables() {
        var params = new LinkedHashMap<String, String>();
        params.put("id", "1");
        params.put("foo", "bar");
        Menu menuItem = new Menu("test-item", params, null, null, null);

        assertThat(menuItem.getArgumentValues()).isEqualTo(new String[] {"1", "bar"});
    }

    @Test
    void onActivePathSucceedsWhenTopLevelItemIsCurrent() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("test-item-1");

        assertThat(menuTree.get(0).onActivePath(currentRoute)).isTrue();
        assertThat(menuTree.get(1).onActivePath(currentRoute)).isFalse();
    }

    @Test
    void onActivePathSucceedsWhenSubLevelItemIsCurrent() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("sub-item-1");

        assertThat(menuTree.get(0).onActivePath(currentRoute)).isFalse();
        assertThat(menuTree.get(1).getChildren().get(0).onActivePath(currentRoute)).isTrue();
        assertThat(menuTree.get(1).getChildren().get(1).onActivePath(currentRoute)).isFalse();
        assertThat(menuTree.get(1).onActivePath(currentRoute)).isTrue();
    }

    @Test
    void getBreadcrumbsReturnsCorrectPathToTopLevelEntry() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("test-item-1");

        assertThat(menuTree.get(0).getBreadcrumbs(currentRoute).size()).isEqualTo(1);
        assertThat(menuTree.get(0).getBreadcrumbs(currentRoute)).contains(menuTree.get(0));
    }

    @Test
    void getBreadcrumbsReturnsNoBreadcrumbsIfCurrentRouteInvalid() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("invalid-route");

        assertThat(menuTree.get(0).getBreadcrumbs(currentRoute).size()).isZero();
    }

    @Test
    void getBreadcrumbsReturnsCorrectPathToSubLevelEntry() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("sub-item-1");

        assertThat(menuTree.get(1).getBreadcrumbs(currentRoute).size()).isEqualTo(2);
        assertThat(
                        menuTree.get(1).getBreadcrumbs(currentRoute).stream()
                                .map(b -> b.getName())
                                .toList())
                .containsExactlyInAnyOrder("test-item-2", "sub-item-1");
    }

    @Test
    void getBreadcrumbsReturnsCorrectPathToSecondarySubLevelEntry() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("sub-item-2");

        assertThat(menuTree.get(1).getBreadcrumbs(currentRoute).size()).isEqualTo(2);
        assertThat(
                        menuTree.get(1).getBreadcrumbs(currentRoute).stream()
                                .map(b -> b.getName())
                                .toList())
                .containsExactlyInAnyOrder("test-item-2", "sub-item-2");
    }

    @Test
    void isActiveNodeReturnTrueForValidNodeWithNoParameters() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("test-item");

        Menu menuItem = new Menu("test-item", null, null, null, null);

        assertThat(menuItem.isActiveNode(currentRoute)).isTrue();
    }

    @Test
    void isActiveNodeReturnFalseForInvalidNodeWithNoParameters() {
        ViewConfiguration.CurrentRoute currentRoute = createMockCurrentRoute("invalid-item");

        Menu menuItem = new Menu("test-item", null, null, null, null);

        assertThat(menuItem.isActiveNode(currentRoute)).isFalse();
    }

    @Test
    void isActiveNodeReturnTrueForValidNodeWithOneParameter() {
        ViewConfiguration.CurrentRoute currentRoute =
                createMockCurrentRoute(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                            }
                        });

        Menu menuItem =
                new Menu(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                            }
                        },
                        null,
                        null,
                        null);

        assertThat(menuItem.isActiveNode(currentRoute)).isTrue();
    }

    @Test
    void isActiveNodeReturnFalseForValidNodeWithOneInvalidParameter() {
        ViewConfiguration.CurrentRoute currentRoute =
                createMockCurrentRoute(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "foo");
                            }
                        });

        Menu menuItem =
                new Menu(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                            }
                        },
                        null,
                        null,
                        null);

        assertThat(menuItem.isActiveNode(currentRoute)).isFalse();
    }

    @Test
    void isActiveNodeReturnTrueForValidNodeWithMultipleParameters() {
        ViewConfiguration.CurrentRoute currentRoute =
                createMockCurrentRoute(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                                put("second-foo", "bar-bar");
                            }
                        });

        Menu menuItem =
                new Menu(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                                put("second-foo", "bar-bar");
                            }
                        },
                        null,
                        null,
                        null);

        assertThat(menuItem.isActiveNode(currentRoute)).isTrue();
    }

    @Test
    void isActiveNodeReturnFalseForValidNodeWithMultipleInvalidParameters() {
        ViewConfiguration.CurrentRoute currentRoute =
                createMockCurrentRoute(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "foo");
                                put("second-foo", "bar-bar");
                            }
                        });

        Menu menuItem =
                new Menu(
                        "test-item",
                        new LinkedHashMap<>() {
                            {
                                put("foo", "bar");
                                put("second-foo", "not-valid");
                            }
                        },
                        null,
                        null,
                        null);

        assertThat(menuItem.isActiveNode(currentRoute)).isFalse();
    }

    private ViewConfiguration.CurrentRoute createMockCurrentRoute(String name) {
        return createMockCurrentRoute(name, new LinkedHashMap<>());
    }

    private ViewConfiguration.CurrentRoute createMockCurrentRoute(
            String name, LinkedHashMap<String, String> pathVariables) {
        ViewConfiguration.CurrentRoute currentRoute = mock(ViewConfiguration.CurrentRoute.class);
        lenient().when(currentRoute.getName()).thenReturn(name);
        lenient().when(currentRoute.getPathVariables()).thenReturn(pathVariables);
        return currentRoute;
    }
}
