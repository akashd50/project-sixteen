package comp3350.project16.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.DoQuizManager;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuizPersistence;

public class DoAQuizActivity extends AppCompatActivity {

    private DoQuizManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_aquiz);

        //Set the action bar for use of the back button
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        final ConstraintLayout cl = findViewById(R.id.do_quiz_constraint_layout);

        cl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        //Get the button that will save the answer and move to the next question
        Button nextQuestionButton = (Button) findViewById(R.id.do_quiz_activity_next_question_button);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveUsersAnswer();
            }
        });

        //Retrieve the Quiz object by looking it up based on its ID passed from the QuizView Activity
        QuizPersistence pers = Services.getQuizPersistence();
        Quiz toDo = pers.getQuiz(getIntent().getIntExtra("quiz_id", 0));

        //Create the new DoQuizManager to run the quiz
        manager = new DoQuizManager(toDo, Services.getQuestionPersistence());

        bar.setTitle(toDo.getName());

        //Display the first question to be answered
        displayQuestion();


    }

    private void displayQuestion(){

        Question nextQuestion = manager.getNextQuestion();
        FrameLayout questionFrame = findViewById(R.id.do_quiz_question_frame);
        LinearLayout answersLayout = new LinearLayout(this);
        answersLayout.setOrientation(LinearLayout.VERTICAL);


        if(nextQuestion != null){

            LinearLayout layout = new LinearLayout(this);
            TextView displayQuestion = new TextView(this);

            if(nextQuestion instanceof MCQuestion){

                TextView answer;

                ArrayList<String> answers = ((MCQuestion) nextQuestion).getAnswers();

                for(int i = 0; i < answers.size(); i++){
                    answer = new TextView(this);
                    answer.setText((i + 1) + ") " + answers.get(i));
                    answer.setTextSize(30);

                    answersLayout.addView(answer);
                }
            }

            //increment the number of questions
            displayQuestion.setText("Question: " + nextQuestion.getQuestion());
            displayQuestion.setTextSize(30);

            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(displayQuestion);
            layout.addView(answersLayout);
            layout.setFocusable(true);
            if(questionFrame.getChildCount() > 0){
                questionFrame.removeAllViews();
            }

            questionFrame.addView(layout);

        }else{

            //Start Results activity if there are no more questions to display
            startActivity(new Intent(DoAQuizActivity.this, ViewResultsActivity.class).putExtra("Quiz",getIntent().getIntExtra("quiz_id", 0) ));
            finish();
            System.out.println("Should be viewing results right now");
        }
    }

    private void saveUsersAnswer(){

        EditText usersBox = findViewById(R.id.do_quiz_users_answer_textview);
        String usersAnswer = (usersBox).getText().toString();

        //Try to set the users answer as the question answer and verify
        try{
            manager.setUsersAnswer(usersAnswer);
            usersBox.setText("");
            displayQuestion();
        }catch(InvalidAnswerException e){
            errorDialog("Invalid Answer", e.getMessage());
        }catch(PersistenceException e){
            errorDialog("Persistence Exception", e.getMessage());
        }
    }

    private void errorDialog(String title, String reason){
        //Display an alert dialog saying the reasoning and has an OK button
        new AlertDialog.Builder(DoAQuizActivity.this)
                .setTitle(title)
                .setMessage(reason)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }}).show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}