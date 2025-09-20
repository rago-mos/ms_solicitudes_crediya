package co.com.crediya.model.application.gateways;

import co.com.crediya.model.loantype.enums.SqsQueueType;
import reactor.core.publisher.Mono;

public interface SqsMessageGateway {

    <T> Mono<String> send(T message, SqsQueueType type);
}
