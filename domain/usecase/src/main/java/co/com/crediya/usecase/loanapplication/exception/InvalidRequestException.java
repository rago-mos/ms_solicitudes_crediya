package co.com.crediya.usecase.loanapplication.exception;


public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message) {
        super(message);
    }
}

