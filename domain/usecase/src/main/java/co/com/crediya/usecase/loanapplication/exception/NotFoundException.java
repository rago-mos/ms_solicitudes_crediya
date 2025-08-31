package co.com.crediya.usecase.loanapplication.exception;


public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }
}

