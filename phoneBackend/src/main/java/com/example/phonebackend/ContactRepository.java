package com.example.phonebackend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsByNumber(String number);

}
