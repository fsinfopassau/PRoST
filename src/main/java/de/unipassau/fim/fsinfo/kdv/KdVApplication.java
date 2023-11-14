package de.unipassau.fim.fsinfo.kdv;

import de.unipassau.fim.fsinfo.kdv.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.dao.User;
import de.unipassau.fim.fsinfo.kdv.repositories.ShopItemRepository;
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
  CommandLineRunner commandLineRunner(UserRepository repository) {
    return args -> {
      User a = new User("testUser", UserRole.ADMINISTRATOR, true);
      try {
        repository.save(a);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  @Bean
  CommandLineRunner commandLineRunner2(ShopItemRepository repository) {
    return args -> {
      ShopItem a = new ShopItem("bier", "drink", "Bier", 10.32);
      try {
        repository.save(a);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

}
