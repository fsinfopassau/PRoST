package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.dto.AuthorizedPRoSTUserDTO;
import de.unipassau.fim.fsinfo.prost.service.AuthenticationService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

  private final AuthenticationService authService;

  @Autowired
  public AuthenticationController(AuthenticationService authService) {
    this.authService = authService;
  }

  @GetMapping
  public ResponseEntity<AuthorizedPRoSTUserDTO> auth(Authentication authentication) {
    Optional<AuthorizedPRoSTUserDTO> user = authService.auth(authentication);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

}
