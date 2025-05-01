package org.ecom.mypetstore.enums;

import java.util.Arrays;

public enum PetStatus {
    AVAILABLE("available"),
    PENDING("pending"),
    SOLD("sold");

    private final String value;

    PetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PetStatus fromString(String value) {
        return Arrays.stream(PetStatus.values())
                .filter(status -> status.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + value));
    }
}