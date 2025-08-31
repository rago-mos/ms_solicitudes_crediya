package co.com.crediya.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateResponseTest {

    @Test
    void shouldBuildStateResponseCorrectly() {
        StateResponse response = StateResponse.builder()
                .name("Approved")
                .description("Solicitud aprobada")
                .build();

        assertEquals("Approved", response.getName());
        assertEquals("Solicitud aprobada", response.getDescription());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        StateResponse response = new StateResponse();

        response.setName("Rejected");
        response.setDescription("Solicitud rechazada");

        assertEquals("Rejected", response.getName());
        assertEquals("Solicitud rechazada", response.getDescription());
    }
}