package co.com.crediya.api.dto.request;

import jakarta.validation.constraints.NotNull;

public record DebtCapacityRequest(

        @NotNull(message = "The field is mandatory")
        Long idApplication

) {
}
