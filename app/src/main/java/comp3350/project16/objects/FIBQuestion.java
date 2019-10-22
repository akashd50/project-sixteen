package comp3350.project16.objects;

import comp3350.project16.Exceptions.InvalidFIBAnswerException;

public class FIBQuestion extends Question{

    //creates fill in the blanks question object
    public FIBQuestion(int ID, String owner) {
        super(ID, owner);
        setType("FIBQuestion");
    }

    @Override
    public boolean setUsersAnswer(String answer) throws InvalidFIBAnswerException {

        if(answer.matches("\\s*\\w+\\s*")){
            this.usersAnswer = answer;
        }else{
            throw new InvalidFIBAnswerException("Fill in the blank answers must be only one word");
        }

        return true;
    }

}
