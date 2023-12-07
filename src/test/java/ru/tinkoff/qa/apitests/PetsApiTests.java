package ru.tinkoff.qa.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.qa.models.Category;
import ru.tinkoff.qa.models.TagsItem;
import ru.tinkoff.qa.models.UserRequest;
import ru.tinkoff.qa.models.UserResponse;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

public class PetsApiTests {
    UserRequest userRequest;

    @BeforeEach
    public void init() {
        userRequest = new UserRequest();
        userRequest.setId(1);
        userRequest.setName("dawg");

        Category category = new Category();
        category.setId(1);
        category.setName("testCategory");
        userRequest.setCategory(category);

        userRequest.setPhotoUrls(List.of("www.test.ru"));

        TagsItem tagsItem = new TagsItem();
        tagsItem.setId(1);
        tagsItem.setName("testTag");
        userRequest.setTags(List.of(tagsItem));

        userRequest.setStatus("available");
    }

    @Test
    public void addPetTest() {
        UserResponse userResponse = RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        Assertions.assertEquals(userRequest.getId(), userResponse.getId(), "Check pet id");
        Assertions.assertEquals(userRequest.getCategory().getId(), userResponse.getCategory().getId(), "Check category id");
        Assertions.assertEquals(userRequest.getTags().get(0).getId(), userResponse.getTags().get(0).getId(), "Check category id");
        Assertions.assertEquals(userRequest.getPhotoUrls().get(0), userResponse.getPhotoUrls().get(0), "Check photo url");
    }

    @Test
    public void addExistingPetTest() {
        RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        UserResponse userResponse = RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        Assertions.assertEquals(userRequest.getId(), userResponse.getId(), "Check pet id");
        Assertions.assertEquals(userRequest.getCategory().getId(), userResponse.getCategory().getId(), "Check category id");
        Assertions.assertEquals(userRequest.getTags().get(0).getId(), userResponse.getTags().get(0).getId(), "Check category id");
        Assertions.assertEquals(userRequest.getPhotoUrls().get(0), userResponse.getPhotoUrls().get(0), "Check photo url");
    }

    @Test
    public void updatePetTest() {
        RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        userRequest.setName("doggie_renamed");

        UserResponse userResponse = RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        Assertions.assertEquals(userRequest.getName(), userResponse.getName(), "Check updated name");
        Assertions.assertEquals(userRequest.getId(), userResponse.getId(), "Check pet id");
        Assertions.assertEquals(userRequest.getCategory().getId(), userResponse.getCategory().getId(), "Check category id");
        Assertions.assertEquals(userRequest.getTags().get(0).getId(), userResponse.getTags().get(0).getId(), "Check category id");
        Assertions.assertEquals(userRequest.getPhotoUrls().get(0), userResponse.getPhotoUrls().get(0), "Check photo url");
    }

    @Test
    public void deletePetTest() {
        RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);


        RestAssured
                .given()
                .delete("https://petstore.swagger.io/v2/pet/" + userRequest.getId())
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured
                .given()
                .get("https://petstore.swagger.io/v2/pet/" + userRequest.getId())
                .then()
                .assertThat()
                .body("code", equalTo(1))
                .body("type", equalTo("error"))
                .statusCode(404);
    }

    @Test
    public void deleteNonExistentPetTest() {
        RestAssured
                .given()
                .delete("https://petstore.swagger.io/v2/pet/40928949")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void findExistentPetByIdTest() {
        RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);

        RestAssured
                .given()
                .get("https://petstore.swagger.io/v2/pet/" + userRequest.getId())
                .then()
                .assertThat()
                .body("id", equalTo(userRequest.getId()))
                .body("category.id", equalTo(userRequest.getCategory().getId()))
                .statusCode(200);
    }

    @Test
    public void findNonExistentPetById() {
        RestAssured
                .given()
                .get("https://petstore.swagger.io/v2/pet/422924")
                .then()
                .assertThat()
                .body("code", equalTo(1))
                .body("type", equalTo("error"))
                .statusCode(404);
    }

    @Test
    public void findByStatusPetTest() {
        UserResponse userResponse = RestAssured.given().
                contentType(ContentType.JSON).
                body(userRequest).
                post("https://petstore.swagger.io/v2/pet").
                as(UserResponse.class);


        RestAssured
                .given()
                .get("https://petstore.swagger.io/v2/pet/findByStatus?status=" + userRequest.getStatus())
                .then()
                .assertThat()
                .body("status", everyItem(equalTo(userRequest.getStatus())))
                .statusCode(200);
    }
}
