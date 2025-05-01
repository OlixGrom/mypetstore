package org.ecom.mypetstore.repository;


import org.ecom.mypetstore.model.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
    //@EntityGraph(attributePaths = {"photoUrls", "tags"})
    Optional<PetEntity> findByExternalId(Long externalId);
}
