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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  UserAuthService userAuthService;

  @Autowired
  UserRepository repository;

  @GetMapping("/list")
  public ResponseEntity<List<User>> list(@RequestHeader("X-API-KEY") String key) {
    if (!userAuthService.auth(key)) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(repository.findAll());
  }

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestHeader("X-API-KEY") String key,
      @RequestBody User user) {
    if (!userAuthService.auth(key)) {
      return ResponseEntity.badRequest().body("Not Allowed");
    }

    if (repository.findByName(user.getUsername()) != null) {
      return ResponseEntity.badRequest().body("Username already exists!");
    }

    try {
      repository.save(user);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.ok("" + user.getId());
  }

}
