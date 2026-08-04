import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContactServiceTest {
    private ContactService service;

    @BeforeEach
    public void setUp() {
        service = new ContactService();
    }

    @Test
    public void testAddContactWithUniqueId() {
        Contact contact = new Contact("C100", "Madison", "Parker", "2075551000", "13 Moonlit Way");

        service.addContact(contact);

        assertEquals(1, service.getContactCount());
        assertEquals(contact, service.getContact("C100"));
    }

    @Test
    public void testCannotAddDuplicateContactId() {
        Contact first = new Contact("C101", "Madison", "Parker", "2075551001", "13 Moonlit Way");
        Contact duplicate = new Contact("C101", "Eliza", "Maine", "2075551002", "44 Harbor Rd");

        service.addContact(first);

        assertThrows(IllegalArgumentException.class, () -> service.addContact(duplicate));
        assertEquals(1, service.getContactCount());
    }

    @Test
    public void testCannotAddNullContact() {
        assertThrows(IllegalArgumentException.class, () -> service.addContact(null));
    }

    @Test
    public void testDeleteContactById() {
        Contact contact = new Contact("C102", "June", "Harlow", "2075551003", "9 Cedar St");

        service.addContact(contact);
        service.deleteContact("C102");

        assertEquals(0, service.getContactCount());
        assertNull(service.getContact("C102"));
    }

    @Test
    public void testCannotDeleteMissingContact() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteContact("MISSING"));
    }

    @Test
    public void testUpdateFirstNameById() {
        service.addContact(new Contact("C103", "Old", "Parker", "2075551004", "13 Moonlit Way"));

        service.updateFirstName("C103", "New");

        assertEquals("New", service.getContact("C103").getFirstName());
    }

    @Test
    public void testUpdateLastNameById() {
        service.addContact(new Contact("C104", "Madison", "Old", "2075551005", "13 Moonlit Way"));

        service.updateLastName("C104", "New");

        assertEquals("New", service.getContact("C104").getLastName());
    }

    @Test
    public void testUpdatePhoneById() {
        service.addContact(new Contact("C105", "Madison", "Parker", "2075551006", "13 Moonlit Way"));

        service.updatePhone("C105", "2075559999");

        assertEquals("2075559999", service.getContact("C105").getPhone());
    }

    @Test
    public void testUpdateAddressById() {
        service.addContact(new Contact("C106", "Madison", "Parker", "2075551007", "13 Moonlit Way"));

        service.updateAddress("C106", "22 Pine St");

        assertEquals("22 Pine St", service.getContact("C106").getAddress());
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
        service.addContact(new Contact("C107", "Madison", "Parker", "2075551008", "13 Moonlit Way"));

        assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("C107", "TooManyLetters"));
        assertThrows(IllegalArgumentException.class, () -> service.updateLastName("C107", "TooManyLetters"));
        assertThrows(IllegalArgumentException.class, () -> service.updatePhone("C107", "207-555-1008"));
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress("C107", "This updated address is too long."));
    }
}
