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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
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

    @Transactional
    public Pet addPetById(Long id) {
        Pet apiPet = getPetById(id);
        savePetToDatabase(apiPet);
        return apiPet;
    }

        public Pet convertToApiModel(PetEntity entity) {
        return petMapper.toApiModel(entity);
    }

    // Метод для добавления или обновления питомца по ID
    @Transactional
    public Pet addOrUpdatePetById(Pet apiPet) {
        // Ищем питомца в базе данных по external_id
        Optional<PetEntity> existingPet = petRepository.findByExternalId(apiPet.getId());

        if (existingPet.isPresent()) {
            // Если питомец с таким external_id найден, обновляем его информацию
            PetEntity petEntity = existingPet.get();
            updatePetEntity(petEntity, apiPet);  // Обновляем поля сущности
            petRepository.save(petEntity);  // Сохраняем обновленную сущность
        } else {
            // Если питомца с таким external_id нет, создаем новый
            savePetToDatabase(apiPet);  // Вставляем новый питомец
        }

        return apiPet;  // Возвращаем API модель питомца
    }

    // Метод для обновления информации о питомце в сущности PetEntity
    private void updatePetEntity(PetEntity petEntity, Pet apiPet) {
        petEntity.setName(apiPet.getName());
        petEntity.setStatus(apiPet.getStatus());
        petEntity.setPhotoUrls(apiPet.getPhotoUrls());

        // Сохраняем или обновляем категорию
        CategoryEntity categoryEntity = categoryRepository
                .findByName(apiPet.getCategory().getName())
                .orElseGet(() -> categoryRepository.save(petMapper.toCategoryEntity(apiPet.getCategory())));

        // Сохраняем или обновляем теги
        List<TagEntity> tagEntities = apiPet.getTags().stream()
                .map(tag -> tagRepository.findByName(tag.getName())
                        .orElseGet(() -> tagRepository.save(petMapper.toTagEntity(tag))))
                .collect(Collectors.toList());

        petEntity.setCategory(categoryEntity);
        petEntity.setTags(tagEntities);
    }

    // Преобразование API модели в сущность PetEntity
    public PetEntity toPetEntity(Pet apiPet) {
        PetEntity entity = new PetEntity();
        entity.setId(null);
        entity.setName(apiPet.getName());
        entity.setStatus(apiPet.getStatus());
        entity.setPhotoUrls(apiPet.getPhotoUrls());
        entity.setExternalId(apiPet.getId());
        entity.setCategory(toCategoryEntity(apiPet.getCategory()));
        entity.setTags(mapTagsToEntities(apiPet.getTags()));
        return entity;
    }

    // Преобразование тега в сущность
    public List<TagEntity> mapTagsToEntities(List<Tag> tags) {
        return tags.stream().map(tag -> {
            TagEntity tagEntity = new TagEntity();
            tagEntity.setId(tag.getId());
            tagEntity.setName(tag.getName());
            return tagEntity;
        }).collect(Collectors.toList());
    }

    // Преобразование категории в сущность
    public CategoryEntity toCategoryEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(null); // id генерируется автоматически
        entity.setName(category.getName());
        entity.setExternalId(category.getId());
        return entity;
    }

    /**
     * Сохраняет питомца, категорию и теги, избегая дубликатов.
     */
    public void savePetToDatabase(Pet apiPet) {
        // 1. Сохраняем категорию (если её нет)
        CategoryEntity categoryEntity = categoryRepository
                .findByExternalId(apiPet.getId())
                .orElseGet(() -> {
                    CategoryEntity newCategory = petMapper.toCategoryEntity(apiPet.getCategory());
                    return categoryRepository.save(newCategory);
                });

        // 2. Сохраняем теги (если их нет)
        List<TagEntity> tagEntities = apiPet.getTags().stream()
                .map(tag -> tagRepository.findByName(tag.getName())
                        .orElseGet(() -> tagRepository.save(petMapper.toTagEntity(tag))))
                .collect(Collectors.toList());

        // 3. Сохраняем питомца
        PetEntity petEntity = petMapper.toPetEntity(apiPet);
        petEntity.setCategory(categoryEntity);
        petEntity.setTags(tagEntities);

        petRepository.save(petEntity);
    }
}