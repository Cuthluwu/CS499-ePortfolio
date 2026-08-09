package edu.snhu.cs499.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Contact;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactServiceTest {
    private ContactService service;

    @BeforeEach
    void setUp() {
        service = new ContactService();
    }

    @Test
    void addStoresCopyAndRejectsDuplicateId() {
        Contact contact = contact("C100");
        service.add(contact);
        contact.setAddress("22 Pine St");

        assertEquals("13 Moonlit Way", service.findById("C100").orElseThrow().getAddress());
        assertThrows(DuplicateRecordException.class, () -> service.add(contact("C100")));
        assertEquals(1, service.size());
    }

    @Test
    void lookupReturnsCopyAndUsesOptionalForMissingId() {
        service.add(contact("C101"));
        Contact result = service.findById("C101").orElseThrow();
        result.setFirstName("June");

        assertEquals("Madison", service.findById("C101").orElseThrow().getFirstName());
        assertFalse(service.findById("C999").isPresent());
    }

    @Test
    void updateAndDeleteUseSpecificFailurePolicy() {
        service.add(contact("C102"));
        service.updateName("C102", "June", "Harlow");
        service.updatePhone("C102", "2075559999");

        Contact updated = service.findById("C102").orElseThrow();
        assertEquals("June", updated.getFirstName());
        assertEquals("2075559999", updated.getPhone());

        service.deleteById("C102");
        assertEquals(0, service.size());
        assertThrows(RecordNotFoundException.class, () -> service.deleteById("C102"));
    }

    @Test
    void publicOperationsRejectBlankIdentifiers() {
        assertThrows(IllegalArgumentException.class, () -> service.findById(" "));
        assertThrows(IllegalArgumentException.class, () -> service.deleteById(" "));
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress(" ", "22 Pine St"));
    }

    @Test
    void multiFieldUpdateIsAtomicWhenValidationFails() {
        service.add(contact("C108"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateName("C108", "June", "TooManyLetters"));

        Contact unchanged = service.findById("C108").orElseThrow();
        assertEquals("Madison", unchanged.getFirstName());
        assertEquals("Parker", unchanged.getLastName());
    }

    @Test
    void lastNameSearchIsCaseInsensitiveAndDeterministicallySorted() {
        service.add(new Contact("C203", "Zoe", "Baker", "2075552003", "3 Main St"));
        service.add(new Contact("C201", "Madison", "Parker", "2075552001", "1 Main St"));
        service.add(new Contact("C202", "Ada", "Baker", "2075552002", "2 Main St"));
        service.add(new Contact("C204", "June", "Harlow", "2075552004", "4 Main St"));

        List<String> identifiers = service.searchByLastName("KeR").stream()
                .map(Contact::getContactId)
                .toList();

        assertIterableEquals(List.of("C202", "C203", "C201"), identifiers);
    }

    @Test
    void lastNameSearchReturnsImmutableSnapshots() {
        service.add(contact("C205"));
        List<Contact> results = service.searchByLastName("parker");
        results.get(0).setFirstName("June");

        assertEquals("Madison", service.findById("C205").orElseThrow().getFirstName());
        assertThrows(UnsupportedOperationException.class, () -> results.add(contact("C206")));
        assertEquals(0, service.searchByLastName("missing").size());
        assertThrows(IllegalArgumentException.class, () -> service.searchByLastName(" "));
    }

    private Contact contact(String identifier) {
        return new Contact(identifier, "Madison", "Parker", "2075551000", "13 Moonlit Way");
    }
}
