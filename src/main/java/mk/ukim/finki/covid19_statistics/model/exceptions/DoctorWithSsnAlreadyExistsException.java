package mk.ukim.finki.covid19_statistics.model.exceptions;

public class DoctorWithSsnAlreadyExistsException extends RuntimeException {
    public DoctorWithSsnAlreadyExistsException() {
        super(String.format("Постои доктор со дадениот ЕМБГ"));
    }
}
