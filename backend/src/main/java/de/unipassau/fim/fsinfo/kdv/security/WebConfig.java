package de.unipassau.fim.fsinfo.kdv.security;

import de.unipassau.fim.fsinfo.kdv.data.UserRole;
import de.unipassau.fim.fsinfo.kdv.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final UserService userService;

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

  public static final String[] MOD_SPACE = {
      "/api/shop/**",
      "/api/users/**",
      "/api/invoice/**",
  };

  public static final String[] ADMIN_SPACE = {
      "/api/**",
  };

  @Autowired
  public WebConfig(UserService userService) {
    this.userService = userService;
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
            .hasAnyAuthority(UserRole.USER.name(), UserRole.MODERATOR.name(),
                UserRole.ADMINISTRATOR.name())
            .requestMatchers(MOD_SPACE)
            .hasAnyAuthority(UserRole.MODERATOR.name(), UserRole.ADMINISTRATOR.name())
            .requestMatchers(ADMIN_SPACE).hasAnyAuthority(UserRole.ADMINISTRATOR.name())
            .anyRequest().authenticated() // Require authentication for all other requests
        )
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Bean
  public AuthenticationProvider daoAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(userService);
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}