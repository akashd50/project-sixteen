package comp3350.project16.persistence;

import java.util.ArrayList;

import comp3350.project16.objects.Result;


public interface ResultPersistence {

    Result getResult(int resultID);
    void insertResult(Result toInsert);
    int incrementResultID();
    ArrayList<Result> getAllResults(String userName);
    //ArrayList<Result> getResultsQName(String userName, String quizName);

}
