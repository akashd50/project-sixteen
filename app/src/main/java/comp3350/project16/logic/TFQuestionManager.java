package comp3350.project16.logic;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidTFQuestionException;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.TFQuestion;
import comp3350.project16.persistence.QuestionPersistence;

public class TFQuestionManager extends QuestionManager {

    public TFQuestionManager(QuestionPersistence qp, String user){
        super(qp, user);
        setQuestion(new TFQuestion(getQuestionPersistence().incrementQuestionID(), user));
    }

    @Override
    public boolean validateQuestion(Question toValidate)throws InvalidInputException, InvalidTFQuestionException {
        TFQuestion castToTF = (TFQuestion)toValidate;
        if(castToTF.getQuestion() == null || castToTF.getQuestion().matches("\\s*")) {
            throw new InvalidInputException("Question cannot be empty");
        }else if(castToTF.getCorrectAnswer() == null){//No correct answer selected
            throw new InvalidTFQuestionException("Must select a correct answer for T/F question");
        }
        return true;
    }
}
