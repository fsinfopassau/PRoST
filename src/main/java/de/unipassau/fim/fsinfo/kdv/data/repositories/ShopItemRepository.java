package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {


  Optional<ShopItem> findById(String name);

}
