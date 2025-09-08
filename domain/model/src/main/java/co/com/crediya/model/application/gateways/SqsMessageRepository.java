package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.dto.UpdateApplicationView;
import reactor.core.publisher.Mono;

public interface SqsMessageRepository {

    Mono<String> send(UpdateApplicationView message);
}
