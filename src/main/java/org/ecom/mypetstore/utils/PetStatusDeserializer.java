package org.ecom.mypetstore.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import org.ecom.mypetstore.enums.PetStatus;

import java.io.IOException;

public class PetStatusDeserializer extends JsonDeserializer<PetStatus> {
    @Override
    public PetStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().toUpperCase(); // Приводим к верхнему регистру
        return PetStatus.valueOf(value); // Возвращаем соответствующий элемент enum
    }
}