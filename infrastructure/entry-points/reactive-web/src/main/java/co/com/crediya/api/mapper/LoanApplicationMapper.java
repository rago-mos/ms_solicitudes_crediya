package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.request.DebtCapacityRequest;
import co.com.crediya.api.dto.request.LoanApplicationRequest;
import co.com.crediya.api.dto.response.LoanApplicationResponse;
import co.com.crediya.api.dto.response.LoanTypeResponse;
import co.com.crediya.api.dto.response.StateResponse;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import org.springframework.stereotype.Component;


@Component
public class LoanApplicationMapper {

    public Application toModel(LoanApplicationRequest dto) {
        return Application.builder()
                .amount(dto.amount())
                .term(dto.term())
                .identityDocument(dto.identityDocument())
                .state(buildState())
                .loanType(buildLoanType(dto.idLoanType()))
                .build();
    }

    public Application toModel(DebtCapacityRequest dto) {
        return Application.builder()
                .idApplication(dto.idApplication())
                .build();
    }

    public LoanApplicationResponse toResponse(Application model) {
        return LoanApplicationResponse.builder()
                .amount(model.getAmount())
                .term(model.getTerm())
                .identityDocument(model.getIdentityDocument())
                .state(StateResponse.builder()
                        .name(model.getState().getName())
                        .description(model.getState().getDescription())
                        .build())
                .loanType(LoanTypeResponse.builder()
                        .name(model.getLoanType().getName())
                        .minimumAmount(model.getLoanType().getMinimumAmount())
                        .maximumAmount(model.getLoanType().getMaximumAmount())
                        .interestRate(model.getLoanType().getInterestRate())
                        .automaticValidation(model.getLoanType().getAutomaticValidation())
                        .build())
                .build();
    }

    private State buildState() {
        return State.builder()
                .idState(1)
                .build();
    }

    private LoanType buildLoanType(Integer loanType) {
        return LoanType.builder()
                .idLoanType(loanType)
                .build();
    }
}
