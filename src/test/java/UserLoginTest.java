import client.UserClient;
import generator.UserGenerator;
import model.User;
import model.UserLogin;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Nullable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

@RunWith(JUnitParamsRunner.class)
public class UserLoginTest {
    UserClient user;
    UserLogin defaultUserLogin;
    User defaultUser;
    String accessToken = "";

    @Before
    public void setUp(){
        user = new UserClient();
        defaultUser = UserGenerator.getDefault();
        defaultUserLogin = UserGenerator.getDefaultLogin();
    }

    @After
    public void tearDown(){
        if (!accessToken.equals("")) user.delete(accessToken);
    }

    @Test
    @DisplayName("Can login with correct credentials")
    public void loginWithCorrectCreds(){
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        response = user.auth(defaultUserLogin);
        Assert.assertEquals(200, response.extract().statusCode());
        Assert.assertTrue(response.extract().body().path("success"));
    }

    @Test
    @DisplayName("Can't login with incorrect credentials")
    @Parameters({"setEmail, null","setPassword, null","setEmail, wrongEmail","setPassword, wrongPass"})
    public void loginWithIncorrectCreds(String method, @Nullable String value) throws Exception{
        String errorMessage = "email or password are incorrect";
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        Method changedField = UserLogin.class.getMethod(method, String.class);
        changedField.invoke(defaultUserLogin, value);
        response = user.auth(defaultUserLogin);
        Assert.assertEquals(401, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }
}
