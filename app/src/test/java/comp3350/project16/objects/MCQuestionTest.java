package comp3350.project16.objects;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import comp3350.project16.Exceptions.InvalidMCAnswerException;
import comp3350.project16.application.Services;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class MCQuestionTest {

    private QuestionPersistence qpers = Services.getQuestionPersistence();

    @Test
    public void testMCSetCorrectAnswer(){

        System.out.println("\nStarting testMCSetCorrectAnswer");

        MCQuestion q1 = new MCQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());

        assertNotNull(q1);

        q1.addAnswer("This is a valid answer");

        //Set the correct answer to an answer that is in the valid answer list
        assertTrue(q1.setCorrectAnswer("This is a valid answer"));
        assertEquals(q1.getCorrectAnswer(), "This is a valid answer");

        //Try setting the answer to one that is not in the valid answers list
        assertFalse(q1.setCorrectAnswer("INVALID"));
        assertEquals(q1.getCorrectAnswer(), "This is a valid answer");

        System.out.println("\nFinished testMCSetCorrectAnswer");
    }

    @Test
    public void testValidMCUserAnswer() throws InvalidMCAnswerException {

        System.out.println("\nStarting testValidMCUserAnswer");

        MCQuestion q1 = new MCQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());

        assertNotNull(q1);

        q1.addAnswer("This is a valid answer");

        //Set the users answer to a valid answer #
        q1.setUsersAnswer("1");
        assertEquals(q1.getUsersAnswer(), "1");

        System.out.println("\nFinished testValidMCUserAnswer");

    }

    @Test
    public void testInvalidMCUserAnswer() throws InvalidMCAnswerException {

        System.out.println("\nStarting testInvalidMCUserAnswer");

        MCQuestion q1 = new MCQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());

        assertNotNull(q1);

        q1.addAnswer("This is a valid answer");

        //Set the users answer to a valid answer #

        try{
            q1.setUsersAnswer("REALLY INVALID");
            fail("InvalidMCAnswerException not thrown");
        }catch(InvalidMCAnswerException e){}

        try{
            q1.setUsersAnswer("3");
            fail("InvalidMCAnswerException not thrown");
        }catch(InvalidMCAnswerException e){}

        try{
            q1.setUsersAnswer("0");
            fail("InvalidMCAnswerException not thrown");
        }catch(InvalidMCAnswerException e){}

        try{
            q1.setUsersAnswer("-1");
            fail("InvalidMCAnswerException not thrown");
        }catch(InvalidMCAnswerException e){}

        assertEquals(q1.getUsersAnswer(), null);

        System.out.println("\nFinished testInvalidMCUserAnswer");
    }

    @Test
    public void testDeleteMCAnswer() throws InvalidMCAnswerException {

        System.out.println("\nStarting testDeleteMCAnswer");

        MCQuestion q1 = new MCQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());

        assertNotNull(q1);

        q1.addAnswer("This is a valid answer");
        q1.addAnswer("Answer 2");

        //Set the correct answer to a valid answer #
        q1.setCorrectAnswer("This is a valid answer");
        assertEquals(q1.getCorrectAnswer(), "This is a valid answer");

        //Try to delete answer2 nothing should change with the correct answer
        q1.deleteAnswer("Answer 2");
        assertEquals(q1.getAnswers().size(), 1);
        assertEquals(q1.getCorrectAnswer(), "This is a valid answer");

        //Now delete the current correct answer
        q1.deleteAnswer("This is a valid answer");
        assertEquals(q1.getAnswers().size(), 0);
        assertNull(q1.getCorrectAnswer());

        System.out.println("\nFinished testDeleteMCAnswer");

    }

}
