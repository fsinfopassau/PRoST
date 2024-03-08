package de.unipassau.fim.fsinfo.kdv.service;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
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
