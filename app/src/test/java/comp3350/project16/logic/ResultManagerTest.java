package comp3350.project16.logic;
import org.junit.Test;
import comp3350.project16.application.Services;
import comp3350.project16.objects.Result;

import static org.junit.Assert.*;

public class ResultManagerTest {
    @Test
    public void testValidResult(){
        System.out.println("\nStarting testValidResult");
        ResultManager resManager = new ResultManager(Services.getResultPersistence(), 0,
                "Quiz 3: The Quizzening", "leHuman", 15 );
        int prevScore = resManager.getScore();

        Result testResult;

        assertNotNull(resManager.getResult());

        // score is 0 initially
        assertEquals(0, prevScore);

        //test if point system added a point
        resManager.addPoint(); // so current score should be 1
        assertEquals(1, resManager.getScore());
        assertEquals(prevScore+1, resManager.getScore());

        // test that correct final score is set for the result
        resManager.setFinalScore(); //should be 1
        testResult = resManager.getResult();
        assertEquals(1, testResult.getScore());

        //date format cannot be invalid since the Java calendar library gives the date in the
          //correct and specified format
        resManager.setDate("02/15/1620");
        testResult = resManager.getResult();
        assertEquals(testResult.getDate(), "02/15/1620");

        //save the Result
        resManager.saveResult(); // throws a persistence exception if there is an issue with
        //the database.


        System.out.println("Finished testValidResult");
    }
}
