package de.unipassau.fim.fsinfo.kdv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

  @GetMapping
  public ResponseEntity<String> auth() {
    return ResponseEntity.ok("Alles Klärchen Bärchen. \uD83D\uDC4D");
  }

}
