package ui;

import static com.codeborne.selenide.Condition.*;
import org.testng.annotations.Test;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selectors.*;

public class ProjectTasksTest extends BasePage {
    @Test
    public void testCreateProjectSuccess() {
        LoginPageTest loginPage = new LoginPageTest();
        loginPage.login();

        open(config.getConfig("kanboard.base.url") + "/projects");

        //Open creation modal
        $(".page-header a[href='/project/create']").click();
        $("#form-name").setValue("My Project 1");
        $("#project-creation-form button[type='submit']").click();

        $(".title-container .title").shouldHave(text("My Project 1"));
    }

    @Test(dependsOnMethods = {"testCreateProjectSuccess"})
    public void testCreateTaskSuccess() {
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

    @Test(dependsOnMethods = {"testCreateProjectSuccess"})
    public void testCreateTaskFail() {
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

    @Test(dependsOnMethods = {"testCreateTaskSuccess"})
    public void testGetTaskSuccess() {
        //Open the tasks page
        $(".view-listing").click();
        $(".table-list-title").find(byText("My Task 1")).click();
        $("#task-summary").find(byText("My Task 1")).should(exist);
    }

    @Test(dependsOnMethods = {"testGetTaskSuccess"})
    public void testUpdateTaskSuccess() {
        $(".sidebar").find(byText("Edit the task")).click();
        $("#form-title").setValue("Updated My Task 1");
        $(".task-form-bottom button[type='submit']").click();
        $("#task-summary").find(byText("Updated My Task 1")).should(exist);
    }

    @Test(dependsOnMethods = {"testUpdateTaskSuccess"})
    public void testDeleteTaskSuccess() {
        $(".sidebar").find(byText("Remove")).click();
        $("#modal-confirm-button").click();
        $(".task-board-title").find(byText("Updated My Task 1")).shouldNot(exist);
    }
}
