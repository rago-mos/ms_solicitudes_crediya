package co.com.crediya.model.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {

    @Test
    void shouldBuildStateCorrectly() {
        State state = State.builder()
                .idState(1)
                .name("Approved")
                .description("Solicitud aprobada")
                .build();

        assertEquals(1, state.getIdState());
        assertEquals("Approved", state.getName());
        assertEquals("Solicitud aprobada", state.getDescription());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        State state = new State();

        state.setIdState(2);
        state.setName("Rejected");
        state.setDescription("Solicitud rechazada");

        assertEquals(2, state.getIdState());
        assertEquals("Rejected", state.getName());
        assertEquals("Solicitud rechazada", state.getDescription());
    }
}