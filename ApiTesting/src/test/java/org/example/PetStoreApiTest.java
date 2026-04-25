package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreApiTest {

    // Use a reproducible ID small enough to avoid int/long Hamcrest mismatch
    private static final int TEST_PET_ID = (int) (System.currentTimeMillis() % 100_000) + 100_000;
    private static long createdOrderId;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        RestAssured.useRelaxedHTTPSValidation();
    }

    @AfterAll
    static void cleanup() {
        // Best-effort cleanup of the pet created during the test run
        try {
            given().delete("/pet/" + TEST_PET_ID);
        } catch (Exception ignored) {}
    }

    // ==================== POSITIVE TESTS ====================

    /**
     * Test 1 – Positive
     * Verify that searching pets by the "available" status returns HTTP 200,
     * a JSON content-type, and a non-empty array whose first element actually
     * carries the requested status.
     */
    @Test
    @Order(1)
    @DisplayName("Positive: GET /pet/findByStatus?status=available → 200, JSON array, status field present")
    void testFindPetsByValidStatus() {
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

    /**
     * Test 2 – Positive
     * Create a new pet with a known ID and verify the response mirrors
     * every submitted field (id, name, status, category, photoUrls).
     */
    @Test
    @Order(2)
    @DisplayName("Positive: POST /pet → 200, response contains all submitted fields")
    void testCreatePet() {
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

    /**
     * Test 3 – Positive
     * Retrieve the pet that was just created and confirm every stored field
     * matches what was submitted.
     */
    @Test
    @Order(3)
    @DisplayName("Positive: GET /pet/{petId} → 200, returns correct pet data")
    void testGetPetById() {
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

    /**
     * Test 4 – Positive
     * Update the existing pet's name and status; verify the response reflects
     * the new values and that the id is unchanged.
     */
    @Test
    @Order(4)
    @DisplayName("Positive: PUT /pet → 200, updated name and status reflected in response")
    void testUpdatePet() {
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

    /**
     * Test 5 – Positive
     * Place a store order for the pet and verify the response contains the
     * expected petId, quantity, status, and a server-assigned order id.
     */
    @Test
    @Order(5)
    @DisplayName("Positive: POST /store/order → 200, order fields match submission")
    void testPlaceOrder() {
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
    }

    // ==================== NEGATIVE TESTS ====================

    /**
     * Test 6 – Negative
     * Requesting a pet with a very large ID that does not exist must return
     * HTTP 404 and a JSON error body with "Pet not found" message.
     */
    @Test
    @Order(6)
    @DisplayName("Negative: GET /pet/{nonExistentId} → 404 with 'Pet not found' message")
    void testGetNonExistentPet() {
        given()
        .when()
            .get("/pet/999888777666555")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Pet not found"));
    }

    /**
     * Test 7 – Negative
     * Attempting to delete a pet that does not exist must return HTTP 404.
     */
    @Test
    @Order(7)
    @DisplayName("Negative: DELETE /pet/{nonExistentId} → 404")
    void testDeleteNonExistentPet() {
        given()
        .when()
            .delete("/pet/999888777666444")
        .then()
            .statusCode(404);
    }

    /**
     * Test 8 – Negative
     * Requesting a store order that does not exist must return HTTP 404
     * and a JSON error body with "Order not found" message.
     */
    @Test
    @Order(8)
    @DisplayName("Negative: GET /store/order/{nonExistentId} → 404 with 'Order not found' message")
    void testGetNonExistentOrder() {
        given()
        .when()
            .get("/store/order/999888777666")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Order not found"));
    }

    /**
     * Test 9 – Negative
     * Looking up a username that was never registered must return HTTP 404
     * and a JSON error body with "User not found" message.
     */
    @Test
    @Order(9)
    @DisplayName("Negative: GET /user/{nonExistentUsername} → 404 with 'User not found' message")
    void testGetNonExistentUser() {
        given()
        .when()
            .get("/user/nonExistentUser99999")
        .then()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("User not found"));
    }

    /**
     * Test 10 – Negative
     * Deleting a store order that does not exist must return HTTP 404.
     */
    @Test
    @Order(10)
    @DisplayName("Negative: DELETE /store/order/{nonExistentId} → 404")
    void testDeleteNonExistentOrder() {
        given()
        .when()
            .delete("/store/order/999777888666555")
        .then()
            .statusCode(404);
    }
}
