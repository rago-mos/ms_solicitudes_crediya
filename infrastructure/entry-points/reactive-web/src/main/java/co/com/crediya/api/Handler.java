package co.com.crediya.api;

import co.com.crediya.api.dto.*;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.mapper.UpdateApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import co.com.crediya.usecase.loanapplication.IUpdateApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;

import static co.com.crediya.api.utils.Utils.extractStatus;
import static co.com.crediya.api.utils.Utils.extractToken;
import static co.com.crediya.model.utils.Constant.*;

@Component
@RequiredArgsConstructor
public class Handler {

    public static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final Validator validator;
    private final ILoanApplicationUseCase loanApplicationUseCase;
    private final IUpdateApplicationUseCase updateApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;
    private final UpdateApplicationMapper updateApplicationMapper;
    private final JwtProvider jwtProvider;


    @Operation(operationId = "registerApplication",
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
            ), @ApiResponse(responseCode = "403",
                    description = "Access denied",
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
    @PreAuthorize("hasAuthority('CLIENTE')")
    public Mono<ServerResponse> listenPOSTApplicationLoan(ServerRequest request) {

        String token = extractToken(request);
        String subjectFromToken = jwtProvider.getSubject(token);

        return request.bodyToMono(LoanApplicationRequest.class)
                .flatMap(loanRequest -> {
                    // Validar que el documento del request coincida con el sub del token
                    if (!loanRequest.identityDocument().equals(subjectFromToken)) {
                        log.warn(LOG_WARN_FORBIDDEN, subjectFromToken, loanRequest.identityDocument());
                        return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue(new ErrorResponseHandler(
                                LocalDateTime.now(), STATUS_ERROR,
                                FORBIDDEN,
                                MESSAGE_ERROR_FORBIDDEN));
                    }

                    return RequestValidator.validate(loanRequest, validator)
                            .map(loanApplicationMapper::toModel)
                            .flatMap(application -> loanApplicationUseCase.registerLoanApplication(application, token))
                            .flatMap(created -> {
                                LoanApplicationResponse response = loanApplicationMapper.toResponse(created);
                                log.info(LOG_INFO_CREATED, response);
                                return ServerResponse.status(HttpStatus.CREATED).bodyValue(response);
                            });
                });
    }


    @Operation(operationId = "registerApplication",
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
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List returned successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageApplicationResponse.class))),

                    @ApiResponse(responseCode = "400",
                            description = "Invalid request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseHandler.class),
                                    examples = {
                                            @ExampleObject(name = "IllegalArgumentException", value = "Invalid status value"),
                                            @ExampleObject(name = "NumberFormatException", value = "For input string")})),

                    @ApiResponse(
                            responseCode = "401",
                            description = "Bad credentials"),

                    @ApiResponse(responseCode = "403",
                            description = "Access denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseHandler.class)
                            )
                    ),

                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseHandler.class)))
            }
    )
    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> listenGetApplicationLoan(ServerRequest request) {

        String token = extractToken(request);
        List<Integer> status = extractStatus(request);

        int page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);

        return loanApplicationUseCase.getLoanApplication(status, page, size, token)
                .flatMap(response -> {
                    log.info(LOG_INFO_RESULT_APPLICATIONS, response.getTotalElements());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                        }

                );
    }


    @Operation(operationId = "updatedApplication",
            summary = "Update a loan application",
            description = "The system receives the application information and sends status confirmation",
            requestBody = @RequestBody(required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationRequest.class)
                    )
            ),
            responses = {@ApiResponse(responseCode = "200",
                    description = "Application updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class)
                    )
            ), @ApiResponse(responseCode = "400",
                    description = "Invalid request format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseHandler.class)
                    )
            ), @ApiResponse(responseCode = "403",
                    description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseHandler.class)
                    )
            ), @ApiResponse(responseCode = "404",
                    description = "Not Found: Object not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseHandler.class),
                            examples = {
                                    @ExampleObject(name = "StateNotFound", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":404,\"error\":\"NotFoundException\",\"message\":\"State not found\"}"),
                                    @ExampleObject(name = "ApplicationNotFound", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":404,\"error\":\"NotFoundException\",\"message\":\"Application not found\"}")
                            }
                    )
            ), @ApiResponse(responseCode = "409",
                    description = "Conflict: Business rule",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseHandler.class),
                            examples = {
                                    @ExampleObject(name = "StateConflict", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":409,\"error\":\"BusinessException\",\"message\":\"The state to update is not valid\"}"),
                                    @ExampleObject(name = "ApplicationStateConflict", value = "{\"timestamp\": \"2025-08-26T14:22:56.402Z\",\"status\":409,\"error\":\"BusinessException\",\"message\":\"The application status is already approved or rejected\"}")
                            }
                    )
            ), @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseHandler.class)
                    )
            )}
    )
    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> listenPutApplicationLoan(ServerRequest request) {

        String token = extractToken(request);

        return request.bodyToMono(ApplicationRequest.class)
                .flatMap(applicationRequest -> RequestValidator.validate(applicationRequest, validator))
                .map(updateApplicationMapper::toModel)
                .flatMap(stateApplication ->
                    updateApplicationUseCase.updateApplication(stateApplication, token)
                            .flatMap(created -> {
                                var response = new GenericResponse(LocalDateTime.now(), STATUS_OK, created);
                                log.info(LOG_INFO_UPDATE, created);
                                return ServerResponse.status(HttpStatus.OK).bodyValue(response);
                            })
                );
    }

}
