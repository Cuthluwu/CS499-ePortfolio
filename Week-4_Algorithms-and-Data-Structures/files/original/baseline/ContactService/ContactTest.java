import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class ContactTest {

    @Test
    public void testContactCreatedWithValidFields() {
        Contact contact = new Contact("CUTH01", "Madison", "Parker", "2075551234", "13 Moonlit Way");

        assertEquals("CUTH01", contact.getContactId());
        assertEquals("Madison", contact.getFirstName());
        assertEquals("Parker", contact.getLastName());
        assertEquals("2075551234", contact.getPhone());
        assertEquals("13 Moonlit Way", contact.getAddress());
    }

    @Test
    public void testContactIdAcceptsTenCharacters() {
        Contact contact = new Contact("1234567890", "Ada", "Lovelace", "1112223333", "Code Hall");

        assertEquals("1234567890", contact.getContactId());
    }

    @Test
    public void testContactIdCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact(null, "Ada", "Lovelace", "1112223333", "Code Hall"));
    }

    @Test
    public void testContactIdCannotBeLongerThanTenCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("CONTACT1000", "Ada", "Lovelace", "1112223333", "Code Hall"));
    }

    @Test
    public void testContactIdIsNotUpdatable() {
        for (Method method : Contact.class.getDeclaredMethods()) {
            assertNotEquals("setContactId", method.getName());
        }
    }

    @Test
    public void testFirstNameAcceptsTenCharacters() {
        Contact contact = new Contact("C001", "TenLetters", "Parker", "2075551234", "13 Moonlit Way");

        assertEquals("TenLetters", contact.getFirstName());
    }

    @Test
    public void testFirstNameCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C001", null, "Parker", "2075551234", "13 Moonlit Way"));
    }

    @Test
    public void testFirstNameCannotBeLongerThanTenCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C001", "MadisonRose", "Parker", "2075551234", "13 Moonlit Way"));
    }

    @Test
    public void testLastNameAcceptsTenCharacters() {
        Contact contact = new Contact("C002", "Madison", "Blackthorn", "2075551234", "13 Moonlit Way");

        assertEquals("Blackthorn", contact.getLastName());
    }

    @Test
    public void testLastNameCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C002", "Madison", null, "2075551234", "13 Moonlit Way"));
    }

    @Test
    public void testLastNameCannotBeLongerThanTenCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C002", "Madison", "ParkerStone", "2075551234", "13 Moonlit Way"));
    }

    @Test
    public void testPhoneMustBeExactlyTenDigits() {
        Contact contact = new Contact("C003", "Madison", "Parker", "2075551234", "13 Moonlit Way");

        assertEquals("2075551234", contact.getPhone());
    }

    @Test
    public void testPhoneCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C003", "Madison", "Parker", null, "13 Moonlit Way"));
    }

    @Test
    public void testPhoneCannotBeShorterThanTenDigits() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C003", "Madison", "Parker", "207555123", "13 Moonlit Way"));
    }

    @Test
    public void testPhoneCannotBeLongerThanTenDigits() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C003", "Madison", "Parker", "20755512345", "13 Moonlit Way"));
    }

    @Test
    public void testPhoneCannotContainLetters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C003", "Madison", "Parker", "20755A1234", "13 Moonlit Way"));
    }

    @Test
    public void testAddressAcceptsThirtyCharacters() {
        String address = "123456789012345678901234567890";
        Contact contact = new Contact("C004", "Madison", "Parker", "2075551234", address);

        assertEquals(address, contact.getAddress());
    }

    @Test
    public void testAddressCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C004", "Madison", "Parker", "2075551234", null));
    }

    @Test
    public void testAddressCannotBeLongerThanThirtyCharacters() {
        assertThrows(IllegalArgumentException.class, () ->
            new Contact("C004", "Madison", "Parker", "2075551234", "This address is definitely too long"));
    }

    @Test
    public void testUpdatableFieldsStillUseValidation() {
        Contact contact = new Contact("C005", "Madison", "Parker", "2075551234", "13 Moonlit Way");

        assertThrows(IllegalArgumentException.class, () -> contact.setFirstName(null));
        assertThrows(IllegalArgumentException.class, () -> contact.setLastName("MoreThanTenLetters"));
        assertThrows(IllegalArgumentException.class, () -> contact.setPhone("bad-phone"));
        assertThrows(IllegalArgumentException.class, () -> contact.setAddress("This address goes past the thirty character limit."));
    }
}
