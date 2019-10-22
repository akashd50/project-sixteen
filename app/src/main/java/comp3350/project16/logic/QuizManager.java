package comp3350.project16.logic;
/*
QuizManager.java
Manages Quizzes.
Provide methods to save, fetch and remove quizzes from the database
provide methods to add, remove, questions from the quizzes
 */

import java.util.List;

import comp3350.project16.Exceptions.EmptyQuizException;
import comp3350.project16.Exceptions.InvalidQuizException;
import comp3350.project16.Exceptions.InvalidQuizNameException;
import comp3350.project16.Exceptions.InvalidQuizQuestionException;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuizPersistence;

public class QuizManager {
    private QuizPersistence quizPersistence;
    private String user;

    public QuizManager(QuizPersistence qp, String user){
        this.quizPersistence = qp;
        this.user = user;
    }//constructor, get the current persistence from the services

    public boolean saveQuiz(Quiz quiz) throws InvalidQuizException {
        if(validateQuiz(quiz)) {
            quizPersistence.insertQuiz(quiz);
            return true;
        }else return false;
    }//saves the quiz in the database

    public Quiz getQuiz(int id){
        return quizPersistence.getQuiz(id);
    }

    public List<Quiz> getQuizList(){
        return quizPersistence.getQuizList(user);
    }//returns the full question list

    public boolean validateQuiz(Quiz q) throws InvalidQuizNameException {
        if(q.getName().compareTo("")==0 || quizPersistence.findQuiz(q.getName())) {
            throw new InvalidQuizNameException("Quiz name can't be empty or same as any other quiz!");
        }
        return true;
    }//validates the quiz, checks it's name etc.

    public boolean validateQuestionForQuiz(int quizID, int questionId) throws InvalidQuizQuestionException {
        Quiz quiz = quizPersistence.getQuiz(quizID);
        if(quiz.contains(questionId)){
            throw new InvalidQuizQuestionException("Question is already present in the current quiz!");
        }
        return true;
    }//validates the question for the quiz before adding it

    public boolean addQuestionToQuiz(int quizId, Question q) throws InvalidQuizException{
        if(validateQuestionForQuiz(quizId, q.getQID())){
            quizPersistence.addQuestionToQuiz(quizId, q.getQID());
            return true;
        }else return false;
    }//adds the question to the quiz, validates it first.

    public boolean removeQuestionFromQuiz(int quizId, Question q){
        return quizPersistence.removeQuestionFromQuiz(quizId, q);
    }//removes the question from the quiz

    public boolean deleteQuiz(int quizId){
        if(quizPersistence.deleteQuiz(quizId)) return true;
        else return false;
    }//deletes the quiz from the database

    public boolean changeQuizName(int quizId, String name) throws InvalidQuizException{

        Quiz copy = new Quiz(name, quizId, user);
        copy.setName(name);

        if(validateQuiz(copy)){
            quizPersistence.updateQuizName(quizId, name);
            return true;
        }else{
            return false;
        }
    }//changes the quiz name after validating that its not empty or same as some other quiz

    public boolean validateQuizForPlay(int quizId) throws EmptyQuizException {
        if(quizPersistence.getQuiz(quizId).getQuestionsList().size()==0){
            throw  new EmptyQuizException("Quiz has no questions. Please add some first!");
        }
        return true;
    }
}
