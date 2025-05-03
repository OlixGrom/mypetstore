package org.ecom.mypetstore.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.ecom.mypetstore.client.PetStoreApiClient;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.ecom.mypetstore.model.external.Pet;
import org.springframework.stereotype.Component;

@Component
public class PetStoreApiSteps {

    private PetStoreApiClient petStoreApiClient;
    private final ObjectMapper objectMapper;

    public PetStoreApiSteps(PetStoreApiClient petStoreApiClient) {
        this.petStoreApiClient = petStoreApiClient;
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Step("Отправка запроса на получение питомца с ID: {id}")
    public ResponseEntity<Pet> sendGetPetByIdRequest(Long id) {
        ResponseEntity<Pet> response = petStoreApiClient.getPetById(id);
        attachJson("Ответ сервера (Pet)", response.getBody());
        return response;
    }

    @Step("Проверка статуса ответа: ожидается {expectedStatus}, фактически {actualStatus}")
    public void checkResponseStatus(HttpStatus expectedStatus, HttpStatusCode actualStatus) {
        Assertions.assertEquals(expectedStatus, actualStatus, "Статус ответа должен быть 200 OK");
    }

    @Step("Проверка тела ответа: ID питомца должен быть {expectedId}")
    public void checkResponseBody(Pet pet, Long expectedId) {
        Assertions.assertNotNull(pet, "Тело ответа не должно быть пустым");
        Assertions.assertEquals(expectedId, pet.getId(), "ID питомца должен быть "+expectedId);
        attachJson("Проверка тела Pet", pet);
    }

    private void attachJson(String name, Object body) {
        try {
            String prettyJson = objectMapper.writeValueAsString(body);
            Allure.addAttachment(name, "application/json", prettyJson, ".json");
        } catch (Exception e) {
            Allure.addAttachment("Ошибка сериализации", e.getMessage());
        }
    }
}