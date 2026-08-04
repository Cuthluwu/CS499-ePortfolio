package edu.snhu.cs499.service;

import edu.snhu.cs499.exception.DuplicateRecordException;
import edu.snhu.cs499.exception.RecordNotFoundException;
import edu.snhu.cs499.model.Contact;
import edu.snhu.cs499.repository.RecordRepository;
import edu.snhu.cs499.validation.Validation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Manages contacts without exposing the mutable records stored internally. */
public final class ContactService implements RecordRepository<Contact> {
    private final Map<String, Contact> contacts = new HashMap<>();

    @Override
    public void add(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null.");
        }
        String identifier = contact.getContactId();
        if (contacts.containsKey(identifier)) {
            throw new DuplicateRecordException("Contact", identifier);
        }
        contacts.put(identifier, new Contact(contact));
    }

    @Override
    public Optional<Contact> findById(String identifier) {
        String normalized = Validation.identifier(identifier, "Contact ID");
        return Optional.ofNullable(contacts.get(normalized)).map(Contact::new);
    }

    @Override
    public void deleteById(String identifier) {
        String normalized = Validation.identifier(identifier, "Contact ID");
        if (contacts.remove(normalized) == null) {
            throw new RecordNotFoundException("Contact", normalized);
        }
    }

    public void updateName(String identifier, String firstName, String lastName) {
        Contact updated = new Contact(requireStored(identifier));
        updated.setFirstName(firstName);
        updated.setLastName(lastName);
        contacts.put(updated.getContactId(), updated);
    }

    public void updateFirstName(String identifier, String firstName) {
        Contact current = requireStored(identifier);
        updateName(current.getContactId(), firstName, current.getLastName());
    }

    public void updateLastName(String identifier, String lastName) {
        Contact current = requireStored(identifier);
        updateName(current.getContactId(), current.getFirstName(), lastName);
    }

    public void updatePhone(String identifier, String phone) {
        Contact updated = new Contact(requireStored(identifier));
        updated.setPhone(phone);
        contacts.put(updated.getContactId(), updated);
    }

    public void updateAddress(String identifier, String address) {
        Contact updated = new Contact(requireStored(identifier));
        updated.setAddress(address);
        contacts.put(updated.getContactId(), updated);
    }

    @Override
    public int size() {
        return contacts.size();
    }

    private Contact requireStored(String identifier) {
        String normalized = Validation.identifier(identifier, "Contact ID");
        Contact contact = contacts.get(normalized);
        if (contact == null) {
            throw new RecordNotFoundException("Contact", normalized);
        }
        return contact;
    }
}
