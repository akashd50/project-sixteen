package comp3350.project16.persistence.stubs;

import java.sql.SQLException;
import java.util.ArrayList;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.persistence.UserPersistence;
import comp3350.project16.objects.User;

public class UserPersistenceStub implements UserPersistence {
    private ArrayList<User> users;

    public UserPersistenceStub(){
        users= new ArrayList<>();
        User admin = new User("admin", "admin@email.com", "admin");
        users.add(admin);
    }

    @Override
    public boolean addUser(User toAdd) {
        return users.add(toAdd);
    }

    @Override
    public String attemptLogin(String loginCredentials, String password)throws PersistenceException, UserNotFoundException {

        String user = "";
        boolean found = false;

        for(int i = 0; i < users.size() && !found; i++){//return true if supplied credentials(username or email) match to an existing user and the passwords match

            User u = users.get(i);
            if(loginCredentials.compareToIgnoreCase(u.getUsername())==0 || loginCredentials.compareToIgnoreCase(u.getEmail()) ==0 && password.compareTo(u.getPassword())==0) {
                user = u.getUsername();
                found = true;
            }
        }

        if(!found){
            throw new UserNotFoundException("Invalid username/password");
        }

        return user;
    }
}
