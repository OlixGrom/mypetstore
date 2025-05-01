package org.ecom.mypetstore.integration;

import org.ecom.mypetstore.model.external.Pet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Тест обращения к внешнему API https://petstore.swagger.io/
 */
@SpringBootTest
public class PetStoreExternalApiTest {
    private static final Logger logger = LoggerFactory.getLogger(PetStoreExternalApiTest.class);

    @Autowired
    private RestTemplate restTemplate; // бин из RestTemplateConfig

    @Autowired
    private PetStoreApiClient petStoreApiClient;


    @Test
    @DisplayName("Проверка получения питомца по ID через внешний API")
    void testGetPetById() {
        ResponseEntity<Pet> response = petStoreApiClient.getPetById(1L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус ответа должен быть 200 OK");
        Assertions.assertNotNull(response.getBody(), "Тело ответа не должно быть пустым");
        Assertions.assertEquals(1L, response.getBody().getId(), "ID питомца должен быть 1");
    }
}

