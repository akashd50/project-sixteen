package comp3350.project16.objects;

import comp3350.project16.Exceptions.InvalidTFAnswerException;

public class TFQuestion extends Question{

    public TFQuestion(int ID, String owner) {
        super(ID, owner);
        setType("TFQuestion");
    }

    @Override
    public boolean setUsersAnswer(String answer) throws InvalidTFAnswerException {

        if(answer.equals("true") || answer.equals("t")){
            this.usersAnswer = "true";
        }else if(answer.equals("false") || answer.equals("f")){
            this.usersAnswer = "false";
        }else{//invalid answer format
            throw new InvalidTFAnswerException("True/False questions answers must be of the form true/false or t/f");
        }

        return true;
    }
}
