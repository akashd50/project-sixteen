package comp3350.project16.objects;

import comp3350.project16.Exceptions.InvalidAnswerException;

public abstract class Question {

    private int ID;
    private String question;
    private String owner;
    private String type;
    protected String correctAnswer;
    protected String usersAnswer;

    public Question(int ID, String owner){
        this.ID = ID;
        this.owner = owner;
    }
    public abstract boolean setUsersAnswer(String answer)throws InvalidAnswerException;

    public void setQuestion(String question){this.question = question;}
    public boolean setCorrectAnswer(String answer){this.correctAnswer = answer; return true;}
    public void setOwner(String owner){this.owner = owner;}
    public void setType(String type){this.type = type;}

    public int getQID(){return this.ID;}
    public String getCorrectAnswer(){return this.correctAnswer;}
    public String getQuestion(){return this.question;}
    public String getUsersAnswer(){return this.usersAnswer;}
    public String getOwner(){return this.owner;}
    public String getType(){return this.type;}

    public String toString(){return this.question;}
}
