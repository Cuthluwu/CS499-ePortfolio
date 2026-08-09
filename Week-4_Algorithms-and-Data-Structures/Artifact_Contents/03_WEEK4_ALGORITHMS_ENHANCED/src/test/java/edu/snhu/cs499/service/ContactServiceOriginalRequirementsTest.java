package edu.snhu.cs499.service;

import edu.snhu.cs499.model.Contact;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContactServiceOriginalRequirementsTest {
    private ContactService service;

    @BeforeEach
    public void setUp() {
        service = new ContactService();
    }

    @Test
    public void testAddContactWithUniqueId() {
        Contact contact = new Contact("C100", "Madison", "Parker", "2075551000", "13 Moonlit Way");

        service.add(contact);

        assertEquals(1, service.size());
        assertEquals(contact, service.findById("C100").orElse(null));
    }

    @Test
    public void testCannotAddDuplicateContactId() {
        Contact first = new Contact("C101", "Madison", "Parker", "2075551001", "13 Moonlit Way");
        Contact duplicate = new Contact("C101", "Eliza", "Maine", "2075551002", "44 Harbor Rd");

        service.add(first);

        assertThrows(IllegalArgumentException.class, () -> service.add(duplicate));
        assertEquals(1, service.size());
    }

    @Test
    public void testCannotAddNullContact() {
        assertThrows(IllegalArgumentException.class, () -> service.add(null));
    }

    @Test
    public void testDeleteContactById() {
        Contact contact = new Contact("C102", "June", "Harlow", "2075551003", "9 Cedar St");

        service.add(contact);
        service.deleteById("C102");

        assertEquals(0, service.size());
        assertTrue(service.findById("C102").isEmpty());
    }

    @Test
    public void testCannotDeleteMissingContact() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteById("MISSING"));
    }

    @Test
    public void testUpdateFirstNameById() {
        service.add(new Contact("C103", "Old", "Parker", "2075551004", "13 Moonlit Way"));

        service.updateFirstName("C103", "New");

        assertEquals("New", service.findById("C103").orElseThrow().getFirstName());
    }

    @Test
    public void testUpdateLastNameById() {
        service.add(new Contact("C104", "Madison", "Old", "2075551005", "13 Moonlit Way"));

        service.updateLastName("C104", "New");

        assertEquals("New", service.findById("C104").orElseThrow().getLastName());
    }

    @Test
    public void testUpdatePhoneById() {
        service.add(new Contact("C105", "Madison", "Parker", "2075551006", "13 Moonlit Way"));

        service.updatePhone("C105", "2075559999");

        assertEquals("2075559999", service.findById("C105").orElseThrow().getPhone());
    }

    @Test
    public void testUpdateAddressById() {
        service.add(new Contact("C106", "Madison", "Parker", "2075551007", "13 Moonlit Way"));

        service.updateAddress("C106", "22 Pine St");

        assertEquals("22 Pine St", service.findById("C106").orElseThrow().getAddress());
    }

    @Test
    public void testCannotUpdateMissingContact() {
        assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("MISSING", "Name"));
        assertThrows(IllegalArgumentException.class, () -> service.updateLastName("MISSING", "Name"));
        assertThrows(IllegalArgumentException.class, () -> service.updatePhone("MISSING", "2075550000"));
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress("MISSING", "22 Pine St"));
    }

    @Test
    public void testUpdatedFieldsStillMeetContactRules() {
        service.add(new Contact("C107", "Madison", "Parker", "2075551008", "13 Moonlit Way"));

        assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("C107", "TooManyLetters"));
        assertThrows(IllegalArgumentException.class, () -> service.updateLastName("C107", "TooManyLetters"));
        assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C107", "207-555-1008"));
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress("C107", "This updated address is too long."));
    }
}
