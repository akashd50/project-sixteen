package comp3350.project16.persistence;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.objects.Question;

public interface QuestionPersistence {

    Question getQuestion(int questionID);
    void insertQuestion(Question toInsert);
    int incrementQuestionID();
    boolean deleteQuestion(int questionID);
    boolean updateQuestion(int questionID, Question toUpdate);
    boolean updateCorrectAnswer(int questionID, String correctAnswer);
    ArrayList<Question> getAllQuestions(String user);
    public boolean setUserAnswer(int questionID, String answer);
}
