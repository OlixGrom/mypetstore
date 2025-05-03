package org.ecom.mypetstore.utils;

import org.ecom.mypetstore.model.external.Category;
import org.ecom.mypetstore.model.external.Pet;
import org.ecom.mypetstore.model.external.Tag;
import org.ecom.mypetstore.enums.PetStatus;
import java.util.Arrays;

public class PetDataProvider {

    // Метод для создания тестового питомца с категориями, тегами и фото
    public static Pet createSamplePet() {
        Pet pet = new Pet();
        pet.setId(1000L); // Например, это внешний ID
        pet.setName("Tima");
        pet.setStatus(PetStatus.AVAILABLE);

        // Устанавливаем категорию
        Category category = new Category();
        category.setId(1L);
        category.setName("home1");
        pet.setCategory(category);

        // Устанавливаем фото
        pet.setPhotoUrls(Arrays.asList("yes"));

        // Устанавливаем теги
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("lalala");
        pet.setTags(Arrays.asList(tag));

        return pet;
    }

    // Метод для создания питомца
    public static Pet createPetForUpdate() {
        Pet pet = new Pet();
        pet.setId(902L);
        pet.setName("Мурзик");
        pet.setStatus(PetStatus.AVAILABLE);

        // Аналогично создаем категорию и теги для обновления
        Category category = new Category();
        category.setId(1L);
        category.setName("home");
        pet.setCategory(category);

        Tag tag = new Tag();
        tag.setId(2L);
        tag.setName("pampam");
        pet.setTags(Arrays.asList(tag));

        pet.setPhotoUrls(Arrays.asList("photo1", "photo2"));

        return pet;
    }
}
