package com.example.phonebackend;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="number")
    private String number;

    @Column(name="email")
    private String email;

    @Column(name = "imei")
    private String imei;


    public User() {}

    public User(String name, String number, String email, String imei) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.imei = imei;
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getImei() {
        return imei;
    }

    // SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
