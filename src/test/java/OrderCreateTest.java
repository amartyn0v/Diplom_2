import client.OrderClient;
import client.UserClient;
import generator.OrderGenerator;
import generator.UserGenerator;
import model.Order;
import model.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class OrderCreateTest {

    OrderClient order;
    UserClient user;
    User defaultUser;
    String accessToken = "";

    @Before
    public void setUp(){
        user = new UserClient();
        defaultUser = UserGenerator.getDefault();
        order = new OrderClient();
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
    }

    @After
    public void tearDown(){
        if (!accessToken.equals("")) user.delete(accessToken);
    }

    @Test
    @DisplayName("Can create order with valid ingredients")
    public void canCreateOrderWithValidIngredients(){
        Order randomOrder = OrderGenerator.getRandomIngredientsOrder();
        ValidatableResponse response = order.create(randomOrder, accessToken);
        Assert.assertEquals(200, response.extract().statusCode());
        Assert.assertTrue(response.extract().body().path("success"));
    }

    @Test
    @DisplayName("Can't create order without any ingredients")
    public void cantCreateOrderWithoutIngredients(){
        String errorMessage = "Ingredient ids must be provided";
        ValidatableResponse response = order.create(new Order(Arrays.asList()), accessToken);
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
        Assert.assertEquals(400, response.extract().statusCode());
    }

    @Test
    @DisplayName("Can't create order with incorrect ingredients hash")
    public void cantCreateOrderWithIncorrectIngredients(){
        ValidatableResponse response = order.create(new Order(Arrays.asList("")), accessToken);
        Assert.assertEquals(500, response.extract().statusCode());
    }

    @Test
    @DisplayName("Can't create order without authorization")
    public void cantCreateOrderWithoutAuthorization(){
        Order randomOrder = OrderGenerator.getRandomIngredientsOrder();
        ValidatableResponse response = order.create(randomOrder, " ");
        Assert.assertEquals(401, response.extract().statusCode());
        Assert.assertFalse("We should receive error message " +
                "when trying to create order without authorization"
                ,response.extract().body().path("success"));

    }
}
