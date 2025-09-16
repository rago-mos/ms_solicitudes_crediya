package co.com.crediya.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterRest {

    private static final String REGISTER_APPLICATION_LOAN = "/api/v1/solicitud";
    private static final String DEBT_CAPACITY = "/api/v1/calcular-capacidad";

    @Bean
    @RouterOperations({
            @RouterOperation(method = RequestMethod.POST,
                    path = REGISTER_APPLICATION_LOAN,
                    beanClass = Handler.class,
                    beanMethod = "listenPOSTApplicationLoan"
            ),
            @RouterOperation(method = RequestMethod.POST,
                    path = DEBT_CAPACITY,
                    beanClass = Handler.class,
                    beanMethod = "listenPostDebtCapacity"
            ),
            @RouterOperation(method = RequestMethod.GET,
                    path = REGISTER_APPLICATION_LOAN,
                    beanClass = Handler.class,
                    beanMethod = "listenGetApplicationLoan"
            ),
            @RouterOperation(method = RequestMethod.PUT,
                    path = REGISTER_APPLICATION_LOAN,
                    beanClass = Handler.class,
                    beanMethod = "listenPutApplicationLoan"
            ),
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(REGISTER_APPLICATION_LOAN), handler::listenPOSTApplicationLoan)
                .andRoute(POST(DEBT_CAPACITY), handler::listenPostDebtCapacity)
                .andRoute(GET(REGISTER_APPLICATION_LOAN), handler::listenGetApplicationLoan)
                .andRoute(PUT(REGISTER_APPLICATION_LOAN), handler::listenPutApplicationLoan);

    }
}
