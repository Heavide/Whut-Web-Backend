package com.app.backend.service;

import com.app.backend.dto.ContactRequest;
import com.app.backend.model.Contact;

import java.util.List;

public interface ContactService {
    List<Contact> getContactsByUserId(Long userId);
    List<Contact> searchContactsByUserId(Long userId, String query);
    Contact getContactById(Long id);
    Contact createContact(ContactRequest contactRequest);
    Contact updateContact(ContactRequest contactRequest);
    boolean deleteContact(Long id);
}