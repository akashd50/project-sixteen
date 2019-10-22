package comp3350.project16.persistence.stubs;

import java.util.ArrayList;
import java.util.Hashtable;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.persistence.TagPersistence;
import comp3350.project16.presentation.CurrentUser;

public class QuestionPersistenceStub implements QuestionPersistence {

    int currentID = 1;
    Hashtable<String, ArrayList<Question>> questionsMap;

    public QuestionPersistenceStub(){

        questionsMap = new Hashtable<>();
        questionsMap.put("admin", new ArrayList<Question>());

        TFQuestion tf1 = new TFQuestion(incrementQuestionID(), "admin");
        tf1.setQuestion("True or False: Lucas Eckhardt is in this group?");
        tf1.setCorrectAnswer("true");

        insertQuestion(tf1);

        MCQuestion mcq1 = new MCQuestion(incrementQuestionID(), "admin");
        mcq1.setQuestion("How many people are in this group?");
        mcq1.addAnswer("2 People");
        mcq1.addAnswer("3 People");
        mcq1.addAnswer("4 People");
        mcq1.addAnswer("5 People");
        mcq1.addAnswer("6 People");
        mcq1.setCorrectAnswer("5 People");

        insertQuestion(mcq1);

        FIBQuestion fibq1 = new FIBQuestion(incrementQuestionID(), "admin");
        fibq1.setQuestion("This class is COMP ____");
        fibq1.setCorrectAnswer("3350");

        insertQuestion(fibq1);
    }

    @Override
    public Question getQuestion(int questionID) {

        Question found = null;

        for(ArrayList<Question> questionsList : questionsMap.values()){
            for(int i = 0; i < questionsList.size(); i++){
                if(questionsList.get(i).getQID() == questionID){
                    return questionsList.get(i);
                }
            }
        }

        return found;
    }

    @Override
    public void insertQuestion(Question toInsert) {

        String currentUser = toInsert.getOwner();

        //If the user doesnt have a questions list create one
        if(!questionsMap.containsKey(currentUser)){
            questionsMap.put(currentUser, new ArrayList<Question>());
        }

        questionsMap.get(currentUser).add(toInsert);
    }

    @Override
    public int incrementQuestionID() {

        int current = currentID;
        currentID++;
        return current;
    }

    @Override
    public boolean deleteQuestion(int questionID){

        for(ArrayList<Question> questionsList : questionsMap.values()){

            for(int i = 0; i < questionsList.size(); i++){
                if(questionsList.get(i).getQID() == questionID){
                    questionsList.remove(i);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean setUserAnswer(int questionID, String answer) {
        boolean completed = false;

        Question change = getQuestion(questionID);

        if(change != null){
            try {
                change.setUsersAnswer(answer);
            }catch(InvalidAnswerException e){
                System.out.println("Users answer not set...should not have made it this far...");
                return false;
            }

            completed = true;
        }

        return completed;
    }

    @Override
    public boolean updateCorrectAnswer(int questionID, String correctAnswer){
        Question q = getQuestion(questionID);
        if(q!=null){
            q.setCorrectAnswer(correctAnswer);
        }
        return false;
    }

    @Override
    public boolean updateQuestion(int questionID, Question toUpdate) {

        boolean updated = false;

        Question change = getQuestion(questionID);

        if(change != null){
            change.setCorrectAnswer(toUpdate.getCorrectAnswer());
            change.setQuestion(toUpdate.getQuestion());

            /*if(toUpdate instanceof MCQuestion){
                //((MCQuestion)change).getAnswers().clear();
                for(String answer: ((MCQuestion)toUpdate).getAnswers()){
                    ((MCQuestion)change).addAnswer(answer);
                }
            }*/
            updated = true;
        }

        return updated;
    }

    @Override
    public ArrayList<Question> getAllQuestions(String username){
        return questionsMap.get(username);
    }
}
