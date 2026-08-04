import java.util.HashMap;
import java.util.Map;

/**
 * Stores contacts in memory. There is no database or user interface for this
 * project, so a HashMap is used to enforce unique contact IDs.
 */
public class ContactService {
    private final Map<String, Contact> contacts = new HashMap<>();

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null.");
        }

        if (contacts.containsKey(contact.getContactId())) {
            throw new IllegalArgumentException("A contact with this ID already exists.");
        }

        contacts.put(contact.getContactId(), contact);
    }

    public void deleteContact(String contactId) {
        Contact contact = findContact(contactId);
        contacts.remove(contact.getContactId());
    }

    public void updateFirstName(String contactId, String firstName) {
        findContact(contactId).setFirstName(firstName);
    }

    public void updateLastName(String contactId, String lastName) {
        findContact(contactId).setLastName(lastName);
    }

    public void updatePhone(String contactId, String phone) {
        findContact(contactId).setPhone(phone);
    }

    public void updateAddress(String contactId, String address) {
        findContact(contactId).setAddress(address);
    }

    public Contact getContact(String contactId) {
        return contacts.get(contactId);
    }

    public int getContactCount() {
        return contacts.size();
    }

    private Contact findContact(String contactId) {
        Contact contact = contacts.get(contactId);

        if (contact == null) {
            throw new IllegalArgumentException("No contact was found with that ID.");
        }

        return contact;
    }
}
