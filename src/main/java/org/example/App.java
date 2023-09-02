package org.example;

import org.example.configuration.MyConfig;
import org.example.entity.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);


        Communication communication = new Communication(new RestTemplate());

        // Получение списка всех пользователей
        List<User> allUsers = communication.getAllUsers();

        // Сохранение пользователя с id = 3
        User newUser = new User();
        newUser.setId(3L);
        newUser.setName("James");
        newUser.setLastName("Brown");
        newUser.setAge((byte) 30); // Укажите возраст на ваш выбор

        ResponseEntity<String> createUserResponse = communication.createUser(newUser);

        // Изменение пользователя с id = 3
        newUser.setName("Thomas");
        newUser.setLastName("Shelby");

        ResponseEntity<String> updateUserResponse = communication.updateUser(newUser);

        // Удаление пользователя с id = 3
        ResponseEntity<String> deleteUserResponse = communication.deleteUser(3L);

        // Получение Session ID
        String sessionId = communication.getSessionId();

        // Собираем итоговый код
        String code = sessionId + createUserResponse.getBody() + updateUserResponse.getBody() + deleteUserResponse.getBody();

        // Выводим итоговый код
        System.out.println("Итоговый код: " + code);
    }
}


