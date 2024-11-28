package de.unipassau.fim.fsinfo.prost.security;

import de.unipassau.fim.fsinfo.prost.data.UserAccessRole;
import de.unipassau.fim.fsinfo.prost.security.CustomUserDetailsContextMapper.CustomUserDetails;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
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
  private final CustomUserDetailsContextMapper userDetailsContextMapper;

  public static final String[] AUTH_WHITELIST = {
      "/api/authentication",
      "/api/statistics/composite/**",
      "/api/statistics/user/**",
      "/api/statistics/item/**"
  };

  public static final String[] USER_SPACE = {
      "/api/user/info",
      "/api/user/me",
      "/api/user/me/**",
      "/api/history/shop/me",
      "/api/transaction/me",
      "/api/invoice/me",
      "/api/shop/item/**",
  };

  public static final String[] KIOSK_SPACE = {
      "/api/history/shop/**",
  };

  public static final String[] ADMIN_SPACE = {
      "/api/user/**",
      "/api/shop/**",
      "/api/invoice/**",
      "/api/transaction/**",
      "/api/statistics/**"
  };

  @Autowired
  public WebConfig(LdapConfig ldapConfig) {
    this.ldapConfig = ldapConfig;
    this.userDetailsContextMapper = new CustomUserDetailsContextMapper();
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
            .access((authentication, object) ->
                new AuthorizationDecision(isServiceAccountOr(authentication.get(),
                    UserAccessRole.FSINFO, UserAccessRole.KIOSK, UserAccessRole.KAFFEEKASSE))
            )
            .requestMatchers(KIOSK_SPACE)
            .access((authentication, object) -> new AuthorizationDecision(
                isServiceAccountOr(authentication.get(), UserAccessRole.KIOSK,
                    UserAccessRole.KAFFEEKASSE))
            )
            .requestMatchers(ADMIN_SPACE).hasAnyAuthority(UserAccessRole.KAFFEEKASSE.name())
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
        .ldapAuthoritiesPopulator(ldapConfig.ldapAuthoritiesPopulator())
        .userDetailsContextMapper(userDetailsContextMapper);
  }

  private boolean isServiceAccountOr(Authentication authentication, UserAccessRole... roles) {
    if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
      boolean isServiceAccount = userDetails.isServiceAccount();
      boolean hasKioskRole = userDetails.getAuthorities().stream()
          .anyMatch(grantedAuthority -> {
            for (UserAccessRole role : roles) {
              if (grantedAuthority.getAuthority().equals(role.name())) {
                return true;
              }
            }
            return false;
          });
      return isServiceAccount || hasKioskRole;
    }
    return false;
  }

}