package api;

import base.ColumnRepository;
import base.ProjectRepository;
import base.TaskRepository;
import base.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.apache.commons.lang3.RandomStringUtils;

public class TaskTest extends BaseTest {
    private ProjectRepository projectRepository = new ProjectRepository();
    private UserRepository userRepository = new UserRepository();
    private TaskRepository taskRepository = new TaskRepository();
    private ColumnRepository columnRepository = new ColumnRepository();

    private Integer projectId;
    private Integer backlogColumnId;
    private Integer userId;

    private void initProject() {
        if (projectId != null) {
            return;
        }

        userId = userRepository.createUser(
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
        projectId = projectRepository.createProject("My Project 1", userId);
    }

    private void initBacklogColumnId() {
        if (backlogColumnId != null) {
            return;
        }

        backlogColumnId = columnRepository.getColumnIdByName(projectId,"Backlog");
    }

    @BeforeMethod
    private void createEntities() {
        initProject();
        initBacklogColumnId();
    }

    @AfterMethod
    private void removeEntities() {
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
    public void createTaskSuccessTest() {
        taskRepository.createTask("Create Task Success Test", projectId, backlogColumnId);
    }

    @Test
    public void createTaskFailTest() {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "createTask");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("project_id", projectId);
        params.put("column_id", backlogColumnId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());
        JSONObject responseError = responseObject.getJSONObject("error");
        String errorDetails = responseError.getString("data");
        Assert.assertEquals("Missing argument: title", errorDetails);
    }

    private JSONObject getTask(int taskId) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "getTask");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("task_id", taskId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());

        return responseObject.getJSONObject("result");
    }

    @Test
    public void getTaskSuccessTest() {
        String taskTitle = "get Task Success Test";
        int taskId = taskRepository.createTask(taskTitle, projectId, backlogColumnId);

        JSONObject resultTask = getTask(taskId);
        Assert.assertEquals(taskId, resultTask.getInt("id"));
        Assert.assertEquals(taskTitle, resultTask.getString("title"));
    }

    @Test
    public void getAllTasksSuccessTest() {
        String task1Title = "Task 1";
        int task1Id = taskRepository.createTask(task1Title, projectId, backlogColumnId);
        String task2Title = "Task 2";
        int task2Id = taskRepository.createTask(task2Title, projectId, backlogColumnId);

        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "getAllTasks");
        body.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("project_id", projectId);
        params.put("status_id", 1); // 1 for active tasks
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());
        JSONArray resultTasks = responseObject.getJSONArray("result");

        boolean task1Found = false;
        boolean task2Found = false;
        for (int i = 0; i < resultTasks.length(); i++) {
            JSONObject resultTask = resultTasks.getJSONObject(i);
            int taskId = resultTask.getInt("id");
            String taskTitle = resultTask.getString("title");

            if (taskId == task1Id && taskTitle.equals(task1Title)) {
                task1Found = true;
            }
            if (taskId == task2Id && taskTitle.equals(task2Title)) {
                task2Found = true;
            }
        }
        Assert.assertTrue(task1Found, "Task 1 was not found in the results.");
        Assert.assertTrue(task2Found, "Task 2 was not found in the results.");
    }

    @Test
    public void updateTasksSuccessTest() {
        String taskTitle = "Task 1";
        int taskId = taskRepository.createTask(taskTitle, projectId, backlogColumnId);;

        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "updateTask");
        body.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("id", taskId);
        params.put("color_id", "blue"); // 1 for active tasks
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .put();
        response.then().log().all().statusCode(200);

        JSONObject updatedTask = getTask(taskId);
        Assert.assertEquals(updatedTask.getString("color_id"), "blue");
    }

    @Test
    public void removeTasksSuccessTest() {
        String taskTitle = "Task 1";
        int taskId = taskRepository.createTask(taskTitle, projectId, backlogColumnId);;

        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "removeTask");
        body.put("id", 1);
        JSONObject params = new JSONObject();
        params.put("task_id", taskId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .delete();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());
        Assert.assertTrue(responseObject.getBoolean("result"), "Task was deleted successfully");
    }
}
