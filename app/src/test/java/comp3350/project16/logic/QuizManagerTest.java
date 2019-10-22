package comp3350.project16.logic;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.EmptyQuizException;
import comp3350.project16.Exceptions.InvalidQuizException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.presentation.CurrentUser;

import static org.junit.Assert.*;

public class QuizManagerTest {

    private QuestionPersistence qp = Services.getQuestionPersistence();
    private static int qNumber = 0;

    @Test
    public void testingInvalidQuizName1(){
        System.out.println("Starting testingInvalidQuizName1");
        CurrentUser.setUser("qmtest1");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz12";
        String n2 = "Quiz21";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        try {
            quizManager.saveQuiz(quiz1);
            quizManager.saveQuiz(quiz2);

        } catch (InvalidQuizException e) {
            fail("This should not throw any exceptions. Quiz names are different and non-empty");
        }

        System.out.println("Finished testingInvalidQuizName1");
    }

    @Test (expected = InvalidQuizException.class)
    public void testingInvalidQuizName2() throws InvalidQuizException{
        System.out.println("Starting testingInvalidQuizName2");
        CurrentUser.setUser("qmtest2");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz1";
        String n2 = "Quiz1";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        quizManager.saveQuiz(quiz1);
        quizManager.saveQuiz(quiz2);
        //quizzes have same name, should throw an exception

        System.out.println("Finished testingInvalidQuizName2");
    }

    @Test (expected = InvalidQuizException.class)
    public void testingInvalidQuizName3() throws InvalidQuizException{
        System.out.println("Starting testingInvalidQuizName3");
        CurrentUser.setUser("qmtest3");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz1";
        String n2 = "";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        quizManager.saveQuiz(quiz1);
        quizManager.saveQuiz(quiz2);
        //quizzes have empty name, should throw an exception

        System.out.println("Finished testingInvalidQuizName3");
    }

    @Test (expected = InvalidQuizException.class)
    public void testingChangingName1() throws InvalidQuizException{
        System.out.println("Starting testingChangingName1");
        CurrentUser.setUser("qmtest4");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz1";
        String n2 = "Quiz2";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        quizManager.saveQuiz(quiz1);
        quizManager.saveQuiz(quiz2);

        quizManager.changeQuizName(quiz2.getID(),n1);
        //quiz2 will have the same name as quiz1, should throw an exception

        System.out.println("Finished testingChangingName1");
    }

    @Test
    public void testingChangingName2(){
        System.out.println("Starting testingChangingName2");
        CurrentUser.setUser("qmtest5");
        QuizManager quizManager;
        Quiz quiz1, quiz2;
        QuizPersistence qp = Services.getQuizPersistence();
        quizManager = new QuizManager(qp, CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz11";
        String n2 = "Quiz21112";
        quiz1 = new Quiz(n1, qp.incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, qp.incrementQuizID(), CurrentUser.getCurrentUser());

        try {
            quizManager.saveQuiz(quiz1);
            quizManager.saveQuiz(quiz2);
        }catch (InvalidQuizException e){
            fail("Unable to save");
        }

        try {
            quizManager.changeQuizName(quiz2.getID(), "NewName");
        }catch (InvalidQuizException e2){
            fail("This should not throw an exception. name change should be successfull.");
        }
        //quiz2 will have the same name as quiz1, should throw an exception

        System.out.println("Finished testingChangingName2");
    }


    @Test (expected = InvalidQuizException.class)
    public void testingInvalidQuizQuestion1() throws InvalidQuizException{
        System.out.println("Starting testingInvalidQuizQuestion1");
        CurrentUser.setUser("qmtest6");
        QuizManager quizManager;
        Quiz quiz1;
        Question q1;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz111";

        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        //create sample questions
        q1 = new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q1.setQuestion("This is a test question.");
        q1.setCorrectAnswer("aanswer");
        q1.setOwner(CurrentUser.getCurrentUser());

        Services.getQuestionPersistence().insertQuestion(q1);
        //add sample questions to the quizzes
       assertTrue(quizManager.saveQuiz(quiz1));

       quizManager.addQuestionToQuiz(quiz1.getID(), q1);
       quizManager.addQuestionToQuiz(quiz1.getID(), q1);
        //same question being added twice, should throw an exception

        System.out.println("Finished testingInvalidQuizQuestion1");
    }

    @Test
    public void testingAddingAndRemovingQuestion(){
        System.out.println("Starting testingAddingAndRemovingQuestion");
        CurrentUser.setUser("qmtest7");
        QuizManager quizManager;
        Quiz quiz1, quiz2;
        Question q1, q2, q3;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz10";
        String n2 = "Quiz20";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        //create sample questions
        q1 = new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q2 = new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());
        q3 = new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser());

