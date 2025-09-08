package co.com.crediya.model.loantype.enums;

import co.com.crediya.model.state.enums.StateEnum;

import java.util.Arrays;
import java.util.Optional;

public enum LoanTypeEnum {

    CREDITO_DE_LIBRE_INVERSION(1, "CREDITO_DE_LIBRE_INVERSION"),
    CREDITO_VEHICULO(2, "CREDITO_VEHICULO"),
    CREDITO_HIPOTECARIO(3, "CREDITO_HIPOTECARIO"),
    CREDITO_EDUCATIVO(4, "CREDITO_EDUCATIVO"),
    CREDITO_ROTATIVO(5, "CREDITO_ROTATIVO");

    private final int id;
    private final String name;

    LoanTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<LoanTypeEnum> fromId(int id) {
        return Arrays.stream(values())
                .filter(loanType -> loanType.id == id)
                .findFirst();
    }

    public static String getNameById(int id) {
        return fromId(id)
                .map(LoanTypeEnum::getname)
                .orElse("UNKNOWN");
    }

    public String getname() {
        return name;
    }
}
