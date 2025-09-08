package co.com.crediya.usecase.loanapplication.validator;

import co.com.crediya.model.application.UpdateStateApplication;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.NotFoundException;
import co.com.crediya.model.state.gateways.StateRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.crediya.model.utils.Constant.*;

@RequiredArgsConstructor
public class UpdateApplicationValidator {

    private final StateRepository stateRepository;
    private final ApplicationRepository applicationRepository;

    public Mono<Void> validate(UpdateStateApplication application) {
        return validateExistsState(application)
                .then(validateExistsApplication(application))
                .then(validateApplicationState(application))
                .then(validateState(application));
    }

    /**
     * @use Válida que el estado exista
     */
    private Mono<Void> validateExistsState(UpdateStateApplication application) {
        return stateRepository.existsState(application.getIdState())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException(STATE_ERROR)));
    }

    /**
     * @use Válida que la solicitud exista
     */
    private Mono<Void> validateExistsApplication(UpdateStateApplication application) {
        return applicationRepository.existsApplication(application.getIdApplication())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException(ERROR_NOT_FOUND_APPLICATION)));
    }

    /**
     * @use Válida que el estado que llega desde el request sea diferente a PENDIENTE_REVISION
     */
    private Mono<Void> validateState(UpdateStateApplication application) {
        return Mono.justOrEmpty(application)
                .handle((app, sink) -> {
                    if (app.getIdState() == 1) {
                        sink.error(new BusinessException(ERROR_BUSINNESS_UPDATE_STATE_APPLICATION));
                    } else {
                        sink.complete();
                    }
                });
    }

    /**
     * @use Válida que la solicitud que se quiere actualizar ya no este en estado APROBADO o RECHAZADO
     */
    private Mono<Void>  validateApplicationState(UpdateStateApplication application) {
        return applicationRepository.getApplication(application.getIdApplication())
                .flatMap(result ->  Mono.justOrEmpty(result)
                        .handle((app, sink) -> {
                            if (app.getState().getIdState() == 2 || app.getState().getIdState() == 4) {
                                sink.error(new BusinessException(ERROR_BUSINNESS_UPDATE_STATE_APPLICATION_ALREADY));
                            } else {
                                sink.complete();
                            }
                        }));
    }
}
