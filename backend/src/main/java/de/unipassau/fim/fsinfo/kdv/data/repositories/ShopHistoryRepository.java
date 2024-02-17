package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistoryEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopHistoryRepository extends JpaRepository<ShopHistoryEntry, Long> {

  List<ShopHistoryEntry> findByUsernameEquals(String username);
}
