package comp3350.project16.logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.InvalidTagException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.objects.Tag;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.TagPersistence;
import comp3350.project16.presentation.CurrentUser;

public class TagManagerTest {

    @Test
    public void testTagInserts(){
        System.out.println("Starting the Tag Manager Test");
        String currentUser = "tagManagerTest";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";String tag2s = "tag2";

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        Tag t2 = new Tag(tag2s,tagsManager.getNextTagID());

        try {
            tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
        }catch(InvalidTagException i){
            i.printStackTrace();
        }

        try {
            tagsManager.insertTag(t2, CurrentUser.getCurrentUser());
        }catch (InvalidTagException i){
            i.printStackTrace();
        }

        System.out.println(tagsManager.getTag(t1.getID(), currentUser).getTag());
        System.out.println(tagsManager.getTag(t2.getID(), currentUser).getTag());
        System.out.println("End of the Tag Manager Test");

    }

    @Test (expected=InvalidTagException.class)
    public void testInvalidInserts1() throws InvalidTagException{
        System.out.println("Starting the InvalidInserts1 Test");
        String currentUser = "tagManagerTest1";
        CurrentUser.setUser(currentUser);

        QuestionPersistence qp = Services.getQuestionPersistence();
        TagPersistence tp = Services.getTagPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);
        String tag1s = ""; //empty tag
        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        tagsManager.insertTag(t1, CurrentUser.getCurrentUser());

        System.out.println("End of the InvalidInsert1 Test");
    }

    @Test (expected=InvalidTagException.class)
    public void testInvalidInserts2() throws InvalidTagException{
        System.out.println("Starting the InvalidInserts2 Test");
        String currentUser = "tagManagerTest2";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
        tagsManager.insertTag(t1, CurrentUser.getCurrentUser());

        System.out.println("End of the InvalidInsert2 Test");
    }

    @Test
    public void validDelete(){
        System.out.println("Starting the ValidDelete Test");
        String currentUser = "tagManagerTest3";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());

        try{
            tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
        }catch (InvalidTagException e){e.printStackTrace();}
        catch (PersistenceException p){p.printStackTrace();}

        try {
            tagsManager.deleteTag(t1, CurrentUser.getCurrentUser());
        }catch (PersistenceException p){p.printStackTrace();}
        catch (InvalidTagException t){t.printStackTrace();}
        //insets and delete a tag

        System.out.println("End of the ValidDelete Test");
    }

    @Test (expected = InvalidTagException.class)
    public void invalidDelete() throws InvalidTagException{
        System.out.println("Starting the inValidDelete Test");
        String currentUser = "tagManagerTest4";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        //tag is never inserted

        tagsManager.deleteTag(t1, CurrentUser.getCurrentUser());
        //insets and delete a tag

        System.out.println("End of the inValidDelete Test");
    }

    @Test
    public void addTagsToQuestionTest1(){
        System.out.println("Starting the addTagsToQuestionTest1 Test");
        String currentUser = "tagManagerTest5";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());

        try {
            tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
            qp.insertQuestion(q);
            tagsManager.addTagToQuestion(q.getQID(), t1.getID(), CurrentUser.getCurrentUser());
        }catch(InvalidTagException i){
            i.printStackTrace();
        }
        List<Tag> l = tagsManager.getAllTagsForQuestion(q.getQID());
        ArrayList<Tag> temp = new ArrayList<>(l);

        Assert.assertEquals(temp.get(0).getTag(),t1.getTag());//asserting the tags are equal

        System.out.println("End of the addTagsToQuestionTest1 Test");
    }

    @Test (expected = InvalidTagException.class)
    public void addTagsToQuestionTest2() throws InvalidTagException{
        System.out.println("Starting the addTagsToQuestionTest2 Test");
        String currentUser = "tagManagerTest6";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());

        tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
        qp.insertQuestion(q);

        tagsManager.addTagToQuestion(q.getQID(), t1.getID(), CurrentUser.getCurrentUser());

        //adding the same tag to the question again, should throw an exception
        tagsManager.addTagToQuestion(q.getQID(), t1.getID(), CurrentUser.getCurrentUser());

        System.out.println("End of the addTagsToQuestionTest2 Test");
    }

    @Test
    public void removeTagsToQuestionTest1(){
        System.out.println("Starting the removeTagsToQuestionTest1 Test");
        String currentUser = "tagManagerTest7";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());

        try {
            tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
            qp.insertQuestion(q);
            tagsManager.addTagToQuestion(q.getQID(), t1.getID(), CurrentUser.getCurrentUser());
        }catch(InvalidTagException i){
            i.printStackTrace();
        }

        ArrayList<Tag> temp = new ArrayList<>(tagsManager.getAllTagsForQuestion(q.getQID()));
        Assert.assertEquals(temp.get(0).getTag(),t1.getTag());//asserting the tags are equal
        Assert.assertEquals(temp.size(),1); //asserting there is only one tag

        //remove tag
        tagsManager.removeTagFromQuestion(q.getQID(), t1.getID(),CurrentUser.getCurrentUser());

        temp = new ArrayList<>(tagsManager.getAllTagsForQuestion(q.getQID()));
        Assert.assertEquals(temp.size(),0); //there shouldn't be any tags

        System.out.println("End of the removeTagsToQuestionTest1 Test");
    }

    @Test
    public void getQuestionsWithTagsTest1(){
        System.out.println("Starting the getQuestionsWithTagsTest1 Test");
        String currentUser = "tagManagerTest8";
        CurrentUser.setUser(currentUser);
        TagPersistence tp = Services.getTagPersistence();
        QuestionPersistence qp = Services.getQuestionPersistence();

        TagsManager tagsManager = new TagsManager(tp, qp);

        String tag1s = "tag1";//tag already present

        Tag t1 = new Tag(tag1s, tagsManager.getNextTagID());
        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        Question q2 = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        try {
            tagsManager.insertTag(t1, CurrentUser.getCurrentUser());
            qp.insertQuestion(q);
            qp.insertQuestion(q2);
            tagsManager.addTagToQuestion(q.getQID(), t1.getID(), CurrentUser.getCurrentUser());
            tagsManager.addTagToQuestion(q2.getQID(), t1.getID(), CurrentUser.getCurrentUser());
        }catch(InvalidTagException i){
            i.printStackTrace();
        }

        ArrayList<String> tags = new ArrayList<>();
        tags.add(t1.getTag());
        try {
            ArrayList<Question> temp = tagsManager.getQuestionsWithTags(tags);
            Assert.assertEquals(temp.size(), 2); //there should be 2 questions with this tag
        }catch(InvalidTagException e){e.printStackTrace();}

        System.out.println("End of the getQuestionsWithTagsTest1 Test");
    }

}
