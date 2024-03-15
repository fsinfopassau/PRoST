package de.unipassau.fim.fsinfo.kdv.controller;

import de.unipassau.fim.fsinfo.kdv.data.UserAccessRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dto.AuthorizedKdVUserDTO;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.kdv.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
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
  public ResponseEntity<AuthorizedKdVUserDTO> auth(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<KdvUser> user = userRepository.findById(userDetails.getUsername());

    AtomicReference<UserAccessRole> userRole = new AtomicReference<>(UserAccessRole.UNASSIGNED);

    authentication.getAuthorities().forEach(authority -> {
      try {
        UserAccessRole role = UserAccessRole.valueOf(authority.getAuthority());
        if (userRole.get().compareTo(role) < 0) {
          userRole.set(role);
        }
      } catch (IllegalArgumentException e) {
        System.err.println(
            "[AC] :: Could not map authority " + authority.getAuthority()
                + " to UserAccessRole of User " + userDetails.getUsername() + "!");
      }
    });

    if (user.isPresent()) {
      return ResponseEntity.ok(new AuthorizedKdVUserDTO(user.get(), userRole.get()));
    }

    user = userService.createUser(userDetails.getUsername(),
        userDetails.getDisplayName(),
        userDetails.getEmail());

    return user.map(kdvUser -> ResponseEntity.ok(
            new AuthorizedKdVUserDTO(kdvUser, userRole.get())))
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

}
