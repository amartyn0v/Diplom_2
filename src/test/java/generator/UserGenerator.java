package generator;

import model.User;
import model.UserLogin;

public class UserGenerator {

    public static User getDefault(){
        return new User("defaultEmail2@yandex.ru", "defaultPassword2", "defaultName2");
    }
    public static UserLogin getDefaultLogin(){
        return new UserLogin("defaultEmail2@yandex.ru", "defaultPassword2");
    }

    public static UserLogin loginFromUser(User user){
        return new UserLogin(user.getEmail(),user.getPassword());
    }
}
