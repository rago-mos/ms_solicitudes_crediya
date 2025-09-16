package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.user.UserApplication;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class GetLoanApplicationUseCase implements IGetLoanApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final UserClientRepository userClientRepository;

    @Override
    public Mono<PageApplicationResponse<LoanApplicationView>> getLoanApplication(List<Integer> status, int page, int size, String token) {
        int offset = (page - 1) * size;

        Mono<Long> totalElementsQuery = applicationRepository.countByStatus(status);
        Mono<List<LoanApplicationView>> contentApplicationTransform =
                applicationRepository.findLoanApplicationDetails(status, size, offset).collectList();

        return Mono.zip(totalElementsQuery, contentApplicationTransform)
                .flatMap(tuple -> {
                    Long totalElements = tuple.getT1();
                    List<LoanApplicationView> content = tuple.getT2();
                    return buildResponse(Flux.fromIterable(content), totalElements, page, size, token);
                });
    }

    private Mono<PageApplicationResponse<LoanApplicationView>> buildResponse(Flux<LoanApplicationView> content,
                                                                             long totalElements, int page,
                                                                             int size, String token) {
        Mono<List<LoanApplicationView>> contentList = content.collectList();

        Mono<List<String>> documentsList = content
                .map(LoanApplicationView::getIdentityDocument)
                .filter(Objects::nonNull)
                .distinct()
                .collectList();

        return Mono.zip(contentList, documentsList)
                .flatMap(tuple -> {
                    List<LoanApplicationView> viewList = tuple.getT1();
                    List<String> documents = tuple.getT2();

                    if (documents.isEmpty()) {
                        int totalPages = calculateTotalPages(totalElements, size);
                        return Mono.just(new PageApplicationResponse<>(viewList, page, size, totalElements, totalPages));
                    }

                    return userClientRepository.getUsersByDocuments(documents, token)
                            .collectMap(UserApplication::getIdentityDocument)
                            .flatMap(users -> enrichApplications(Flux.fromIterable
                                    (viewList), users, page, size, totalElements));
                });
    }

    private Mono<PageApplicationResponse<LoanApplicationView>> enrichApplications(
            Flux<LoanApplicationView> contentFlux,
            Map<String, UserApplication> users,
            int page, int size,
            long totalElements) {

        return contentFlux
                .map(application -> {
                    UserApplication user = users.get(application.getIdentityDocument());
                    return user == null
                            ? application
                            : application.toBuilder()
                            .email(user.getEmail())
                            .baseSalary(user.getBaseSalary())
                            .fullName(user.getFirstName() + " " + user.getLastName())
                            .build();
                })
                .collectList()
                .map(enriched -> {
                    int totalPages = calculateTotalPages(totalElements, size);
                    return new PageApplicationResponse<>(enriched, page, size, totalElements, totalPages);
                });
    }

    private int calculateTotalPages(long totalElements, int size) {
        return (int) Math.ceil((double) totalElements / size);
    }
}
