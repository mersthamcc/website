package cricket.merstham.website.frontend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
class HomeControllerTest {

    @Configuration
    static class HomeControllerTestContextConfiguration {}

    @Test
    void homepage() {}
}
