package comp3350.project16.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Result;
import comp3350.project16.logic.ResultManager;

public class ViewResultsActivity extends AppCompatActivity {

    Quiz displayingFor;
    ResultManager resultManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        Button finish = (Button) findViewById(R.id.close_results_button);

        finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        Intent calledFrom = getIntent();
        //Quiz callingQuiz
        displayingFor = Services.getQuizPersistence().getQuiz(calledFrom.getIntExtra("Quiz", 0));
        populateResults(displayingFor);
    }

    private void populateResults(Quiz toPopulate){
        resultManager = new ResultManager(Services.getResultPersistence(), displayingFor.getID(),
                displayingFor.getName(), displayingFor.getOwner(),displayingFor.getQuestionsList().size());
        int totalQuestions = 0;

        LinearLayout questionLayout = findViewById(R.id.questionLayout);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LinearLayout layout;
        TextView question;
        TextView usersAnswer;
        TextView correctAnswer;
        LinearLayout otherAnswersList;
        ArrayList<Question> questions = toPopulate.getQuestionsList();
        Question currentQuestion;

        for(int i = 0; i < questions.size(); i++){ //Loop through all questions in the quiz

            //increment the number of questions
            totalQuestions++;

            currentQuestion = questions.get(i);

            layout = new LinearLayout(this);
            question = new TextView(this);
            usersAnswer = new TextView(this);
            correctAnswer = new TextView(this);
            otherAnswersList = new LinearLayout(this);

            question.setText("Question " + totalQuestions + ": " + currentQuestion.getQuestion());
            correctAnswer.setText("The correct answer: " + currentQuestion.getCorrectAnswer());
            usersAnswer.setText("Your Answer: " + currentQuestion.getUsersAnswer());

            question.setTextSize(30);
            correctAnswer.setTextSize(30);
            usersAnswer.setTextSize(30);

            //Special handling for MC questions display
            if(currentQuestion instanceof MCQuestion){

                otherAnswersList.setOrientation(LinearLayout.VERTICAL);
                int count = 1;
                ArrayList<String> answers = ((MCQuestion) currentQuestion).getAnswers();
                TextView toAdd;

                for(int j = 0; j < answers.size(); j++){

                    toAdd = new TextView(this);
                    toAdd.setTextSize(30);
                    toAdd.setText(count + ") " + answers.get(j));
                    count++;

                    if(answers.get(j).equals(currentQuestion.getCorrectAnswer())){
                        toAdd.setTextColor(Color.parseColor("#00FF00"));
                    }

                    otherAnswersList.addView(toAdd);
                }
            }


            String correctAnswerString = currentQuestion.getCorrectAnswer().trim().toLowerCase();
            if(currentQuestion instanceof MCQuestion){
                ArrayList<String> mcAnswers = ((MCQuestion) currentQuestion).getAnswers();
                int userAnswerNum = Integer.valueOf(currentQuestion.getUsersAnswer()) - 1;
                String userAnswer = mcAnswers.get(userAnswerNum).trim().toLowerCase();

                if(!correctAnswerString.equals(userAnswer)){//Incorrect
                    usersAnswer.setTextColor(Color.parseColor("#FF0000"));

                }else{//Correct
                    usersAnswer.setTextColor(Color.parseColor("#00FF00"));
                    resultManager.addPoint();
                }
            }else{
                if(!correctAnswerString.trim().toLowerCase().equals(currentQuestion.getUsersAnswer().trim().toLowerCase())){//Incorrect
                    usersAnswer.setTextColor(Color.parseColor("#FF0000"));

                }else{//Correct
                    usersAnswer.setTextColor(Color.parseColor("#00FF00"));
                    resultManager.addPoint();
                }
            }


            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(question);

            if(currentQuestion instanceof MCQuestion){
                layout.addView(otherAnswersList);
            }else{
                layout.addView(correctAnswer);
            }

            layout.addView(usersAnswer);
            layout.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels - 16, ViewGroup.LayoutParams.MATCH_PARENT));
            questionLayout.addView(layout);
        }

        //save the results
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.getTime();

        String date = dateFormat.format(cal.getTime()); //get the date

        resultManager.setFinalScore();
        resultManager.setDate(date);
        resultManager.saveResult();

        //Change the headings to display the correct information for the quiz
        ((TextView) findViewById(R.id.textViewQuizName)).setText("Quiz Name: " + displayingFor.getName());
        ((TextView) findViewById(R.id.textViewScore)).setText("Score: " + resultManager.getScore()+
            "/" + totalQuestions);

    }
}
