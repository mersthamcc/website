package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import com.google.common.collect.Lists;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.shared.dto.Message;
import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.shared.dto.Venue;
import cricket.merstham.website.frontend.model.DynamicMenu;
import cricket.merstham.website.graph.MenusQuery;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.min;
import static java.util.Objects.nonNull;

@Service
public class MenuService {

    private static final Logger LOG = LoggerFactory.getLogger(MenuService.class);
    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public MenuService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public DynamicMenu getDynamicMenuItems() {
        try {
            Response<MenusQuery.Data> result = graphService.executeQuery(new MenusQuery());
            var seasons = Lists.reverse(result.getData().getFixtureArchiveSeasons());
            if (!seasons.isEmpty()) seasons = seasons.subList(1, min(20, seasons.size()));
            return DynamicMenu.builder()
                    .venues(venues(result.getData()))
                    .seasons(seasons)
                    .contactCategories(categories(result.getData()))
                    .banner(
                            nonNull(result.getData().getMessage())
                                    ? modelMapper.map(result.getData().getMessage(), Message.class)
                                    : null)
                    .menus(
                            result.getData().getPagesForMenus().stream()
                                    .map(p -> modelMapper.map(p, StaticPage.class))
                                    .collect(Collectors.groupingBy(StaticPage::getMenu)))
                    .build();
        } catch (Exception e) {
            return DynamicMenu.builder().build();
        }
    }

    private List<ContactCategory> categories(MenusQuery.Data data) {
        return data.getContactCategoryFeed().stream()
                .map(c -> modelMapper.map(c, ContactCategory.class))
                .toList();
    }

    private List<Venue> venues(MenusQuery.Data data) {
        return data.getVenuesForMenu().stream().map(m -> modelMapper.map(m, Venue.class)).toList();
    }
}
