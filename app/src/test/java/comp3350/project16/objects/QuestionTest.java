package comp3350.project16.objects;

import org.junit.Test;

import comp3350.project16.application.Services;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class QuestionTest {

    private QuestionPersistence qpers = Services.getQuestionPersistence();
    @Test
    public void testQuestion1(){
        Question question1;
        Question question2;
        Question question3;

        System.out.println("\nStarting testQuestion1");

        int currentID = qpers.incrementQuestionID();

        //Using FIBQuestion because it modifies nothing of the abstract base Question class
        question1 = new FIBQuestion(currentID, CurrentUser.getCurrentUser());
        question2 = new FIBQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());
        question3 = new FIBQuestion(qpers.incrementQuestionID(), CurrentUser.getCurrentUser());

        //Check that the Question's were successfully created
        assertNotNull(question1);
        assertNotNull(question2);
        assertNotNull(question3);

        //Check that the IDs are assigned in order correctly
        assertEquals(question1.getQID(),currentID);
        assertEquals(question2.getQID(),currentID + 1);
        assertEquals(question3.getQID(),currentID + 2);

        //Assign a question to each one and verify the assignment
        String q1 = "Why am I doing this?";
        String q2 = "Can I be done yet?";
        String q3 = "When are we done?";

        question1.setQuestion(q1);
        question2.setQuestion(q2);
        question3.setQuestion(q3);

        assertEquals(question1.getQuestion(), q1);
        assertEquals(question2.getQuestion(), q2);
        assertEquals(question3.getQuestion(), q3);

        //Reassign the question of question1
        String q4 = "Changing this up";
        question1.setQuestion(q4);
        assertEquals(question1.getQuestion(), q4);

        //Create answers for all 3
        String a1 = "Because I have to";
        String a2 = "Nope got another couple unit tests to go";
        String a3 = "April";

        question1.setCorrectAnswer(a1);
        question2.setCorrectAnswer(a2);
        question3.setCorrectAnswer(a3);

        assertEquals(question1.getCorrectAnswer(), a1);
        assertEquals(question2.getCorrectAnswer(), a2);
        assertEquals(question3.getCorrectAnswer(), a3);

        //Change one answer and verify still working
        String a4 = "Changing this thing now";
        question3.setCorrectAnswer(a4);
        assertEquals(question3.getCorrectAnswer(), a4);

        System.out.println("Finished testQuestion1");
    }
}
