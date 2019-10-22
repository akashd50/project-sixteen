package comp3350.project16.persistence;

import java.util.List;

import comp3350.project16.objects.Question;

public interface MCAnswerPersistence {

    void updateMCAnswers(Question toUpdate);
    void deleteMCAnswers(int questionID);
    List<String> getMCAnswers(int questionID);
    void insertMCAnswers(Question toInsert);

    int incrementMCAnswerID();

    void deleteMCAnswer(int questionID, String toDelete);

    void updateMCAnswer(int questionID, String oldAnswer, String newAnswer);

    void addMCAnswer(int questionID, String newAnswer);

}
