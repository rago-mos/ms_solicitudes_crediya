package co.com.crediya.usecase.loanapplication.exception;


public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

}
