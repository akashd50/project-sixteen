package comp3350.project16.Exceptions;

public class InvalidAnswerException extends Exception {
    public InvalidAnswerException(String errorMessage) {
        super(errorMessage);
    }
}
