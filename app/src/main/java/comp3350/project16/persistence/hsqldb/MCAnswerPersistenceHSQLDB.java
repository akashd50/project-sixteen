package comp3350.project16.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.persistence.MCAnswerPersistence;

public class MCAnswerPersistenceHSQLDB implements MCAnswerPersistence {

    private final String dbPath;

    public MCAnswerPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    @Override
    public ArrayList<String> getMCAnswers(int questionID){

        ArrayList<String> answers = new ArrayList<>();

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT * FROM MCANSWERS WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                answers.add(rs.getString("ANSWER"));
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            System.out.println("COULD NOT GET FROM MCANSWER");
            throw new PersistenceException(e);
        }

        return answers;
    }

    @Override
    public void deleteMCAnswer(int questionID, String toDelete){
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("DELETE FROM MCANSWERS WHERE QUESTION_ID=? AND ANSWER=?");
            st.setString(1, Integer.toString(questionID));
            st.setString(2,toDelete);
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            System.out.println("COULD NOT DELETE THE MCANSWER");
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateMCAnswer(int questionID, String oldAnswer, String newAnswer){
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("UPDATE MCANSWERS SET ANSWER=? WHERE QUESTION_ID=? AND ANSWER=?");
            st.setString(1, newAnswer);
            st.setString(2,Integer.toString(questionID));
            st.setString(3,oldAnswer);
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            System.out.println("COULD NOT UPDATE THE MCANSWER");
            throw new PersistenceException(e);
        }
    }

    @Override
    public void addMCAnswer(int questionID, String newAnswer){
        try(Connection c = connection()){
            String currentID = Integer.toString(incrementMCAnswerID());
            PreparedStatement st = c.prepareStatement("INSERT INTO MCANSWERS VALUES(?,?,?)");
            st.setString(1, Integer.toString(questionID));
            st.setString(2, currentID);
            st.setString(3, newAnswer);
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            System.out.println("COULD NOT UPDATE THE MCANSWER");
            throw new PersistenceException(e);
        }
    }
    @Override
    public void deleteMCAnswers(int questionID)throws PersistenceException{

        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("DELETE FROM MCANSWERS WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            System.out.println("COULD NOT DELETE FROM MCANSWER");
            throw new PersistenceException(e);
        }
    }

    @Override
    public void updateMCAnswers(Question toUpdate) throws PersistenceException{
        deleteMCAnswers(toUpdate.getQID());
        insertMCAnswers(toUpdate);
    }

    @Override
    public void insertMCAnswers(Question toInsert)throws PersistenceException{

        String currentID;
        String questionID = Integer.toString(toInsert.getQID());

        for(String answer : ((MCQuestion)toInsert).getAnswers()){
            currentID = Integer.toString(incrementMCAnswerID());
            try(Connection c = connection()){

                PreparedStatement st = c.prepareStatement("INSERT INTO MCANSWERS VALUES(?,?,?)");
                st.setString(1, questionID);
                st.setString(2, currentID);
                st.setString(3, answer);

                st.executeUpdate();

                st.close();
            }catch(SQLException e){
                System.out.println("COULD NOT INSert FROM MCANSWER");
                throw new PersistenceException(e);
            }
        }
    }

    @Override
    public int incrementMCAnswerID()throws PersistenceException{
        int nextID = 0;

        try(Connection c = connection()){

            PreparedStatement getID = c.prepareStatement("SELECT CURRENT_MC_ANSWER_ID FROM PUBLIC.STATICS");
            ResultSet rs = getID.executeQuery();
            rs.next();
            nextID = Integer.parseInt(rs.getString("CURRENT_MC_ANSWER_ID"));
            nextID++;
            PreparedStatement setID = c.prepareStatement("UPDATE PUBLIC.STATICS SET CURRENT_MC_ANSWER_ID=?");
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
}
