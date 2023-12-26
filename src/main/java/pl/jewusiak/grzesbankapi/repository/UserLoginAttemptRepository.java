package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.jewusiak.grzesbankapi.model.domain.UserLoginAttempt;
import pl.jewusiak.grzesbankapi.model.domain.User;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface UserLoginAttemptRepository extends CrudRepository<UserLoginAttempt, UUID> {
    int countLoginAttemptsByDateAfterAndUserAndOverrideDateIsNullAndSuccessfulIsFalse(ZonedDateTime countSince, User user);
    
    @Modifying
    @Query("update UserLoginAttempt ula set ula.overrideDate=?1 where ula.user = ?2")
    void overrideLoginsForUser(ZonedDateTime overrideDate, User user);
}
