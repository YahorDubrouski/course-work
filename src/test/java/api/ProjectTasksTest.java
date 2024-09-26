package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

public class ProjectTasksTest extends BaseTest {
    private Integer projectId;
    private Integer backlogColumnId;

    private void createProject() {
        if (projectId != null) {
            return;
        }

        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "createProject");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("name", "My Project 1");
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());

        projectId = responseObject.getInt("result");
    }

    private JSONArray getColumns(int projectId) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "getColumns");
        body.put("id", 1);//Any numeric value is acceptable
        int[] params = {projectId};
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());

        return responseObject.getJSONArray("result");
    }

    private int getColumnIdByName(int projectId, String name) {
        JSONArray columns = getColumns(projectId);

        for (Object obj : columns) {
            JSONObject column = (JSONObject) obj;
            if (column.getString("title").equals(name)) {
                return column.getInt("id");
            }
        }

        throw new NotFoundException("The column was not found");
    }

    private void initBacklogColumnId() {
        if (backlogColumnId != null) {
            return;
        }

        backlogColumnId = getColumnIdByName(projectId,"Backlog");
    }

    @BeforeMethod
    private void initVariables() {
        createProject();
        initBacklogColumnId();
    }

    @Test
    public void createTaskSuccessTest() {
        createTask("Create Task Success Test");
    }

    private int createTask(String title) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "createTask");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("title", title);
        params.put("project_id", projectId);
        params.put("column_id", backlogColumnId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());
        return responseObject.getInt("result");
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
        int taskId = createTask(taskTitle);

        JSONObject resultTask = getTask(taskId);
        Assert.assertEquals(taskId, resultTask.getInt("id"));
        Assert.assertEquals(taskTitle, resultTask.getString("title"));
    }

    @Test
    public void getAllTasksSuccessTest() {
        String task1Title = "Task 1";
        int task1Id = createTask(task1Title);
        String task2Title = "Task 2";
        int task2Id = createTask(task2Title);

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
        int taskId = createTask(taskTitle);

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
        int taskId = createTask(taskTitle);

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
