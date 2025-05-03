package org.ecom.mypetstore.repository;

import org.ecom.mypetstore.model.entity.CategoryEntity;
import org.ecom.mypetstore.model.external.Category;
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

    default CategoryEntity findOrCreateCategory(Category category) {
        String name = category.getName() != null ? category.getName() : "";
        return findByExternalId(category.getId())
                .map(existingCategory -> {
                    existingCategory.setName(name); // <--- обновляем имя
                    return save(existingCategory);   // сохраняем изменения
                })
                .orElseGet(() -> {
                    CategoryEntity newCategory = new CategoryEntity();
                    newCategory.setExternalId(category.getId());
                    newCategory.setName(name);
                    return save(newCategory);
                });
    }
}