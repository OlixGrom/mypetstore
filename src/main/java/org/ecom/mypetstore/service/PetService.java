package org.ecom.mypetstore.service;

import org.ecom.mypetstore.client.PetStoreApiClient;
import org.ecom.mypetstore.mapper.PetMapper;
import org.ecom.mypetstore.model.entity.CategoryEntity;
import org.ecom.mypetstore.model.entity.PetEntity;
import org.ecom.mypetstore.model.entity.TagEntity;
import org.ecom.mypetstore.model.external.Category;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.model.external.Tag;
import org.ecom.mypetstore.repository.CategoryRepository;
import org.ecom.mypetstore.repository.PetRepository;
import org.ecom.mypetstore.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
public class PetService {
    private static final Logger logger = LoggerFactory.getLogger(PetService.class);
    private final PetStoreApiClient apiClient;
    private final PetRepository petRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PetMapper petMapper;

    @Autowired
    public PetService(PetStoreApiClient apiClient, PetRepository petRepository, PetMapper petMapper,
                      CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.apiClient = apiClient;
        this.petRepository = petRepository;
        this.petMapper = petMapper;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    // Метод для получения питомца по ID из внешнего API
    public Pet getPetById(Long id) {
        return apiClient.getPetById(id).getBody();
    }

    public Pet convertToApiModel(PetEntity entity) {
        return petMapper.toApiModel(entity);
    }


    // Метод для добавления или обновления питомца по ID в БД
    @Transactional
    public Pet addOrUpdatePetById(Pet apiPet) {
        validatePet(apiPet); // Добавили валидацию
        return petRepository.findByExternalId(apiPet.getId())
                .map(existingPet -> updatePet(existingPet, apiPet))
                .orElseGet(() -> createPet(apiPet));
    }

    private CategoryEntity processCategory(Category category) {
        return category == null ? null
                : categoryRepository.findOrCreateCategory(category);
    }

    private List<TagEntity> processTags(List<Tag> tags) {
        return tags == null ? Collections.emptyList()
                : tagRepository.findOrCreateTags(tags);
    }

    private Pet createPet(Pet apiPet) {
        PetEntity newPet = petMapper.toPetEntity(apiPet);
        newPet.setCategory(processCategory(apiPet.getCategory()));
        newPet.setTags(processTags(apiPet.getTags()));
        petRepository.save(newPet);
        logger.info("Created new pet with id: {}", apiPet.getId());
        return apiPet;
    }

    private Pet updatePet(PetEntity existingPet, Pet apiPet) {
        petMapper.updatePetEntity(existingPet, apiPet);
        existingPet.setCategory(processCategory(apiPet.getCategory()));//-------------------------
        existingPet.setTags(processTags(apiPet.getTags()));
        petRepository.save(existingPet);
        logger.info("Updated pet with id: {}", apiPet.getId());
        return apiPet;
    }

    private void validatePet(Pet pet) {
        if (pet == null || pet.getId() == null) {
            throw new IllegalArgumentException("Pet or pet ID must not be null");
        }
        if (pet.getCategory() == null) {
            throw new IllegalArgumentException("Pet category must not be null");
        }
        if (pet.getTags() == null) {
            pet.setTags(Collections.emptyList());
        }
    }
}