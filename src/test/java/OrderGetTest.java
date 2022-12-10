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

public class OrderGetTest  {

    OrderClient order;
    UserClient user;
    User defaultUser;
    String accessToken = "";

    @Before
    public void setUp(){
        //RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = new UserClient();
        defaultUser = UserGenerator.getDefault();
        order = new OrderClient();
        ValidatableResponse response = user.register(defaultUser);
        Assert.assertTrue(response.extract().body().path("success"));
        accessToken = response.extract().body().path("accessToken");
        Order randomOrder = OrderGenerator.getRandomIngredientsOrder();
        response = order.create(randomOrder, accessToken);
        Assert.assertTrue(response.extract().body().path("success"));
    }

    @After
    public void tearDown(){
        if (!accessToken.equals("")) user.delete(accessToken);
    }

    @Test
    @DisplayName("Can get orders for authorized user")
    public void canGetOrderByAuthorizedUser(){
        ValidatableResponse response = order.getOrder(accessToken);
        Assert.assertEquals(200, response.extract().statusCode());
        Assert.assertTrue(response.extract().body().path("success"));
        Assert.assertNotNull(response.extract().body().path("orders[0].createdAt"));
    }

    @Test
    @DisplayName("Can't get orders for unauthorized user")
    public void cantGetOrderUnauthorized(){
        String errorMessage = "You should be authorised";
        ValidatableResponse response = order.getOrder("accessToken");
        Assert.assertEquals(401, response.extract().statusCode());
        Assert.assertFalse(response.extract().body().path("success"));
        Assert.assertEquals(errorMessage, response.extract().body().path("message"));
    }
}
