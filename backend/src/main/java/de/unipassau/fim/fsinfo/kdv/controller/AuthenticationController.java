package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dto.AuthorizedKdVUserDTO;
import de.unipassau.fim.fsinfo.kdv.service.AuthenticationService;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
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

  private final UserService userService;
  private final AuthenticationService authService;

  @Autowired
  public AuthenticationController(UserService userService, AuthenticationService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  @GetMapping
  public ResponseEntity<AuthorizedKdVUserDTO> auth(Authentication authentication) {
    Optional<AuthorizedKdVUserDTO> user = authService.auth(authentication);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

}
