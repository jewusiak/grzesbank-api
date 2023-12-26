package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.jewusiak.grzesbankapi.utils.ValidationService;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferOrderRequest {
    @Pattern(regexp = ValidationService.plTextboxRegex)
    @NotBlank
    private String recipientName;
    @Pattern(regexp = ValidationService.plTextboxRegex)
    @NotBlank
    private String recipientAddress;
    @Pattern(regexp = ValidationService.accNumberRegex)
    @NotBlank
    private String recipientAccountNumber;
    private BigDecimal amount;
    @Pattern(regexp = ValidationService.plTextboxRegex)
    @NotBlank
    private String title;
}
