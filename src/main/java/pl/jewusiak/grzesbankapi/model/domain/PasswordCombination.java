package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    private String indices;

    @Getter
    @Setter
    private String passwordHash;
    
    @ManyToOne()
    @Setter
    private User user;

    public Integer[] getIndices() {
        return Arrays.stream(indices.split(",")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
    }

    public void setIndices(Integer[] indices) {
        this.indices = Arrays.toString(indices).replace("[", "").replace("]", "").replace(" ", "");
    }
}
