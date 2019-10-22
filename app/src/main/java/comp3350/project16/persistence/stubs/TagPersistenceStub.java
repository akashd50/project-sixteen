package comp3350.project16.persistence.stubs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import comp3350.project16.application.Services;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.QuestionTagPair;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Tag;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.TagPersistence;
import comp3350.project16.presentation.CurrentUser;

public class TagPersistenceStub implements TagPersistence {
    private int currentID = 1;
    private Hashtable<String, ArrayList<Tag>> tagMap;
    private Hashtable<String , ArrayList<QuestionTagPair>> questionTagTable;

    public TagPersistenceStub(){
        tagMap = new Hashtable<>();
        questionTagTable = new Hashtable<>();
        questionTagTable.put(CurrentUser.getCurrentUser(),new ArrayList<QuestionTagPair>());
        tagMap.put(CurrentUser.getCurrentUser(),new ArrayList<Tag>());

        tagMap.get(CurrentUser.getCurrentUser()).add(new Tag("COMP", this.incrementTagID()));
        tagMap.get(CurrentUser.getCurrentUser()).add(new Tag("3350", this.incrementTagID()));
        tagMap.get(CurrentUser.getCurrentUser()).add(new Tag("sample", this.incrementTagID()));
    }
    public Tag getTag(int tagID, String user){
        ArrayList<Tag> tags = tagMap.get(user);
        for(int i = 0; i < tags.size(); i++){
            if(tags.get(i).getID() == tagID){
                return tags.get(i);
            }
        }
        return new Tag("",1);
    }
    public List<Tag> getAllTags(String user){
        return tagMap.get(CurrentUser.getCurrentUser());
    }
    public void insertTag(Tag toInsert, String user){
        if(!tagMap.containsKey(user)){
            tagMap.put(user, new ArrayList<Tag>());
        }
        tagMap.get(user).add(toInsert);
    }

    public boolean deleteTag(int tadID, String user){
        ArrayList<Tag> tags = tagMap.get(user);
        if(tags==null) return false;
        for(int i = 0; i < tags.size(); i++){
            if(tags.get(i).getID() == tadID){
                removeTagFromAllQuestions(tags.get(i));
                tags.remove(i);
                return true;
            }
        }
        return false;
    }
    public int incrementTagID(){
        int toRet = currentID;
        currentID++;
        return toRet;
    }
    public List<Integer> getQuestionsWithTag(String tag){
        ArrayList<Tag> tempTag = tagMap.get(CurrentUser.getCurrentUser());
        Tag toFind = null;
        for(int i = 0; i < tempTag.size(); i++){
            if(tempTag.get(i).getTag() == tag){
                toFind = tempTag.get(i);
                break;
            }
        }

        if(toFind==null) return null;

        ArrayList<Integer> toReturn = new ArrayList<>();
        ArrayList<QuestionTagPair> qtp = questionTagTable.get(CurrentUser.getCurrentUser());
        if(qtp!=null) {
            for (QuestionTagPair q : qtp) {
                int currTag = q.getTagID();
                if (currTag == toFind.getID()) {
                    toReturn.add(q.getQuestionID());
                }
            }
        }
        return toReturn;
    }
    public void removeAllTagsFromQuestion(int questionID){
        ArrayList<QuestionTagPair> qtp = questionTagTable.get(CurrentUser.getCurrentUser());
        for(int i = 0; i < qtp.size(); i++){
            QuestionTagPair q = qtp.get(i);
            if(q.getQuestionID()==questionID) {
                qtp.remove(i);
                i--;
            }
        }
    }
    public void removeFromQuestionTagTable(int questionID, ArrayList<Tag> tags){
        ArrayList<QuestionTagPair> qtp = questionTagTable.get(CurrentUser.getCurrentUser());
        for(Tag t: tags){
            for(int i = 0; i < qtp.size(); i++){
                QuestionTagPair q = qtp.get(i);
                if(q.getQuestionID() == questionID && q.getTagID() == t.getID()) {
                    qtp.remove(i);
                    i--;
                }
            }
        }
    }

    private void removeTagFromAllQuestions(Tag t){
        ArrayList<QuestionTagPair> qtp = questionTagTable.get(CurrentUser.getCurrentUser());
        if(qtp!=null) {
            for (int i = 0; i < qtp.size(); i++) {
                QuestionTagPair q = qtp.get(i);
                if (q.getTagID() == t.getID()) {
                    qtp.remove(i);
                    i--;
                }
            }
        }
    }

    public void insertInQuestionTagTable(int q, Tag t){
        ArrayList<QuestionTagPair> temp = questionTagTable.get(CurrentUser.getCurrentUser());
        if(temp==null){
            questionTagTable.put(CurrentUser.getCurrentUser(),new ArrayList<QuestionTagPair>());
        }
        temp = questionTagTable.get(CurrentUser.getCurrentUser());
        temp.add(new QuestionTagPair(q,t.getID()));
    }
    public List<Tag> getQuestionTags(int questionID){
        ArrayList<Tag> toReturn = new ArrayList<>();
        ArrayList<QuestionTagPair> qtp = questionTagTable.get(CurrentUser.getCurrentUser());

        if(qtp==null)return null;//no data present

        for(QuestionTagPair q: qtp){
            if(q.getQuestionID() == questionID) {
                toReturn.add(this.getTag(q.getTagID(),CurrentUser.getCurrentUser()));
            }
        }
        return toReturn;
    }
}
