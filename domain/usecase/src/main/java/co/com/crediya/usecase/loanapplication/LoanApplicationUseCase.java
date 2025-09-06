package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.model.user.UserApplication;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements ILoanApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final StateRepository stateRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanApplicationValidator validator;
    private final UserClientRepository userClientRepository;


    @Override
    public Mono<Application> registerLoanApplication(Application application, String token) {
        return validator.validate(application, token)
                .then(applicationRepository.registerApplication(application))
                .flatMap(this::setApplication);
    }

    private Mono<Application> setApplication(Application application) {
        Mono<State> stateMono = stateRepository.findState(application.getState().getIdState());
        Mono<LoanType> loanTypeMono = loanTypeRepository.findLoanType(application.getLoanType().getIdLoanType());

        return Mono.zip(stateMono, loanTypeMono)
                .map(tuple -> {
                    application.setState(tuple.getT1());
                    application.setLoanType(tuple.getT2());
                    return application;
                });
    }


    @Override
    public Mono<PageApplicationResponse<LoanApplicationView>> getLoanApplication(List<Integer> status, int page, int size, String token) {
        int offset = (page - 1) * size;

        return applicationRepository.countByStatus(status)
                .flatMap(totalElements ->
                        applicationRepository.findLoanApplicationDetails(status, size, offset)
                                .collectList()
                                .flatMap(content -> buildResponse(content, totalElements, page, size, token))
                );
    }

    private Mono<PageApplicationResponse<LoanApplicationView>> buildResponse(List<LoanApplicationView> content, long totalElements, int page, int size, String token) {
        List<String> documents = extractDocuments(content);

        if (documents.isEmpty()) {
            int totalPages = calculateTotalPages(totalElements, size);
            return Mono.just(new PageApplicationResponse<>(content, page, size, totalElements, totalPages));
        }

        return userClientRepository.getUsersByDocuments(documents, token)
                .collectMap(UserApplication::getIdentityDocument)
                .map(users -> enrichApplications(content, users, page, size, totalElements));
    }

    private List<String> extractDocuments(List<LoanApplicationView> content) {
        return content.stream()
                .map(LoanApplicationView::getIdentityDocument)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private PageApplicationResponse<LoanApplicationView> enrichApplications(List<LoanApplicationView> content,
                                                                            Map<String, UserApplication> users,
                                                                            int page, int size, long totalElements) {
        List<LoanApplicationView> enriched = content.stream()
                .map(application -> {
                    UserApplication user = users.get(application.getIdentityDocument());
                    return application.toBuilder()
                            .email(user.getEmail())
                            .baseSalary(user.getBaseSalary())
                            .fullName(user.getFirstName() + " " + user.getLastName())
                            .build();
                })
                .toList();

        int totalPages = calculateTotalPages(totalElements, size);
        return new PageApplicationResponse<>(enriched, page, size, totalElements, totalPages);
    }

    private int calculateTotalPages(long totalElements, int size) {
        return (int) Math.ceil((double) totalElements / size);
    }
}
