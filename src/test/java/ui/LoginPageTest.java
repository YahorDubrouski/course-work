package ui;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPageTest extends BasePage {
    public void login() {
        open(config.getConfig("kanboard.base.url") + "/login");

        $("#form-username").setValue(config.getConfig("kanboard.auth.username"));
        $("#form-password").setValue(config.getConfig("kanboard.auth.password"));
        $("button[type='submit']").click();
    }
}
