package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  @Value("${MASTER_PASSWORD:password}")
  private String masterPassword;

  @Autowired
  private UserRepository users;

  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  public boolean createUser(KdvUser userTemplate) {
    if (users.findById(userTemplate.getId()).isPresent()) {
      return false;
    }

    if (!isValidEmail(userTemplate.getEmail())) {
      return false;
    }

    userTemplate.setBalance(0d);
    users.save(userTemplate);
    return true;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    KdvUser user = users.findById(username)
        .orElseThrow(() -> new UsernameNotFoundException("User \"" + username + "\" not found!"));

    UserDetails u = User
        .withUsername(username)
        .password(new BCryptPasswordEncoder().encode(masterPassword))
        .authorities(user.getRole().name())
        .build();

    return u;
  }
}
