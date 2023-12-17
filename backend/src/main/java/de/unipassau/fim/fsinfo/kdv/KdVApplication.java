package de.unipassau.fim.fsinfo.kdv;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
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
  CommandLineRunner userTestInit(UserRepository repository) {
    return args -> {
      User a = new User("admin", UserRole.ADMINISTRATOR, true);
      User a2 = new User("normalUser", UserRole.USER, true);
      try {
        repository.save(a);
        repository.save(a2);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  @Bean
  CommandLineRunner shopItemTestInit(ShopItemRepository repository) {
    return args -> {
      ShopItem a = new ShopItem("bier", "drink", "Bier", 1.50);
      ShopItem a2 = new ShopItem("spezi", "drink", "Spezi", 1.00);
      try {
        repository.save(a);
        repository.save(a2);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

}
