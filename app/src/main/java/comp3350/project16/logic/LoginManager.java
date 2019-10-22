package comp3350.project16.logic;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.persistence.UserPersistence;

public class LoginManager {

    private UserPersistence userPersistence;

    public LoginManager(UserPersistence up){
        userPersistence=up;
    }

    public String attemptLogin(String loginCred, String password) throws PersistenceException, InvalidInputException, UserNotFoundException {

        String user;

        validateLoginInfo(loginCred, password);
        user = userPersistence.attemptLogin(loginCred, password);

        return user;
    }

    private boolean validateLoginInfo(String login, String password) throws InvalidInputException{
        if(login.length()==0){
            throw new InvalidInputException("login field cannot be empty");
        }

        if(password.length()==0){
            throw new InvalidInputException("password field cannot be empty");
        }

        if(login.length()>19){
            throw new InvalidInputException("usernames cannot have more than 20 characters");
        }

        if(password.length()>19){
            throw new InvalidInputException("passwords cannot have more than 20 characters");
        }
        return true;
    }
}
