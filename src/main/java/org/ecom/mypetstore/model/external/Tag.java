package org.ecom.mypetstore.model.external;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Tag {
    private Long id;
    private String name;
}
