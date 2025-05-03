package org.ecom.mypetstore.integration;

import org.ecom.mypetstore.client.PetStoreApiClient;
import org.ecom.mypetstore.enums.PetStatus;
import org.ecom.mypetstore.mapper.PetMapper;
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
import org.ecom.mypetstore.utils.PetDataProvider;
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
    private PetMapper petMapper;

    @Test
    @DisplayName("Получение питомца из API и сохранение в БД")
    void testSavePetFromApiToDatabase() {
        // Шаг 1: Получение питомца из API
        ResponseEntity<Pet> response = petStoreApiSteps.sendGetPetByIdRequest(100L);
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

    @Test
    @DisplayName("Создание нового питомца через API и сохранение в БД")
    void testCreatePetFromApiAndSaveToDatabase() {
        // Шаг 1: Создание нового питомца
        Pet newPet = PetDataProvider.createSamplePet();

        ResponseEntity<Pet> response = petStoreApiSteps.sendCreatePetRequest(newPet);
        petStoreApiSteps.checkResponseStatus(HttpStatus.OK, response.getStatusCode());

        Pet createdPet = response.getBody();
        Assertions.assertNotNull(createdPet, "API не вернул тело созданного питомца");

        // Шаг 2: Сохраняем в БД
        petRepositorySteps.savePetToDatabase(createdPet);

        // Шаг 3: Проверка сохранения
        PetEntity savedPet = petRepositorySteps.verifyPetSavedInDatabase(createdPet.getId());
        petRepositorySteps.compareApiAndDatabasePet(createdPet, savedPet);

        // Шаг 4: Проверка обратной конверсии
        petRepositorySteps.verifyConversionToApiModel(savedPet, createdPet);
    }

    @Test
    @DisplayName("Обновление питомца через API и сохранение обновлений в БД")
    void testUpdatePetFromApiAndSaveToDatabase() {
        // Шаг 1: Получение питомца из БД по id
        Long petId = 100L; // например, питомец с id 902
        PetEntity petEntityFromDb = petRepositorySteps.getPetById(petId);

        // Шаг 2: Создание объекта для API, обновление данных (например, имя питомца)
        petEntityFromDb.setName("Мурзик-обновлённый");
        Pet petFromDb = petMapper.toApiModel(petEntityFromDb);

        // Шаг 3: Отправка запроса на обновление питомца через API
        ResponseEntity<Pet> response = petStoreApiSteps.sendUpdatePetRequest(petFromDb);
        petStoreApiSteps.checkResponseStatus(HttpStatus.OK, response.getStatusCode());

        Pet apiUpdatedPet = response.getBody();
        Assertions.assertNotNull(apiUpdatedPet, "API не вернул тело обновлённого питомца");

        // Шаг 4: Сохранение обновлённого питомца в БД
        petRepositorySteps.savePetToDatabase(apiUpdatedPet);

        // Шаг 5: Проверка обновлённых данных в БД
        PetEntity savedPet = petRepositorySteps.verifyPetSavedInDatabase(apiUpdatedPet.getId());
        petRepositorySteps.compareApiAndDatabasePet(apiUpdatedPet, savedPet);

        // Шаг 6: Проверка обратной конверсии (проверка, что данные в БД и API совпадают)
        petRepositorySteps.verifyConversionToApiModel(savedPet, apiUpdatedPet);
    }

}
