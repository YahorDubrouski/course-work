package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class TaskRepository {
    public int createTask(String title, int projectId, int backlogColumnId) {
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
}
