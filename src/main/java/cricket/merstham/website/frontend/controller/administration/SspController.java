package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.datatables.SspBaseResponseData;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

public abstract class SspController<T extends SspBaseResponseData> {
    public abstract SspResponse getData(
            Principal principal,
            @RequestBody SspRequest request);
}
