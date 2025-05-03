package org.ecom.mypetstore.integration;

import org.ecom.mypetstore.MypetstoreApplication;
import org.ecom.mypetstore.client.PetStoreApiClient;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.steps.PetStoreApiSteps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


/**
 * Тест обращения к внешнему API https://petstore.swagger.io/
 */
@SpringBootTest(classes = MypetstoreApplication.class)
public class PetStoreExternalApiTest {
    private static final Logger logger = LoggerFactory.getLogger(PetStoreExternalApiTest.class);

    @Autowired
    private PetStoreApiSteps petStoreApiSteps;

    @Autowired
    private PetStoreApiClient petStoreApiClient;

    @BeforeEach
    void checkClient() {
        Assertions.assertNotNull(petStoreApiClient, "petStoreApiClient не внедрён!");
    }

    @Test
    @DisplayName("Проверка получения питомца по ID через внешний API")
    public void testGetPetById() {
        logger.info("petStoreApiClient is {}", petStoreApiClient);
        Assertions.assertNotNull(petStoreApiClient, "petStoreApiClient не инициализирован!");

        logger.info("petStoreApiClient is {}", petStoreApiClient);
        Assertions.assertNotNull(petStoreApiClient, "petStoreApiClient не инициализирован!");

        // Шаг 1: Отправка запроса
        ResponseEntity<Pet> response = petStoreApiSteps.sendGetPetByIdRequest(1L);

        // Шаг 2: Проверка статуса ответа
        petStoreApiSteps.checkResponseStatus(HttpStatus.OK, response.getStatusCode());

        // Шаг 3: Проверка тела ответа
        petStoreApiSteps.checkResponseBody(response.getBody(), 1L);
    }
}

