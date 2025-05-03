package org.ecom.mypetstore.client;

import io.qameta.allure.Allure;
import org.ecom.mypetstore.exception.ExternalApiException;
import org.ecom.mypetstore.model.external.Pet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;

//Для работы с внешним апи
@Component
public class PetStoreApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PetStoreApiClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public PetStoreApiClient(@Value("${petstore.api.url}") String baseUrl, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Recover
    public ResponseEntity<Pet> recover(HttpServerErrorException e, Long petId) {
        logger.error("Failed to get pet after 3 retries, petId: {}", petId);
        throw new ExternalApiException("Сервер не отвечает после 3 попыток", e);
    }

    @Retryable(
            retryFor = { HttpServerErrorException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000))
    public ResponseEntity<Pet> getPetById(Long petId) {
        String url = baseUrl + "/pet/" + petId;
        logger.info("Отправляем GET запрос к URL: {}", url);
        Allure.addAttachment("GET /pet/{id}", "Запрос по URL: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Pet> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Pet.class
            );

            logger.info("Ответ получен: статус = {}, тело = {}", response.getStatusCode(), response.getBody());
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Питомец с ID {} не найден. Ответ сервера: {}", petId, e.getResponseBodyAsString());
            throw new ExternalApiException("Питомец не найден!", e);

        } catch (HttpServerErrorException e) {
            logger.error("Ошибка сервера при обращении к внешнему API: {}", e.getResponseBodyAsString());
            throw new ExternalApiException("Ошибка сервера внешнего API", e);

        } catch (Exception e) {
            logger.error("Неизвестная ошибка при обращении к внешнему API", e);
            throw new ExternalApiException("Неизвестная ошибка при обращении к внешнему API", e);
        }
    }

    @Retryable(
            retryFor = { HttpServerErrorException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<Pet> createPet(Pet pet) {
        String url = baseUrl + "/pet";
        logger.info("Создание питомца через API: {}", pet);
        Allure.addAttachment("Создание питомца через API", "Запрос по URL: " + url);

        // Добавляем тело запроса (pet) в Allure
        try {
            ObjectMapper mapper = new ObjectMapper();
            String petJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pet);
            Allure.addAttachment("Тело запроса (Pet)", "application/json", petJson, ".json");
        } catch (Exception e) {
            logger.warn("Не удалось сериализовать тело запроса Pet для Allure", e);
        }

        try {
            ResponseEntity<Pet> response = restTemplate.postForEntity(url, pet, Pet.class);
            logger.info("Создан питомец: {}", response.getBody());
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Питомец с ID {} не найден. Ответ сервера: {}", pet.getId(), e.getResponseBodyAsString());
            throw new ExternalApiException("Питомец не найден!", e);
        } catch (HttpServerErrorException e) {
            logger.error("Ошибка сервера при создании питомца: {}", e.getResponseBodyAsString());
            throw new ExternalApiException("Ошибка при создании питомца", e);
        } catch (Exception e) {
            logger.error("Ошибка при создании питомца: {}", e.getMessage());
            throw new ExternalApiException("Неизвестная ошибка", e);
        }
    }

    @Retryable(
            retryFor = { HttpServerErrorException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public ResponseEntity<Pet> updatePet(Pet pet) {
        String url = baseUrl + "/pet";
        logger.info("Отправляем PUT-запрос для обновления питомца: {}", pet);
        Allure.addAttachment("Отправляем PUT-запрос для обновления питомца", "Запрос по URL: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Pet> entity = new HttpEntity<>(pet, headers);

        // Добавляем тело запроса (pet) в Allure
        try {
            ObjectMapper mapper = new ObjectMapper();
            String petJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pet);
            Allure.addAttachment("Тело запроса (Pet)", "application/json", petJson, ".json");
        } catch (Exception e) {
            logger.warn("Не удалось сериализовать тело запроса Pet для Allure", e);
        }

        try {
            ResponseEntity<Pet> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Pet.class
            );

            logger.info("Питомец обновлён: статус = {}, тело = {}", response.getStatusCode(), response.getBody());
            return response;

        } catch (HttpServerErrorException e) {
            logger.error("Ошибка сервера при обновлении питомца: {}", e.getResponseBodyAsString());
            throw new ExternalApiException("Ошибка сервера при обновлении питомца", e);

        } catch (Exception e) {
            logger.error("Неизвестная ошибка при обновлении питомца", e);
            throw new ExternalApiException("Неизвестная ошибка при обновлении питомца", e);
        }
    }

}
