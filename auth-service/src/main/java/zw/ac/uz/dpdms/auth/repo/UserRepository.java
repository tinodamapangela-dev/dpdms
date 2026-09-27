package zw.ac.uz.dpdms.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpdms.auth.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}