package comp3350.project16.persistence.hsqldb;

import android.provider.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.objects.User;
import comp3350.project16.persistence.UserPersistence;

public class UserPersistenceHSQLDB implements UserPersistence {

    private final String dbPath;

    public UserPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    @Override
    public boolean addUser(User toAdd) throws PersistenceException {
        try(Connection c = connection()){

            //Insert user values to db
            PreparedStatement st = c.prepareStatement("INSERT INTO USERS VALUES(?,?,?)");
            st.setString(1, toAdd.getUsername());
            st.setString(2, toAdd.getPassword());
            st.setString(3, toAdd.getEmail());
            st.executeUpdate();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }

    @Override
    public String attemptLogin(String loginCredentials, String password)throws PersistenceException, UserNotFoundException{

        String user = "";

        try(Connection c = connection()){
            //search for users with the same password and credentials
            PreparedStatement st=c.prepareStatement("SELECT * FROM USERS WHERE (USERNAME = ? OR EMAIL = ?) AND PASSWORD = ?");
            st.setString(1, loginCredentials);
            st.setString(2, loginCredentials);
            st.setString(3, password);
            ResultSet rs = st.executeQuery();

            if(!rs.next()){
                throw new UserNotFoundException("Invalid username/password");
            }else{
                user = rs.getString("USERNAME");
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return user;
    }
}
