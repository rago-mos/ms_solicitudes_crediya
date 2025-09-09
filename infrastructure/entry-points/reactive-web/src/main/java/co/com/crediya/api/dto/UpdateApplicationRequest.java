package co.com.crediya.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateApplicationRequest(

        @NotNull(message = "The field is mandatory")
        String idApplication,

        @NotNull(message = "The field is mandatory")
        Integer idState

) {
}
