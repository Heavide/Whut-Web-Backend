package com.app.backend.service.impl;

import com.app.backend.dto.ContactRequest;
import com.app.backend.mapper.ContactMapper;
import com.app.backend.model.Contact;
import com.app.backend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactMapper contactMapper;

    @Override
    public List<Contact> getContactsByUserId(Long userId) {
        return contactMapper.findByUserId(userId);
    }

    @Override
    public List<Contact> searchContactsByUserId(Long userId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getContactsByUserId(userId);
        }
        return contactMapper.searchByUserId(userId, query);
    }

    @Override
    public Contact getContactById(Long id) {
        return contactMapper.findById(id);
    }

    @Override
    @Transactional
    public Contact createContact(ContactRequest contactRequest) {
        Contact contact = new Contact();
        contact.setName(contactRequest.getName());
        contact.setCity(contactRequest.getCity());
        contact.setProvince(contactRequest.getProvince());
        contact.setAddress(contactRequest.getAddress());
        contact.setZipcode(contactRequest.getZipcode());
        contact.setUserId(contactRequest.getUserId());
        contact.setContactDate(contactRequest.getContactDate());

        int result = contactMapper.insert(contact);
        if (result <= 0) {
            throw new RuntimeException("创建联系人失败");
        }

        return contactMapper.findById(contact.getId());
    }

    @Override
    @Transactional
    public Contact updateContact(ContactRequest contactRequest) {
        Contact existingContact = contactMapper.findById(contactRequest.getId());
        if (existingContact == null) {
            throw new RuntimeException("联系人不存在");
        }

        existingContact.setName(contactRequest.getName());
        existingContact.setCity(contactRequest.getCity());
        existingContact.setProvince(contactRequest.getProvince());
        existingContact.setAddress(contactRequest.getAddress());
        existingContact.setZipcode(contactRequest.getZipcode());
        existingContact.setContactDate(contactRequest.getContactDate());

        int result = contactMapper.update(existingContact);
        if (result <= 0) {
            throw new RuntimeException("更新联系人失败");
        }

        return contactMapper.findById(existingContact.getId());
    }

    @Override
    @Transactional
    public boolean deleteContact(Long id) {
        Contact contact = contactMapper.findById(id);
        if (contact == null) {
            throw new RuntimeException("联系人不存在");
        }
        return contactMapper.deleteById(id) > 0;
    }
}