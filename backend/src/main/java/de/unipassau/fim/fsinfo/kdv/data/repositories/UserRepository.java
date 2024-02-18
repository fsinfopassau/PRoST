package de.unipassau.fim.fsinfo.kdv.data.repositories;

import de.unipassau.fim.fsinfo.kdv.data.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
