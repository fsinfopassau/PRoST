package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.UserRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  UserRepository userRepository;

  /**
   * List all Users
   *
   * @return List of all Users
   */
  @GetMapping
  public ResponseEntity<List<User>> list() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  /**
   * Create a new User
   *
   * @param user with a unique username
   * @return the ID of the new user - nothing if User can not be created
   */
  @PostMapping
  public ResponseEntity<String> create(@RequestBody User user) {

    if (userRepository.findById(user.getUsername()).isPresent()) {
      return ResponseEntity.badRequest().build();
    }

    try {
      userRepository.save(user);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.ok(user.getUsername());
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable String id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    }

    return ResponseEntity.badRequest().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<User> delete(@PathVariable String id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      userRepository.delete(user.get());
      return ResponseEntity.ok(user.get());
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/balance")
  public ResponseEntity<Double> balance(@PathVariable String id, @RequestBody String value) {
    Optional<User> user = userRepository.findById(id);

    try {
      Double d = Double.parseDouble(value);
      if (user.isPresent()) {
        user.get().setBalance(d);
        userRepository.save(user.get());
        return ResponseEntity.ok(user.get().getBalance());
      }
    } catch (NumberFormatException e) {
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/enable")
  public ResponseEntity<String> enable(@PathVariable String id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(true);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/disable")
  public ResponseEntity<String> disable(@PathVariable String id) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setEnabled(false);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/{id}/role")
  public ResponseEntity<String> role(@PathVariable String id, @RequestBody UserRole role) {
    Optional<User> user = userRepository.findById(id);

    if (user.isPresent()) {
      user.get().setRole(role);
      userRepository.save(user.get());
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.badRequest().build();
  }

}
