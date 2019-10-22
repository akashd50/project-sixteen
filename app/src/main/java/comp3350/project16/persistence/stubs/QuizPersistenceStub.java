package comp3350.project16.persistence.stubs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import comp3350.project16.application.Services;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.presentation.CurrentUser;

public class QuizPersistenceStub implements QuizPersistence {
    
    private int ID = 1;
    Hashtable<String, ArrayList<Quiz>> quizMap;

    public QuizPersistenceStub(){

        quizMap = new Hashtable<>();
        quizMap.put(CurrentUser.getCurrentUser(), new ArrayList<Quiz>());

        Quiz q1 = new Quiz("Sample Quiz", incrementQuizID(), "admin");
        q1.addQuestion(Services.getQuestionPersistence().getQuestion(1));
        q1.addQuestion(Services.getQuestionPersistence().getQuestion(2));
        q1.addQuestion(Services.getQuestionPersistence().getQuestion(3));

        insertQuiz(q1);
    }

    public Quiz getQuiz(int quizID){
        ArrayList<Quiz> quizList = quizMap.get(CurrentUser.getCurrentUser());
        if(quizList==null) return null;
         for(int i = 0; i < quizList.size(); i++){
                if(quizList.get(i).getID() == quizID){
                    return quizList.get(i);
                }
         }
        
        return null;
    }

    public boolean insertQuiz(Quiz toInsert){

        String currentUser = toInsert.getOwner();

        if(!quizMap.containsKey(currentUser)){
            quizMap.put(currentUser, new ArrayList<Quiz>());
        }

        quizMap.get(currentUser).add(toInsert);

        return true;
    }

    public boolean deleteQuiz(int quizID){
        ArrayList<Quiz> quizList = quizMap.get(CurrentUser.getCurrentUser());
        if(quizList==null) return false;
        for(int i = 0; i < quizList.size(); i++){
            if(quizList.get(i).getID() == quizID){
                quizList.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean findQuiz(String name){
        ArrayList<Quiz> quizList = quizMap.get(CurrentUser.getCurrentUser());
        if(quizList==null) return false;
        for(int i = 0; i < quizList.size(); i++){
            if(quizList.get(i).getName().equals(name)){
                return true;
            }
        }

        return false;
    }

    public int incrementQuizID(){
        int currentID = ID;
        ID++;
        return currentID;
    }

    public void updateQuizName(int ID, String name){
        Quiz toChange = getQuiz(ID);
        if(toChange != null){
            toChange.setName(name);
        }
    }

    @Override
    public void addQuestionToQuiz(int quizID, int questionID){

        Question toAdd = Services.getQuestionPersistence().getQuestion(questionID);
        getQuiz(quizID).addQuestion(toAdd);
    }

    @Override
    public boolean removeQuestionFromQuiz(int quizID, Question toRemove) {
        return getQuiz(quizID).remove(toRemove.getQID());
    }

    @Override
    public List<Quiz> getQuizList(String user){return quizMap.get(user);}
}
