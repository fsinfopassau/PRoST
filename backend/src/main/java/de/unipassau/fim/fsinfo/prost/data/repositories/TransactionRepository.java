package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntry, Long> {

  Page<TransactionEntry> findByReceiverId(String receiverId, Pageable pageable);
}
