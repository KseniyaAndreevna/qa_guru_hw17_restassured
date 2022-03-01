import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


public class ReqresTests {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in/api/users";
    }

    @Test
    @DisplayName("GET request: find user")
    void singleUserTest() {
        int userId = 2;

        given()
                .basePath("/{userId}")
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("data.id", is(userId));
    }

    @Test
    @DisplayName("GET request: user not found")
    void singleUserNotFoundTest() {
        int userId = 50;

        given()
                .basePath("/{userId}")
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("POST request: create user")
    void createUserTest() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "Albus Dumbledore");
        user.put("job", "wizard");

        given()
                .contentType(ContentType.JSON)
                .body(user)
        .when()
                .post()
        .then()
                .statusCode(201)
                .body(
                        "name", is(user.get("name")),
                        "job", is(user.get("job")),
                        "id", notNullValue(),
                        "createdAt", notNullValue()
                );
    }

    @Test
    @DisplayName("PUT request: update user")
    void updateUserTest() {

        // Подготовка к тесту - создание пользователя и запоминание его id
        Map<String, String> user = new HashMap<>();
        user.put("name", "Albus Dumbledore");
        user.put("job", "wizard");

        String createdUserId =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                .when()
                        .post()
                .then().statusCode(201).extract().path("id");

        System.out.println("createdUserId: " + createdUserId);

        // Обновление созданного пользователя
        Map<String, String> userUpdate = new HashMap<>();
        userUpdate.put("name", "Albus Dumbledore");
        userUpdate.put("job", "professor and wizard");

        given()
                .contentType(ContentType.JSON)
                .basePath("/{userId}")
                .pathParam("userId", createdUserId)
                .body(userUpdate)
        .when()
                .put()
        .then()
                .statusCode(200)
                .body(
                        "name", is(userUpdate.get("name")),
                        "job", is(userUpdate.get("job")),
                        "updatedAt", notNullValue()
                );
    }

    @Test
    @DisplayName("DELETE request: delete user")
    void deleteUserTest() {

        // Подготовка к тесту - создание пользователя и запоминание его id
        Map<String, String> user = new HashMap<>();
        user.put("name", "Albus Dumbledore");
        user.put("job", "wizard");

        String createdUserId =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                .when()
                        .post()
                .then().statusCode(201).extract().path("id");

        System.out.println("createdUserId: " + createdUserId);

        // Удаление созданного подтзователя
        given()
                .contentType(ContentType.JSON)
                .basePath("/{userId}")
                .pathParam("userId", createdUserId)
        .when()
                .delete()
        .then()
                .statusCode(204);
    }
}
