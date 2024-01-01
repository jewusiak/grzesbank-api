package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpAccessAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    
    private String ip;
    
    @Enumerated(EnumType.STRING)
    private AccessibleResource resource;
    
    private ZonedDateTime date;
    
    private ZonedDateTime overrideDate;
    
    private Boolean successful;
    
    public enum AccessibleResource {
        IP_LOGIN, IP_PASS_RESET_REQUEST, IP_PASS_RESET_2ND;
    }
}
