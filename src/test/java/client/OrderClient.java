package client;

import model.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client{

    private final String ORDERS_PATH = "api/orders";

    @Step("Create an order")
    public ValidatableResponse create(Order order, String accessToken){
        return given()
                .spec(getSpecs())
                .body(order).header("Authorization",accessToken)
                .when().post(ORDERS_PATH)
                .then();
    }

    @Step("Get user orders")
    public ValidatableResponse getOrder(String accessToken){
        return given()
                .spec(getSpecs())
                .header("Authorization", accessToken)
                .when().get(ORDERS_PATH)
                .then();
    }
}
