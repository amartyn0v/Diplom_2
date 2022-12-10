package generator;

import client.IngredientsClient;
import model.Order;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrderGenerator {

    static IngredientsClient ingredients = new IngredientsClient();

    public static Order getRandomIngredientsOrder(){
        List<String> list = new ArrayList<>();
        int number = new Random().nextInt(10) + 1;
        int random;
        ValidatableResponse response = ingredients.getIngredients();
        List<String> ingredients = response.extract().body().jsonPath().getList("data._id");
        for (int i=0; i<number; i++){
            random = new Random().nextInt(13) + 1;
            list.add(ingredients.get(random));
        }
        return new Order(list);
    }
}
