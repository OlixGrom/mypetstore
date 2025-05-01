package org.ecom.mypetstore.model.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.experimental.Accessors;
import org.ecom.mypetstore.enums.PetStatus;
import org.ecom.mypetstore.utils.PetStatusDeserializer;

import java.util.List;

@Data
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;

    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = PetStatusDeserializer.class)
    private PetStatus status;
}
