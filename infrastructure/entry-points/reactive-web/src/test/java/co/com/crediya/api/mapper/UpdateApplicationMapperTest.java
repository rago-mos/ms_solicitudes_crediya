package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UpdateApplicationRequest;
import co.com.crediya.model.application.UpdateStateApplication;
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

        UpdateApplicationRequest request = new UpdateApplicationRequest("APP123", 5);

        UpdateStateApplication result = mapper.toModel(request);

        assertNotNull(result);
        assertEquals("APP123", result.getIdApplication());
        assertEquals(5, result.getIdState());
    }
}