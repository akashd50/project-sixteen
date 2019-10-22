package comp3350.project16.logic;

import org.junit.Test;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.InvalidFIBAnswerException;
import comp3350.project16.Exceptions.InvalidMCAnswerException;
import comp3350.project16.Exceptions.InvalidMCQuestionException;
import comp3350.project16.Exceptions.InvalidTFAnswerException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.persistence.stubs.QuestionPersistenceStub;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class DoQuizManagerTest {

    @Test
    public void testValidMCUserAnswer() throws InvalidAnswerException {
        System.out.println("Starting testValidMCUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());
        manager.setUsersAnswer("4");

        System.out.println("Finished testValidMCUserAnswer");
    }

    @Test
    public void testValidTFUserAnswer() throws InvalidAnswerException {
        System.out.println("Starting testValidTFUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Move to the T/F question
        manager.getNextQuestion();
        manager.getNextQuestion();

        //Test all possible correct answers
        manager.setUsersAnswer("true");
        manager.setUsersAnswer("TRUE");
        manager.setUsersAnswer("t");
        manager.setUsersAnswer("false");
        manager.setUsersAnswer("FALSE");
        manager.setUsersAnswer("f");

        System.out.println("Finished testValidTFUserAnswer");
    }

    @Test
    public void testValidFIBUserAnswer() throws InvalidAnswerException {
        System.out.println("Starting testValidFIBUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Move to the FIB question
        manager.getNextQuestion();
        manager.getNextQuestion();
        manager.getNextQuestion();

        //Test all possible correct answers
        manager.setUsersAnswer("lucas");
        manager.setUsersAnswer("    lucas   ");

        System.out.println("Finished testValidFIBUserAnswer");
    }

    @Test(expected = InvalidMCAnswerException.class)
    public void testInvalidMCQuestionUserAnswer() throws InvalidAnswerException{
        System.out.println("Starting testInvalidMCQuestionUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Testing all possible incorrect answer formats for a mc question
        manager.setUsersAnswer("0");
        manager.setUsersAnswer("999");
        manager.setUsersAnswer("testinggg");
        manager.setUsersAnswer("         ");

        System.out.println("Finished testInvalidMCQuestionUserAnswer");

    }

    @Test(expected = InvalidTFAnswerException.class)
    public void testInvalidTFQuestionUserAnswer()throws InvalidAnswerException{

        System.out.println("Starting testInvalidTFQuestionUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Switch to the TF question
        manager.getNextQuestion();
        manager.getNextQuestion();

        //Testing all possible incorrect answer formats for a T/F question
        manager.setUsersAnswer("        ");
        manager.setUsersAnswer("testinggg");

        System.out.println("Finished testInvalidTFQuestionUserAnswer");

    }

    @Test(expected = InvalidFIBAnswerException.class)
    public void testInvalidFIBQuestionUserAnswer()throws InvalidAnswerException{

        System.out.println("Starting testInvalidFIBQuestionUserAnswer");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Switch to the FIB question
        manager.getNextQuestion();
        manager.getNextQuestion();
        manager.getNextQuestion();

        //Testing all possible incorrect answer formats for a FIB question
        manager.setUsersAnswer("        ");
        manager.setUsersAnswer("testinggg testingggg");

        System.out.println("Finished testInvalidFIBQuestionUserAnswer");

    }

    @Test
    public void testDoAQuizQuestionRetrievers(){

        System.out.println("Starting testDoAQuizQuestionRetrievers");

        Quiz q1 = createValidQuiz();
        DoQuizManager manager = new DoQuizManager(q1, Services.getQuestionPersistence());

        //Check that current question returns the first questions which will be a MC Question
        assertNotNull(manager.getCurrentQuestion());
        assertTrue(manager.getCurrentQuestion() instanceof MCQuestion);

        //call next question2 times to first get the MC question and then move to the T/F question
        assertNotNull(manager.getNextQuestion());
        assertNotNull(manager.getNextQuestion());

        //Check the current T/F question
        assertNotNull(manager.getCurrentQuestion());
        assertTrue(manager.getCurrentQuestion() instanceof TFQuestion);

        //Check the FIB question
        assertNotNull(manager.getNextQuestion());
        assertNotNull(manager.getCurrentQuestion());
        assertTrue(manager.getCurrentQuestion() instanceof FIBQuestion);

        //Move past the end of the questions list and check that null is returned by getNextQuestion()
        assertNull(manager.getNextQuestion());
        assertNotNull(manager.getCurrentQuestion());

        System.out.println("Finished testDoAQuizQuestionRetrievers");
    }

    private Quiz createValidQuiz(){
        //Creates a valid quiz object with 3 questions, first MC then T/F then FIB

        QuestionPersistence qp = Services.getQuestionPersistence();
        QuizPersistence qzp = Services.getQuizPersistence();

        Quiz q1 = new Quiz("This is a test quiz", qzp.incrementQuizID(), CurrentUser.getCurrentUser());

        //Create MC Question
        MCQuestion numPeople = new MCQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        numPeople.setQuestion("How many people are in this group?");
        ((MCQuestion) numPeople).addAnswer("2");
        ((MCQuestion) numPeople).addAnswer("3");
        ((MCQuestion) numPeople).addAnswer("4");
        ((MCQuestion) numPeople).addAnswer("5");
        numPeople.setCorrectAnswer("5");

        //Create T/F question
        TFQuestion namePeople = new TFQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        namePeople.setQuestion("True or false, Lucas Eckhardt is in this group?");
        namePeople.setCorrectAnswer("true");

        //Create FIB question

        FIBQuestion myName = new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        myName.setQuestion("What is my name");
        myName.setCorrectAnswer("lucas");

        q1.addQuestion(numPeople);
        q1.addQuestion(namePeople);
        q1.addQuestion(myName);

        return q1;
    }
}
