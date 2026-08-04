package edu.snhu.cs499.model;

import edu.snhu.cs499.validation.Validation;
import java.util.Objects;

/** Represents one contact while preserving the original CS 320 field limits. */
public final class Contact {
    public static final int MAX_NAME_LENGTH = 10;
    public static final int MAX_ADDRESS_LENGTH = 30;

    private final String contactId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public Contact(String contactId, String firstName, String lastName, String phone, String address) {
        this.contactId = Validation.identifier(contactId, "Contact ID");
        this.firstName = Validation.requiredText(firstName, "First name", MAX_NAME_LENGTH);
        this.lastName = Validation.requiredText(lastName, "Last name", MAX_NAME_LENGTH);
        this.phone = Validation.phone(phone);
        this.address = Validation.requiredText(address, "Address", MAX_ADDRESS_LENGTH);
    }

    public Contact(Contact source) {
        this(source.contactId, source.firstName, source.lastName, source.phone, source.address);
    }

    public String getContactId() {
        return contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setFirstName(String firstName) {
        this.firstName = Validation.requiredText(firstName, "First name", MAX_NAME_LENGTH);
    }

    public void setLastName(String lastName) {
        this.lastName = Validation.requiredText(lastName, "Last name", MAX_NAME_LENGTH);
    }

    public void setPhone(String phone) {
        this.phone = Validation.phone(phone);
    }

    public void setAddress(String address) {
        this.address = Validation.requiredText(address, "Address", MAX_ADDRESS_LENGTH);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Contact contact)) {
            return false;
        }
        return contactId.equals(contact.contactId)
                && firstName.equals(contact.firstName)
                && lastName.equals(contact.lastName)
                && phone.equals(contact.phone)
                && address.equals(contact.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId, firstName, lastName, phone, address);
    }
}
