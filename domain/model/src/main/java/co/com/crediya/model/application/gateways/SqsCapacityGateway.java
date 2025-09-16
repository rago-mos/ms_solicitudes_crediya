package co.com.crediya.model.application.gateways;

import reactor.core.publisher.Mono;

public interface SqsCapacityGateway {

    <T> Mono<String> send(T message);

}
