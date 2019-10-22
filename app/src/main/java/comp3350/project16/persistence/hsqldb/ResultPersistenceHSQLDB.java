package comp3350.project16.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.Result;
import comp3350.project16.persistence.ResultPersistence;

public class ResultPersistenceHSQLDB implements ResultPersistence{

    private final String dbPath;

    public ResultPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    public Result buildResult(final ResultSet resultSet) throws SQLException{
        Result builtResult;

        final int resultID = Integer.parseInt(resultSet.getString("RESULT_ID"));
        final int quizID = Integer.parseInt(resultSet.getString("QUIZ_ID"));
        final int score = Integer.parseInt(resultSet.getString("SCORE"));
        final String date = resultSet.getString("DATE");
        final String quizName = resultSet.getString("QUIZ_NAME");
        final String userName = resultSet.getString("USERNAME");
        final int maxScore = Integer.parseInt(resultSet.getString("MAX_SCORE"));
        //build the Result object from the above variables
        builtResult = new Result(resultID, quizID, quizName, userName, maxScore);
        builtResult.setScore(score);
        builtResult.setDate(date);

        return builtResult;
    }

    @Override
    public Result getResult(int resultID){
        Result result = null;

        try (Connection c = connection()){

            PreparedStatement st = c.prepareStatement("SELECT * FROM PUBLIC.RESULT WHERE RESULT_ID=?");
            st.setString(1, Integer.toString(resultID));

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                result = buildResult(rs);
            }else{
                throw new PersistenceException(new SQLException("Result not found"));
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return result;
    }

    @Override
    public void insertResult(Result toInsert)throws PersistenceException{
        try(Connection c = connection()){

            //Insert the results values
            PreparedStatement st = c.prepareStatement("INSERT INTO RESULT VALUES(?,?,?,?,?,?,?)");
            st.setString(1, Integer.toString(toInsert.getResultID()));
            st.setString(2, Integer.toString(toInsert.getQuizID()) );
            st.setString(3, Integer.toString(toInsert.getScore()) );
            st.setString(4, toInsert.getDate());
            st.setString(5, toInsert.getQuizName());
            st.setString(6, toInsert.getUserName());
            st.setString(7, Integer.toString(toInsert.getMaxScore()));
            st.executeUpdate();

            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public int incrementResultID() throws PersistenceException{
        int nextID = 0;

        try(Connection c = connection()){

            PreparedStatement getID = c.prepareStatement("SELECT CURRENT_RESULT_ID FROM PUBLIC.STATICS");
            ResultSet rs = getID.executeQuery();
            rs.next();
            nextID = Integer.parseInt(rs.getString("CURRENT_RESULT_ID"));
            nextID++;
            PreparedStatement setID = c.prepareStatement("UPDATE PUBLIC.STATICS SET CURRENT_RESULT_ID=?");
            setID.setString(1, Integer.toString(nextID));
            setID.executeUpdate();

            rs.close();
            getID.close();
            setID.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return nextID;
    }

    @Override
    public ArrayList<Result> getAllResults(String userName) throws PersistenceException{
        ArrayList<Result>userResults = new ArrayList<Result>();

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM RESULT WHERE USERNAME=?");
            st.setString(1, userName);
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                Result builtResult = buildResult(rs);
                userResults.add(builtResult);
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return userResults;
    }

    public ArrayList<Result> getAllResultsQName(String quizName) throws PersistenceException{
        ArrayList<Result>userResults = new ArrayList<Result>();

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM RESULT WHERE QUIZ_NAME=?");
            st.setString(1, quizName);
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                Result builtResult = buildResult(rs);
                userResults.add(builtResult);
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return userResults;
    }
}
