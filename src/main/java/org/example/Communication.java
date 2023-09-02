package org.example;

import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Communication {

    private final RestTemplate restTemplate;
    private final String URL = "http://94.198.50.185:7081/api/users";
    private String sessionId; // Переменная для хранения Session ID

    public String getSessionId() {
        return sessionId;
    }

    @Autowired
    public Communication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        HttpHeaders headers = new HttpHeaders();

        if (sessionId != null) {
            headers.add("Cookie", "JSESSIONID=" + sessionId);
        }

        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                new HttpEntity<>(headers), // Передаем заголовки в запрос
                new ParameterizedTypeReference<List<User>>() {
                }
        );

        // Получаем заголовки из ответа
        HttpHeaders responseHeaders = responseEntity.getHeaders();

        // Получаем значение Session ID из заголовка Set-Cookie
        List<String> cookies = responseHeaders.get("Set-Cookie");
        if (cookies != null && !cookies.isEmpty()) {
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID=")) {
                    this.sessionId= cookie.split(";")[0].substring("JSESSIONID=".length());
                    System.out.println(sessionId);
                    break;
                }
            }
        }

        List<User> allUsers = responseEntity.getBody();
        return allUsers;
    }

    public ResponseEntity<String> createUser(User user) {
        HttpHeaders headers = new HttpHeaders();

        if (sessionId != null) {
            // Устанавливаем Session ID в заголовок запроса
            headers.add("Cookie", "JSESSIONID=" + sessionId);
        }

        // Выполняем POST-запрос с установленными заголовками
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                new HttpEntity<>(user, headers),
                String.class
        );

        System.out.println("new user was added to database");
        System.out.println(responseEntity.getBody());

        return responseEntity;
    }

    public ResponseEntity<String> updateUser(User user) {
        Long id = user.getId();
        HttpHeaders headers = new HttpHeaders();

        if (sessionId != null) {
            // Устанавливаем Session ID в заголовок запроса
            headers.add("Cookie", "JSESSIONID=" + sessionId);
        }

        ResponseEntity<String> responseEntity;

        if (id != null) {
            // Выполняем PUT-запрос с установленными заголовками
            responseEntity = restTemplate.exchange(
                    URL,
                    HttpMethod.PUT,
                    new HttpEntity<>(user, headers),
                    String.class
            );

            System.out.println("user with id " + id + " was updated");
            System.out.println(responseEntity.getBody());
        } else {
            // Если id равно null, верните соответствующий ответ
            responseEntity = new ResponseEntity<>("ID is required for update.", HttpStatus.BAD_REQUEST);
        }

        return responseEntity;
    }


    public ResponseEntity<String> deleteUser(long userId) {
        HttpHeaders headers = new HttpHeaders();

        if (sessionId != null) {
            // Устанавливаем Session ID в заголовок запроса
            headers.add("Cookie", "JSESSIONID=" + sessionId);
        }

        // Выполняем DELETE-запрос с установленными заголовками
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                URL + "/" + userId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );

        // Возвращаем ответ как ResponseEntity<String>

        return responseEntity;
    }
}

