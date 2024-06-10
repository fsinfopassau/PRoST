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
    if (auth == null) {
      return Optional.empty();
    }

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
        userDetails.getEmail(), false);

    return user.map(prostUser -> new AuthorizedPRoSTUserDTO(prostUser, role.get()));
  }

  public Optional<UserAccessRole> getHighestRole(Collection<UserAccessRole> roles) {
    if (roles == null || roles.isEmpty()) {
      return Optional.empty();
    }

    AtomicReference<UserAccessRole> highest = new AtomicReference<>(roles.iterator().next());

    roles.forEach(role -> {
      if (role != null && highest.get().compareTo(role) < 0) {
        highest.set(role);
      }
    });

    return Optional.of(highest.get());
  }

  public Collection<UserAccessRole> getRoles(Authentication authentication) {
    if (authentication == null) {
      return new ArrayList<>();
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    List<UserAccessRole> userRoles = new ArrayList<>();

    if (userDetails != null && userDetails.isServiceAccount()) {
      userRoles.add(UserAccessRole.KIOSK);
    }

    authentication.getAuthorities().forEach(authority -> {
      if (authority != null) {
        try {
          UserAccessRole role = UserAccessRole.valueOf(authority.getAuthority());
          if (!userRoles.contains(role)) {
            userRoles.add(role);
          }
        } catch (IllegalArgumentException e) {
          System.err.println(
              "[AC] :: Could not map authority " + authority.getAuthority()
                  + " to UserAccessRole of User " + (userDetails == null ? "[No UserDetails]"
                  : userDetails.getUsername()) + "!");
        }
      }
    });

    return userRoles;
  }

}
