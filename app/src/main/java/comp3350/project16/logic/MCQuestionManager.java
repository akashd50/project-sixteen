package comp3350.project16.logic;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidMCQuestionException;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.persistence.QuestionPersistence;

public class MCQuestionManager extends QuestionManager {

    public MCQuestionManager(QuestionPersistence qp, String user){
        super(qp, user);
        setQuestion(new MCQuestion(getQuestionPersistence().incrementQuestionID(), user));
    }

    @Override
    protected boolean validateQuestion(Question toValidate)throws InvalidInputException, InvalidMCQuestionException {

        MCQuestion castToMC = (MCQuestion)toValidate;

        if(castToMC.getQuestion() == null || castToMC.getQuestion().matches("\\s*")){//Question is just whitespace
            throw new InvalidInputException("Question can't be blank.");
        }else if(castToMC.getAnswers().size() < 3){//Not enough answers for a multiple choice question
            throw new InvalidMCQuestionException("Multiple choice questions must have more than 2 answers");
        }else if(castToMC.getCorrectAnswer() == null){//No correct answer selected
            throw new InvalidMCQuestionException("You must select a correct answer for multiple choice questions");
        }

        return true;
    }

    public boolean addPossibleAnswer(String answer)throws InvalidInputException{

        validateAnswer(answer);
        ((MCQuestion)getQuestion()).addAnswer(answer);

        return true;
    }

    public boolean deletePossibleAnswer(String todelete){
        ((MCQuestion)getQuestion()).deleteAnswer(todelete);
        return true;
    }
}
