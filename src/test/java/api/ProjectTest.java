package api;

import base.ProjectRepository;
import base.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class ProjectTest extends BaseTest {
    private ProjectRepository projectRepository = new ProjectRepository();
    private UserRepository userRepository = new UserRepository();

    private Integer createdProjectId;

    @Test
    public void createProjectSuccessTest() {
        int userId = userRepository.createUser(
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
        createdProjectId = projectRepository.createProject("My Project", userId);
    }

    @Test(dependsOnMethods = {"createProjectSuccessTest"})
    public void deleteProjectSuccessTest() {
        projectRepository.deleteProject(createdProjectId);
    }
}
