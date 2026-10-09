package exceptions;

public sealed class HospitalException extends Exception
        permits DuplicateRecordException, InvalidAppointmentException, PaymentException {
    public HospitalException(String message) {
        super(message);
    }
}
