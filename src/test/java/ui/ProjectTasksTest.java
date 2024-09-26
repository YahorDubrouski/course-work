package ui;

import static com.codeborne.selenide.Condition.*;

import base.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selectors.*;

public class ProjectTasksTest extends BasePage {
    private UserRepository userRepository = new UserRepository();
    private ProjectRepository projectRepository = new ProjectRepository();
    private LoginPageTest loginPage = new LoginPageTest();
    private ApiAuthService apiAuthService = new ApiAuthService();
    private ColumnRepository columnRepository = new ColumnRepository();
    private TaskRepository taskRepository = new TaskRepository();

    private boolean isApiLoggedIn = false;

    private Integer userId;
    private Integer projectId;
    private Integer backlogColumnId;

    @BeforeMethod
    public void setUp() {
        super.setUp();

        loginPage.login();

        if (!isApiLoggedIn) {
            apiAuthService.login();
            isApiLoggedIn = true;
        }
        if (projectId == null) {
            userId = userRepository.createUser(
                    RandomStringUtils.randomAlphanumeric(10),
                    RandomStringUtils.randomAlphanumeric(10)
            );
            projectId = projectRepository.createProject("My Project 1", userId);
            backlogColumnId = columnRepository.getColumnIdByName(projectId, "Backlog");
        }
    }

    @AfterClass
    private void tearDownClass() {
        apiAuthService.logout();
    }

    @AfterMethod
    public void tearDown() {
        super.tearDown();
        if (projectId != null) {
            projectRepository.deleteProject(projectId);
            projectId = null;
        }
        if (userId != null) {
            userRepository.deleteUser(userId);
            userId = null;
        }
        if (backlogColumnId != null) {
            backlogColumnId = null;
        }
    }

    @Test
    public void testCreateProjectSuccess() {
        open(config.getConfig("kanboard.base.url") + "/projects");

        //Open creation modal
        $(".page-header a[href='/project/create']").click();
        $("#form-name").setValue("My Project 1");
        $("#project-creation-form button[type='submit']").click();

        $(".title-container .title").shouldHave(text("My Project 1"));
    }

    @Test
    public void testCreateTaskSuccess() {
        open(config.getConfig("kanboard.base.url") + "/project/" + projectId);
        //Open Modal
        $(".project-header .dropdown-component").click();
        $(".dropdown-submenu-open").find(byText("Add a new task")).click();

        //Create a task
        $("#form-title").setValue("My Task 1");
        $(".task-form-bottom button[type='submit']").click();

        //Check if was created
        $(".dropdown-menu[title='Default filters']").click();
        $(".dropdown-submenu-open").find(byText("Open tasks")).click();

        //Check that the task is visible
        $(".table-list-title").find(byText("My Task 1")).should(exist);
    }

    @Test
    public void testCreateTaskFail() {
        open(config.getConfig("kanboard.base.url") + "/project/" + projectId);
        //Open Modal
        $(".project-header .dropdown-component").click();
        $(".dropdown-submenu-open").find(byText("Add a new task")).click();

        //Create a task
        $(".task-form-bottom button[type='submit']").click();

        //Check if was created
        $(".form-errors").find(byText("The title is required")).should(exist);

        //Close the modal
        $(".js-submit-buttons-rendered").find(byText("cancel")).click();
    }

    @Test
    public void testGetTaskSuccess() {
        taskRepository.createTask("My Task 1", projectId, backlogColumnId);
        open(config.getConfig("kanboard.base.url") + "/project/" + projectId + "/overview");

        //Open the tasks page
        $(".view-listing").click();
        $(".table-list-title").find(byText("My Task 1")).click();
        $("#task-summary").find(byText("My Task 1")).should(exist);
    }

    @Test
    public void testUpdateTaskSuccess() {
        int taskId = taskRepository.createTask("Task 1", projectId, backlogColumnId);
        open(config.getConfig("kanboard.base.url") + "/task/" + taskId);

        $(".sidebar").find(byText("Edit the task")).click();
        $("#form-title").setValue("Updated My Task 1");
        $(".task-form-bottom button[type='submit']").click();
        $("#task-summary").find(byText("Updated My Task 1")).should(exist);
    }

    @Test
    public void testDeleteTaskSuccess() {
        int taskId = taskRepository.createTask("Task 1", projectId, backlogColumnId);
        open(config.getConfig("kanboard.base.url") + "/task/" + taskId);

        $(".sidebar").find(byText("Remove")).click();
        $("#modal-confirm-button").click();
        $(".task-board-title").find(byText("Updated My Task 1")).shouldNot(exist);
    }

    @Test
    public void testCloseTaskSuccess() {
        int taskId = taskRepository.createTask("Task 1", projectId, backlogColumnId);
        open(config.getConfig("kanboard.base.url") + "/task/" + taskId);

        $(".sidebar").find(byText("Close this task")).click();
        $("#modal-confirm-button").click();
        $(".task-summary-column").find(byText("closed")).should(exist);
    }

    @Test
    public void testAddCommentToTaskSuccess() {
        int taskId = taskRepository.createTask("Task 1", projectId, backlogColumnId);
        open(config.getConfig("kanboard.base.url") + "/task/" + taskId);

        $(".sidebar").find(byText("Add a comment")).click();
        $("#modal-content textarea[name='comment']").setValue("My Comment");
        $("#modal-content button[type=\"submit\"]").click();
        $(".comment-content").find(byText("My Comment")).should(exist);
    }
}
