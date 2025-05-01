package org.ecom.mypetstore.repository;

import org.ecom.mypetstore.model.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    // Дополнительные методы при необходимости
    // Например, поиск по имени:
    //CategoryEntity findByName(String name);
    Optional<CategoryEntity> findByName(String name);
    Optional<CategoryEntity> findByExternalId(Long externalId);
}