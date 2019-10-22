package comp3350.project16.logic;

import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.Result;
import comp3350.project16.persistence.ResultPersistence;

public class ResultManager {
    private ResultPersistence resultPersistence;
    private Result myResult;
    private int currScore = 0;

    public ResultManager(ResultPersistence resultPersistence, int quizID, String quizName, String userName, int maxScore) {
        this.resultPersistence = resultPersistence;

        //create Result object
        setResult(new Result(resultPersistence.incrementResultID(), quizID, quizName, userName, maxScore) );
    }

    public Result getResult(){
        return this.myResult;
    }
    public void setResult(Result toSet){this.myResult = toSet;}

    //increment score
    public void addPoint() {
        currScore++;
    }

    public void setFinalScore(){
        myResult.setScore(currScore);
    }

    public void setDate(String date){ myResult.setDate(date);}

    public int getScore(){
        return currScore;
    }

    //saveResults method
    public void saveResult() throws PersistenceException {
        resultPersistence.insertResult(myResult);
    }


}
