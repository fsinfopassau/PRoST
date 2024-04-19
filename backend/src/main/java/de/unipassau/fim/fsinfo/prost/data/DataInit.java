package de.unipassau.fim.fsinfo.prost.data;

import de.unipassau.fim.fsinfo.prost.data.dao.ProstUser;
import de.unipassau.fim.fsinfo.prost.data.dao.ShopItem;
import de.unipassau.fim.fsinfo.prost.data.repositories.ShopItemRepository;
import de.unipassau.fim.fsinfo.prost.data.repositories.UserRepository;
import java.math.BigDecimal;
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
            new ProstUser("admin", "Kai Nepanik", "it@paulsenik.de", true));
        repository.save(
            new ProstUser("mod", "Lasse Maranda", "it@paulsenik.de", true));
        repository.save(
            new ProstUser("normalUser", "Erhart Haramasch", "it@paulsenik.de", false));
        for (int i = 1; i <= 20; i++) {
          repository.save(
              new ProstUser("testN" + i, "KekW " + i, "it@paulsenik.de", true));
        }
      } catch (Exception ignored) {
      }
    };
  }

  @Bean
  CommandLineRunner shopItemTestInit(ShopItemRepository repository) {
    return args -> {
      ShopItem a = new ShopItem("bier", "drink", "Bier", new BigDecimal("1.5"));
      ShopItem a2 = new ShopItem("spezi", "drink", "Spezi", new BigDecimal("1.00"));
      ShopItem a3 = new ShopItem("uran-235", "snack", "Uran 235", new BigDecimal("1.00"));
      a3.setEnabled(false);
      try {
        repository.save(a);
        repository.save(a2);
        repository.save(a3);
      } catch (Exception ignored) {
      }
    };
  }

}
