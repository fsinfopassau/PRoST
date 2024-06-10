package de.unipassau.fim.fsinfo.prost.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Configuration
public class LdapConfig {

  @Value("${prost.ldap-uri}")
  private String ldapUri;

  @Bean
  public DefaultSpringSecurityContextSource contextSource() {
    return new DefaultSpringSecurityContextSource(ldapUri);
  }

  @Bean
  public LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {
    DefaultLdapAuthoritiesPopulator authorities = new DefaultLdapAuthoritiesPopulator(
        contextSource(), "ou=groups");
    authorities.setGroupRoleAttribute("cn");
    authorities.setGroupSearchFilter("(member={0})");
    authorities.setConvertToUpperCase(true);
    authorities.setRolePrefix("");
    authorities.setIgnorePartialResultException(true); // Handle service accounts without groups
    return authorities;
  }

}
