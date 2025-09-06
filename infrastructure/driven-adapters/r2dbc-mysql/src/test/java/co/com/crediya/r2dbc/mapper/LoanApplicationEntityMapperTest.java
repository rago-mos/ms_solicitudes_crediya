package co.com.crediya.r2dbc.mapper;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.entities.LoanApplicationViewEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationEntityMapperTest {

    private LoanApplicationEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoanApplicationEntityMapper();
    }

    @Test
    void shouldMapApplicationToEntityCorrectly() {
        Application application = Application.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("500000"))
                .term(12)
                .identityDocument("123456789")
                .state(State.builder().idState(1).build())
                .loanType(LoanType.builder().idLoanType(2).build())
                .date(LocalDate.of(2025, 8, 31))
                .build();

        ApplicationEntity entity = mapper.toEntity(application);

        assertEquals("APP-001", entity.getIdApplication());
        assertEquals(new BigDecimal("500000"), entity.getAmount());
        assertEquals(12, entity.getTerm());
        assertEquals("123456789", entity.getIdentityDocument());
        assertEquals(1, entity.getIdState());
        assertEquals(2, entity.getIdLoanType());
        assertNull(entity.getDate()); // porque no se mapea en toEntity
    }

    @Test
    void shouldMapEntityToApplicationCorrectly() {
        ApplicationEntity entity = ApplicationEntity.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("500000"))
                .term(12)
                .identityDocument("123456789")
                .idState(1)
                .idLoanType(2)
                .date(LocalDate.of(2025, 8, 31))
                .build();

        Application application = mapper.toDomain(entity);

        assertEquals("APP-001", application.getIdApplication());
        assertEquals(new BigDecimal("500000"), application.getAmount());
        assertEquals(12, application.getTerm());
        assertEquals("123456789", application.getIdentityDocument());
        assertEquals(1, application.getState().getIdState());
        assertEquals(2, application.getLoanType().getIdLoanType());
        assertNull(application.getDate()); // porque no se mapea en toDomain
    }

    @Test
    void shouldReturnNullStateAndLoanTypeWhenIdsAreNull() {
        ApplicationEntity entity = ApplicationEntity.builder()
                .idApplication("APP-002")
                .amount(new BigDecimal("100000"))
                .term(6)
                .identityDocument("987654321")
                .idState(null)
                .idLoanType(null)
                .build();

        Application application = mapper.toDomain(entity);

        assertNull(application.getState());
        assertNull(application.getLoanType());
    }

    @Test
    void shouldMapEntityToViewCorrectly() {
        // Arrange
        LoanApplicationViewEntity entity = LoanApplicationViewEntity.builder()
                .amount(new BigDecimal("1000000"))
                .monthTerm(12)
                .identityDocument("123456789")
                .monthAmountApprovedApplication(new BigDecimal("85000"))
                .statusName("Approved")
                .interestRate(new BigDecimal("0.05"))
                .loanTypeName("Personal")
                .baseSalary(new BigDecimal("3000000"))
                .fullName("Rubén Tester") // no se mapea
                .email("ruben@example.com") // no se mapea
                .build();

        LoanApplicationView result = mapper.toView(entity);

        assertThat(result.getAmount()).isEqualByComparingTo("1000000");
        assertThat(result.getMonthTerm()).isEqualTo(12);
        assertThat(result.getIdentityDocument()).isEqualTo("123456789");
        assertThat(result.getMonthAmountApprovedApplication()).isEqualByComparingTo("85000");
        assertThat(result.getStatusName()).isEqualTo("Approved");
        assertThat(result.getInterestRate()).isEqualByComparingTo("0.05");
        assertThat(result.getLoanTypeName()).isEqualTo("Personal");
        assertThat(result.getBaseSalary()).isEqualByComparingTo("3000000");

        assertThat(result.getFullName()).isNull();
        assertThat(result.getEmail()).isNull();
    }
}