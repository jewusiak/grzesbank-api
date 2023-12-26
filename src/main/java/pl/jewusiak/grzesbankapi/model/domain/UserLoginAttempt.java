package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    
    ZonedDateTime date;
    
    Boolean successful;
    
    ZonedDateTime overrideDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
