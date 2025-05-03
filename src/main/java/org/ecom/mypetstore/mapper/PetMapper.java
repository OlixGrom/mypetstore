package org.ecom.mypetstore.mapper;

import org.ecom.mypetstore.model.entity.CategoryEntity;
import org.ecom.mypetstore.model.entity.PetEntity;
import org.ecom.mypetstore.model.entity.TagEntity;
import org.ecom.mypetstore.model.external.Category;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.model.external.Tag;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// PetMapper.java (в пакете org.ecom.mypetstore.mapper)
@Component
public class PetMapper {

    public PetEntity toPetEntity(Pet apiModel) {
        if (apiModel == null) return null;

        PetEntity entity = new PetEntity();
        entity.setId(null);
        entity.setName(apiModel.getName());
        entity.setStatus(apiModel.getStatus());
        entity.setPhotoUrls(apiModel.getPhotoUrls());
        entity.setExternalId(apiModel.getId());
        entity.setCategory(null);
        return entity;
    }

    public CategoryEntity toCategoryEntity(Category category) {
        if (category == null) return null;

        CategoryEntity entity = new CategoryEntity();
        entity.setId(null);
        entity.setName(category.getName());
        entity.setExternalId(category.getId());
        return entity;
    }

    public TagEntity toTagEntity(Tag tag) {
        if (tag == null) return null;
        return new TagEntity()
                .setExternalId(tag.getId())  // Устанавливаем только externalId
                .setName(tag.getName());
    }


    public Pet toApiModel(PetEntity entity) {
        if (entity == null) return null;

        Pet pet = new Pet();
        pet.setId(entity.getExternalId());
        pet.setName(entity.getName());
        pet.setStatus(entity.getStatus());

        if (entity.getCategory() != null) {
            Category category = new Category();
            //category.setId(entity.getCategory().getId());
            category.setId(entity.getCategory().getExternalId());
            category.setName(entity.getCategory().getName());
            pet.setCategory(category);
        }

        pet.setTags(mapTagsToApi(entity.getTags()));

        return pet;
    }

    public void updatePetEntity(PetEntity existingEntity, Pet apiModel) {
        if (apiModel == null || existingEntity == null) return;

        existingEntity.setName(apiModel.getName());
        existingEntity.setStatus(apiModel.getStatus());
        existingEntity.setPhotoUrls(apiModel.getPhotoUrls());
    }

    private List<Tag> mapTagsToApi(List<TagEntity> tagEntities) {
        if (tagEntities == null) return Collections.emptyList();

        return tagEntities.stream()
                .map(te -> new Tag()
                        .setId(te.getExternalId())  // Используем externalId
                        .setName(te.getName()))
                .collect(Collectors.toList());
    }

}
