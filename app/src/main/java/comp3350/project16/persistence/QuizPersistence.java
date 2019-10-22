package comp3350.project16.persistence;

import java.util.List;

import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Tag;

public interface QuizPersistence {
    Quiz getQuiz(int quizID);
    boolean insertQuiz(Quiz toInsert);
    boolean deleteQuiz(int quizID);
    List<Quiz> getQuizList(String user);
    boolean findQuiz(String name);
    void updateQuizName(int ID, String name);
    int incrementQuizID();
    void addQuestionToQuiz(int quizID, int questionID);
    boolean removeQuestionFromQuiz(int quizID, Question toRemove);
}
