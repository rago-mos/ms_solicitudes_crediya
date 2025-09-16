package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.request.ApplicationRequest;
import co.com.crediya.model.application.StateApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationMapperTest {

    private UpdateApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UpdateApplicationMapper();
    }

    @Test
    void shouldMapUpdateApplicationRequestToModelCorrectly() {

        ApplicationRequest request = new ApplicationRequest(123L, 5);

        StateApplication result = mapper.toModel(request);

        assertNotNull(result);
        assertEquals(123L, result.getIdApplication());
        assertEquals(5, result.getIdState());
    }
}