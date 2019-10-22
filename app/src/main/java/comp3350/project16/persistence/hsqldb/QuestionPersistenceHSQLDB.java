package comp3350.project16.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.MCAnswerPersistence;
import comp3350.project16.persistence.QuestionPersistence;

public class QuestionPersistenceHSQLDB implements QuestionPersistence {

    private final String dbPath;
    private MCAnswerPersistence mcPers;

    public QuestionPersistenceHSQLDB(final String dbPath, final MCAnswerPersistence mcPers) {
        this.dbPath = dbPath;
        this.mcPers = mcPers;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    public int incrementQuestionID()throws PersistenceException{
        int nextID = 0;

        try(Connection c = connection()){

            PreparedStatement getID = c.prepareStatement("SELECT CURRENT_QUESTION_ID FROM PUBLIC.STATICS");
            ResultSet rs = getID.executeQuery();
            rs.next();
            nextID = Integer.parseInt(rs.getString("CURRENT_QUESTION_ID"));
            nextID++;
            PreparedStatement setID = c.prepareStatement("UPDATE PUBLIC.STATICS SET CURRENT_QUESTION_ID=?");
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

    public Question fromResultSet(final ResultSet rs) throws SQLException {

        Question builtQuestion;

        final String type = rs.getString("TYPE");
        final String question = rs.getString("QUESTION");
        final String correctAnswer = rs.getString("ANSWER");
        final String usersAnswer = rs.getString("USERS_ANSWER");
        final int id = Integer.parseInt(rs.getString("QUESTION_ID"));
        final String owner = rs.getString("USERNAME");

        if(type.equals("FIBQuestion")){
            builtQuestion = new FIBQuestion(id, owner);
        }else if(type.equals("MCQuestion")){
            builtQuestion = new MCQuestion(id, owner);
            List<String> answers = mcPers.getMCAnswers(id);
            for(String a : answers){((MCQuestion) builtQuestion).addAnswer(a);}

        }else{
            builtQuestion = new TFQuestion(id, owner);
        }

        builtQuestion.setType(type);
        builtQuestion.setQuestion(question);
        builtQuestion.setCorrectAnswer(correctAnswer);
        builtQuestion.setOwner(owner);

        if(usersAnswer != null){
            try{
                builtQuestion.setUsersAnswer(usersAnswer);
            }catch(InvalidAnswerException e){
                throw new PersistenceException(new InvalidAnswerException("Invalid users answer pulled from database"));
            }
        }

        return builtQuestion;
    }

    public Question getQuestion(int questionID)throws PersistenceException{

        Question result = null;

        try (Connection c = connection()){

            PreparedStatement st = c.prepareStatement("SELECT * FROM PUBLIC.QUESTION WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                result = fromResultSet(rs);
            }else{
                throw new PersistenceException(new SQLException("Question not found"));
            }

            rs.close();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return result;
    }

    @Override
    public void insertQuestion(Question toInsert)throws PersistenceException{

        try(Connection c = connection()){

            PreparedStatement st = c.prepareStatement("INSERT INTO PUBLIC.QUESTION VALUES(?,?,?,?,?,?)");

            st.setString(1, Integer.toString(toInsert.getQID()));
            st.setString(2, toInsert.getOwner());
            st.setString(3, toInsert.getQuestion());
            st.setString(4, toInsert.getCorrectAnswer());
            st.setString(5, toInsert.getUsersAnswer());
            st.setString(6, toInsert.getType());

            st.executeUpdate();

            st.close();

            //If it's a MC question then add the answers to the table
            if(toInsert instanceof MCQuestion){
                mcPers.insertMCAnswers(toInsert);
            }

        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public ArrayList<Question> getAllQuestions(String username)throws PersistenceException{

        ArrayList<Question> questions = new ArrayList<>();

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM QUESTION WHERE USERNAME=?");
            st.setString(1, username);
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                Question q = fromResultSet(rs);
                questions.add(q);
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return questions;
    }

    public boolean deleteQuestion(int questionID)throws PersistenceException{

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("DELETE FROM QUESTION WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }


    @Override
    public boolean setUserAnswer(int questionID, String answer)throws PersistenceException{

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("UPDATE QUESTION SET USERS_ANSWER=? WHERE QUESTION_ID=?");
            st.setString(1, answer);
            st.setString(2, Integer.toString(questionID));
            st.executeUpdate();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }

    @Override
    public boolean updateQuestion(int questionID, Question toUpdate)throws PersistenceException{

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("UPDATE QUESTION SET QUESTION=?, ANSWER=?, USERS_ANSWER=? WHERE QUESTION_ID=?");
            st.setString(1, toUpdate.getQuestion());
            st.setString(2, toUpdate.getCorrectAnswer());
            st.setString(3, toUpdate.getUsersAnswer());
            st.setString(4, Integer.toString(questionID));
            st.executeUpdate();
            st.close();

            //If its a MC question update its answers list
            if(toUpdate instanceof MCQuestion){
                mcPers.updateMCAnswers(toUpdate);
            }

        }catch(SQLException e){
            throw new PersistenceException(e);
        }

        return true;
    }

    @Override
    public boolean updateCorrectAnswer(int questionID, String correctAnswer){
        //update the correct answer in the database.
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("UPDATE QUESTION SET ANSWER=? WHERE QUESTION_ID=?");
            st.setString(1, correctAnswer);
            st.setString(2, Integer.toString(questionID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return true;
    }



}
