package com.example.phonebackend;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userRepository.save(user);
        return ResponseEntity.ok("User added successfully");
    }
    @GetMapping("/check/{imei}")
    public ResponseEntity<Boolean> checkUserExist(@PathVariable String imei) {
        boolean exists = userRepository.existsByImei(imei);
        return ResponseEntity.ok(exists);
    }


}
