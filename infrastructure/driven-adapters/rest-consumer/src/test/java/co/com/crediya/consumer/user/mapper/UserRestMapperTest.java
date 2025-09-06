package co.com.crediya.consumer.user.mapper;

import co.com.crediya.consumer.user.UserDocumentsResponse;
import co.com.crediya.model.user.UserApplication;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserRestMapperTest {

    private final UserRestMapper mapper = new UserRestMapper();

    @Test
    void shouldMapUserDocumentsResponseToUserApplication() {

        UserDocumentsResponse response = UserDocumentsResponse.builder()
                .firstName("Rubén")
                .lastName("Tester")
                .email("ruben@example.com")
                .identityDocument("123456789")
                .baseSalary(new BigDecimal("3000000"))
                .build();

        UserApplication result = mapper.toUserApplication(response);

        assertThat(result.getFirstName()).isEqualTo("Rubén");
        assertThat(result.getLastName()).isEqualTo("Tester");
        assertThat(result.getEmail()).isEqualTo("ruben@example.com");
        assertThat(result.getIdentityDocument()).isEqualTo("123456789");
        assertThat(result.getBaseSalary()).isEqualByComparingTo("3000000");
    }
}