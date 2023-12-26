package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jewusiak.grzesbankapi.model.domain.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}
