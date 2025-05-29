package gl2.example.patientservice.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String message) {
        super(message);
    }
}