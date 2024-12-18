package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.ShopItemHistoryEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopItemHistoryRepository extends JpaRepository<ShopItemHistoryEntry, Long> {

  List<ShopItemHistoryEntry> findByUserId(String userId);

  Page<ShopItemHistoryEntry> findByUserIdEquals(String username, Pageable pageable);

  List<ShopItemHistoryEntry> findByUserIdAndTimestampBetween(String userId, Long startTimestamp,
      Long endTimestamp);

  List<ShopItemHistoryEntry> findAllByItemIdAndTimestampBetween(String itemId, Long startTimestamp,
      Long endTimestamp);

  @Query("SELECT SUM(entry.amount) FROM PRoST_ShopItemHistoryEntry entry " +
      "WHERE entry.itemId = :itemId " +
      "AND entry.timestamp BETWEEN :startTimestamp AND :endTimestamp")
  Optional<Long> getTotalAmountPurchasedInTimeFrame(
      @Param("itemId") String itemId,
      @Param("startTimestamp") Long startTimestamp,
      @Param("endTimestamp") Long endTimestamp
  );
}
