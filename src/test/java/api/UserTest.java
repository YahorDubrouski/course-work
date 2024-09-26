package api;

import base.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class UserTest extends BaseTest {
    private UserRepository userRepository = new UserRepository();

    private Integer createdUserId;

    @Test
    public void createUserSuccessTest() {
        createdUserId = userRepository.createUser(
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
    }

    @Test(dependsOnMethods = {"createUserSuccessTest"})
    public void deleteUserSuccessTest() {
        userRepository.deleteUser(createdUserId);
    }
}
