package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.KdVApplication;
import de.unipassau.fim.fsinfo.kdv.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
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

  @GetMapping("/list")
  public ResponseEntity<String> list(@RequestHeader("X-API-KEY") String key) {
    if (!userAuthService.auth(key)) {
      return ResponseEntity.badRequest().body("Not Allowed");
    }

    return ResponseEntity.ok(KdVApplication.userRepository.findAll().toString());
  }

  @PostMapping("/create")
  public ResponseEntity<String> create(@RequestHeader("X-API-KEY") String key,
      @RequestBody RequestEntity<de.unipassau.fim.fsinfo.kdv.User> entity) {
    if (!userAuthService.auth(key)) {
      return ResponseEntity.badRequest().body("Not Allowed");
    }

    System.out.println(entity.getBody());

    return ResponseEntity.ok("TODO");
  }

}