        Services.getQuestionPersistence().insertQuestion(q1);
        Services.getQuestionPersistence().insertQuestion(q2);

        //add sample questions to the quizzes
        try {
            quizManager.saveQuiz(quiz1);
            quizManager.saveQuiz(quiz2);

            quizManager.addQuestionToQuiz(quiz1.getID(), q1);
            quizManager.addQuestionToQuiz(quiz2.getID(), q2);

        } catch (InvalidQuizException e) {
            fail("This should not throw any exceptions.");
        }

        assertTrue(quizManager.removeQuestionFromQuiz(quiz1.getID(),q1));
        assertTrue(quizManager.removeQuestionFromQuiz(quiz2.getID(),q2));

        assertFalse(quizManager.removeQuestionFromQuiz(quiz1.getID(),q1));
        assertFalse(quizManager.removeQuestionFromQuiz(quiz2.getID(),q2));

        System.out.println("Finished testingAddingAndRemovingQuestion");
    }

    @Test
    public void testingDeletingQuiz(){
        System.out.println("Starting testingDeletingQuiz");
        CurrentUser.setUser("qmtest8");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz100";
        String n2 = "Quiz200";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        try {
            quizManager.saveQuiz(quiz1);
            quizManager.saveQuiz(quiz2);

        } catch (InvalidQuizException e) {
            fail("This should not throw any exceptions.");
        }//add quizzes

        assertTrue(quizManager.deleteQuiz(quiz1.getID()));
        assertTrue(quizManager.deleteQuiz(quiz2.getID()));
        //delete quizzes and then try deleting again.

        assertFalse(quizManager.deleteQuiz(quiz1.getID()));
        assertFalse(quizManager.deleteQuiz(quiz2.getID()));

        System.out.println("Finished testingDeletingQuiz");
    }

    @Test
    public void testGettingQuizList(){
        System.out.println("Starting testGettingQuizList");
        CurrentUser.setUser("qmtest9");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz122";
        String n2 = "Quiz211";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        try {
            quizManager.saveQuiz(quiz1);
            quizManager.saveQuiz(quiz2);

        } catch (InvalidQuizException e) {
            fail("This should not throw any exceptions.");
        }//add quizzes

        List arrayList = quizManager.getQuizList();
        assertNotNull(arrayList);

        System.out.println("Finished testGettingQuizList");
    }

    @Test (expected = InvalidQuizException.class)
    public void testvalidateQuizForPlay() throws InvalidQuizException{
        System.out.println("Starting testvalidateQuizForPlay");
        CurrentUser.setUser("qmtest10");
        QuizManager quizManager;
        Quiz quiz1, quiz2;

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());
        assertNotNull(quizManager);//make sure the object was successfully created

        //create sample quizzes
        String n1 = "Quiz1222";
        String n2 = "Quiz2112";
        quiz1 = new Quiz(n1, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());
        quiz2 = new Quiz(n2, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser());

        quiz2.addQuestion(new FIBQuestion(qp.incrementQuestionID(), CurrentUser.getCurrentUser())); //adding sample question

        quizManager.saveQuiz(quiz1);
        quizManager.saveQuiz(quiz2);

        try{
            assertTrue(quizManager.validateQuizForPlay(quiz2.getID()));
        }catch (InvalidQuizException e){
            fail("This exception should not be thrown");
        }

        quizManager.validateQuizForPlay(quiz1.getID()); //this should throw an exception.

        System.out.println("Finished testvalidateQuizForPlay");
    }


}
