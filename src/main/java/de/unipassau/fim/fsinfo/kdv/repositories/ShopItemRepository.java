package de.unipassau.fim.fsinfo.kdv.repositories;

import de.unipassau.fim.fsinfo.kdv.dao.ShopItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {


  Optional<ShopItem> findById(String name);

}
