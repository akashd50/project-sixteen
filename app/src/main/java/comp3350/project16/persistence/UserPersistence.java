package comp3350.project16.persistence;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.objects.User;

public interface UserPersistence {
    boolean addUser(User toAdd);
    String attemptLogin(String loginCredentials, String password)throws PersistenceException, UserNotFoundException;
}
