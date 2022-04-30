package mk.ukim.finki.covid19_statistics.model.exceptions;

public class CannotDeleteVisitException extends RuntimeException {
    public CannotDeleteVisitException() {
        super("Cannot delete this visit. Try deleting it from the referrals page.");
    }
}
