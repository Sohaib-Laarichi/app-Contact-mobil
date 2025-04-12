package com.example.phonebackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addContact(@RequestBody Contact contact) {
        if (contactRepository.existsByNumber(contact.getNumber())) {
            return ResponseEntity.ok("Contact déjà existe");
        }

        contactRepository.save(contact);
        return ResponseEntity.ok("Contact ajouté avec succès");
    }
}
