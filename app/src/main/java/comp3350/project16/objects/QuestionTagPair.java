package comp3350.project16.objects;
/*
This class provides an object question tag pair to be stored in the stubs.
Contains a question id and an associated tag id.
A question can have many different tags and the same tag can be used by many questions.
 */
public class QuestionTagPair {
    private int questionID;
    private int tagID;
    public QuestionTagPair(int qid, int tid){
        questionID = qid;tagID = tid;
    }

    public int getQuestionID() {
        return questionID;
    }

    public int getTagID() {
        return tagID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }
}//majority of the class is just getters and setters.
