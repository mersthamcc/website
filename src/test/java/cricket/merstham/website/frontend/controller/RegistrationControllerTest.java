package cricket.merstham.website.frontend.controller;

import com.c4_soft.springaddons.security.oauth2.test.annotations.IdTokenClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.OidcStandardClaims;
import com.c4_soft.springaddons.security.oauth2.test.annotations.keycloak.WithMockKeycloakAuth;
import cricket.merstham.website.frontend.configuration.SecurityConfiguration;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RegistrationController.class)
@ActiveProfiles("test")
class RegistrationControllerTest {

    @Configuration
    static class UnitTestContextConfiguration {
        @Bean
        public AdapterConfig getKeycloakConfig() {
            var config = new AdapterConfig();
            config.setAuthServerUrl("https://auth-server");
            config.setRealm("Test Realm");
            return config;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GraphService graphService;
    @MockBean
    private MembershipService membershipService;
    @MockBean
    private PaymentServiceManager paymentServiceManager;

    private MockHttpSession session = new MockHttpSession();

    @Test
    void shouldReturnRedirectToLoginIfNotLoggedIn() throws Exception {
        MvcResult result = mockMvc.perform(get("/register").accept(MediaType.TEXT_HTML).with(anonymous())).andReturn();
        assertThat(result.getResponse().getStatus(), equalTo(SC_MOVED_TEMPORARILY));
        assertThat(result.getResponse().getRedirectedUrl(), equalTo("http://localhost/login"));
    }

//    @Test
//    @WithMockKeycloakAuth(
//            authorities = { "USER" },
//            id = @IdTokenClaims(sub = "1"),
//            oidc = @OidcStandardClaims(
//                    email = "test@example.com",
//                    emailVerified = true,
//                    nickName = "Test",
//                    preferredUsername = "test-user")
//    )
//    void shouldDisplayPageIfLoggedIn() throws Exception {
//        MvcResult result = mockMvc.perform(authenticatedRequest("/register")).andReturn();
//        assertThat(result.getResponse().getStatus(), equalTo(SC_OK));
//    }

    private MockHttpServletRequestBuilder authenticatedRequest(String uri) {
        return get(uri)
                .accept(MediaType.TEXT_HTML)
                .session(session);
    }
}