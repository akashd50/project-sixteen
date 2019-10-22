package comp3350.project16.persistence.stubs;

import comp3350.project16.persistence.ResultPersistence;
import comp3350.project16.objects.Result;
import comp3350.project16.presentation.CurrentUser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ResultPersistenceStub implements ResultPersistence {
    int currResultID = 1;

    ArrayList<Result> resultList; // result list

    public ResultPersistenceStub(){
        resultList = new ArrayList<Result>();
    }

    @Override
    public Result getResult(int resultID){
        Result found = null;
        boolean notFound = true;

        for(int i = 0; i < resultList.size() && notFound; i++){
            if(resultList.get(i).getResultID() == resultID){
                notFound=false;
                found = resultList.get(i);
            }
        }

        return found;
    }

    @Override
    public void insertResult(Result toInsert){ resultList.add(toInsert);}

    @Override
    public int incrementResultID() {

        int current = currResultID;
        currResultID++;
        return current;
    }

    //get past results of all quizzes with a specific username
    //using linear search like an amateur :(
    @Override
    public ArrayList<Result> getAllResults(String userName){
        ArrayList<Result>userResults = new ArrayList<Result>();
        for(int i = 0; i < resultList.size(); i++){
            if(userName.equals(resultList.get(i).getUserName())){
                userResults.add(resultList.get(i));
            }
        }
        return userResults;
    }

    //get past results of all quizzes with a specific quizName
    //using linear search like an amateur! (Must be a quiz that the user made)
    public ArrayList<Result> getResultsQName(String userName, String quizName){
        ArrayList<Result>userResults = new ArrayList<Result>();
        for(int i = 0; i < resultList.size(); i++){
            if(userName.equals(resultList.get(i).getUserName()) &&
            quizName.equals(resultList.get(i).getQuizName())){
                userResults.add(resultList.get(i));
            }
        }
        return userResults;
    }
}
