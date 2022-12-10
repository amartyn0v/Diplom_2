package client;
import model.User;
import model.UserLogin;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends Client{

    private final String AUTH_PATH = "api/auth/login";
    private final String REGISTER_PATH = "api/auth/register";
    private final String EDIT_PATH = "api/auth/user";

    @Step("User authorizes")
    public ValidatableResponse auth(UserLogin userLogin){
        return given().spec(getSpecs())
                .body(userLogin)
                .when().post(AUTH_PATH)
                .then();
    }

    @Step("User registers")
    public ValidatableResponse register(User user){
        return given().spec(getSpecs())
                .body(user)
                .when().post(REGISTER_PATH)
                .then();
    }

    @Step("User updated")
    public ValidatableResponse update(User user, String accessToken){
        return given().spec(getSpecs())
                .body(user).header("Authorization",accessToken)
                .when().patch(EDIT_PATH)
                .then();
    }

    @Step("User deleted")
    public ValidatableResponse delete(String accessToken){
        return given().spec(getSpecs())
                .header("Authorization",accessToken)
                .when().delete(EDIT_PATH)
                .then();
    }

    @Step("Get user data")
    public ValidatableResponse getData(String accessToken){
        return given().spec(getSpecs())
                .header("Authorization",accessToken)
                .when().get(EDIT_PATH)
                .then();
    }
}
