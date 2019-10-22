package comp3350.project16.logic;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.objects.Question;
import comp3350.project16.persistence.QuestionPersistence;

public abstract class QuestionManager {

    private QuestionPersistence questionPersistence;
    private Question theQuestion;
    private String user;

    public QuestionManager(QuestionPersistence qp, String user){
        this.questionPersistence = qp;
        this.user = user;
    }

    public void setQuestion(Question toSet){this.theQuestion = toSet;}
    public Question getQuestion(){return theQuestion;}
    public QuestionPersistence getQuestionPersistence(){return questionPersistence;}


    protected boolean validateAnswer(String answer)throws InvalidInputException{

        if(answer == null || answer.matches("\\s*")){//Nothing typed/empty answer
            throw new InvalidInputException("Answers cannot be blank.");
        }

        return true;
    }

    public boolean saveQuestion()throws InvalidInputException, InvalidQuestionException, PersistenceException{
        theQuestion.setOwner(user);
        validateQuestion(theQuestion);

        getQuestionPersistence().insertQuestion(theQuestion);

        return true;
    }

    public void setUsersQuestion(String question){theQuestion.setQuestion(question);}

    public void setCorrectAnswer(String answer){theQuestion.setCorrectAnswer(answer);}

    abstract boolean validateQuestion(Question toValidate) throws InvalidInputException, InvalidQuestionException;
}
