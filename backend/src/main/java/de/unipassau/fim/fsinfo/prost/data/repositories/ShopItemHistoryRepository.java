package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemHistoryRepository extends JpaRepository<ShopItemHistoryEntry, Long> {

  Page<ShopItemHistoryEntry> findByUserIdEquals(String username, Pageable pageable);

  List<ShopItemHistoryEntry> findByUserIdAndTimestampBetween(String userId, Long startTimestamp,
      Long endTimestamp);

}
