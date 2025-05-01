package org.ecom.mypetstore.mapper;

import org.ecom.mypetstore.enums.PetStatus;
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
        entity.setCategory(toCategoryEntity(apiModel.getCategory()));
        entity.setTags(mapTagsToEntities(apiModel.getTags()));

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

        TagEntity entity = new TagEntity();
        entity.setId(tag.getId());
        entity.setName(tag.getName());
        return entity;
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

    private List<TagEntity> mapTagsToEntities(List<Tag> tags) {
        if (tags == null) return Collections.emptyList();
        return tags.stream()
                .map(this::toTagEntity)
                .collect(Collectors.toList());
    }

    private List<Tag> mapTagsToApi(List<TagEntity> tagEntities) {
        if (tagEntities == null) return Collections.emptyList();
        return tagEntities.stream()
                .map(te -> {
                    Tag tag = new Tag();
                    tag.setId(te.getId());
                    tag.setName(te.getName());
                    return tag;
                })
                .collect(Collectors.toList());
    }
}
