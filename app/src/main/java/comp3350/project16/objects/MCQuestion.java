package comp3350.project16.objects;

import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.InvalidMCAnswerException;

public class MCQuestion extends Question {

    private ArrayList<String> answers = new ArrayList<>();

    public MCQuestion(int ID, String owner){

        super(ID, owner);
        setType("MCQuestion");
    }

    public void addAnswer(String answer){
        answers.add(answer);
    }
    public void deleteAnswer(String toDelete){

        if(getCorrectAnswer() != null && getCorrectAnswer().trim().toLowerCase().equals(toDelete.trim().toLowerCase())){
            correctAnswer = null;
        }

        answers.remove(toDelete);
    }
    public ArrayList<String> getAnswers(){return answers;}

    @Override
    public boolean setUsersAnswer(String answer) throws InvalidMCAnswerException {

        if(answer.matches("\\d") && Integer.valueOf(answer) > 0 && Integer.valueOf(answer) <= getAnswers().size()){
            this.usersAnswer = answer;
        }else{
            throw new InvalidMCAnswerException("Multiple choice answers must be a number representing an answer.");
        }

        return true;
    }

    @Override
    public boolean setCorrectAnswer(String answer) {
        boolean answerFound = false;
        for(String s: answers){
            if(s.trim().toLowerCase().compareTo(answer.trim().toLowerCase())==0){
                answerFound = true;
            }
        }
        if(answerFound) {
            super.setCorrectAnswer(answer);
            return true;
        }else return false;
    }

}
