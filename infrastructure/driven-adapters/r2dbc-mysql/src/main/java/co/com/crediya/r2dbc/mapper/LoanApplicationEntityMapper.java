package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.ApplicationAprovedView;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.ApplicationAprovedViewEntity;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.entities.LoanApplicationViewEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoanApplicationEntityMapper {

    public ApplicationEntity toEntity(Application application) {
        return ApplicationEntity.builder()
                .idApplication(application.getIdApplication())
                .amount(application.getAmount())
                .term(application.getTerm())
                .identityDocument(application.getIdentityDocument())
                .idState(application.getState().getIdState())
                .idLoanType(application.getLoanType().getIdLoanType())
                .date(application.getDate())
                .build();
    }

    public Application toDomain(ApplicationEntity entity) {
        return Application.builder()
                .idApplication(entity.getIdApplication())
                .amount(entity.getAmount())
                .term(entity.getTerm())
                .identityDocument(entity.getIdentityDocument())
                .state(buildState(entity.getIdState()))
                .loanType(buildLoanType(entity.getIdLoanType()))
                .date(entity.getDate())
                .build();
    }

    public LoanApplicationView toView(LoanApplicationViewEntity entity) {
        return LoanApplicationView.builder()
                .amount(entity.getAmount())
                .monthTerm(entity.getMonthTerm())
                .identityDocument(entity.getIdentityDocument())
                .monthAmountApprovedApplication(entity.getMonthAmountApprovedApplication())
                .statusName(entity.getStatusName())
                .interestRate(entity.getInterestRate())
                .loanTypeName(entity.getLoanTypeName())
                .baseSalary(entity.getBaseSalary())
                .build();
    }

    public ApplicationAprovedView toViewAproved(ApplicationAprovedViewEntity entity) {
        return ApplicationAprovedView.builder()
                .amount(entity.getMonto())
                .term(entity.getPlazo())
                .interest(entity.getTasaInteres())
                .build();
    }

    private State buildState(Integer state) {
        if (state == null) return null;

        return State.builder()
                .idState(state)
                .build();
    }

    private LoanType buildLoanType(Integer loanType) {
        if (loanType == null) return null;

        return LoanType.builder()
                .idLoanType(loanType)
                .build();
    }
}
