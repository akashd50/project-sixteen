package comp3350.project16.logic;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.InvalidTagException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Tag;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.persistence.TagPersistence;

public class TagsManager {
    private QuestionPersistence questionPersistence;
    private TagPersistence tagPersistence;

    public TagsManager(TagPersistence tp, QuestionPersistence qp){
        questionPersistence = qp;
        tagPersistence = tp;
    }

    public boolean deleteTag(Tag t, String user) throws InvalidTagException, PersistenceException{
            if(tagPersistence.deleteTag(t.getID(), user)) {
                return true;
            }else throw new InvalidTagException("Unable to find this tag");
    }

    public void insertTag(Tag tag, String user) throws InvalidTagException, PersistenceException{
       if(tag.getTag().compareTo("")==0){
            throw new InvalidTagException("The tag shouldn't be empty!");
        }else{
           List<Tag> l = tagPersistence.getAllTags(user);
           if(l!=null) {
               ArrayList<Tag> tags = new ArrayList<>(l);
               if (tags.size() > 0) {
                   for (Tag t : tags) {
                       if (t.getTag().compareTo(tag.getTag()) == 0) {
                           throw new InvalidTagException("Tag already exists!");
                       }
                   }
               }
           }
       }
        tagPersistence.insertTag(tag, user);
    }//inserts the tag into the system

    public Tag getTag(int tagID, String user) throws PersistenceException{
            return tagPersistence.getTag(tagID, user);
    }//gets the tag with the id and user

    public void addTagToQuestion(int questionID, int tagID, String user) throws InvalidTagException, PersistenceException{
        Tag tag = getTag(tagID, user);
        if(validateDuplicateTags(questionID,tag)) {
            tagPersistence.insertInQuestionTagTable(questionID, tag);
        }else{
            throw new InvalidTagException("Question already contains this tag");
        }
    }//adds tag to the question after some validation

    private boolean validateDuplicateTags(int questionID, Tag tag) throws PersistenceException{
        List<Tag> l = tagPersistence.getQuestionTags(questionID);
        if(l==null) return true;
        ArrayList<Tag> tags = new ArrayList<>(l);
            for (Tag t : tags) {
                if (t.getID() == tag.getID()) {
                    return false;
                }
            }
        return true;
    }//internal; validates if there are duplicate tags for a question

    public void removeTagFromQuestion(int questionID, int tagID, String user) throws PersistenceException{
        Tag t = getTag(tagID, user);
        ArrayList<Tag> al = new ArrayList<Tag>();
        al.add(t);
        tagPersistence.removeFromQuestionTagTable(questionID, al);
    }//removes the tag from the question

    public void removeAllTags(int questionID) throws PersistenceException{
        tagPersistence.removeAllTagsFromQuestion(questionID);
    }//removes all the tags from the system

    public List<Tag> getAllTags(String user) throws PersistenceException{return tagPersistence.getAllTags(user);}
    //gets all the tags present in the system

    public ArrayList<Question> getQuestionsWithTags(ArrayList<String> tags) throws PersistenceException,InvalidTagException{
        ArrayList<Question> toReturn = new ArrayList<>();
        ArrayList<Integer> temp = new ArrayList<>();
        for(String s: tags) {
            if(s.compareTo("")==0) throw new InvalidTagException("Empty Tag Found");
            temp.addAll(tagPersistence.getQuestionsWithTag(s));
        }//get all question ids with each of the tags

        for(Integer i: temp){
            toReturn.add(questionPersistence.getQuestion(i));
        }//get all question objects

        return toReturn;
    }//gets all questions associated with a tag

    public List<Tag> getAllTagsForQuestion(int questionID) throws PersistenceException{
        return tagPersistence.getQuestionTags(questionID);
    }//get all tags associated to a question

    public int getNextTagID() throws PersistenceException {return tagPersistence.incrementTagID();} //gets the next tag id
}
