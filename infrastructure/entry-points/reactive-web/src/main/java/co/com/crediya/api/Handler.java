package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.validator.RequestValidator;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    public static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final Validator validator;
    private final ILoanApplicationUseCase loanApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;

    public Mono<ServerResponse> listenPOSTApplicationLoan(ServerRequest request) {

        return request.bodyToMono(LoanApplicationRequest.class)
                .flatMap(loanRequest ->
                    RequestValidator.validate(loanRequest, validator)
                        .flatMap(validated ->
                            loanApplicationUseCase.registerLoanApplication(loanApplicationMapper.toModel(validated))
                                .flatMap(created -> {
                                    LoanApplicationResponse response = loanApplicationMapper.toResponse(created);
                                    log.info("Application created successfully: {}", response);
                                    return ServerResponse.status(201).bodyValue(response);
                                })
                        )
                );
    }

}
