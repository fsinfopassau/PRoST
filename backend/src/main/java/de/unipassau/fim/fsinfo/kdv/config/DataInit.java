package de.unipassau.fim.fsinfo.kdv.config;

import de.unipassau.fim.fsinfo.kdv.data.UserRole;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import de.unipassau.fim.fsinfo.kdv.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.kdv.data.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInit {

  @Bean
  CommandLineRunner userTestInit(UserRepository repository) {
    return args -> {
      User a = new User("admin", "Kai Nepanik", UserRole.ADMINISTRATOR, false);
      User a2 = new User("mod", "Lasse Maranda", UserRole.MODERATOR, true);
      User a3 = new User("normalUser", "Erhart Haramasch", UserRole.USER, true);
      try {
        repository.save(a);
        repository.save(a2);
        repository.save(a3);
        for (int i = 1; i <= 20; i++) {
          repository.save(new User("testN" + i, "KekW " + i, UserRole.USER, true));
        }
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
      ShopItem a3 = new ShopItem("uran-235", "snack", "Uran 235", 1.00);
      a3.setEnabled(false);
      try {
        repository.save(a);
        repository.save(a2);
        repository.save(a3);
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

}
