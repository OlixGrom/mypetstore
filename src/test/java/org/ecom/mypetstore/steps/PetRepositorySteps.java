package org.ecom.mypetstore.steps;

import io.qameta.allure.Step;
import org.ecom.mypetstore.model.entity.PetEntity;
import org.ecom.mypetstore.model.entity.TagEntity;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.model.external.Tag;
import org.ecom.mypetstore.repository.PetRepository;
import org.ecom.mypetstore.service.PetService;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetRepositorySteps {

    private final PetService petService;
    private final PetRepository petRepository;

    public PetRepositorySteps(PetService petService, PetRepository petRepository) {
        this.petService = petService;
        this.petRepository = petRepository;
    }

    @Step("Сохранение питомца в БД")
    public void savePetToDatabase(Pet pet) {
        petService.addOrUpdatePetById(pet);
    }

    public PetEntity getPetById(Long petId) {
        return petRepository.findByExternalId(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
    }

    @Step("Проверка, что питомец с ID {externalId} сохранён в базе")
    public PetEntity verifyPetSavedInDatabase(Long externalId) {
        PetEntity savedPet = petRepository.findByExternalId(externalId)
                .orElseThrow(() -> new AssertionError("Питомец не найден в БД"));
        Assertions.assertNotNull(savedPet, "Питомец должен быть сохранен в БД");
        return savedPet;
    }

    @Step("Сравнение данных питомца из API и из БД")
    public void compareApiAndDatabasePet(Pet apiPet, PetEntity savedPet) {
        Assertions.assertEquals(apiPet.getName(), savedPet.getName(), "Имена питомцев должны совпадать");
        Assertions.assertEquals(apiPet.getStatus().name(), savedPet.getStatus().getValue().toUpperCase(), "Статусы должны совпадать");

        // Проверка категории
        Assertions.assertNotNull(savedPet.getCategory(), "Категория не должна быть null");
        Assertions.assertEquals(apiPet.getCategory().getId(), savedPet.getCategory().getExternalId(), "ID категорий должны совпадать");
        Assertions.assertEquals(
                safeString(apiPet.getCategory().getName()),
                safeString(savedPet.getCategory().getName()),
                "Названия категорий должны совпадать"
        );

        // Проверка тегов
        Assertions.assertNotNull(savedPet.getTags(), "Список тегов не должен быть null");
        Assertions.assertEquals(apiPet.getTags().size(), savedPet.getTags().size(), "Количество тегов должно совпадать");

        List<Long> savedTagIds = savedPet.getTags().stream().map(TagEntity::getExternalId).collect(Collectors.toList());
        List<Long> apiTagIds = apiPet.getTags().stream().map(Tag::getId).collect(Collectors.toList());
        Assertions.assertTrue(savedTagIds.containsAll(apiTagIds), "Теги должны содержать все ID из API");
    }

    @Step("Проверка конвертации PetEntity обратно в модель API")
    public void verifyConversionToApiModel(PetEntity savedPet, Pet originalPet) {
        Pet convertedPet = petService.convertToApiModel(savedPet);
        Assertions.assertEquals(originalPet.getId(), convertedPet.getId(), "ID должны совпадать");
        Assertions.assertEquals(originalPet.getName(), convertedPet.getName(), "Имена должны совпадать");
        Assertions.assertEquals(originalPet.getStatus().name().toUpperCase(), convertedPet.getStatus().name().toUpperCase(), "Статусы должны совпадать");
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }
}