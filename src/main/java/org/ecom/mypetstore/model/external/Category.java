package org.ecom.mypetstore.model.external;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true) // Добавляем для поддержки цепочки вызовов
public class Category {
    private Long id;
    private String name;
}
