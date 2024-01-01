package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jewusiak.grzesbankapi.model.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
}
