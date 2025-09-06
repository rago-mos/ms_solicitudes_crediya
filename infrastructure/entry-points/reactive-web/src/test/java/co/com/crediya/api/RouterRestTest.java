package co.com.crediya.api;


import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.dto.LoanTypeResponse;
import co.com.crediya.api.dto.StateResponse;
import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ILoanApplicationUseCase loanApplicationUseCase;

    @MockitoBean
    private LoanApplicationMapper loanApplicationMapper;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    JwtProvider  jwtProvider;

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {

        LoanApplicationRequest request = new LoanApplicationRequest(
                new BigDecimal("1000000"),
                12,
                "123456789",
                2
        );

        Application application = Application.builder()
                .amount(request.amount())
                .term(request.term())
                .identityDocument(request.identityDocument())
                .state(State.builder().idState(1).name("Approved").description("Approved application").build())
                .loanType(LoanType.builder()
                        .idLoanType(2)
                        .name("Personal")
                        .minimumAmount(new BigDecimal("50000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .interestRate(new BigDecimal("0.05"))
                        .automaticValidation(true)
                        .build())
                .date(LocalDate.now())
                .build();

        LoanApplicationResponse response = LoanApplicationResponse.builder()
                .amount(application.getAmount())
                .term(application.getTerm())
                .identityDocument(application.getIdentityDocument())
                .state(StateResponse.builder()
                        .name(application.getState().getName())
                        .description(application.getState().getDescription()).build())
                .loanType(LoanTypeResponse.builder()
                        .name(application.getLoanType().getName())
                        .interestRate(application.getLoanType().getInterestRate())
                        .minimumAmount(application.getLoanType().getMinimumAmount())
                        .maximumAmount(application.getLoanType().getMaximumAmount())
                        .automaticValidation(application.getLoanType().getAutomaticValidation())
                        .build())
                .build();

        when(validator.validate(any())).thenReturn(Set.of());
        when(loanApplicationMapper.toModel(request)).thenReturn(application);
        when(loanApplicationUseCase.registerLoanApplication(application, "jkdsajs")).thenReturn(Mono.just(application));
        when(loanApplicationMapper.toResponse(application)).thenReturn(response);

        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN", "ASESOR"})
    @Test
    void shouldReturnLoanApplicationPageSuccessfully() {

        String token = "Bearer jkdsajs";
        int page = 1;
        int size = 10;

        LoanApplicationView view1 = LoanApplicationView.builder()
                .identityDocument("123456789")
                .amount(new BigDecimal("1000000"))
                .monthTerm(12)
                .monthAmountApprovedApplication(new BigDecimal("85000"))
                .fullName("Rubén Tester")
                .email("ruben@example.com")
                .statusName("Approved")
                .interestRate(new BigDecimal("0.05"))
                .loanTypeName("Personal")
                .baseSalary(new BigDecimal("3000000"))
                .build();

        LoanApplicationView view2 = view1.toBuilder().identityDocument("987654321").build();

        PageApplicationResponse<LoanApplicationView> pageResponse = new PageApplicationResponse<>(
                List.of(view1, view2),
                page,
                size,
                2L,
                1
        );

        when(jwtProvider.getSubject(anyString())).thenReturn("123456789");
        when(loanApplicationUseCase.getLoanApplication(anyList(), anyInt(), anyInt(), anyString()))
                .thenReturn(Mono.just(pageResponse));

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/solicitud")
                        .queryParam("status", "1", "2")
                        .queryParam("page", String.valueOf(page))
                        .queryParam("size", String.valueOf(size))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.page").isEqualTo(page)
                .jsonPath("$.size").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.content[0].identityDocument").isEqualTo("123456789")
                .jsonPath("$.content[1].identityDocument").isEqualTo("987654321");
    }
}

@TestConfiguration
@EnableReactiveMethodSecurity
class SecurityConfigTest {

}

