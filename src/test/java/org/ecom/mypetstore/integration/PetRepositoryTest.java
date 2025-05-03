package org.ecom.mypetstore.integration;

import org.ecom.mypetstore.client.PetStoreApiClient;
import org.ecom.mypetstore.model.entity.PetEntity;
import org.ecom.mypetstore.model.entity.TagEntity;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.model.external.Tag;
import org.ecom.mypetstore.repository.CategoryRepository;
import org.ecom.mypetstore.repository.PetRepository;
import org.ecom.mypetstore.repository.TagRepository;
import org.ecom.mypetstore.service.PetService;
import org.ecom.mypetstore.steps.PetRepositorySteps;
import org.ecom.mypetstore.steps.PetStoreApiSteps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
public class PetRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PetStoreExternalApiTest.class);

    @Autowired
    private PetStoreApiSteps petStoreApiSteps;

    @Autowired
    private PetRepositorySteps petRepositorySteps;

    @Autowired
    private PetService petService;

    @Autowired
    private RestTemplate restTemplate; // бин из RestTemplateConfig

    @Autowired
    private PetStoreApiClient petStoreApiClient;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("Получение питомца из API и сохранение в БД")
    void testSavePetFromApiToDatabase() {
        // Шаг 1: Получение питомца из API
        ResponseEntity<Pet> response = petStoreApiSteps.sendGetPetByIdRequest(900L);
        petStoreApiSteps.checkResponseStatus(HttpStatus.OK, response.getStatusCode());
        Pet apiPet = response.getBody();
        Assertions.assertNotNull(apiPet, "API вернул null");

        // Шаг 2: Сохраняем в БД
        petRepositorySteps.savePetToDatabase(apiPet);

        // Шаг 3: Проверка данных в БД
        PetEntity savedPet = petRepositorySteps.verifyPetSavedInDatabase(apiPet.getId());
        petRepositorySteps.compareApiAndDatabasePet(apiPet, savedPet);

        // Шаг 4: Проверка обратной конверсии
        petRepositorySteps.verifyConversionToApiModel(savedPet, apiPet);
    }
}
