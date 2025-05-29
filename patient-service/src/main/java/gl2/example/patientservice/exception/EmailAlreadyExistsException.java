package gl2.example.patientservice.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}