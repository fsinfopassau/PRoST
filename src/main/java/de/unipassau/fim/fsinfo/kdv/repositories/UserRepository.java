package de.unipassau.fim.fsinfo.kdv.repositories;

import de.unipassau.fim.fsinfo.kdv.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByName(String name);
}
