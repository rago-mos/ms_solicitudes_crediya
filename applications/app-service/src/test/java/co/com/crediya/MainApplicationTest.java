package co.com.crediya;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = MainApplication.class,
        properties = {
                "jwt.secret=my-super-secret-key-12345678901234567890123456789012"
        }
)
class MainApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldRunMainWithoutErrors() {
        System.setProperty("jwt.secret", "my-super-secret-key-12345678901234567890123456789012");

        MainApplication.main(new String[]{});
    }
}