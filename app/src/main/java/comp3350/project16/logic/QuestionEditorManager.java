package comp3350.project16.logic;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.Exceptions.InvalidDeletionRequest;
import comp3350.project16.Exceptions.InvalidFIBQuestionException;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidMCQuestionException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.InvalidTFQuestionException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.application.Services;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.MCAnswerPersistence;
import comp3350.project16.persistence.QuestionPersistence;

public class QuestionEditorManager {
    private QuestionPersistence questionPersistence;
    private MCAnswerPersistence mcAnswerPersistence;
    private String CURRENT_USER;
    public QuestionEditorManager(QuestionPersistence qp, MCAnswerPersistence mcAnswer, String currentUser){
        questionPersistence = qp;
        mcAnswerPersistence = mcAnswer;
        CURRENT_USER = currentUser;
    }

    public void updateQuestion(int questionID, String newQuestion) throws InvalidInputException, PersistenceException {
        //this method updates the question to the new string after checking
        //if it's valid.
        validateQuestion(newQuestion);
        Question q = questionPersistence.getQuestion(questionID);
        if(q!=null) {
            q.setQuestion(newQuestion);
            questionPersistence.updateQuestion(questionID,q);
        }
    }

    private void validateQuestion(String question) throws InvalidInputException{
        //validates the question string
        if(question.compareTo("")==0) throw new InvalidInputException("Question Shouldn't be empty");
    }

    public void updateCorrectAnswer(int questionID, String newAnswer) throws InvalidInputException, PersistenceException{
        //updates the correct answer for the question
        //MCQuestionManager m = new MCQuestionManager(questionPersistence, CURRENT_USER);
        validateAnswer(newAnswer);
        Question q = questionPersistence.getQuestion(questionID);

        if(!q.setCorrectAnswer(newAnswer)){
            throw new InvalidInputException("Correct answer must be present in the answers list. " +
                    "Please add it there first!");
        }else{
            questionPersistence.updateCorrectAnswer(questionID,newAnswer);
            //update the answer in the database.
        }
    }

    private void validateAnswer(String answer)throws InvalidInputException{
        //validates the correct answer string
        if(answer.compareTo("")==0) throw new InvalidInputException("Answer shouldn't be empty");
    }

    public void removeMCAnswer(int questionID, String answer) throws InvalidDeletionRequest, PersistenceException{
        //removes the answer from the MC question list.
        Question q = questionPersistence.getQuestion(questionID);
        if(q instanceof MCQuestion){
            MCQuestion nq = (MCQuestion)q;
            ArrayList<String> temp = nq.getAnswers();
            for(int i=0;i<temp.size();i++){
                if(answer.compareTo(temp.get(i))==0) {
                    if(answer.compareTo(q.getCorrectAnswer())==0){
                        throw new InvalidDeletionRequest("This is the correct answer. Please update the correct answer first!");
                    }else {
                        temp.remove(i);
                        if(Services.isTesting()){}
                        else {
                            mcAnswerPersistence.deleteMCAnswer(questionID,answer);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void addMCAnswer(int questionID, String answer) throws InvalidInputException{
        //adds the answer to the MC question list.
        Question q = questionPersistence.getQuestion(questionID);
        //if(q instanceof MCQuestion){
        MCQuestion nq = (MCQuestion)q;
        ArrayList<String> temp = nq.getAnswers();
        validateAnswer(answer);
        temp.add(answer);
        if(Services.isTesting()){}else {
            mcAnswerPersistence.addMCAnswer(questionID,answer);
        }
    }

    public void updateMCAnswer(int questionID, String oldAnswer, String newAnswer) throws InvalidInputException{
        //updates the answer from the MC question list.
        Question q = questionPersistence.getQuestion(questionID);
        validateAnswer(newAnswer);
        // if(q instanceof MCQuestion){
        MCQuestion nq = (MCQuestion)q;
        ArrayList<String> temp = nq.getAnswers();
        for(int i=0;i<temp.size();i++){
            if(oldAnswer.compareTo(temp.get(i))==0) {
                if(nq.getCorrectAnswer().compareTo(oldAnswer)==0){
                    nq.setCorrectAnswer(newAnswer);
                    questionPersistence.updateCorrectAnswer(questionID,newAnswer);
                }
                temp.set(i, newAnswer);
                if(nq.getCorrectAnswer().compareTo(oldAnswer)==0){
                    nq.setCorrectAnswer(newAnswer);
                }

                if(Services.isTesting()){}else {
                    mcAnswerPersistence.updateMCAnswer(questionID, oldAnswer, newAnswer);
                }
                break;
            }
        }
        //}
    }

    public Question getQuestionByID(int questionID){
        return questionPersistence.getQuestion(questionID);
    }
    public List<String> getMCAnswers(int questionID){
        if(Services.isTesting()){
             MCQuestion q = (MCQuestion)questionPersistence.getQuestion(questionID);
             if(q!=null){
                 return q.getAnswers();
             }else return null;
        }else {
             return mcAnswerPersistence.getMCAnswers(questionID);
        }
    }

}
