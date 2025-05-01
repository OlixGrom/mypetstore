package org.ecom.mypetstore.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.ecom.mypetstore.enums.PetStatus;

import java.util.List;

@Data
@Entity
@Table(name = "pets")
public class PetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "external_id", unique = true)
    private Long externalId;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(name = "pet_photos", joinColumns = @JoinColumn(name = "pet_id"))
    private List<String> photoUrls;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pet_tags",
            joinColumns = @JoinColumn(name = "pet_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<TagEntity> tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PetStatus status;
}
