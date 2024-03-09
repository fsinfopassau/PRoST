package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopItemHistoryEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemHistoryRepository extends JpaRepository<ShopItemHistoryEntry, Long> {

  List<ShopItemHistoryEntry> findByUserIdEquals(String username);

  List<ShopItemHistoryEntry> findByUserIdAndTimestampBetween(String userId, Long startTimestamp,
      Long endTimestamp);

}
