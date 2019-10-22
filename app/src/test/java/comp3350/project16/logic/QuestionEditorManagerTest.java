package comp3350.project16.logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.InvalidDeletionRequest;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.MCAnswerPersistence;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.presentation.CurrentUser;

public class QuestionEditorManagerTest {

    @Test
    public void updateQuestionTest1(){
        System.out.println("Starting the updateQuestionTest1");
        CurrentUser.setUser("qem1");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String qs2 = "Is this a new Question?";

        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);
        qp.insertQuestion(q);
        Assert.assertEquals(q.getQuestion(),qs1);

        try {
            questionEditorManager.updateQuestion(q.getQID(), qs2);
        }catch(InvalidInputException i){i.printStackTrace();}

        Assert.assertEquals(q.getQuestion(),qs2);
        Assert.assertEquals(questionEditorManager.getQuestionByID(q.getQID()).getQuestion(),qs2);

        System.out.println("updateQuestionTest1 Done!");
    }

    @Test (expected = InvalidInputException.class)
    public void updateQuestionTest2() throws InvalidInputException{
        System.out.println("Starting the updateQuestionTest2");
        CurrentUser.setUser("qem2");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String qs2 = "";

        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);
        qp.insertQuestion(q);
        Assert.assertEquals(q.getQuestion(),qs1);

        questionEditorManager.updateQuestion(q.getQID(), qs2);

        System.out.println("updateQuestionTest2 Done!");
    }

    @Test
    public void updateCATest1(){
        System.out.println("Starting the updateCATest1");
        CurrentUser.setUser("qem3");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";

        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);
        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);

        try {
            questionEditorManager.updateCorrectAnswer(q.getQID(), answer2);
        }catch(InvalidInputException i){i.printStackTrace();}

        Assert.assertEquals(q.getCorrectAnswer(),answer2);

        System.out.println("updateCATest1 Done!");
    }

    @Test (expected = InvalidInputException.class)
    public void updateCATest2() throws InvalidInputException{
        System.out.println("Starting the updateCATest2");
        CurrentUser.setUser("qem4");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "";

        Question q = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);
        q.setCorrectAnswer(answer1);
        qp.insertQuestion(q);

        Assert.assertEquals(answer1,q.getCorrectAnswer());
        questionEditorManager.updateCorrectAnswer(q.getQID(), answer2);

        System.out.println("updateCATest2 Done!");
    }

    @Test
    public void removeMCAnswerTest1(){
        System.out.println("Starting the removeMCAnswerTest1");
        CurrentUser.setUser("qem5");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";


        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        try {
            questionEditorManager.removeMCAnswer(q.getQID(), answer3);
        }catch(InvalidDeletionRequest i){i.printStackTrace();}

        Assert.assertEquals(2, q.getAnswers().size());

        System.out.println("removeMCAnswerTest1 Done!");
    }

    @Test (expected = InvalidDeletionRequest.class)
    public void removeMCAnswerTest2() throws InvalidDeletionRequest{
        System.out.println("Starting the removeMCAnswerTest2");
        CurrentUser.setUser("qem6");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";


        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());
        //trying to delete the correct answer
        //shouldn't be able to do so
        questionEditorManager.removeMCAnswer(q.getQID(), answer1);

        System.out.println("removeMCAnswerTest2 Done!");
    }

    @Test
    public void addMCAnswerTest1(){
        System.out.println("Starting the addMCAnswerTest1");
        CurrentUser.setUser("qem7");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";


        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        try {
            questionEditorManager.addMCAnswer(q.getQID(), "NEWANSWER");
        }catch(InvalidInputException i){i.printStackTrace();}

        Assert.assertEquals(4, q.getAnswers().size());

        System.out.println("addMCAnswerTest1 Done!");
    }

    @Test (expected = InvalidInputException.class)
    public void addMCAnswerTest2() throws InvalidInputException{
        System.out.println("Starting the addMCAnswerTest2");
        CurrentUser.setUser("qem8");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";


        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        questionEditorManager.addMCAnswer(q.getQID(), "");

        System.out.println("addMCAnswerTest2 Done!");
    }

    @Test
    public void updateMCAnswerTest1(){
        System.out.println("Starting the updateMCAnswerTest1");
        CurrentUser.setUser("qem9");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";

        String newAnswer = "newAnswer";
        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        try {
            questionEditorManager.updateMCAnswer(q.getQID(), answer2,newAnswer);
        }catch(InvalidInputException i){i.printStackTrace();}

        List<String> answers = questionEditorManager.getMCAnswers(q.getQID());
        Assert.assertNotNull(answers);
        Assert.assertEquals(newAnswer, answers.get(1));

        System.out.println("updateMCAnswerTest1 Done!");
    }

    @Test (expected = InvalidInputException.class)
    public void updateMCAnswerTest2() throws InvalidInputException{
        System.out.println("Starting the updateMCAnswerTest2");
        CurrentUser.setUser("qem10");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";

        String newAnswer = ""; //new answer is empty
        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        questionEditorManager.updateMCAnswer(q.getQID(), answer1,newAnswer);

        System.out.println("updateMCAnswerTest2 Done!");
    }

    @Test
    public void updateMCAnswerTest3(){
        System.out.println("Starting the updateMCAnswerTest3");
        CurrentUser.setUser("qem11");
        QuestionPersistence qp = Services.getQuestionPersistence();
        MCAnswerPersistence mcp = Services.getMCAnswerPersistence();

        QuestionEditorManager questionEditorManager = new QuestionEditorManager(
                qp, mcp,CurrentUser.getCurrentUser());

        String qs1 = "What is the Question?";
        String answer1 = "Answer1";
        String answer2 = "Answer2";
        String answer3 = "Answer3";

        String newAnswer = "newAnswer";
        MCQuestion q = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q.setQuestion(qs1);

        q.addAnswer(answer1);
        q.addAnswer(answer2);
        q.addAnswer(answer3);

        q.setCorrectAnswer(answer1);

        qp.insertQuestion(q);

        Assert.assertEquals(q.getCorrectAnswer(),answer1);
        Assert.assertEquals(3,q.getAnswers().size());

        //updating the correct answer in the list, the correct answer should also be updated
        try {
            questionEditorManager.updateMCAnswer(q.getQID(), answer1, newAnswer);
        }catch(InvalidInputException e){e.printStackTrace();}

        List<String> answers = questionEditorManager.getMCAnswers(q.getQID());
        Assert.assertNotNull(answers);
        Assert.assertEquals(newAnswer, answers.get(0));
        Assert.assertEquals(newAnswer,q.getCorrectAnswer());

        System.out.println("updateMCAnswerTest3 Done!");
    }
}
