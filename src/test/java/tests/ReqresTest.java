package tests;

import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import objects.Data;
import objects.Resource;
import objects.User;
import objects.Users;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class ReqresTest {

    @BeforeMethod
    public void beforeMethod() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    public void getUsers() {
        given()
                .when()
                .get("/api/users?page=2")
                .then()
                .log().body()
                .statusCode(200)
                .body("total", equalTo(12));
    }

    @Test
    void getUser() {
        String responseBody = given()
                .when()
                .get("/api/users")
                .then()
                .log().body()
                .statusCode(200)
                .extract().body().asString();
        Users users = new Gson().fromJson(responseBody, Users.class);
        Data user = users.getData().get(0);
        given()
                .when()
                .get("/api/users/" + user.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(user.getId()),
                        "data.email", equalTo(user.getEmail()),
                        "data.first_name", equalTo(user.getFirstName()),
                        "data.last_name", equalTo(user.getLastName()),
                        "data.avatar", equalTo(user.getAvatar()));
    }

    @Test
    public void getUserNotFound() {
        given()
                .when()
                .get("/api/users/23")
                .then()
                .log().body()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    public void getResources() {
        given()
                .when()
                .get("/api/unknown")
                .then()
                .log().body()
                .statusCode(200)
                .body("total", equalTo(12));
    }

    @Test
    public void getResource() {
        Resource resource = Resource.builder()
                .id(2)
                .name("fuchsia rose")
                .year(2001)
                .color("#C74375")
                .pantoneValue("17-2031")
                .build();
        given()
                .when()
                .get("/api/unknown/" + resource.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("data.id", equalTo(resource.getId()),
                        "data.name", equalTo(resource.getName()),
                        "data.year", equalTo(resource.getYear()),
                        "data.color", equalTo(resource.getColor()),
                        "data.pantone_value", equalTo(resource.getPantoneValue()));
    }

    @Test
    public void getResourceNotFound() {
        given()
                .when()
                .get("/api/unknown/23")
                .then()
                .log().body()
                .statusCode(404)
                .body(equalTo("{}"));
    }

    @Test
    public void createUser() {
        User user = User.builder()
                .name("Evgeniy")
                .job("QA")
                .build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/api/users")
                .then()
                .log().body()
                .statusCode(201)
                .body("$", hasKey("id"),
                        "name", equalTo(user.getName()),
                        "job", equalTo(user.getJob()),
                        "$", hasKey("createdAt"));
    }

    @Test
    public void putUpdateUser() {
        User newUser = User.builder()
                .name("Evgeniy")
                .job("QA")
                .build();
        String response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/api/users")
                .then()
                .log().body()
                .statusCode(201)
                .extract().body().asString();
        User currentUser = new Gson().fromJson(response, User.class);
        User updatedUser = User.builder()
                .id(currentUser.getId())
                .name("Mike")
                .job("AQA")
                .build();
        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/api/users/" + currentUser.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(updatedUser.getName()),
                        "job", equalTo(updatedUser.getJob()),
                        "id", equalTo(updatedUser.getId()),
                        "$", hasKey("updatedAt"));
    }

    @Test
    public void patchUpdateUser() {
        User newUser = User.builder()
                .name("Evgeniy")
                .job("QA")
                .build();
        String response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/api/users")
                .then()
                .log().body()
                .statusCode(201)
                .extract().body().asString();
        User currentUser = new Gson().fromJson(response, User.class);
        User updatedUser = User.builder()
                .id(currentUser.getId())
                .name("Mike")
                .job("AQA")
                .build();
        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .patch("/api/users/" + currentUser.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(updatedUser.getName()),
                        "job", equalTo(updatedUser.getJob()),
                        "id", equalTo(updatedUser.getId()),
                        "$", hasKey("updatedAt"));
    }

    @Test
    public void deleteUser() {
        User newUser = User.builder()
                .name("Evgeniy")
                .job("QA")
                .build();
        String response = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/api/users")
                .then()
                .log().body()
                .statusCode(201)
                .extract().body().asString();
        User currentUser = new Gson().fromJson(response, User.class);
        given()
                .when()
                .delete("/api/users/" + currentUser.getId())
                .then()
                .log().body()
                .statusCode(204)
                .body(equalTo(""));
    }

    @Test
    public void registerSuccessful() {
        User user = User.builder()
                .email("eve.holt@reqres.in")
                .password("pistol")
                .build();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/register")
                .then()
                .log().body()
                .statusCode(200)
                .body("$", hasKey("id"),
                        "$", hasKey("token"));
    }

    @Test
    public void registerUnsuccessful() {
        User user = User.builder()
                .email("sydney@fife")
                .build();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/register")
                .then()
                .log().body()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void loginSuccessful() {
        User user = User.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/login")
                .then()
                .log().body()
                .statusCode(200)
                .body("token", equalTo("QpwL5tke4Pnpja7X4"));
    }

    @Test
    public void loginUnsuccessful() {
        User user = User.builder()
                .email("peter@klaven")
                .build();
        given()
                .when()
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/login")
                .then()
                .log().body()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test
    public void delayedResponse() {
        given()
                .when()
                .get("/api/users?delay=3")
                .then()
                .log().body()
                .statusCode(200)
                .time(Matchers.greaterThanOrEqualTo(3L), SECONDS);
    }
}
