package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class ProjectRepository {
    public int createProject(String name, int ownerId) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "createProject");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("name", name);
        params.put("owner_id", ownerId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());

        return responseObject.getInt("result");
    }

    public void deleteProject(int projectId) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "removeProject");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("project_id", projectId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .delete();
        response.then().log().all().statusCode(200);
    }
}
