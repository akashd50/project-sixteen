package comp3350.project16.objects;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResultTest {
    @Test
    public void testResult1(){
        Result result;

        System.out.println("\nStarting testResult1");
        result = new Result(0, 0, "Test Quiz", "defaulte"
                , 10);
        result.setScore(4);
        result.setDate("05/16/2013");
        assertTrue(0 == result.getResultID());
        assertTrue(0 == result.getQuizID());
        assertTrue("Test Quiz".equals(result.getQuizName()));
        assertTrue("defaulte".equals(result.getUserName()));
        assertTrue(10 == result.getMaxScore());
        assertTrue(4 == result.getScore());
        assertTrue("05/16/2013".equals(result.getDate()));
        System.out.println("Finished testResult1");
    }

}
