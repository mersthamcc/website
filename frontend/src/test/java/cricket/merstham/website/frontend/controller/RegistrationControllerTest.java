package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
@ActiveProfiles("test")
class RegistrationControllerTest {

    @Configuration
    static class UnitTestContextConfiguration {}

    @Autowired private MockMvc mockMvc;

    @MockBean private GraphService graphService;
    @MockBean private MembershipService membershipService;
    @MockBean private PaymentServiceManager paymentServiceManager;

    private MockHttpSession session = new MockHttpSession();

    @Test
    void shouldReturnRedirectToLoginIfNotLoggedIn() throws Exception {
        MvcResult result =
                mockMvc.perform(get("/register").accept(MediaType.TEXT_HTML).with(anonymous()))
                        .andReturn();
        assertThat(result.getResponse().getStatus(), equalTo(SC_MOVED_TEMPORARILY));
        assertThat(result.getResponse().getRedirectedUrl(), equalTo("http://localhost/login"));
    }

    private MockHttpServletRequestBuilder authenticatedRequest(String uri) {
        return get(uri).accept(MediaType.TEXT_HTML).session(session);
    }
}
