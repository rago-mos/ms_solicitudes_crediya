package co.com.crediya.model.state.enums;

import co.com.crediya.model.loantype.enums.LoanTypeEnum;

import java.util.Arrays;
import java.util.Optional;

public enum StateEnum {

    PENDIENTE_REVISION(1, "PENDIENTE REVISION"),
    RECHAZADA(2, "RECHAZADA"),
    REVISION_MANUAL(3, "REVISION MANUAL"),
    APROBADA(4, "APROBADA");

    private final int id;
    private final String name;

    StateEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<StateEnum> fromId(int id) {
        return Arrays.stream(values())
                .filter(state -> state.id == id)
                .findFirst();
    }

    public static String getNameById(int id) {
        return fromId(id)
                .map(StateEnum::getname)
                .orElse("UNKNOWN");
    }

    public String getname() {
        return name;
    }
}
