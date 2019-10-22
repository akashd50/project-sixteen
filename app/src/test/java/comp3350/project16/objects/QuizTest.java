package comp3350.project16.objects;
import org.junit.Test;

import comp3350.project16.application.Services;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class QuizTest {

    private QuestionPersistence qpers = Services.getQuestionPersistence();
    private QuizPersistence quizPers = Services.getQuizPersistence();
    @Test
    public void quizTest1(){
        Quiz quiz1;
        Quiz quiz2;
        Quiz quiz3;

        System.out.println("Starting quizTest1");

        String n1 = "Quiz1";
        String n2 = "Quiz2";
        String n3 = "Quiz2";

        int currentID = quizPers.incrementQuizID();

        quiz1 = new Quiz(n1,currentID, CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, quizPers.incrementQuizID(), CurrentUser.getCurrentUser());
        quiz3 = new Quiz(n3, quizPers.incrementQuizID(), CurrentUser.getCurrentUser());

        //check that objects were properly created.
        assertNotNull(quiz1);
        assertNotNull(quiz2);
        assertNotNull(quiz3);

        //proper name was assigned
        assertEquals(quiz1.getName(), n1);
        assertEquals(quiz2.getName(), n2);
        assertEquals(quiz3.getName(), n3);

        //proper id's are assigned
        assertEquals(quiz1.getID(),currentID);
        assertEquals(quiz2.getID(),currentID + 1);
        assertEquals(quiz3.getID(),currentID + 2);

        //create a bunch of questions to add to the quizzes.
        Question q1 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q1.setQuestion("Is this the question1?");

        Question q2 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q2.setQuestion("Is this the question2?");

        Question q3 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q3.setQuestion("Is this the question3?");

        Question q4 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q4.setQuestion("Is this the question4?");

        Question q5 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q5.setQuestion("Is this the question5?");

        Question q6 = new FIBQuestion(qpers.incrementQuestionID(),CurrentUser.getCurrentUser());
        q6.setQuestion("Is this the question6?");

        //adding questions to the quiz.
        quiz1.addQuestion(q1);
        quiz1.addQuestion(q2);

        quiz2.addQuestion(q3);
        quiz2.addQuestion(q4);

        quiz3.addQuestion(q5);
        quiz3.addQuestion(q6);

        //checking searching by question
        assertTrue(quiz1.search(q1));
        assertTrue(quiz2.search(q4));
        assertTrue(quiz3.search(q6));

        assertFalse(quiz1.search(q5)); //not added to quiz1

        //checking if the questions were properly added to the quiz
        //searching by question id's
        assertTrue(quiz1.contains(q1.getQID()));
        assertTrue(quiz2.contains(q3.getQID()));
        assertTrue(quiz3.contains(q5.getQID()));

        assertFalse(quiz1.contains(q3.getQID())); //should return false, quiz1 doesn't contains q3


        //checking if removing the questions from the quiz works
        assertTrue(quiz1.remove(q1.getQID()));
        assertTrue(quiz2.remove(q3.getQID()));
        assertTrue(quiz3.remove(q5.getQID()));

        //should return false, quiz1 doesn't contains q3
        assertFalse(quiz1.remove(q3.getQID()));

        //checking if the questions were actually removed
        assertFalse(quiz1.contains(q1.getQID()));
        assertFalse(quiz2.contains(q3.getQID()));
        assertFalse(quiz3.contains(q5.getQID()));

        //checking if change name works
        String n4 = "New Quiz 1";
        String n5 = "New Quiz 2";
        String n6 = "New Quiz 3";
        quiz1.setName(n4);
        quiz2.setName(n5);
        quiz3.setName(n6);

        assertEquals(quiz1.getName(), n4);
        assertEquals(quiz2.getName(), n5);
        assertEquals(quiz3.getName(), n6);

        //getting the full questionList
        assertNotNull(quiz1.getQuestionsList());

        System.out.println("Finished quizTest1");
    }
}
