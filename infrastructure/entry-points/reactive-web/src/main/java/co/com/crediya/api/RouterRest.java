package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponseHandler;
import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterRest {

    private static final String REGISTER_APPLICATION_LOAN = "/api/v1/solicitud";

    @Bean
    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST,
                    path = REGISTER_APPLICATION_LOAN,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTApplicationLoan",
                    operation = @Operation(operationId = "registerApplication",
                            summary = "Register a new loan application",
                            description = "The system receives the application information and sends it for confirmation",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = LoanApplicationRequest.class)
                                    )
                            ),
                            responses = {@ApiResponse(responseCode = "201",
                                    description = "Application created successfully",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = LoanApplicationResponse.class)
                                    )
                            ), @ApiResponse(responseCode = "400",
                                    description = "Invalid request format",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class)
                                    )
                            ), @ApiResponse(responseCode = "404",
                                    description = "Not Found: Object not found",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class),
                                            examples = {
                                                    @ExampleObject(name = "StateNotFound", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":404,\"error\":\"BusinessException\",\"message\":\"State not found\"}"),
                                                    @ExampleObject(name = "LoanTypeNotFound", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":404,\"error\":\"BusinessException\",\"message\":\"The loan type does not exist\"}"),
                                                    @ExampleObject(name = "UserNotFound", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":404,\"error\":\"BusinessException\",\"message\":\"User does not exist\"}")
                                            }
                                    )
                            ), @ApiResponse(responseCode = "409",
                                    description = "Conflict: The amount is not valid",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class),
                                            examples = {
                                                    @ExampleObject(name = "AmountConflict", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":409,\"error\":\"BusinessException\",\"message\":\"The amount is not valid\"}")
                                            }
                                    )
                            ), @ApiResponse(responseCode = "500",
                                    description = "Internal server error",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class)
                                    )
                            )}
                    )
            ),
            @RouterOperation(method = RequestMethod.GET,
                    path = REGISTER_APPLICATION_LOAN,
                    beanClass = Handler.class,
                    beanMethod = "listenGetApplicationLoan",
                    operation = @Operation(operationId = "registerApplication",
                            summary = "List of requests for manual review",
                            description = "View a list of all requests that need review",
                            parameters = {
                                    @Parameter(
                                            name = "status",
                                            description = "List of statuses",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            example = "1,2,3",
                                            schema = @Schema(type = "array")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            description = "Number of records",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            example = "10",
                                            schema = @Schema(type = "integer")
                                    ),
                                    @Parameter(
                                            name = "page",
                                            description = "Selected page",
                                            in = ParameterIn.QUERY,
                                            required = false,
                                            example = "1",
                                            schema = @Schema(type = "integer")
                                    )
                            },
                            responses = {@ApiResponse(responseCode = "200",
                                    description = "List returned successfully",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = PageApplicationResponse.class)
                                    )
                            ), @ApiResponse(responseCode = "400",
                                    description = "Invalid request",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class),
                                            examples = {
                                                    @ExampleObject(name = "IllegalArgumentException", value = "Invalid status value"),
                                                    @ExampleObject(name = "NumberFormatException", value = "For input string")
                                            }
                                    )
                            ), @ApiResponse(responseCode = "500",
                                    description = "Internal server error",
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponseHandler.class)
                                    )
                            )}
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(REGISTER_APPLICATION_LOAN), handler::listenPOSTApplicationLoan)
                .andRoute(GET(REGISTER_APPLICATION_LOAN), handler::listenGetApplicationLoan);

    }
}
