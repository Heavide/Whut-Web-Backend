package com.app.backend.controller;

import com.app.backend.common.Result;
import com.app.backend.dto.ContactRequest;
import com.app.backend.model.Contact;
import com.app.backend.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://localhost:8081")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public Result<List<Contact>> getContactsByUserId(@RequestParam("userId") Long userId,
                                                    @RequestParam(value = "query", required = false) String query) {
        try {
            Long currentUserId = userId;

            List<Contact> contacts;
            if (query != null && !query.isEmpty()) {
                contacts = contactService.searchContactsByUserId(currentUserId, query);
            } else {
                contacts = contactService.getContactsByUserId(currentUserId);
            }
            return Result.success(contacts);
        } catch (Exception e) {
            log.error("获取联系人列表失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Contact> getContactById(@PathVariable Long id, @RequestParam("userId") Long userId) {
        try {
            Contact contact = contactService.getContactById(id);
            if (contact == null) {
                return Result.fail("联系人不存在");
            }

            if (!contact.getUserId().equals(userId)) {
                return Result.fail("没有权限访问该联系人");
            }

            return Result.success(contact);
        } catch (Exception e) {
            log.error("获取联系人详情失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping
    public Result<Contact> createContact(@RequestBody ContactRequest contactRequest) {
        try {
            Contact contact = contactService.createContact(contactRequest);
            return Result.success(contact);
        } catch (Exception e) {
            log.error("创建联系人失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Contact> updateContact(@PathVariable Long id,
                                        @RequestBody ContactRequest contactRequest,
                                        @RequestParam("userId") Long userId) {
        try {
            Contact existingContact = contactService.getContactById(id);
            if (existingContact == null) {
                return Result.fail("联系人不存在");
            }

            if (!existingContact.getUserId().equals(userId)) {
                return Result.fail("没有权限更新该联系人");
            }

            contactRequest.setId(id);
            Contact contact = contactService.updateContact(contactRequest);
            return Result.success(contact);
        } catch (Exception e) {
            log.error("更新联系人失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteContact(@PathVariable Long id, @RequestParam("userId") Long userId) {
        try {
            Contact existingContact = contactService.getContactById(id);
            if (existingContact == null) {
                return Result.fail("联系人不存在");
            }

            if (!existingContact.getUserId().equals(userId)) {
                return Result.fail("没有权限删除该联系人");
            }

            boolean result = contactService.deleteContact(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除联系人失败", e);
            return Result.fail(e.getMessage());
        }
    }
}