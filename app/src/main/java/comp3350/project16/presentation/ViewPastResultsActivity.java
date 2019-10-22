package comp3350.project16.presentation;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.objects.Result;
import comp3350.project16.persistence.ResultPersistence;

public class ViewPastResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_results);

        //Set the action bar for use of the back button
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Past Results");
        bar.setDisplayHomeAsUpEnabled(true);

        populateResults();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }//back arrow finishes the activity and takes back to the main/parent activity.

    public void populateResults(){

        LinearLayout linear = findViewById(R.id.view_past_results_list);
        linear.removeAllViews();
        ResultPersistence rp = Services.getResultPersistence();

        List<Result> resultsList = rp.getAllResults(CurrentUser.getCurrentUser());

        if(resultsList.isEmpty()){
            TextView message = new TextView(this);
            message.setText("Sorry no results available right now");
            linear.addView(message);
        }else{

            LinearLayout row;

            TextView vpr_qName;
            TextView vpr_date;
            TextView vpr_score;

            for(Result r : resultsList){

                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                vpr_qName = new TextView(this);
                vpr_date = new TextView(this);
                vpr_score = new TextView(this);

                vpr_qName.setText(r.getQuizName());
                vpr_date.setText(r.getDate());
                vpr_score.setText(Integer.toString(r.getScore()) + "/" + Integer.toString(r.getMaxScore()));

                vpr_qName.setTextSize(30);
                vpr_date.setTextSize(30);
                vpr_score.setTextSize(30);

                vpr_qName.setPadding(8,8,8,8);
                vpr_date.setPadding(8,8,8,8);
                vpr_score.setPadding(8,8,8,8);
                
                row.addView(vpr_qName);
                row.addView(vpr_date);
                row.addView(vpr_score);

                linear.addView(row);
            }
        }
    }
}
