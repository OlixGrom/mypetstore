package org.ecom.mypetstore.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Optional;

@Data
@Entity
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "external_id")
    private Long externalId;
}