package co.com.crediya.model.utils;

public class Constant {

    public static final String STATE_ERROR = "State not found";
    public static final String LOAN_TYPE_ERROR = "The loan type does not exist";
    public static final String AMOUNT_ERROR = "The amount is not valid; it must be between %s and %s";
    public static final String USER_ERROR = "User does not exist";
    public static final String FORBIDDEN = "Forbidden";
    public static final String MESSAGGE_ERROR_FORBIDDEN = "Document does not match authenticated user";
    public static final String LOG_WARN_FORBIDDEN = "Identity mismatch: token sub = {}, request document = {}";
    public static final String LOG_INFO_CREATED = "Application created successfully: {}";
    public static final String ERROR_BAD_TOKEN = "bad token";
    public static final String ERROR_STATE = "state not found";
    public static final String ERROR_LOAN_TYPE = "loan type not found";
    public static final String ERROR_ACCES_DENIED = "Access denied. You do not have the necessary permissions for this resource";

    private Constant(){
        throw new UnsupportedOperationException("util class");
    }
}
