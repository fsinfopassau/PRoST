package de.unipassau.fim.fsinfo.kdv;

import de.unipassau.fim.fsinfo.kdv.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KdVApplication {

  public static void main(String[] args) {
    SpringApplication.run(KdVApplication.class, args);
  }

  @Bean
  CommandLineRunner commandLineRunner(UserRepository userRepository) {
    return args -> {
      User user = new User("testUser", UserRole.ADMINISTRATOR, true);
      try {
        userRepository.save(user);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

}
