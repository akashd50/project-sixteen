package comp3350.project16.logic;

import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuestionPersistence;

public class DoQuizManager {

    private ArrayList<Question> questions;
    private int currentQuestion;
    private QuestionPersistence qp;

    public DoQuizManager(Quiz theQuiz, QuestionPersistence qp){
        this.questions = theQuiz.getQuestionsList();
        this.currentQuestion = 0;
        this.qp = qp;
    }

    public Question getNextQuestion(){

        Question nextQuestion = null;

        if(currentQuestion < questions.size()){
            nextQuestion = questions.get(currentQuestion);
            currentQuestion++;
        }

        return nextQuestion;
    }

    public Question getCurrentQuestion() {

        Question current;

        if (currentQuestion == 0) {
            current = questions.get(0);
        } else {
            current = questions.get(currentQuestion - 1);
        }

        return current;

    }

    //Testing
    public boolean setUsersAnswer(String answer) throws InvalidAnswerException, PersistenceException {

        answer = answer.trim().toLowerCase();
        getCurrentQuestion().setUsersAnswer(answer);
        qp.setUserAnswer(getCurrentQuestion().getQID(),getCurrentQuestion().getUsersAnswer());

        return true;
    }
}
