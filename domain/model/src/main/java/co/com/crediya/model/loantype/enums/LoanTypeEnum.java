package co.com.crediya.model.loantype.enums;

import co.com.crediya.model.state.enums.StateEnum;

import java.util.Arrays;
import java.util.Optional;

public enum LoanTypeEnum {

    CREDITO_DE_LIBRE_INVERSION(1, "CREDITO DE LIBRE INVERSION"),
    CREDITO_VEHICULO(2, "CREDITO VEHICULO"),
    CREDITO_HIPOTECARIO(3, "CREDITO HIPOTECARIO"),
    CREDITO_EDUCATIVO(4, "CREDITO EDUCATIVO"),
    CREDITO_ROTATIVO(5, "CREDITO ROTATIVO");

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
