package comp3350.project16.objects;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import comp3350.project16.application.Services;

public class Quiz{
    private ArrayList<Question> quizQuestions;
    private String name;
    private int ID;
    private String owner;

    public Quiz(String n, int ID, String owner){
        quizQuestions = new ArrayList<>();
        this.name = n;
        this.ID = ID;
        this.owner = owner;
    }

    public boolean search(Question q){
        for(int i=0;i<quizQuestions.size();i++){
            if(quizQuestions.get(i).toString().compareTo(q.toString())==0){
                return true;
            }
        }
        return false;
    }

    public boolean contains(int qID){
        for(int i=0;i<quizQuestions.size();i++){
            if(quizQuestions.get(i).getQID() == qID){
                return true;
            }
        }
        return false;
    }

    public boolean remove(int qID){
        for(int i=0;i<quizQuestions.size();i++){
            if(quizQuestions.get(i).getQID() == qID){
                quizQuestions.remove(i);
                return true;
            }
        }
        return false;
    }

    public void addQuestion(Question q){
        quizQuestions.add(q);
    }

    public ArrayList<Question> getQuestionsList(){
        return this.quizQuestions;
    }

    public int getID(){return this.ID;}

    public String toString(){
        return this.name;
    }
    public String getName(){return this.name;}
    public String getOwner(){return this.owner;}

    public void setOwner(String owner){this.owner = owner;}
    public void setName(String newName){this.name = newName;}
}
