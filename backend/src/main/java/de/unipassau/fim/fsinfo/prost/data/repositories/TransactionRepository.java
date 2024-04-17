package de.unipassau.fim.fsinfo.prost.data.repositories;

import de.unipassau.fim.fsinfo.prost.data.dao.TransactionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntry, Long> {

}
