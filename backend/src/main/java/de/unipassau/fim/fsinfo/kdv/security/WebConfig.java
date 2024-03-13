package de.unipassau.fim.fsinfo.kdv.security;

import de.unipassau.fim.fsinfo.kdv.data.UserRole;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebConfig implements WebMvcConfigurer {

  @Value("${ALLOWED_ORIGINS:*,localhost}")
  private String[] allowedOrigins;
  private final LdapConfig ldapConfig;

  public static final String[] AUTH_WHITELIST = {
      "/api/users",
      "/api/users/*",
      "/api/shop",
      "/api/shop/*",
      "/api/shop/*/picture",
      "/api/shop/consume/**",
      "/api/history/**",
  };

  public static final String[] USER_SPACE = {
      "/api/",
      "/api/users/*/invoices",
  };

  public static final String[] KIOSK_SPACE = {
      "/api/shop/**",
      "/api/users/**",
      "/api/invoice/**",
  };

  public static final String[] ADMIN_SPACE = {
      "/api/**",
  };

  @Autowired
  public WebConfig(LdapConfig ldapConfig) {
    this.ldapConfig = ldapConfig;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(AUTH_WHITELIST).permitAll()
            .requestMatchers(USER_SPACE)
            .hasAnyAuthority(UserRole.FSINFO.name(), UserRole.KIOSK.name(),
                UserRole.ADMIN.name())
            .requestMatchers(KIOSK_SPACE)
            .hasAnyAuthority(UserRole.KIOSK.name(), UserRole.ADMIN.name())
            .requestMatchers(ADMIN_SPACE).hasAnyAuthority(UserRole.ADMIN.name())
            .anyRequest().authenticated() // Require authentication for all other requests
        )
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=users", "uid={0},ou=serviceAccounts")
        .groupSearchBase("ou=groups")
        .rolePrefix("")
        .contextSource(ldapConfig.contextSource())
        .ldapAuthoritiesPopulator(ldapConfig.ldapAuthoritiesPopulator());
  }

}