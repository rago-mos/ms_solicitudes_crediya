package co.com.crediya.consumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectResponseTest {

    @Test
    void shouldBuildObjectResponseCorrectly() {
        ObjectResponse response = ObjectResponse.builder()
                .exists(true)
                .build();

        assertTrue(response.getExists());
    }

    @Test
    void shouldSetAndGetExistsFieldCorrectly() {
        ObjectResponse response = new ObjectResponse();
        response.setExists(false);

        assertFalse(response.getExists());
    }

    @Test
    void shouldCreateObjectResponseWithAllArgsConstructor() {
        ObjectResponse response = new ObjectResponse(true);

        assertTrue(response.getExists());
    }
}