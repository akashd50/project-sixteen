package comp3350.project16.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.MCQuestionManager;

public class MCActivity extends AppCompatActivity {

    private ArrayList<View> checkBoxes = new ArrayList<>(); //List of checkboxes of all answers
    private MCQuestionManager qManager = new MCQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc);


        final Button enterAnswer = findViewById(R.id.addAnswerButton);
        final Button saveQuestion = findViewById(R.id.saveQuestionButton);
        final ConstraintLayout cl = findViewById(R.id.mc_activity_constraint_layout);

        cl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //Add the listeners for the enter answer and save buttons
        enterAnswer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                enterAnswerClicked();
            }
        });
        saveQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    private void saveClicked(){

        String question = ((TextView) findViewById(R.id.questionBox)).getText().toString(); //The current question

        //Set the question object being worked on to have the value of the question textfield
        qManager.setUsersQuestion(question);

        try{
            qManager.saveQuestion();
            finish();
        }catch(InvalidInputException e){
            errorDialog("Invalid Input", e.getMessage());
        }catch(InvalidQuestionException e){
            errorDialog("Invalid Question", e.getMessage());
        }catch(PersistenceException e){
            errorDialog("Persistence Exception", e.getCause().getMessage());
        }
    }

    private void enterAnswerClicked(){
        LinearLayout answersList = findViewById(R.id.answersListLayout);
        TextView answerInput = findViewById(R.id.answerBox);
        String answer = answerInput.getText().toString();
        TextView newAnswer;
        LinearLayout newAnswerLayout;
        CheckBox markCorrect;

        try{

            //Check that the users answer isnt blank
            qManager.addPossibleAnswer(answer);

            //Create a new answer layout for the list of possible answers
            markCorrect = new CheckBox(this);
            newAnswer = new TextView(this);
            newAnswerLayout = new LinearLayout(this);

            newAnswer.setText(answer);
            newAnswer.setTextSize(30);
            newAnswer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            markCorrect.setChecked(false);
            markCorrect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    checkboxClicked(v);
                }
            });

            checkBoxes.add(markCorrect);

            newAnswerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            newAnswerLayout.setLongClickable(true);
            newAnswerLayout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    final View view = v;
                    new AlertDialog.Builder(MCActivity.this)
                            .setTitle("Delete Answer")
                            .setMessage("Do you really want to delete this answer?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deleteAnswer(view);
                                    dialog.dismiss();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                    return true;
                }
            });

            //Add each view to the hierarchy
            newAnswerLayout.addView(newAnswer);
            newAnswerLayout.addView(markCorrect);
            answersList.addView(newAnswerLayout);
            answerInput.setText("");

        }catch(Exception e){
            errorDialog("Invalid Answer", e.getMessage());
        }
    }

    private void deleteAnswer(View v){

        //Get the layout which contains the current answer
        System.out.println("View: " + v.toString());

        //Try and delete the answer from the question
        qManager.deletePossibleAnswer(((TextView)((LinearLayout)v).getChildAt(0)).getText().toString());

        //Remove the corresponding GUI element for the deleted answer
        ((ViewManager)v.getParent()).removeView(v);
    }

    private void checkboxClicked(View v){
        //All logic to treat the checkboxes as if they were radio buttons
        //Because you can have a radiogroup in this situation

        CheckBox clicked = (CheckBox)v;
        LinearLayout parent = (LinearLayout)clicked.getParent();
        TextView correspondingText = (TextView)parent.getChildAt(0);

        if(clicked.isChecked()){//A new correct answer is checked

            //Set all check boxes to unclicked
            for(int i = 0; i < checkBoxes.size(); i++){
                ((CheckBox)checkBoxes.get(i)).setChecked(false);
            }

            //Check the new correct answer box and set it as the correct answer
            clicked.setChecked(true);
            try {
                qManager.setCorrectAnswer(correspondingText.getText().toString());
            }catch(Exception e){}

        }else{//The current correct answer is unchecked
            qManager.setCorrectAnswer(null);
        }
    }

    private void errorDialog(String title, String reason){
        //Display an alert dialog saying the reasoning and has an OK button
        new AlertDialog.Builder(MCActivity.this)
                .setTitle(title)
                .setMessage(reason)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }}).show();
    }
}
