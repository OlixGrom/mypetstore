package org.ecom.mypetstore.repository;

import org.ecom.mypetstore.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    // Поиск тега по имени
    Optional<TagEntity> findByName(String name);
}
