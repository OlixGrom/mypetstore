package org.ecom.mypetstore.repository;

import org.ecom.mypetstore.model.entity.TagEntity;
import org.ecom.mypetstore.model.external.Tag;
import org.ecom.mypetstore.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    static final Logger logger = LoggerFactory.getLogger(TagRepository.class);
    // Поиск тега по имени
    Optional<TagEntity> findByName(String name);

    Optional<TagEntity> findByExternalId(Long externalId);

    default List<TagEntity> findOrCreateTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        return tags.stream()
                .map(tag -> {
                    String name = tag.getName() != null ? tag.getName() : "";

                    return findByExternalId(tag.getId())
                            .map(existingTag -> {
                                existingTag.setName(name); // обновляем имя
                                return save(existingTag);  // сохраняем изменения
                            })
                            .orElseGet(() -> {
                                TagEntity newTag = new TagEntity()
                                        .setExternalId(tag.getId())
                                        .setName(name);
                                logger.debug("Creating new tag: {}", tag);
                                return save(newTag);
                            });
                })
                .collect(Collectors.toList());
    }
}
