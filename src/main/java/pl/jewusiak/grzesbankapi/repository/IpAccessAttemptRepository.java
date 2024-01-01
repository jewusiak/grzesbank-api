package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.jewusiak.grzesbankapi.model.domain.IpAccessAttempt;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface IpAccessAttemptRepository extends CrudRepository<IpAccessAttempt, UUID> {
    int countResourceAccessAttemptsByResourceAndIpAndDateAfterAndOverrideDateIsNullAndSuccessfulIsFalse(IpAccessAttempt.AccessibleResource resource, String ip, ZonedDateTime since);

    int countResourceAccessAttemptsByResourceAndIpAndDateAfterAndOverrideDateIsNull(IpAccessAttempt.AccessibleResource resource, String ip, ZonedDateTime since);

    @Modifying
    @Query("UPDATE IpAccessAttempt iaa SET iaa.overrideDate=?1 WHERE iaa.resource=?2 AND iaa.ip=?3 AND iaa.date >= ?4 AND iaa.successful != true")
    void overrideAccessAttemptsForResource(ZonedDateTime overrideDate, IpAccessAttempt.AccessibleResource resource, String ip, ZonedDateTime overrideSince);
}
