package comp3350.project16.presentation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidAnswerException;
import comp3350.project16.Exceptions.InvalidQuizException;
import comp3350.project16.Exceptions.InvalidQuizQuestionException;
import comp3350.project16.Exceptions.InvalidTagException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.QuestionManager;
import comp3350.project16.logic.QuizManager;
import comp3350.project16.logic.TagsManager;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Tag;

public class QuizModificationActivity extends AppCompatActivity {
    private ListView questionList; //list view to show questions
    private FloatingActionButton questionAddIcon; //button to add questions to quiz
    private QuizManager quizManager;
    private int currentQuiz;
    private ArrayAdapter<Question> questionArrayAdapter; //adapter for the questions list
    private ActionBar actionBar; //current activity's action bar
    //private QuestionManager questionManager;
    private TagsManager tagsManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content view
        setContentView(R.layout.quiz_modification_main);
        //Fetch all the view objects
        Toolbar toolbar = (Toolbar) findViewById(R.id.quiz_modify_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //questionManager = new QuestionManager();
        tagsManager = new TagsManager(Services.getTagPersistence(), Services.getQuestionPersistence());

        //fetch the current window and change the status bar color to match the app style
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        //get the additional information about the quiz to display for modification
        int qID = getIntent().getIntExtra("quiz_name",-1);
        questionList = findViewById(R.id.question_view_list);
        questionAddIcon = findViewById(R.id.question_add_icon);

        //create quiz and question manager instances
        quizManager = new QuizManager(Services.getQuizPersistence(), CurrentUser.getCurrentUser());

        //fetch the current quiz
        if(qID!=-1) currentQuiz = qID;
        System.out.println("Looking at ID: " + currentQuiz);
        System.out.println("Quiz List: " + Services.getQuizPersistence().getQuizList(CurrentUser.getCurrentUser()));
        actionBar.setTitle(quizManager.getQuiz(currentQuiz).getName()); //set the action bar title with the current quiz's name.

        //create adadpter to display the question list for the quiz
        questionArrayAdapter = new ArrayAdapter<Question>(this,R.layout.quiz_questions_view_item_main,
                                        R.id.quiz_question_view_item_main_text,new ArrayList<Question>(quizManager.getQuiz(currentQuiz).getQuestionsList()));
        questionList.setAdapter(questionArrayAdapter);
        //questionList.setClickable(false);

        questionAddIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuestionChoiceMenu(v);
            }
        });//on click listener for adding questions to the quiz

        FloatingActionButton deleteQuestions = findViewById(R.id.question_remove_icon);
        deleteQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQuestionRemoveDialog().show();
            }
        });//on click listener for floating button for deleting questions.
    }

    private AlertDialog createQuestionSelectorDialog(){
        /*
        This method create a dialog layout to select the questions to be added to the quiz.
         */
        final LayoutInflater inflater = QuizModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.select_questions_dialog, null);
        Toolbar title = v.findViewById(R.id.select_questions_toolbar);
        title.setTitle(R.string.select_questions_ta);

        final ArrayList<Question> selectedQuestions = new ArrayList<Question>();

        final ListView list = (ListView)v.findViewById(R.id.question_selection_view_list);
        final ArrayList<Question> temporaryList = new ArrayList<Question>(Services.getQuestionPersistence().getAllQuestions(CurrentUser.getCurrentUser()));
        final ArrayAdapter<Question> temporaryAdapter = new ArrayAdapter<Question>(this,R.layout.quiz_questions_view_item,
                R.id.quiz_question_view_item_text,temporaryList);
        list.setAdapter(temporaryAdapter);

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

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizModificationActivity.this);
        builder.setView(v).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        boolean duplicateQuestions = false;
                        for(Question q: selectedQuestions) {
                            try {
                                quizManager.addQuestionToQuiz(currentQuiz, q);
                               } catch (InvalidQuizException e) {
                                e.printStackTrace();
                                duplicateQuestions = true;
                            }//Question already present warning.
                        }
                        questionArrayAdapter.clear();
                        questionArrayAdapter.addAll(quizManager.getQuiz(currentQuiz).getQuestionsList());
                        questionArrayAdapter.notifyDataSetChanged();//notify the adapter to udapte the list
                        if(duplicateQuestions){
                            Toast.makeText(QuizModificationActivity.this, "Saved! Some questions were already present.", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(QuizModificationActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return builder.create();
    }//user wants to add questions to the quiz.

    private AlertDialog createQuestionRemoveDialog(){
        /*
        This method creates a dialog layout to remove the questions from the quiz.
         */
        final LayoutInflater inflater = QuizModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.select_questions_dialog, null);

        final ArrayList<Question> selectedQuestions = new ArrayList<Question>();

        Toolbar title = v.findViewById(R.id.select_questions_toolbar);
        title.setTitle(R.string.select_questions_tr);
        final ListView list = (ListView)v.findViewById(R.id.question_selection_view_list);
        final ArrayList<Question> temporaryList = (ArrayList<Question>)quizManager.getQuiz(currentQuiz).getQuestionsList().clone();
        final ArrayAdapter<Question> temporaryAdapter = new ArrayAdapter<Question>(this,R.layout.quiz_questions_view_item,
                R.id.quiz_question_view_item_text,temporaryList);
        list.setAdapter(temporaryAdapter);

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
        });//set on click listener for the quiz.

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizModificationActivity.this);
        builder.setView(v).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //delete the selected questions from the current quiz.
                for(Question q: selectedQuestions) {
                    quizManager.removeQuestionFromQuiz(currentQuiz,q);
                }
                questionArrayAdapter.clear();
                questionArrayAdapter.addAll(quizManager.getQuiz(currentQuiz).getQuestionsList());
                questionArrayAdapter.notifyDataSetChanged();//notify the adapter to udapte the list
                Toast.makeText(QuizModificationActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }//removing questions from the quiz



    private AlertDialog createWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizModificationActivity.this);
        builder.setMessage(R.string.delete_quiz_warning)//warning message.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        quizManager.deleteQuiz(currentQuiz);
                        finish();
                    }
                })//set the positive button to carry out the delete operation
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });//do nothing on cancel
        // Create the AlertDialog object and return it
        return builder.create();
    }//warning dialog before deleting a quiz.

    private void showTagSelectorDialog(){
        final LayoutInflater inflater = QuizModificationActivity.this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.tag_selection_layout, null);
        Toolbar t = v.findViewById(R.id.tag_selector_toolbar);
       // t.inflateMenu(R.menu.tag_selector_menu);
        t.setTitle(R.string.select_tags);

        final ArrayList<String> selectedTags = new ArrayList<String>();
        final ListView tList = v.findViewById(R.id.tags_list);
        tList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final ArrayAdapter<Tag> tListAdapter = new ArrayAdapter<Tag>(this,R.layout.tag_view_item,
                R.id.tag_view_item_textview,tagsManager.getAllTags(CurrentUser.getCurrentUser()));
        tList.setAdapter(tListAdapter);
        tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = view.findViewById(R.id.tag_view_item_checkbox);
                if(!cb.isChecked()) {
                    cb.setChecked(true);
                    selectedTags.add(tListAdapter.getItem(position).getTag());
                }else{
                    cb.setChecked(false);
                    selectedTags.remove(tListAdapter.getItem(position).getTag());
                }
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(QuizModificationActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /*AlertDialog al = showLoading(true, null);
                        al.show();*/
                        dialog.dismiss();
                        Toast.makeText(QuizModificationActivity.this, "Adding Questions, Please wait..", Toast.LENGTH_LONG).show();
                        if(selectedTags.size()!=0) {
                            boolean duplicateQuestions = false;
                            ArrayList<Question> temp = null;
                            try {
                                temp = tagsManager.getQuestionsWithTags(selectedTags);
                            }catch (PersistenceException e){
                                e.printStackTrace();
                            }catch(InvalidTagException i){
                                i.printStackTrace();
                            }
                            for(Question q: temp){
                                try {
                                    quizManager.addQuestionToQuiz(currentQuiz, q);
                                }catch(InvalidQuizException e){
                                    duplicateQuestions = true;
                                }
                            }
                            questionArrayAdapter.clear();
                            questionArrayAdapter.addAll(quizManager.getQuiz(currentQuiz).getQuestionsList());
                            questionArrayAdapter.notifyDataSetChanged();
                            if(duplicateQuestions){
                                Toast.makeText(QuizModificationActivity.this, "Saved! Some questions were already present!", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(QuizModificationActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })//set positive button to carry out the name change
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });//cancel do nothing
        builder.create().show();

    }

    private void showQuestionChoiceMenu(View v){
        /*
        This method creates and shows a menu in case a list item is clicked for the
        answer list of the MC questions
         */
        PopupMenu menu = new PopupMenu(this,v);
        menu.setGravity(Gravity.CENTER);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.question_addition_choice_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.all_questions_ic:
                        createQuestionSelectorDialog().show();
                        return true;
                    case R.id.tag_questions_ic:
                        showTagSelectorDialog();
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }//showMenu

    private AlertDialog createNewNameDialog(){
        final LayoutInflater inflater = QuizModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_quiz, null);
        final EditText et = (v.findViewById(R.id.enter_new_name));
        if(et.getText().toString().compareTo("")==0){
            et.setError("Quiz name can't be empty!");
        }//if the text box is empty set the text box error

        android.support.v7.widget.Toolbar toolbar = v.findViewById(R.id.enter_new_name_toolbar);
        toolbar.setTitle(R.string.new_name_dialog); //set the of the dialog action bar

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizModificationActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if (quizManager.changeQuizName(currentQuiz, et.getText().toString())) {
                                actionBar.setTitle(quizManager.getQuiz(currentQuiz).getName()); //set the action bar title to new name
                                et.getText().clear(); //clear the edit text textbox
                            }
                        }catch(InvalidQuizException e){
                            createNewNameDialog().show(); //create the new name dialog to input a valid
                            e.printStackTrace();
                        }
                    }
                })//set positive button to carry out the name change
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });//cancel do nothing
        return builder.create();
    }//user clicked to change the name.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_modify_menu, menu);
        return true;
    }//set the menu contents from the menu xml file.

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }//on back button pressed on the action bar would finish the current activity.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.quiz_modify_delete) { //delete current quiz and go back to the quizzes screen
            createWarningDialog().show();
            return true;
        }else if (id == R.id.quiz_modify_change_name) { //change the name of the current quiz
            createNewNameDialog().show();
            return true;
        }else if( id == R.id.quiz_play_icon){
            //Launch the do a quiz activity.
            try{
                if(quizManager.validateQuizForPlay(currentQuiz)){
                    Intent doaquiz = new Intent(this, DoAQuizActivity.class);
                    doaquiz.putExtra("quiz_id", currentQuiz);
                    startActivity(doaquiz);
                }
            }catch(InvalidQuizException e){
                AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuizModificationActivity.this);
                warningBuilder.setMessage(e.getMessage()).create().show();
                e.printStackTrace();
            }
        }//if the quiz play icon is clicked, it would launch the DoQuizActivity
        return super.onOptionsItemSelected(item);
    }//if an item is clicked in the menu.

}
