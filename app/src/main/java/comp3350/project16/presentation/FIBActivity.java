package comp3350.project16.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import comp3350.project16.Exceptions.InvalidFIBQuestionException;
import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidQuestionException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.FIBQuestionManager;
import comp3350.project16.logic.QuestionManager;
import comp3350.project16.objects.FIBQuestion;

public class FIBActivity extends AppCompatActivity{

    private QuestionManager qManager = new FIBQuestionManager(Services.getQuestionPersistence(), CurrentUser.getCurrentUser());

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fib);

        final Button saveQuestion = findViewById(R.id.saveQuestionButton);
        final ConstraintLayout cl = findViewById(R.id.fib_activity_constraint_layout);

        cl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        saveQuestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveClicked();
            }
        });
    }

    private void saveClicked() {

        String question = ((TextView) findViewById(R.id.questionBox)).getText().toString(); //The current question
        String answer = ((TextView) findViewById(R.id.answerBox)).getText().toString();
        //Set the question object being worked on to have the value of the question text field
        qManager.setUsersQuestion(question);
        qManager.setCorrectAnswer(answer);

        //Try to validate the question object
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
        new AlertDialog.Builder(FIBActivity.this)
                .setTitle(title)
                .setMessage(reason)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }}).show();
    }
}
