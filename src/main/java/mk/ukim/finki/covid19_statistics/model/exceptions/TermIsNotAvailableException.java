package mk.ukim.finki.covid19_statistics.model.exceptions;

import java.time.LocalDateTime;

public class TermIsNotAvailableException extends RuntimeException {
    public TermIsNotAvailableException(LocalDateTime term) {
        super(String.format("The term %s is not available!", term.toString()));
    }
}
