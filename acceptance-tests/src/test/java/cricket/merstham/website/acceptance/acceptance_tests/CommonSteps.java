package cricket.merstham.website.acceptance.acceptance_tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CommonSteps {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;

    private Page page;

    @BeforeAll
    public static void startPlaywright() {
        playwright = Playwright.create();
        browser = playwright
                .chromium()
                .launch(
                        new BrowserType
                                .LaunchOptions()
                                .setChromiumSandbox(true)
                                .setHeadless(true));
        context = browser.newContext();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(false)
                .setSources(false));
    }

    @AfterAll
    public static void stopPlaywright() {
        context
                .tracing()
                .stop(new Tracing.StopOptions()
                        .setPath(
                                Paths.get("build/reports/trace.zip")));
        browser.close();
        playwright.close();
    }

    @Before
    public void openPage(Scenario scenario) {
        page = context.newPage();
    }

    @After
    public void closePage(Scenario scenario) {
        if (scenario.isFailed()) {

        }
        page.close();
    }

    @Given("user visits homepage")
    public void userVisitsHomepage() {
        page.navigate("https://www.test.mersthamcc.co.uk");
    }

    @Then("the title should be {string}")
    public void theTitleShouldBe(String title) {
        assertThat(page).hasTitle(title);
    }
}
