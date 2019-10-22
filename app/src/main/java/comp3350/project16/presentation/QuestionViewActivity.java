package comp3350.project16.presentation;
/*
QuestionViewActivity.java
This class shows the questions on the screen in a list and
Handles the addition or deletion of question.
It calls the manager classes to carry out the actions.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.objects.Question;

public class QuestionViewActivity extends AppCompatActivity {
    private ArrayAdapter<Question> questionListAdapter;
    private ListView questionList;
    private FloatingActionButton questionAddIcon, questionRemoveIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_view_activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.question_title);
        bar.setDisplayHomeAsUpEnabled(true); //sets the action bar title and back button action

        questionList = (ListView)findViewById(R.id.question_view_list);

        //creates the question adapter to show the questions in the list
        questionListAdapter = new ArrayAdapter<Question>(this, R.layout.question_view_item,
                R.id.question_view_item_text, new ArrayList<Question>(Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser())));
        questionList.setAdapter(questionListAdapter);

        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //no action given yet
                Question selected = questionListAdapter.getItem(position);
                showMenu(view, selected);

            }
        });//onClick for the question list.

        questionAddIcon = findViewById(R.id.question_add_icon);
        questionAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createQuestionTypeDialog().show();//would bring up a dialog to select the type of question to add.
                showQuestionTyeMenu(v);
            }
        });//on click for the add a new question button.
        questionRemoveIcon = findViewById(R.id.question_remove_icon);
        questionRemoveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQuestionRemoveDialog().show();//would bring up a dialog to select the type of question to add.
            }
        });//on click for the add a new question button.
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }//back arrow finishes the activity and takes back to the main/parent activity.

    @Override
    protected void onRestart() {
        super.onRestart();
        questionListAdapter.clear();
        questionListAdapter.addAll(Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser()));
        questionListAdapter.notifyDataSetChanged();
    }//if the activity restarts make sure to update the questions list.

    private void showQuestionTyeMenu(View v){
        /*
        This method shows a menu to select the type of question the user wants to add
         */
        PopupMenu menu = new PopupMenu(this,v);
        menu.setGravity(Gravity.RIGHT);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.add_question_type_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mcq_select:
                        startActivity(new Intent(QuestionViewActivity.this, MCActivity.class));
                        return true;
                    case R.id.fib_select:
                        startActivity(new Intent(QuestionViewActivity.this, FIBActivity.class));
                        return true;
                    case R.id.tnf_select:
                        startActivity(new Intent(QuestionViewActivity.this, TFActivity.class));
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }//showMenu

    private AlertDialog createQuestionRemoveDialog(){
        /*
        Creates a dialog to select the questions to remove from the list
         */
        final LayoutInflater inflater = QuestionViewActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.select_questions_dialog, null);
        Toolbar title = v.findViewById(R.id.select_questions_toolbar);
        title.setTitle(R.string.select_questions_tr);
        final ListView list = (ListView)v.findViewById(R.id.question_selection_view_list);
        ArrayList<Question> temporaryList = Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser());
        final ArrayAdapter<Question> temporaryAdapter = new ArrayAdapter<Question>(this,R.layout.quiz_questions_view_item,
                R.id.quiz_question_view_item_text,temporaryList);
        list.setAdapter(temporaryAdapter);

        final ArrayList<Question> selectedQuestions = new ArrayList<>();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question selected = temporaryAdapter.getItem(position);
                CheckBox cb = view.findViewById(R.id.quiz_questions_checkbox);
                if(!cb.isChecked()) {
                    cb.setChecked(true);
                    selectedQuestions.add(selected);
                }else{
                    cb.setChecked(false);
                    selectedQuestions.remove(selected);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionViewActivity.this);
        builder.setView(v).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                for(Question q: selectedQuestions) {
                    Services.getQuestionPersistence().deleteQuestion(q.getQID());
                }

                questionListAdapter.clear();
                questionListAdapter.addAll(Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser()));
                questionListAdapter.notifyDataSetChanged();
                Toast.makeText(QuestionViewActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }//deleting questions

    private void showMenu(View v, final Question selected){
        PopupMenu menu = new PopupMenu(this,v);
        menu.setGravity(Gravity.RIGHT);

        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.question_select_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.question_edit_icon:
                        Toast.makeText(QuestionViewActivity.this, "Go to Edit Activity",Toast.LENGTH_SHORT).show();
                        Intent editQuestion = new Intent(QuestionViewActivity.this,QuestionModificationActivity.class);
                        editQuestion.putExtra("question_id", selected.getQID());
                        startActivity(editQuestion);
                        return true;
                    case R.id.question_delete_icon:
                        Services.getQuestionPersistence().deleteQuestion(selected.getQID());
                        questionListAdapter.clear();
                        questionListAdapter.addAll(Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser()));
                        questionListAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.question_show_answer_icon:
                        AlertDialog.Builder answer = new AlertDialog.Builder(QuestionViewActivity.this);
                        answer.setMessage(selected.getCorrectAnswer()).create().show();
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }

}
