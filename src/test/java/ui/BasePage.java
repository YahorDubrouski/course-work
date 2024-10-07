package ui;

import com.codeborne.selenide.Configuration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import base.ConfigLoader;

public class BasePage {
    protected ConfigLoader config = new ConfigLoader();

    @BeforeMethod
    public void setUp() {
        Configuration.browser = config.getConfig("ui.browser");
        Configuration.headless = Boolean.parseBoolean(config.getConfig("ui.browser.headless"));
        Configuration.remote = config.getConfig("ui.driver.remote.url");
        Configuration.timeout = Long.parseLong(config.getConfig("ui.browser.wait.seconds.timeout"));
    }

    @AfterMethod
    public void tearDown() {
        closeWebDriver();
    }
}
