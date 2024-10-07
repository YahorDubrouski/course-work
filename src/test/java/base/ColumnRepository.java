package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.NotFoundException;

public class ColumnRepository {
    public int getColumnIdByName(int projectId, String name) {
        JSONArray columns = getColumns(projectId);

        for (Object obj : columns) {
            JSONObject column = (JSONObject) obj;
            if (column.getString("title").equals(name)) {
                return column.getInt("id");
            }
        }

        throw new NotFoundException("The column was not found");
    }

    public JSONArray getColumns(int projectId) {
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
}
