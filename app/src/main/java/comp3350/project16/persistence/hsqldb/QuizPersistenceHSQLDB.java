package comp3350.project16.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;

public class QuizPersistenceHSQLDB implements QuizPersistence {

    private final String dbPath;
    private final QuestionPersistence qp;

    public QuizPersistenceHSQLDB(final String dbPath, final QuestionPersistence qp) {
        this.dbPath = dbPath;
        this.qp = qp;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    @Override
    public int incrementQuizID(){
        int nextID = 0;

        try(Connection c = connection()){

            PreparedStatement getID = c.prepareStatement("SELECT CURRENT_QUIZ_ID FROM PUBLIC.STATICS");
            ResultSet rs = getID.executeQuery();
            rs.next();
            nextID = Integer.parseInt(rs.getString("CURRENT_QUIZ_ID"));
            nextID++;
            PreparedStatement setID = c.prepareStatement("UPDATE PUBLIC.STATICS SET CURRENT_QUIZ_ID=?");
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

    private Quiz fromResultSet(final ResultSet rs) throws SQLException {

        Quiz builtQuiz = null;

        //Build quiz basics
        final String quizName = rs.getString("QUIZ_NAME");
        final String username = rs.getString("USERNAME");
        final int ID = Integer.valueOf(rs.getString("QUIZ_ID"));

        builtQuiz = new Quiz(quizName, ID, username);
        builtQuiz.setOwner(username);

        //Retrieve the questions
        try(Connection c = connection()){

            //Insert the basic quiz values
            PreparedStatement st = c.prepareStatement("SELECT * FROM QUIZQUESTIONS WHERE QUIZ_ID=?");
            st.setString(1, Integer.toString(ID));
            ResultSet questionsResult = st.executeQuery();

            while(questionsResult.next()){
                int questionID = Integer.valueOf(questionsResult.getString("QUESTION_ID"));
                builtQuiz.addQuestion(qp.getQuestion(questionID));
            }
            questionsResult.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return builtQuiz;
    }

    @Override
    public Quiz getQuiz(int quizID) {

        Quiz built = null;

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM QUIZ WHERE QUIZ_ID=?");
            st.setString(1, Integer.toString(quizID));

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                built = fromResultSet(rs);
            }else{
                st.close();
                rs.close();
                throw new PersistenceException(new Exception("Could not retrieve quiz from database"));
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return built;
    }

    @Override
    public boolean insertQuiz(Quiz toInsert)throws PersistenceException{

        try(Connection c = connection()){

            //Insert the basic quiz values
            PreparedStatement st = c.prepareStatement("INSERT INTO QUIZ VALUES(?,?,?,?)");
            st.setString(1, Integer.toString(toInsert.getID()));
            st.setString(2, toInsert.getOwner());
            st.setString(3, toInsert.getName());
            st.setString(4, Integer.toString(toInsert.getQuestionsList().size()));
            st.executeUpdate();

            //Insert the quiz question mappings
            for(Question q : toInsert.getQuestionsList()){
                st = c.prepareStatement("INSERT INTO QUIZQUESTIONS VALUES(?,?)");
                st.setString(1, Integer.toString(toInsert.getID()));
                st.setString(2, Integer.toString(q.getQID()));
                st.executeUpdate();
            }

            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }

    @Override
    public boolean deleteQuiz(int quizID)throws PersistenceException{
        try(Connection c = connection()){

            //Delete quiz
            PreparedStatement st = c.prepareStatement("DELETE FROM QUIZ WHERE QUIZ_ID=?");
            st.setString(1, Integer.toString(quizID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }

    @Override
    public ArrayList<Quiz> getQuizList(String user) {
        ArrayList<Quiz> quizzes = new ArrayList<>();

        try(Connection c = connection()){
            //Retrieve all quizzes
            PreparedStatement st = c.prepareStatement("SELECT * FROM QUIZ WHERE USERNAME=?");
            st.setString(1, user);
            ResultSet rs = st.executeQuery();

            //Read all quizzes from the result set
            while(rs.next()){
                quizzes.add(fromResultSet(rs));
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return quizzes;
    }

    @Override
    public boolean findQuiz(String name)throws PersistenceException{

        boolean result = false;

        try(Connection c = connection()){
            //Try to find the quiz
            PreparedStatement st = c.prepareStatement("SELECT * FROM QUIZ WHERE QUIZ_NAME=?");
            st.setString(1, name);
            ResultSet rs = st.executeQuery();

            //Read all quizzes from the result set
            if(rs.next()){
                result = true;
            }
            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return result;
    }

    @Override
    public void updateQuizName(int ID, String name)throws PersistenceException{

        try(Connection c = connection()){
            //Try to find the quiz
            PreparedStatement st = c.prepareStatement("UPDATE QUIZ SET QUIZ_NAME=? WHERE QUIZ_ID=?");
            st.setString(1, name);
            st.setString(2, Integer.toString(ID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public void addQuestionToQuiz(int quizID, int questionID)throws PersistenceException{

        try(Connection c = connection()){
            //Try to find the quiz
            PreparedStatement st = c.prepareStatement("INSERT INTO QUIZQUESTIONS VALUES(?,?)");
            st.setString(1, Integer.toString(quizID));
            st.setString(2, Integer.toString(questionID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public boolean removeQuestionFromQuiz(int quizID, Question toRemove)throws PersistenceException{
        try(Connection c = connection()){
            //Try to find the quiz
            PreparedStatement st = c.prepareStatement("DELETE FROM QUIZQUESTIONS WHERE QUIZ_ID=? AND QUESTION_ID=?");
            st.setString(1, Integer.toString(quizID));
            st.setString(2, Integer.toString(toRemove.getQID()));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }
}
