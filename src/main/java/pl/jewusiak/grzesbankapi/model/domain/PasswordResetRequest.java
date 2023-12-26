package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import pl.jewusiak.grzesbankapi.model.domain.User;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PasswordResetRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private ZonedDateTime validity;
    
    @ManyToOne(optional = false)
    private User user;
    
    @CreatedDate
    private ZonedDateTime createdAt;
    
    private boolean isUsed;
}
