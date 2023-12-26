package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jewusiak.grzesbankapi.model.domain.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, String> {
}
