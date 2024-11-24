package de.unipassau.fim.fsinfo.prost.controller;

import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import de.unipassau.fim.fsinfo.prost.service.AuthenticationService;
import de.unipassau.fim.fsinfo.prost.service.UserService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;
  private final AuthenticationService authService;

  @Autowired
  public UserController(UserService userService, AuthenticationService auth) {
    this.userService = userService;
    this.authService = auth;
  }

  /**
   * List all Users Also show hidden Users (if access-rights allow to do so) because filtering out
   * in the frontend is easier.
   *
   * @return List of all Users
   */
  @GetMapping("/info")
  public ResponseEntity<List<ProstUser>> list(Authentication authentication,
      @RequestParam(required = false) String id) {

    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Collection<UserAccessRole> roles = authService.getRoles(authentication);

    if (userDetails.getUsername().equals(id)) {
      // always show own profile
      return userService.info(id, true).map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    } else if (roles.contains(UserAccessRole.KAFFEEKASSE) || roles.contains(UserAccessRole.KIOSK)) {
      // allow single-user access for admin & kiosk
      return userService.info(id, true).map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    return userService.info(null, false).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping("/me")
  public ResponseEntity<ProstUser> me(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    Optional<List<ProstUser>> users = userService.info(userDetails.getUsername(), true);
    return users.map(prostUsers -> ResponseEntity.ok(prostUsers.get(0)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/me/hide")
  public ResponseEntity<String> hidePersonal(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userService.setHidden(userDetails.getUsername(), true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/me/show")
  public ResponseEntity<String> showPersonal(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userService.setHidden(userDetails.getUsername(), false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/me/disable-kiosk")
  public ResponseEntity<String> disableKiosk(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userService.setKiosk(userDetails.getUsername(), false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/me/enable-kiosk")
  public ResponseEntity<String> enableKiosk(Authentication authentication) {
    if (authentication == null) {
      return ResponseEntity.badRequest().build();
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    if (userService.setKiosk(userDetails.getUsername(), true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/create")
  public ResponseEntity<ProstUser> create(@RequestBody ProstUser userTemplate) {

    Optional<ProstUser> user = userService.createUser(userTemplate.getId(),
        userTemplate.getDisplayName(),
        userTemplate.getEmail(), true);

    if (user.isPresent() && userTemplate.getTotalSpent() != null) {
      userService.setMoneySpent(user.get().getId(), userTemplate.getTotalSpent().toString());
    }

    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @DeleteMapping("/delete")
  public ResponseEntity<String> delete(@RequestParam String id) {
    if (userService.delete(id)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/name")
  public ResponseEntity<String> name(@RequestParam String id, @RequestParam String value) {
    if (userService.rename(id, value)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/email")
  public ResponseEntity<String> email(@RequestParam String id, @RequestParam String value) {
    if (userService.setEmail(id, value)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/moneySpent")
  public ResponseEntity<String> moneySpent(@RequestParam String id, @RequestParam String value) {
    if (userService.setMoneySpent(id, value)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/enable")
  public ResponseEntity<String> enable(@RequestParam String id) {
    if (userService.setEnabled(id, true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/disable")
  public ResponseEntity<String> disable(@RequestParam String id) {
    if (userService.setEnabled(id, false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/hide")
  public ResponseEntity<String> hide(@RequestParam String id) {
    if (userService.setHidden(id, true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/show")
  public ResponseEntity<String> show(@RequestParam String id) {
    if (userService.setHidden(id, false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/disable-kiosk")
  public ResponseEntity<String> disableKiosk(@RequestParam String id) {
    if (userService.setKiosk(id, false)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/enable-kiosk")
  public ResponseEntity<String> enableKiosk(@RequestParam String id) {
    if (userService.setKiosk(id, true)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

}
