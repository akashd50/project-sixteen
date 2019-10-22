package comp3350.project16.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Tag;
import comp3350.project16.persistence.TagPersistence;

public class TagPersistenceHSQLDB implements TagPersistence {
    private String dbPath;
    public TagPersistenceHSQLDB(final String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException{
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    @Override
    public Tag getTag(int tagID, String user) throws PersistenceException{
        Tag tag;
        String toReturn;
        int tID;
        try (Connection c = connection()){

            PreparedStatement st = c.prepareStatement("SELECT * FROM TAGS WHERE TAG_ID=? AND USERNAME=?");
            st.setString(1, Integer.toString(tagID));
            st.setString(2,user);

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                tID = Integer.parseInt(rs.getString("TAG_ID"));
                toReturn = rs.getString("TAG");
            }else{
                throw new PersistenceException(new SQLException("Tag not found"));
            }
            rs.close();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return new Tag(toReturn,tID);
    }

    @Override
    public List<Tag> getQuestionTags(int questionID) throws PersistenceException{
        ArrayList<Tag> list = new ArrayList<>();
        try (Connection c = connection()){

            PreparedStatement st = c.prepareStatement("SELECT TAG, TAG_ID FROM QUESTIONTAGS WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));

            ResultSet rs = st.executeQuery();

            while(rs.next()){
                list.add(getTagObject(rs));
            }
            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return list;
    }

    @Override
    public void insertInQuestionTagTable(int q, Tag t) throws PersistenceException{
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("INSERT INTO QUESTIONTAGS VALUES(?,?,?)");
            st.setString(1, Integer.toString(q));
            st.setString(2, Integer.toString(t.getID()));
            st.setString(3, t.getTag());
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }
    @Override
    public void removeFromQuestionTagTable(int questionID, ArrayList<Tag> tags) throws PersistenceException{
        try(Connection c = connection()){
            for(Tag t: tags) {
                PreparedStatement st = c.prepareStatement("DELETE FROM QUESTIONTAGS WHERE QUESTION_ID=? AND TAG=?");
                st.setString(1, Integer.toString(questionID));
                st.setString(2, t.getTag());
                st.executeUpdate();
                st.close();
            }
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public void removeAllTagsFromQuestion(int questionID) throws PersistenceException{
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("DELETE FROM QUESTIONTAGS WHERE QUESTION_ID=?");
            st.setString(1, Integer.toString(questionID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Integer> getQuestionsWithTag(String tag) throws PersistenceException{
        ArrayList<Integer> qList = new ArrayList<>();
        try (Connection c = connection()){

            PreparedStatement st = c.prepareStatement("SELECT QUESTION_ID FROM QUESTIONTAGS WHERE TAG=?");
            st.setString(1, tag);

            ResultSet rs = st.executeQuery();

            if(rs.next()){
                qList.add(Integer.parseInt(rs.getString("QUESTION_ID")));
            }
            rs.close();
            st.close();

        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return qList;
    }

    @Override
    public void insertTag(Tag toInsert, String user) throws PersistenceException{
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("INSERT INTO TAGS VALUES(?,?,?)");
            st.setString(1, Integer.toString(toInsert.getID()));
            st.setString(2, toInsert.getTag());
            st.setString(3, user);
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Tag> getAllTags(String user) throws PersistenceException{
        ArrayList<Tag> tags = new ArrayList<>();
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("SELECT TAG_ID, TAG FROM TAGS WHERE USERNAME=?");
            st.setString(1, user);
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                Tag t = getTagObject(rs);
                tags.add(t);
            }

            rs.close();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return tags;
    }

    private Tag getTagObject(ResultSet resultSet) throws SQLException{
        Tag temp;
        String tagID = resultSet.getString("TAG_ID");
        String tag = resultSet.getString("TAG");
        temp = new Tag(tag, Integer.parseInt(tagID));
        return temp;
    }

    @Override
    public boolean deleteTag(int tagID, String user) throws PersistenceException{
        try(Connection c = connection()){
            PreparedStatement st = c.prepareStatement("DELETE FROM TAGS WHERE TAG_ID=?");
            st.setString(1, Integer.toString(tagID));
            st.executeUpdate();
            st.close();
        }catch(SQLException e){
            throw new PersistenceException(e);
        }
        return true;
    }

    @Override
    public int incrementTagID() throws PersistenceException{
        int nextID = 0;
        try(Connection c = connection()){
            PreparedStatement getID = c.prepareStatement("SELECT CURRENT_TAG_ID FROM STATICS");
            ResultSet rs = getID.executeQuery();
            rs.next();
            nextID = Integer.parseInt(rs.getString("CURRENT_TAG_ID"));
            nextID++;
            PreparedStatement setID = c.prepareStatement("UPDATE STATICS SET CURRENT_TAG_ID=?");
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
