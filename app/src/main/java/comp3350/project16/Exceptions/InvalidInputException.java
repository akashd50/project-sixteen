package comp3350.project16.Exceptions;

public class InvalidInputException extends Exception {

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}