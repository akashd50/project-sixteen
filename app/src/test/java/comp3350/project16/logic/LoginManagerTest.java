package comp3350.project16.logic;

import org.junit.Test;
import static org.junit.Assert.*;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.application.Services;
import comp3350.project16.persistence.UserPersistence;
import comp3350.project16.objects.User;
import comp3350.project16.presentation.CurrentUser;

public class LoginManagerTest {


    private UserPersistence userPersistence = Services.getUserPersistence();

    @Test(expected = UserNotFoundException.class)
    public void testInvalidUser()throws UserNotFoundException, InvalidInputException{

        System.out.println("\nStarting testInvalidUser");
        User invalidUser=new User("INVALIDUSER", "fakeemail@email.com", "insecurepassword1");
        assertEquals(invalidUser.getUsername(), "INVALIDUSER");
        LoginManager loginManager = new LoginManager(userPersistence);
        loginManager.attemptLogin(invalidUser.getUsername(), invalidUser.getPassword());

        System.out.println("Finished testing testInvalidUser");
    }

    @Test(expected = InvalidInputException.class)
    public void testInvalidInput()throws UserNotFoundException, InvalidInputException{

        System.out.println("Starting testInvalidInput");
        LoginManager loginManager = new LoginManager(userPersistence);
        String emptyField="";
        loginManager.attemptLogin(emptyField,emptyField);
        System.out.println("Finished testing testInvalidInput");
    }

    @Test
    public void testProperLogin()throws UserNotFoundException, InvalidInputException{
        System.out.println("Starting testProperLogin");
        User testUser = new User("tester1", "tester@test.com", "test");
        LoginManager loginManager = new LoginManager(userPersistence);
        //add new user to database
        userPersistence.addUser(testUser);
        //try to log in as testUser
        assertEquals(loginManager.attemptLogin(testUser.getUsername(),testUser.getPassword()), testUser.getUsername());
        System.out.println("Finished testing testProperLogin");
    }
}
