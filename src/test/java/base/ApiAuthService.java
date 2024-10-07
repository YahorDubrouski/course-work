package base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ApiAuthService {
    protected ConfigLoader config = new ConfigLoader();

    public void login() {
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
    }

    public void logout() {
        RestAssured.requestSpecification = null;
    }
}
