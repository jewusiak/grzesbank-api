package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.constraints.*;
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
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "2147483647")
    @NotNull
    private BigDecimal amount;
    @Pattern(regexp = ValidationService.plTextboxRegex)
    @NotBlank
    private String title;
}
