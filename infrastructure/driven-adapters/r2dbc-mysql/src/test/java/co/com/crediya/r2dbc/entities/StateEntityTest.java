package co.com.crediya.r2dbc.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateEntityTest {

    @Test
    void shouldBuildStateEntityCorrectly() {
        StateEntity entity = StateEntity.builder()
                .idState(1)
                .name("Approved")
                .description("Application approved")
                .build();

        assertEquals(1, entity.getIdState());
        assertEquals("Approved", entity.getName());
        assertEquals("Application approved", entity.getDescription());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        StateEntity entity = new StateEntity();

        entity.setIdState(2);
        entity.setName("Rejected");
        entity.setDescription("Application rejected");

        assertEquals(2, entity.getIdState());
        assertEquals("Rejected", entity.getName());
        assertEquals("Application rejected", entity.getDescription());
    }
}