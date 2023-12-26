package pl.jewusiak.grzesbankapi.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PasswordCombinationResponse {
    private UUID pcid;
    private Integer[] indices;
}
