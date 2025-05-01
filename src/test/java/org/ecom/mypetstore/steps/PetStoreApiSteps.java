package org.ecom.mypetstore.steps;

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


    public PetStoreApiSteps(PetStoreApiClient petStoreApiClient) {
        this.petStoreApiClient = petStoreApiClient;
    }

    @Step("Отправка запроса на получение питомца с ID: {id}")
    public ResponseEntity<Pet> sendGetPetByIdRequest(Long id) {
        return petStoreApiClient.getPetById(id);
    }

    @Step("Проверка статуса ответа: ожидается {expectedStatus}, фактически {actualStatus}")
    public void checkResponseStatus(HttpStatus expectedStatus, HttpStatusCode actualStatus) {
        Assertions.assertEquals(expectedStatus, actualStatus, "Статус ответа должен быть 200 OK");
    }

    @Step("Проверка тела ответа: ID питомца должен быть {expectedId}")
    public void checkResponseBody(Pet pet, Long expectedId) {
        Assertions.assertNotNull(pet, "Тело ответа не должно быть пустым");
        Assertions.assertEquals(expectedId, pet.getId(), "ID питомца должен быть 1");
    }
}