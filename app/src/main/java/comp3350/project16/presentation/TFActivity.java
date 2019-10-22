package comp3350.project16.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.QuestionManager;
import comp3350.project16.logic.TFQuestionManager;

public class TFActivity extends AppCompatActivity{

    private QuestionManager qManager = new TFQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tf);

        final Button saveQuestion = findViewById(R.id.saveQuestionButton); // button to save questions
        final ConstraintLayout cl = findViewById(R.id.tf_activity_constraint_layout);

        cl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        //Add the listener for the save button
        saveQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    // set the answer to either True or False
    public void onTFRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        String trueAnswer = "true";
        String falseAnswer = "false";

        // Check which T/F radio button was clicked
        switch(view.getId()) {
            case R.id.trueRButton:
                if (checked)
                    // set Answer to True
                    qManager.setCorrectAnswer(trueAnswer);
                    break;
            case R.id.falseRButton:
                if (checked)
                    // set answer to False
                    qManager.setCorrectAnswer(falseAnswer);
                    break;
        }
    }

    private void saveClicked(){

        String question = ((TextView) findViewById(R.id.questionBox)).getText().toString(); //The current question
            // typed by the user in the question box

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

    private void errorDialog(String title, String reason){
        //Display an alert dialog saying the reasoning and has an OK button
        new AlertDialog.Builder(TFActivity.this)
                .setTitle(title)
                .setMessage(reason)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }}).show();
    }
}
