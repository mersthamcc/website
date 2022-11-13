package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class SspController<T> {
    public abstract SspResponse<SspResponseDataWrapper<T>> getData(
            OAuth2AuthorizedClient authorizedClient, @RequestBody SspRequest request);
}
