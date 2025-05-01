package org.ecom.mypetstore.model.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "tags")
public class TagEntity {
    @Id
    private Long id;
    private String name;  // Добавили поле (в API-модели Tag его нет, но в БД может пригодиться)
}