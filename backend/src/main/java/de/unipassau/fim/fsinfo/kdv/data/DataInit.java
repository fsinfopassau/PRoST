package de.unipassau.fim.fsinfo.kdv.data;

import de.unipassau.fim.fsinfo.kdv.data.dao.KdvUser;
import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
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
      try {
        repository.save(
            new KdvUser("admin", "Kai Nepanik", "it@paulsenik.de", UserRole.ADMINISTRATOR, true));
        repository.save(
            new KdvUser("mod", "Lasse Maranda", "it@paulsenik.de", UserRole.MODERATOR, true));
        repository.save(
            new KdvUser("normalUser", "Erhart Haramasch", "it@paulsenik.de", UserRole.USER, false));
        for (int i = 1; i <= 20; i++) {
          repository.save(
              new KdvUser("testN" + i, "KekW " + i, "it@paulsenik.de", UserRole.USER, true));
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
