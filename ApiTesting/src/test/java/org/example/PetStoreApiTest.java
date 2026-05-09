package org.example;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("API Tests")
@Feature("PetStore API")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreApiTest {

    private static final Logger log = LoggerFactory.getLogger(PetStoreApiTest.class);

    private static final int TEST_PET_ID = (int) (System.currentTimeMillis() % 100_000) + 100_000;
    private static long createdOrderId;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.filters(new AllureRestAssured());
        log.info("PetStore API base URI set; pet ID for this run: {}", TEST_PET_ID);
    }

    @AfterAll
    static void cleanup() {
        RestAssured.replaceFiltersWith(java.util.Collections.emptyList());
        try {
            given().delete("/pet/" + TEST_PET_ID);
            log.info("Cleaned up test pet {}", TEST_PET_ID);
        } catch (Exception ignored) {}
    }

    // ==================== POSITIVE TESTS ====================

    @Test
    @Order(1)
    @Story("Find pets")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that searching by 'available' status returns HTTP 200 and a non-empty JSON array")
    @DisplayName("Positive: GET /pet/findByStatus?status=available → 200, JSON array, status field present")
    void testFindPetsByValidStatus() {
        log.info("GET /pet/findByStatus?status=available");
        given()
            .queryParam("status", "available")
        .when()
            .get("/pet/findByStatus")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", not(empty()))
            .body("[0].status", notNullValue());
    }

    @Test
    @Order(2)
    @Story("Create pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new pet and verify the response mirrors all submitted fields")
    @DisplayName("Positive: POST /pet → 200, response contains all submitted fields")
    void testCreatePet() {
        log.info("POST /pet — creating pet with id={}", TEST_PET_ID);
        String body = String.format("""
                {
                    "id": %d,
                    "name": "TestDoggy",
                    "status": "available",
                    "photoUrls": ["http://example.com/photo.jpg"],
                    "category": {"id": 1, "name": "Dogs"}
                }
                """, TEST_PET_ID);

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/pet")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id",            equalTo(TEST_PET_ID))
            .body("name",          equalTo("TestDoggy"))
            .body("status",        equalTo("available"))
            .body("category.name", equalTo("Dogs"))
            .body("photoUrls",     not(empty()));
    }

    @Test
    @Order(3)
    @Story("Get pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Retrieve the pet by ID and verify stored fields match what was submitted")
    @DisplayName("Positive: GET /pet/{petId} → 200, returns correct pet data")
    void testGetPetById() {
        log.info("GET /pet/{}", TEST_PET_ID);
        given()
        .when()
            .get("/pet/" + TEST_PET_ID)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id",     equalTo(TEST_PET_ID))
            .body("name",   equalTo("TestDoggy"))
            .body("status", equalTo("available"));
    }

    @Test
    @Order(4)
    @Story("Update pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update pet name and status; verify the response reflects the new values")
    @DisplayName("Positive: PUT /pet → 200, updated name and status reflected in response")
    void testUpdatePet() {
        log.info("PUT /pet — updating pet {}", TEST_PET_ID);
        String body = String.format("""
                {
                    "id": %d,
                    "name": "UpdatedDoggy",
                    "status": "sold",
                    "photoUrls": ["http://example.com/photo.jpg"]
                }
                """, TEST_PET_ID);

        given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .put("/pet")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id",     equalTo(TEST_PET_ID))
            .body("name",   equalTo("UpdatedDoggy"))
            .body("status", equalTo("sold"));
    }

    @Test
    @Order(5)
    @Story("Place order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Place a store order for the pet and verify all fields in the response")
    @DisplayName("Positive: POST /store/order → 200, order fields match submission")
    void testPlaceOrder() {
        log.info("POST /store/order — placing order for pet {}", TEST_PET_ID);
        String body = String.format("""
                {
                    "petId": %d,
                    "quantity": 2,
                    "shipDate": "2026-04-25T10:00:00.000Z",
                    "status": "placed",
                    "complete": false
                }
                """, TEST_PET_ID);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/store/order")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("petId",    equalTo(TEST_PET_ID))
            .body("quantity", equalTo(2))
            .body("status",   equalTo("placed"))
            .body("complete", equalTo(false))
            .body("id",       notNullValue())
            .extract().response();

        createdOrderId = response.jsonPath().getLong("id");
        log.info("Order created with id={}", createdOrderId);
    }

    // ==================== NEGATIVE TESTS ====================

    @Test
    @Order(6)
    @Story("Get pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Requesting a non-existent pet ID must return 404 with 'Pet not found' message")
    @DisplayName("Negative: GET /pet/{nonExistentId} → 404 with 'Pet not found' message")
    void testGetNonExistentPet() {
        log.info("GET /pet/999888777666555 — expecting 404");
        given()
        .when()
            .get("/pet/999888777666555")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Pet not found"));
    }

    @Test
    @Order(7)
    @Story("Delete pet")
    @Severity(SeverityLevel.NORMAL)
    @Description("Deleting a non-existent pet must return HTTP 404")
    @DisplayName("Negative: DELETE /pet/{nonExistentId} → 404")
    void testDeleteNonExistentPet() {
        log.info("DELETE /pet/999888777666444 — expecting 404");
        given()
        .when()
            .delete("/pet/999888777666444")
        .then()
            .statusCode(404);
    }

    @Test
    @Order(8)
    @Story("Get order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Requesting a non-existent order must return 404 with 'Order not found' message")
    @DisplayName("Negative: GET /store/order/{nonExistentId} → 404 with 'Order not found' message")
    void testGetNonExistentOrder() {
        log.info("GET /store/order/999888777666 — expecting 404");
        given()
        .when()
            .get("/store/order/999888777666")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Order not found"));
    }

    @Test
    @Order(9)
    @Story("Get user")
    @Severity(SeverityLevel.NORMAL)
    @Description("Looking up a non-existent username must return 404 with 'User not found' message")
    @DisplayName("Negative: GET /user/{nonExistentUsername} → 404 with 'User not found' message")
    void testGetNonExistentUser() {
        log.info("GET /user/nonExistentUser99999 — expecting 404");
        given()
        .when()
            .get("/user/nonExistentUser99999")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("User not found"));
    }

    @Test
    @Order(10)
    @Story("Delete order")
    @Severity(SeverityLevel.NORMAL)
    @Description("Deleting a non-existent store order must return HTTP 404")
    @DisplayName("Negative: DELETE /store/order/{nonExistentId} → 404")
    void testDeleteNonExistentOrder() {
        log.info("DELETE /store/order/999777888666555 — expecting 404");
        given()
        .when()
            .delete("/store/order/999777888666555")
        .then()
            .statusCode(404);
    }
}
