package api;

import base.ApiAuthService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected ApiAuthService authService = new ApiAuthService();

    private boolean isLoggedIn = false;

    @BeforeMethod
    public void setup() {
        if (!isLoggedIn) {
            authService.login();
            isLoggedIn = true;
        }
    }

    @AfterClass
    public void tearDown() {
        authService.logout();
    }
}
