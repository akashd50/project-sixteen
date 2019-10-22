package comp3350.project16.logic;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import comp3350.project16.Exceptions.InvalidFIBQuestionException;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidMCQuestionException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.InvalidTFQuestionException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class QuestionManagerTest {

    @Test
    public void testValidMCQuestion(){
        System.out.println("Starting testValidMCQuestion");

        try{
            MCQuestionManager manager1 = new MCQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());
            getValidMCQuestion(manager1);
            assertNotNull(manager1);
            assertNotNull(manager1.getQuestion());
            manager1.saveQuestion();
        }catch(Exception e){
            fail("No exception should be thrown by a valid MC Question");
        }

        System.out.println("Finished testValidMCQuestion");
    }

    @Test(expected = InvalidMCQuestionException.class)
    public void testNoAnswerSelectedMCQuestion() throws InvalidInputException, InvalidQuestionException{

        System.out.println("Starting testNoAnswerSelectedMCQuestion");

        MCQuestionManager manager1 = new MCQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        assertNotNull(manager1);
        assertNotNull(manager1.getQuestion());
        manager1.setUsersQuestion("This is a test question");
        manager1.addPossibleAnswer("A1");
        manager1.addPossibleAnswer("A2");
        manager1.addPossibleAnswer("A3");

        manager1.saveQuestion();

        System.out.println("Finished testNoAnswersSelectedMCQuestion");

    }

    @Test(expected = InvalidMCQuestionException.class)
    public void testNotEnoughMCAnswers() throws InvalidInputException, InvalidQuestionException{

        System.out.println("Starting testNotEnoughMCAnswers");

        MCQuestionManager manager1 = new MCQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        //Create a perfectly valid question and validate it using the manager
        manager1.setUsersQuestion("How do I test this?");
        manager1.addPossibleAnswer("By doing this");
        manager1.setCorrectAnswer("By doing this");

        assertNotNull(manager1);
        assertNotNull(manager1.getQuestion());

        manager1.saveQuestion();

        System.out.println("Finished testNotEnoughMCAnswers");
    }

    @Test(expected = InvalidInputException.class)
    public void testNoQuestionEntered() throws InvalidInputException, InvalidQuestionException{
        System.out.println("Starting testNoQuestionEntered");

        MCQuestionManager manager1 = new MCQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());
        getValidMCQuestion(manager1);

        assertNotNull(manager1);
        assertNotNull(manager1.getQuestion());

        manager1.setUsersQuestion("                  ");
        manager1.saveQuestion();

        System.out.println("Finished testNoQuestionEntered");

    }

    @Test
    public void testValidTFQuestion() throws InvalidInputException, InvalidQuestionException{

        //Used to test the validateTFQuestion function
        TFQuestionManager qManager3;
        String answerTrue = "true";

        System.out.println("\nStarting testValidateValidTFQuestion");

        //Create QuestionManager and TFQuestion objects and verify that they are created successfully
        qManager3 = new TFQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        assertNotNull(qManager3);
        assertNotNull(qManager3.getQuestion());

        //Create a perfectly valid question and validate it using the manager
        qManager3.setUsersQuestion("Is testing important for software engineers?");
        qManager3.setCorrectAnswer(answerTrue);

        // make sure question added is valid
        qManager3.saveQuestion();

        System.out.println("Finished testValidateValidTFQuestion");
    }

    @Test(expected = InvalidInputException.class)
    public void testBlankQuestionTFQuestion()throws InvalidInputException, InvalidQuestionException{
        //Used to test the validateTFQuestion function

        TFQuestionManager qManager4;

        System.out.println("\nStarting testBlankQuestionTFQuestion");

        //Create QuestionManager and TFQuestion objects and verify that they are created successfully
        qManager4 = new TFQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        assertNotNull(qManager4);
        assertNotNull(qManager4.getQuestion());

        //Create a perfectly valid question and validate it using the manager
        qManager4.setUsersQuestion("");

        // make sure questions added are invalid and given the correct error indicator:
        //should throw InvalidInputException
        qManager4.saveQuestion();

        System.out.println("Finished testBlankQuestionTFQuestion");
    }

    @Test(expected = InvalidTFQuestionException.class)
    public void testNoAnswerTFQuestion()throws InvalidInputException, InvalidQuestionException{

        //Used to test the validateTFQuestion function

        TFQuestionManager qManager5;

        System.out.println("\nStarting testNoAnswerTFQuestion");

        //Create QuestionManager and TFQuestion objects and verify that they are created successfully
        qManager5 = new TFQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        assertNotNull(qManager5);
        assertNotNull(qManager5.getQuestion());

        //Create a perfectly valid question and validate it using the manager
        qManager5.setUsersQuestion("This is a valid question?");

        // make sure questions added are invalid and given the correct error indicator:
        //should throw InvalidInputException
        qManager5.saveQuestion();

        System.out.println("Finished testNoAnswerTFQuestion");
    }

    @Test(expected = InvalidFIBQuestionException.class)
    public void testNoAnswerTypedFIBQuestion() throws InvalidInputException, InvalidQuestionException {
        //checks that a fill in the blanks question with a blank answer is considered invalid
        System.out.println("Starting testNoAnswerTypedFIBQuestion");

        //Create QuestionManager and FIBQuestion objects and verify that they are created successfully
        FIBQuestionManager manager7 = new FIBQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());
        getValidFIBQuestion(manager7);
        assertNotNull(manager7);
        assertNotNull(manager7.getQuestion());

        manager7.setCorrectAnswer(null);

        // make sure question added is invalid and give the correct error indicator:
        // should throw InvalidFIBQuestionException
        manager7.saveQuestion();

        System.out.println("Finished testNoAnswerTypedFIBQuestion");
    }

    @Test(expected = InvalidInputException.class)
    public void testBlankFIBQuestionEntered() throws InvalidInputException, InvalidQuestionException {
        //checks that a fill in the blanks question with a blank question(just whitespace) is considered invalid
        System.out.println("Starting testBlankFIBQuestionEntered");

        //Create QuestionManager and FIBQuestion objects and verify that they are created successfully
        FIBQuestionManager manager8 = new FIBQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());
        getValidFIBQuestion(manager8);

        manager8.setUsersQuestion("                  ");

        // make sure question added is invalid and give the correct error indicator:
        // should throw InvalidInputException
        manager8.saveQuestion();

        System.out.println("Finished testBlankFIBQuestionEntered");
    }

    @Test(expected = InvalidFIBQuestionException.class)
    public void testMoreThanOneWordAnswer() throws InvalidInputException, InvalidQuestionException {
        //checks that a fill in the blanks question with more than a single word answer is considered invalid

        System.out.println("Starting testMoreThanOneWordAnswer");

        //Create QuestionManager and FIBQuestion objects and verify that they are created successfully
        FIBQuestionManager manager9 = new FIBQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

        //Create a perfectly valid question and validate it using the manager
        manager9.setUsersQuestion("What are we doing?");
        manager9.setCorrectAnswer("Trying to test");

        assertNotNull(manager9);
        assertNotNull(manager9.getQuestion());

        // make sure question added is invalid and give the correct error indicator:
        // should throw InvalidFIBQuestionException
        manager9.saveQuestion();

        System.out.println("Finished testMoreThanOneWordAnswer");
    }

    private void getValidMCQuestion(MCQuestionManager m) throws InvalidInputException{

        //Create a perfectly valid MC question and validate it using the manager
        m.setUsersQuestion("How do I test this?");
        m.addPossibleAnswer("Using JUNIT");
        m.addPossibleAnswer("Not sure");
        m.addPossibleAnswer("By doing this");
        m.setCorrectAnswer("Not sure");
    }

    private void getValidFIBQuestion(FIBQuestionManager m) {
        //Creates and returns a perfectly valid fill in the blanks question
        m.setUsersQuestion("What are we doing?");
        m.setCorrectAnswer("Testing");

    }
}
