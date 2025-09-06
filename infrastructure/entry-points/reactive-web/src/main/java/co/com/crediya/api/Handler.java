package co.com.crediya.api;

import co.com.crediya.api.dto.ErrorResponseHandler;
import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
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
    private final LoanApplicationMapper loanApplicationMapper;
    private final JwtProvider jwtProvider;


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
                                LocalDateTime.now(), 403,
                                FORBIDDEN,
                                MESSAGE_ERROR_FORBIDDEN));
                    }

                    return RequestValidator.validate(loanRequest, validator)
                            .flatMap(validated -> {
                                var model = loanApplicationMapper.toModel(validated);
                                return loanApplicationUseCase.registerLoanApplication(model, token)
                                        .flatMap(created -> {
                                            LoanApplicationResponse response = loanApplicationMapper.toResponse(created);
                                            log.info(LOG_INFO_CREATED, response);
                                            return ServerResponse.status(HttpStatus.CREATED).bodyValue(response);
                                        });
                            });
                });
    }

    @PreAuthorize("hasAuthority('ASESOR')")
    public Mono<ServerResponse> listenGetApplicationLoan(ServerRequest request) {

        String token = extractToken(request);
        List<Integer> status = extractStatus(request);

        int page = request.queryParam("page").map(Integer::parseInt).orElse(1);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);

        return loanApplicationUseCase.getLoanApplication(status, page, size, token)
                .flatMap(response ->
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response)
                );
    }

}
