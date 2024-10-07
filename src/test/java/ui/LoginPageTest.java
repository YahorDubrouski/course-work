package ui;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Condition.*;

public class LoginPageTest extends BasePage {
    public void login() {
        open(config.getConfig("kanboard.base.url") + "/login");

        $("#form-username").setValue(config.getConfig("kanboard.auth.username"));
        $("#form-password").setValue(config.getConfig("kanboard.auth.password"));
        $("button[type='submit']").click();
    }

    @DataProvider(name = "failedLoginData")
    public Object[][] failedLoginDataProvider() {
        return new Object[][] {
                { " ", " "},
                { "Not Existing User", "Not Existing Password" }
        };
    }

    @Test(dataProvider = "failedLoginData")
    public void loginFailedTest(String username, String password) {
        open(config.getConfig("kanboard.base.url") + "/login");

        $("#form-username").setValue(username);
        $("#form-password").setValue(password);
        $("button[type='submit']").click();

        $(".alert-error").shouldHave(text("Bad username or password")).shouldBe(visible);
    }

    @Test
    public void loginSuccessTest() {
        login();
        $(".title-container .title").shouldHave(text("Dashboard"));
    }
}
