package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.ShopHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopHistoryRepository extends JpaRepository<ShopHistory, Long> {

  List<ShopHistory> findByUsernameEquals(String username);
}
