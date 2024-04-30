package de.unipassau.fim.fsinfo.prost.service;

import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dto.AuthorizedPRoSTUserDTO;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final UserRepository userRepository;
  private final UserService userService;

  @Autowired
  public AuthenticationService(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  public Optional<AuthorizedPRoSTUserDTO> auth(Authentication auth) {
    Optional<UserAccessRole> role = getHighestRole(getRoles(auth));

    if (role.isEmpty()) {
      return Optional.empty();
    }

    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Optional<ProstUser> user = userRepository.findById(userDetails.getUsername());

    if (user.isPresent()) {
      return Optional.of(new AuthorizedPRoSTUserDTO(user.get(), role.get()));
    }

    user = userService.createUser(userDetails.getUsername(),
        userDetails.getDisplayName(),
        userDetails.getEmail());

    return user.map(prostUser -> new AuthorizedPRoSTUserDTO(prostUser, role.get()));
  }

  public Optional<UserAccessRole> getHighestRole(Collection<UserAccessRole> roles) {
    if (roles == null || roles.isEmpty()) {
      return Optional.empty();
    }

    AtomicReference<UserAccessRole> highest = new AtomicReference<>(roles.iterator().next());

    roles.forEach(role -> {
      if (highest.get().compareTo(role) < 0) {
        highest.set(role);
      }
    });

    return Optional.of(highest.get());
  }

  public Collection<UserAccessRole> getRoles(Authentication authentication) {
    if (authentication == null) {
      System.err.println(
          "[AC] :: No null-Authentication!");
      return new ArrayList<>();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    List<UserAccessRole> userRoles = new ArrayList<>();

    authentication.getAuthorities().forEach(authority -> {
      try {
        UserAccessRole role = UserAccessRole.valueOf(authority.getAuthority());
        userRoles.add(role);
      } catch (IllegalArgumentException e) {
        System.err.println(
            "[AC] :: Could not map authority " + authority.getAuthority()
                + " to UserAccessRole of User " + userDetails.getUsername() + "!");
      }
    });

    return userRoles;
  }

}
