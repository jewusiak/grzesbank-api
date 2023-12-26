package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.repository.CrudRepository;
import pl.jewusiak.grzesbankapi.model.domain.ResetPasswordIpAttempt;

import java.util.UUID;

public interface ResetPasswordIpAttemptRepository extends CrudRepository<ResetPasswordIpAttempt, UUID> {
}
