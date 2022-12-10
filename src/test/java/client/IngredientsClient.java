package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends Client {

    private final String INGREDIENTS_URL = "api/ingredients";

    @Step("Get all ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpecs())
                .when().get(INGREDIENTS_URL)
                .then();
    }
}
