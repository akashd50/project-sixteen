package comp3350.project16.presentation;
/*
QuizViewManager.java
Shows the list of all the quizzes on the screen
Handling the proper calls for adding quizzes
If a quiz is clicked, launches the appropriate activity to handle the action.
 */

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidQuizException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.QuizManager;
import comp3350.project16.objects.Quiz;
import comp3350.project16.persistence.QuizPersistence;
import comp3350.project16.persistence.stubs.QuizPersistenceStub;

public class QuizViewActivity extends AppCompatActivity {
    private ArrayAdapter<Quiz> adapter;
    private FloatingActionButton quizAddButton;
    private ListView quizView;
    protected QuizManager quizManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view_main);

        //get the action bar and set the title
        ActionBar bar = getSupportActionBar();
        bar.setTitle(R.string.quizzes_title);
        bar.setDisplayHomeAsUpEnabled(true);

        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());// initialize the quizManager instance

        //get the layout
        quizView = (ListView)findViewById(R.id.quiz_view_list);

        //create an adapter for the list to display the quizzes
        adapter = new ArrayAdapter<Quiz>(this,R.layout.quiz_view_item,
                                            R.id.quiz_view_item_text, new ArrayList<Quiz>(Services.getQuizPersistence().getQuizList(CurrentUser.getCurrentUser())));
        quizView.setAdapter(adapter);

        quizView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Quiz selected = adapter.getItem(position);
                showQuizSelectMenu(view,selected);
            }//on item click
        });//on click listener for the list
        final AlertDialog add = createDialog();
        quizAddButton = (FloatingActionButton)findViewById(R.id.quiz_add_icon);
        quizAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.show();
            }
        });//shows the add quiz dialog
    }

    private void showQuizSelectMenu(final View v, final Quiz selected){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.quiz_select_menu);
        popupMenu.setGravity(Gravity.RIGHT);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.quiz_edit_ic:
                        Intent editQuiz = new Intent(QuizViewActivity.this, QuizModificationActivity.class);
                        editQuiz.putExtra("quiz_name",selected.getID());
                        startActivity(editQuiz);
                        adapter.notifyDataSetChanged();
                        return true;
                    case R.id.quiz_play_ic:
                        try{
                            if(quizManager.validateQuizForPlay(selected.getID())){
                                Intent doaquiz = new Intent(QuizViewActivity.this, DoAQuizActivity.class);
                                doaquiz.putExtra("quiz_id", selected.getID());
                                startActivity(doaquiz);
                            }
                        }catch(InvalidQuizException e){
                            AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuizViewActivity.this);
                            warningBuilder.setMessage(e.getMessage()).create().show();
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.quiz_delete_ic:
                        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(QuizViewActivity.this);
                        confirmDelete.setTitle("Are you sure that you want to delete this Quiz?");
                        confirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                quizManager.deleteQuiz(selected.getID());
                                adapter.clear();
                                adapter.addAll(quizManager.getQuizList());
                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing, close the dialog
                            }
                        }).show();
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private AlertDialog createDialog(){
        /*
        Creates the dialog for entering a new quiz name
         */
        final LayoutInflater inflater = QuizViewActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_quiz, null);

        final EditText et = (v.findViewById(R.id.enter_new_name));
        Toolbar toolbar = v.findViewById(R.id.enter_new_name_toolbar);
        toolbar.setTitle(R.string.new_name_dialog);

        if(et.getText().toString().compareTo("")==0){
            et.setError("Quiz name can't be empty or same as any other quiz!");
        }//sets the warning for the edit text in case it's empty

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizViewActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       //add a quiz.
                        String name = et.getText().toString();
                        try {
                            if (quizManager.saveQuiz(new Quiz(name, Services.getQuizPersistence().incrementQuizID(), CurrentUser.getCurrentUser()))) {
                                adapter.clear();
                                adapter.addAll(quizManager.getQuizList());
                                adapter.notifyDataSetChanged();//notify changes to the adapter
                                et.getText().clear();
                                Toast.makeText(QuizViewActivity.this, "New Quiz Added. Click To Edit", Toast.LENGTH_LONG).show();
                            }
                        }catch(InvalidQuizException e){
                            createDialog().show();//show the new name dialog again until a valid name is enterer
                            //or the dialog is canceled
                            e.printStackTrace();
                        }
                    }
                })//set positive button to add the quiz
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });//set negative button to cancel
        return builder.create();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.clear();
        adapter.addAll(quizManager.getQuizList());
        adapter.notifyDataSetChanged();
    }//if the activity restarts, update the notify the adapter to update the list.

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }//on back arrow go to the parent activity
}
