package api;

import base.ConfigLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.testng.annotations.BeforeMethod;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class BaseTest {
    protected ConfigLoader config = new ConfigLoader();

    private boolean isLoggedIn = false;

    @BeforeMethod
    public void setup() {
        if (isLoggedIn) {
            return;
        }

        RestAssured.baseURI = config.getConfig("kanboard.api.base.url");

        String username = config.getConfig("kanboard.api.auth.username");
        String apiToken = config.getConfig("kanboard.api.auth.password");

        String authPair = username + ":" + apiToken;
        String token = Base64.getEncoder().encodeToString(authPair.getBytes(StandardCharsets.UTF_8));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", "Basic " + token)
                .addHeader("Content-Type", "application/json")
                .build()
                .filter(new AllureRestAssured());

        isLoggedIn = true;
    }
}
