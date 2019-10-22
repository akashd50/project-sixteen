package comp3350.project16.persistence;

import java.util.ArrayList;
import java.util.List;

import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Tag;

public interface TagPersistence {
    Tag getTag(int tagID, String user);
    List<Tag> getAllTags(String user);
    void insertTag(Tag toInsert, String user);
    boolean deleteTag(int tadID, String user);
    int incrementTagID();
    List<Integer> getQuestionsWithTag(String tag);
    void removeAllTagsFromQuestion(int questionID);
    void removeFromQuestionTagTable(int questionID, ArrayList<Tag> tags);
    void insertInQuestionTagTable(int q, Tag t);
    List<Tag> getQuestionTags(int questionID);

    }
