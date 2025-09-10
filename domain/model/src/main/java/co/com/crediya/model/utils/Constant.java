package co.com.crediya.model.utils;

public class Constant {

    public static final String STATE_ERROR = "State not found";
    public static final String LOAN_TYPE_ERROR = "The loan type does not exist";
    public static final String AMOUNT_ERROR = "The amount is not valid; it must be between %s and %s";
    public static final String USER_ERROR = "User does not exist";
    public static final String FORBIDDEN = "Forbidden";
    public static final String MESSAGE_ERROR_FORBIDDEN = "Document does not match authenticated user";

    public static final String LOG_WARN_FORBIDDEN = "Identity mismatch: token sub = {}, request document = {}";
    public static final String LOG_INFO_CREATED = "Application created successfully: {}";
    public static final String LOG_INFO_UPDATE = "Application updated successfully: {}";
    public static final String LOG_VALIDATING_USER = "Validating user by document number: {}";
    public static final String LOG_QUERY_USER = "Service: Querying users by document";
    public static final String LOG_INFO_RESULT_APPLICATIONS = "Satisfactory query with this number of elements: {}";
    public static final String LOG_DEBUG_SQS_SEND = "Message sent {}";

    public static final String STATUS_OK = "CODE_001";

    public static final String ERROR_BAD_TOKEN = "bad token";
    public static final String ERROR_JSON_PROCESSING = "Error serializing message";
    public static final String ERROR_STATE = "state not found";
    public static final String ERROR_LOAN_TYPE = "loan type not found";
    public static final String ERROR_ACCES_DENIED = "Access denied. You do not have the necessary permissions for this resource";
    public static final String ERROR_ACCES_DENIED_ARGUMENT = "Access denied. You do not have the necessary permissions for this resource: {}";
    public static final String ERROR_EXTRACT_STATUS = "Invalid status value: ";
    public static final String ERROR_NOT_FOUND_APPLICATION = "Application not found";
    public static final String ERROR_BUSINNESS_UPDATE_STATE_APPLICATION = "The state to update is not valid";
    public static final String ERROR_BUSINNESS_UPDATE_STATE_APPLICATION_ALREADY = "The application status is already approved or rejected";

    public static final String URL_CONSUMER_USER_DOCUMENT = "/api/v1/usuarios/{documentIdentity}";
    public static final String URL_CONSUMER_USER_APPLICATION = "/api/v1/usuarioSolicitudes";

    public static final String MESSAGE_UPDATED_APPLICATION = "state updated successfully";


    private Constant(){
        throw new UnsupportedOperationException("util class");
    }
}
