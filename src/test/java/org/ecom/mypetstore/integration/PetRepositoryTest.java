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
        // 1. Получаем данные питомца из API
        ResponseEntity<Pet> response = petStoreApiClient.getPetById(999L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Статус код должен быть 200");

        Pet apiPet = response.getBody();
        Assertions.assertNotNull(apiPet, "Тело ответа не должно быть null");
        //Assertions.assertEquals(1L, apiPet.getId(), "ID питомца должен быть 1");

        // 2. Сохраняем полученные данные в БД
        petService.addOrUpdatePetById(apiPet);

        // 3. Проверяем, что данные сохранились корректно
        PetEntity savedPet = petRepository.findByExternalId(apiPet.getId())
                .orElseThrow(() -> new AssertionError("Питомец не найден в БД"));

        Assertions.assertEquals(apiPet.getName(), savedPet.getName(), "Имена питомцев должны совпадать");
        Assertions.assertEquals(apiPet.getStatus().name(), savedPet.getStatus().getValue().toUpperCase(), "Статусы должны совпадать");

        // Проверяем категорию
        Assertions.assertNotNull(savedPet.getCategory(), "Категория не должна быть null");
        Assertions.assertEquals(apiPet.getCategory().getId(), savedPet.getCategory().getExternalId(),
                "ID категорий должны совпадать");
        Assertions.assertEquals(apiPet.getCategory().getName(), savedPet.getCategory().getName(),
                "Названия категорий должны совпадать");

        // Проверяем теги
        Assertions.assertNotNull(savedPet.getTags(), "Список тегов не должен быть null");
        Assertions.assertEquals(apiPet.getTags().size(), savedPet.getTags().size(),
                "Количество тегов должно совпадать");

        List<Long> savedTagIds = savedPet.getTags().stream()
                .map(TagEntity::getId)
                .collect(Collectors.toList());

        List<Long> apiTagIds = apiPet.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(savedTagIds.containsAll(apiTagIds), "Теги должны содержать все ID из API");

        // 4. (Дополнительно) Проверяем преобразование обратно в API модель
        Pet convertedPet = petService.convertToApiModel(savedPet);
        Assertions.assertEquals(apiPet.getId(), convertedPet.getId(), "ID должны совпадать");
        Assertions.assertEquals(apiPet.getName(), convertedPet.getName(), "Имена должны совпадать");
        Assertions.assertEquals(apiPet.getStatus().name().toUpperCase(), convertedPet.getStatus().name().toUpperCase(), "Статусы должны совпадать");
    }

}
