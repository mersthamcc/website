package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class SspController<T> {
    public abstract SspResponse<SspResponseDataWrapper<T>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request);
}
