import client.UserClient;
import generator.UserGenerator;
import model.User;
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
public class UserCreateTest {

    UserClient user;
    User defaultUser;
    String accessToken = "";

    @Before
    public void setUp(){
        user = new UserClient();
        defaultUser = UserGenerator.getDefault();
    }

    @After
    public void tearDown(){
        if (!accessToken.equals("")) user.delete(accessToken);
    }

    @Test
    @DisplayName("Can create unique user")
    public void canCreateUniqueUser(){
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        Assert.assertEquals(200, response.extract().statusCode());
        accessToken = response.extract().body().path("accessToken");
    }

    @Test
    @DisplayName("Can't create user with existing email")
    public void cantCreateExistingUser(){
        String errorMessage = "User already exists";
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        response = user.register(defaultUser);
        Assert.assertEquals(403, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }

    @Test
    @DisplayName("Can't create user without one of the mandatory fields")
    @Parameters({"setEmail, null","setPassword, null","setName, null"})
    public void cantCreateUserWithoutMandatoryField(String method,@Nullable String value) throws Exception{
        String errorMessage = "Email, password and name are required fields";
        Method changedField = User.class.getMethod(method, String.class);
        changedField.invoke(defaultUser, value);
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertEquals(403, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }
}
