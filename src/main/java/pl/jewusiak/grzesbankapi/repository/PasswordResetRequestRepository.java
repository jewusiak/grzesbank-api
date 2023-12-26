package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jewusiak.grzesbankapi.model.domain.PasswordResetRequest;

import java.util.UUID;

@Repository
public interface PasswordResetRequestRepository extends CrudRepository<PasswordResetRequest, UUID> {
}
