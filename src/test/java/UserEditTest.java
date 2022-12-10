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
public class UserEditTest {

    UserClient user;
    UserLogin defaultUserLogin;
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
    @DisplayName("Can update any of the user fields")
    @Parameters({"setEmail, newEmailTest@yandex.ru","setPassword, newPassword","setName, newName"})
    public void canUpdateAnyUserField(String method, String value)throws Exception{
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        Method changedField = User.class.getMethod(method, String.class);
        changedField.invoke(defaultUser, value);
        response = user.update(defaultUser, accessToken);
        Assert.assertTrue(response.extract().body().path("success"));
        response = user.getData(accessToken);
        Assert.assertEquals(defaultUser.getEmail().toLowerCase(), response.extract().body().path("user.email"));
        Assert.assertEquals(defaultUser.getName(), response.extract().body().path("user.name"));
        response = user.auth(UserGenerator.loginFromUser(defaultUser));
        Assert.assertTrue(response.extract().body().path("success"));
    }

    @Test
    @DisplayName("Can't update fields by unauthorized user")
    @Parameters({"setEmail, newEmailTest@yandex.ru","setPassword, newPassword","setName, newName"})
    public void cantUpdateUnauthorized(String method, @Nullable String value) throws Exception{
        String errorMessage = "You should be authorised";
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        Method changedField = User.class.getMethod(method, String.class);
        changedField.invoke(defaultUser, value);
        response = user.update(defaultUser, "");
        Assert.assertEquals(401, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }

    @Test
    @DisplayName("Can't update user email, if the email already exists")
    public void cantUpdateEmailIfAlreadyExists(){
        String errorMessage = "User with such email already exists";
        String newEmailAlreadyExists = "newEmail@yandex.ru";
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        defaultUser.setEmail(newEmailAlreadyExists);
        response = user.update(defaultUser, accessToken);
        Assert.assertEquals(403, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }
}
