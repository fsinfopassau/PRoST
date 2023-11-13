package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.User;
import de.unipassau.fim.fsinfo.kdv.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.service.UserAuthService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  UserAuthService userAuthService;

  @Autowired
  UserRepository userRepository;

  @GetMapping("/")
  public ResponseEntity<List<User>> list() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestBody User user) {

    if (userRepository.findByName(user.getUsername()) != null) {
      return ResponseEntity.badRequest().body("Username already exists!");
    }

    try {
      userRepository.save(user);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.ok(String.valueOf(user.getId()));
  }

}
