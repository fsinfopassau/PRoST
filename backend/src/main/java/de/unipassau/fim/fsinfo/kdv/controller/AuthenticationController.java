package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.security.CustomUserDetailsContextMapper.CustomUserDetails;
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
  private final UserRepository userRepository;

  @Autowired
  public AuthenticationController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @GetMapping
  public ResponseEntity<KdvUser> auth(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<KdvUser> user = userRepository.findById(userDetails.getUsername());

    if (user.isPresent()) {
      return ResponseEntity.ok(user.get());
    }

    user = userService.createUser(userDetails.getUsername(),
        userDetails.getDisplayName(),
        userDetails.getEmail());

    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

}
