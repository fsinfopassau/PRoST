package de.unipassau.fim.fsinfo.kdv.security;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

public class CustomUserDetailsContextMapper implements UserDetailsContextMapper {

  @Override
  public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
      Collection<? extends GrantedAuthority> authorities) {
    CustomUserDetails userDetails = new CustomUserDetails();
    userDetails.setAuthorities(authorities);
    userDetails.setUsername(username);
    userDetails.setDisplayName(ctx.getStringAttribute("uid"));
    userDetails.setEmail("it@paulsenik.de");

    return userDetails;
  }

  @Override
  public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
    // Implement if needed
  }

  @Getter
  @Setter
  public static final class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private String displayName;
    private String email;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return true;
    }
  }
}
