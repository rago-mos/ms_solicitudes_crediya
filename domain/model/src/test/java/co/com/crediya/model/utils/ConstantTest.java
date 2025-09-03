package co.com.crediya.model.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConstantTest {

    @Test
    void shouldHaveExpectedConstantValues() {
        assertThat(Constant.STATE_ERROR).isEqualTo("State not found");
        assertThat(Constant.LOAN_TYPE_ERROR).isEqualTo("The loan type does not exist");
        assertThat(Constant.AMOUNT_ERROR).isEqualTo("The amount is not valid; it must be between %s and %s");
        assertThat(Constant.USER_ERROR).isEqualTo("User does not exist");
        assertThat(Constant.FORBIDDEN).isEqualTo("Forbidden");
        assertThat(Constant.MESSAGGE_ERROR_FORBIDDEN).isEqualTo("Document does not match authenticated user");
        assertThat(Constant.LOG_WARN_FORBIDDEN).isEqualTo("Identity mismatch: token sub = {}, request document = {}");
        assertThat(Constant.LOG_INFO_CREATED).isEqualTo("Application created successfully: {}");
        assertThat(Constant.ERROR_BAD_TOKEN).isEqualTo("bad token");
        assertThat(Constant.ERROR_STATE).isEqualTo("state not found");
        assertThat(Constant.ERROR_LOAN_TYPE).isEqualTo("loan type not found");
        assertThat(Constant.ERROR_ACCES_DENIED).isEqualTo("Access denied. You do not have the necessary permissions for this resource");
    }

    @Test
    void shouldNotAllowInstantiation() throws Exception {
        Constructor<Constant> constructor = Constant.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("util class", exception.getCause().getMessage());
    }
}