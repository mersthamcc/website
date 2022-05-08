package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.datatables.SspBaseResponseData;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class SspController<T extends SspBaseResponseData> {
    public abstract SspResponse getData(
            OAuth2AuthorizedClient authorizedClient, @RequestBody SspRequest request);
}
