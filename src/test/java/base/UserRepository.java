package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class UserRepository {
    public int createUser(String username, String password) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "createUser");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("username", username);
        params.put("password", password);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .post();
        response.then().log().all().statusCode(200);

        JSONObject responseObject = new JSONObject(response.getBody().asString());

        return responseObject.getInt("result");
    }

    public void deleteUser(int userId) {
        JSONObject body = new JSONObject();
        body.put("jsonrpc", "2.0");
        body.put("method", "removeUser");
        body.put("id", 1);//Any numeric value is acceptable
        JSONObject params = new JSONObject();
        params.put("user_id", userId);
        body.put("params", params);

        Response response = RestAssured
                .given()
                .body(body.toString())
                .delete();
        response.then().log().all().statusCode(200);
    }
}
