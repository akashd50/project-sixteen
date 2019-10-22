package comp3350.project16.application;

import comp3350.project16.persistence.MCAnswerPersistence;
import comp3350.project16.persistence.QuestionPersistence;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.persistence.ResultPersistence;
import comp3350.project16.persistence.TagPersistence;
import comp3350.project16.persistence.UserPersistence;
import comp3350.project16.persistence.hsqldb.MCAnswerPersistenceHSQLDB;
import comp3350.project16.persistence.hsqldb.QuestionPersistenceHSQLDB;
import comp3350.project16.persistence.hsqldb.QuizPersistenceHSQLDB;
import comp3350.project16.persistence.hsqldb.ResultPersistenceHSQLDB;
import comp3350.project16.persistence.hsqldb.TagPersistenceHSQLDB;
import comp3350.project16.persistence.hsqldb.UserPersistenceHSQLDB;
import comp3350.project16.persistence.stubs.QuestionPersistenceStub;
import comp3350.project16.persistence.stubs.QuizPersistenceStub;
import comp3350.project16.persistence.stubs.ResultPersistenceStub;
import comp3350.project16.persistence.stubs.TagPersistenceStub;
import comp3350.project16.persistence.stubs.UserPersistenceStub;

public class Services {

    //CHANGE THIS LINE TO SWITCH BETWEEN THE STUBS AND ACTUAL DATABASE
    private static final String mode = "TESTING";//Set to "TESTING" to use stubs otherwise hsqldb will be used

    private static QuestionPersistence questionPersistence = null;
    private static QuizPersistence quizPersistence = null;
    private static TagPersistence tagPersistence = null;
    private static MCAnswerPersistence mcAnswerPersistence = null;
    private static UserPersistence userPersistence=null;
    private static ResultPersistence resultPersistence = null;


    public static synchronized QuestionPersistence getQuestionPersistence(){
        if (questionPersistence == null) {
            if(mode.equals("TESTING")){
                questionPersistence = new QuestionPersistenceStub();
            }else{
                questionPersistence = new QuestionPersistenceHSQLDB(Main.getDBPathName(), Services.getMCAnswerPersistence());
            }
        }

        return questionPersistence;
    }

    public static synchronized TagPersistence getTagPersistence(){
        if (tagPersistence == null) {
            if(mode.equals("TESTING")){
                tagPersistence = new TagPersistenceStub();
            }else{
                tagPersistence = new TagPersistenceHSQLDB(Main.getDBPathName());
            }
        }
        return tagPersistence;
    }

    public static synchronized QuizPersistence getQuizPersistence(){
        if (quizPersistence == null) {
            if(mode.equals("TESTING")){
                quizPersistence = new QuizPersistenceStub();
            }else{
                quizPersistence = new QuizPersistenceHSQLDB(Main.getDBPathName(), Services.getQuestionPersistence());
            }
        }
        return quizPersistence;
    }

    public static synchronized MCAnswerPersistence getMCAnswerPersistence(){
        if (mcAnswerPersistence == null) {
            mcAnswerPersistence = new MCAnswerPersistenceHSQLDB(Main.getDBPathName());
        }
        return mcAnswerPersistence;
    }

    public static synchronized boolean isTesting(){
        if(mode.equals("TESTING")) return true;
        else return false;
    }

    public static synchronized UserPersistence getUserPersistence(){
        if(userPersistence==null)
            if(mode.equals("TESTING"))
                userPersistence=new UserPersistenceStub();
            else
                userPersistence=new UserPersistenceHSQLDB(Main.getDBPathName());

        return userPersistence;
    }

    public static synchronized String getMode(){return mode;}

    public static synchronized ResultPersistence getResultPersistence(){
        if (resultPersistence == null) {
            if(mode.equals("TESTING")){
                resultPersistence = new ResultPersistenceStub();
            }else{
                resultPersistence = new ResultPersistenceHSQLDB(Main.getDBPathName());
            }
        }

        return resultPersistence;
    }

}
