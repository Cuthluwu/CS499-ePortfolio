package edu.snhu.cs499.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ContactTest {
    @Test
    void constructorNormalizesRequiredText() {
        Contact contact = new Contact(" C100 ", " Madison ", " Parker ", "2075551000", " 13 Moonlit Way ");

        assertEquals("C100", contact.getContactId());
        assertEquals("Madison", contact.getFirstName());
        assertEquals("Parker", contact.getLastName());
        assertEquals("13 Moonlit Way", contact.getAddress());
    }

    @Test
    void blankRequiredFieldsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Contact(" ", "Madison", "Parker", "2075551000", "13 Moonlit Way"));
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C101", " ", "Parker", "2075551000", "13 Moonlit Way"));
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C101", "Madison", "Parker", "2075551000", " "));
    }

    @Test
    void originalFieldLimitsRemainEnforced() {
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("12345678901", "Madison", "Parker", "2075551000", "13 Moonlit Way"));
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C102", "MadisonRose", "Parker", "2075551000", "13 Moonlit Way"));
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C102", "Madison", "ParkerStone", "2075551000", "13 Moonlit Way"));
    }

    @Test
    void phoneRequiresExactlyTenDigits() {
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C103", "Madison", "Parker", "207-555-1000", "13 Moonlit Way"));
        assertThrows(IllegalArgumentException.class,
                () -> new Contact("C103", "Madison", "Parker", "207555100", "13 Moonlit Way"));
    }

    @Test
    void copyCanChangeWithoutChangingSource() {
        Contact source = new Contact("C104", "Madison", "Parker", "2075551000", "13 Moonlit Way");
        Contact copy = new Contact(source);

        copy.setAddress("22 Pine St");

        assertEquals("13 Moonlit Way", source.getAddress());
        assertNotEquals(source, copy);
    }
}
