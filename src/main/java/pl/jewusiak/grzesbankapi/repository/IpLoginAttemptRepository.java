package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jewusiak.grzesbankapi.model.domain.IpLoginAttempt;

import java.util.UUID;

public interface IpLoginAttemptRepository extends CrudRepository<IpLoginAttempt, UUID> {
}
