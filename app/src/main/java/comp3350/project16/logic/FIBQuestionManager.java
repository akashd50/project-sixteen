package comp3350.project16.logic;

import comp3350.project16.Exceptions.InvalidFIBQuestionException;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.objects.FIBQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.persistence.QuestionPersistence;

public class FIBQuestionManager extends QuestionManager {

    public FIBQuestionManager(QuestionPersistence qp, String user){
        super(qp, user);
        setQuestion(new FIBQuestion(getQuestionPersistence().incrementQuestionID(), user));
    }

    @Override
    public boolean validateQuestion(Question toValidate) throws InvalidInputException, InvalidFIBQuestionException {
        //checks whether the fill in the blanks question is valid or not
        //if the question is valid then the function returns true
        FIBQuestion castToFIB = (FIBQuestion)toValidate;
        String ans = castToFIB.getCorrectAnswer();

        if (castToFIB.getQuestion() == null || castToFIB.getQuestion().matches("\\s*")) {//Question is just whitespace
            throw new InvalidInputException("Invalid input: question cannot be blank");
        } else if (ans == null || ans.matches("\\s*")) {//Nothing typed/empty answer
            throw new InvalidFIBQuestionException("Invalid Input : answer cannot be blank");
        } else if (!ans.matches("\\s*\\w+\\s*")) {//More than one word typed as an answer
            throw new InvalidFIBQuestionException("Invalid answer: fill in the blanks answer should be only one word");
        }

        return true;
    }
}
