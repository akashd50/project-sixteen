package comp3350.project16.objects;

public class Result {
    private int myResultID;
    private int quizID; // the quiz ID that this result belongs to
    private int score = 0; // quiz score
    private int maxScore; // what the quiz is out of
    private String date = "";
    private String quizName;
    private String userName;

    public Result(int resultID, int quizID, String quizName, String userName, int maxScore){
        myResultID = resultID;
        this.quizID = quizID;
        this.quizName = quizName;
        this.userName = userName;
        this.maxScore = maxScore;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public int getScore(){return score;}
    public void setScore(int score){
        this.score = score;
    }

    public int getResultID(){
        return this.myResultID;
    }

    public int getQuizID() {
        return quizID;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getUserName() {
        return userName;
    }

    public int getMaxScore(){return maxScore;}

}
