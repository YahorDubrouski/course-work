package api;

import base.ApiAuthService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected ApiAuthService authService = new ApiAuthService();

    @BeforeClass
    public void setup() {
        authService.login();
    }

    @AfterClass
    public void tearDown() {
        authService.logout();
    }
}
