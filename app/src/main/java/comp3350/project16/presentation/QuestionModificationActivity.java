package comp3350.project16.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.InvalidQuizException;
import comp3350.project16.Exceptions.InvalidRequestException;
import comp3350.project16.Exceptions.InvalidTagException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.R;
import comp3350.project16.application.Services;
import comp3350.project16.logic.MCQuestionManager;
import comp3350.project16.logic.QuestionEditorManager;
import comp3350.project16.logic.QuestionManager;
import comp3350.project16.logic.TagsManager;
import comp3350.project16.objects.MCQuestion;
import comp3350.project16.objects.Question;
import comp3350.project16.objects.Quiz;
import comp3350.project16.objects.Tag;


public class QuestionModificationActivity extends AppCompatActivity {
    //private QuestionManager questionManager;
    private final int UPDATE_QUESTION = 1;
    private final int UPDATE_CORRECT_ANSWER = 2;
    private final int UPDATE_ANSWER = 3;
    private final int ADD_ANSWER = 4;
    private final int ADD_TAGS_TO_QUESTON = 5;
    private final int REMOVE_TAGS_FROM_QUESTION = 6;
    private ArrayAdapter<String> mcAnswersAdapter;
    private Question currrentQuestion;
    private TextView correctAnswerBox, questionBox, otherAnswers;
    private ListView answers;
    private Button addAnswer;
    private TagsManager tagsManager;
    private QuestionEditorManager questionEditorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_question_main);

        //get the editor
        questionEditorManager = new QuestionEditorManager(Services.getQuestionPersistence(), Services.getMCAnswerPersistence(), CurrentUser.getCurrentUser());
        tagsManager = new TagsManager(Services.getTagPersistence(),Services.getQuestionPersistence());
        int qID = getIntent().getIntExtra("question_id",-1);
        if (qID != -1) {
            currrentQuestion = questionEditorManager.getQuestionByID(qID);
        }//check if the proper id was received


        otherAnswers = findViewById(R.id.other_answers);
        addAnswer = findViewById(R.id.add_answer_button);
        addAnswer.setOnClickListener(listener);
        correctAnswerBox = findViewById(R.id.correct_answer_box);
        correctAnswerBox.setText(currrentQuestion.getCorrectAnswer());
        correctAnswerBox.setOnClickListener(listener);

        questionBox = findViewById(R.id.question_box);
        questionBox.setText(currrentQuestion.toString());
        questionBox.setOnClickListener(listener);

        if(currrentQuestion instanceof MCQuestion) {
            MCQuestion temp = (MCQuestion)currrentQuestion;
            answers = (ListView) findViewById(R.id.mc_answers);

            ArrayList<String> tempAnswers = new ArrayList<String>(questionEditorManager.getMCAnswers(temp.getQID()));
            mcAnswersAdapter = new ArrayAdapter<String>(this, R.layout.question_view_item,
            R.id.question_view_item_text, tempAnswers);
            answers.setAdapter(mcAnswersAdapter);

            answers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showMenu(view,currrentQuestion, mcAnswersAdapter.getItem(position));
                }
            });
        }else{
            //set visibility to 0 in case it's not the MC question as the other ones don't need
            //these options
            addAnswer.setVisibility(View.GONE);
            otherAnswers.setVisibility(View.GONE);
        }

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Click to edit"); //finally set the title bar text.
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add_answer_button:
                    //getUserInput();
                    //Toast.makeText(QuestionModificationActivity.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                    getUserInput("Enter a new answer",currrentQuestion,null, ADD_ANSWER);
                    break;
                case R.id.correct_answer_box:
                    getUserInput("Enter the new correct answer", currrentQuestion,
                            correctAnswerBox.getText().toString(), UPDATE_CORRECT_ANSWER);
                    break;
                case R.id.question_box:
                    getUserInput("Enter the new Question", currrentQuestion, null, UPDATE_QUESTION);
                    break;
            }
        }
    }; //on click listener for the normal UI buttons and text views

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_add_remove_tags, menu);
        return true;
    }//set the menu contents from the menu xml file.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_tags_to_question:
                showTagSelectorDialog(ADD_TAGS_TO_QUESTON);
                return true;
            case R.id.remove_tags_from_question:
                showTagSelectorDialog(REMOVE_TAGS_FROM_QUESTION);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }//menu listener for the activity

    private void showTagSelectorDialog(final int operation){
        /*
        Shows a dialog to select the tags to add to the question or to select the tags to remove
        from the question based on the parameter 'operation'

        Statics are defined at the top.
         */

        //get the layout inflater, inflate the layout, get the toolbar and set the toolbar's title
        final LayoutInflater inflater = QuestionModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.tag_selection_layout, null);
        Toolbar t = v.findViewById(R.id.tag_selector_toolbar);
        String title = "";
        if(operation==ADD_TAGS_TO_QUESTON) title+="Add";
        else if(operation==REMOVE_TAGS_FROM_QUESTION) title+="Remove";

        t.setTitle("Select Tags To "+title);

        //new arraylist to store the marked tags, listview to show the tags.
        final ArrayList<Tag> selectedTags = new ArrayList<>();
        final ListView tList = v.findViewById(R.id.tags_list);
        tList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayList<Tag> tagsList = null;
        switch (operation){
            case ADD_TAGS_TO_QUESTON:
                tagsList = new ArrayList<Tag>(tagsManager.getAllTags(CurrentUser.getCurrentUser()));
                t.inflateMenu(R.menu.tag_selector_menu);
                break;
            case REMOVE_TAGS_FROM_QUESTION:
                tagsList = new ArrayList<Tag>(tagsManager.getAllTagsForQuestion(currrentQuestion.getQID()));
                break;
        }
        final ArrayAdapter<Tag> tListAdapter = new ArrayAdapter<Tag>(this,R.layout.tag_view_item,
                R.id.tag_view_item_textview,tagsList);
        tList.setAdapter(tListAdapter);
        tList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = view.findViewById(R.id.tag_view_item_checkbox);
                if(!cb.isChecked()) {
                    cb.setChecked(true);
                    selectedTags.add(tListAdapter.getItem(position));
                    System.out.println(selectedTags);
                }else{
                    cb.setChecked(false);
                    selectedTags.remove(tListAdapter.getItem(position));
                    System.out.println(selectedTags);
                }//on click it marks the item and adds it to a list, if it's already marked, it
                //un marks the item and removes it from the list.
            }
        });//onClick Listener for the tag list.

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionModificationActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        switch (operation){
                            case ADD_TAGS_TO_QUESTON:
                                boolean dups = false;
                                if(selectedTags.size()>0){
                                    for(Tag t: selectedTags){
                                        try {
                                            tagsManager.addTagToQuestion(currrentQuestion.getQID(), t.getID(), CurrentUser.getCurrentUser());
                                        }catch(InvalidTagException e){
                                            //nothing else needs to be done here.
                                            dups = true;
                                        }
                                    }
                                    if(dups) Toast.makeText(QuestionModificationActivity.this,
                                            "Saved! Some tags were already present!", Toast.LENGTH_SHORT).show();
                                    else Toast.makeText(QuestionModificationActivity.this,
                                            "Saved!", Toast.LENGTH_SHORT).show();
                                }//adds tags to the current question, if the list of the selected tags
                                //has more than one item.
                                break;
                            case REMOVE_TAGS_FROM_QUESTION:
                                if(selectedTags.size()>0){
                                    for(Tag t: selectedTags){
                                        try {
                                            tagsManager.removeTagFromQuestion(currrentQuestion.getQID(), t.getID(), CurrentUser.getCurrentUser());
                                        }catch(PersistenceException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }//removes the tag from the current question
                                break;
                        }//based on the operation that needs to be carried, the appropriate case runs
                    }
                })//set positive button to carry out the name change
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });//cancel do nothing
        builder.create().show();

        //the menu for the toolbar of the tag selector dialog. Gives the user an option to enter a
        //new tag and save it in the system.
        t.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.add_tag_icon:
                        insertNewTagInput(tListAdapter, "");
                        //takes the user input and saves it as
                        // a new tag
                        return true;
                    case R.id.delete_tag_icon:
                        LayoutInflater inflater = QuestionModificationActivity.this.getLayoutInflater();
                        View v = inflater.inflate(R.layout.tag_selection_layout, null);
                        Toolbar t = v.findViewById(R.id.tag_selector_toolbar);
                        t.setTitle("Select Tags To Delete");
                        //new arraylist to store the marked tags, listview to show the tags.
                        final ArrayList<Tag> selectedTagsToDelete = new ArrayList<>();
                        final ListView deleteTagLView = v.findViewById(R.id.tags_list);
                        ArrayList<Tag> tagsList = new ArrayList<Tag>(tagsManager.getAllTags(CurrentUser.getCurrentUser()));
                        final ArrayAdapter<Tag> tagArrayAdapter = new ArrayAdapter<Tag>(QuestionModificationActivity.this,
                                R.layout.tag_view_item,
                                R.id.tag_view_item_textview,tagsList);
                        deleteTagLView.setAdapter(tagArrayAdapter);
                        deleteTagLView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                CheckBox cb = view.findViewById(R.id.tag_view_item_checkbox);
                                if(!cb.isChecked()) {
                                    cb.setChecked(true);
                                    selectedTagsToDelete.add(tagArrayAdapter.getItem(position));
                                }else{
                                    cb.setChecked(false);
                                    selectedTagsToDelete.remove(tagArrayAdapter.getItem(position));
                                }//on click it marks the item and adds it to a list, if it's already marked, it
                                //un marks the item and removes it from the list.
                            }
                        });//onClick Listener for the tag list.
                        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionModificationActivity.this);
                        builder.setView(v).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        for(Tag t: selectedTagsToDelete) {
                                            tagsManager.deleteTag(t, CurrentUser.getCurrentUser());
                                        }
                                    }catch (InvalidTagException e){
                                        e.printStackTrace();
                                    }
                                tListAdapter.clear();
                                tListAdapter.addAll(tagsManager.getAllTags(CurrentUser.getCurrentUser()));
                                tListAdapter.notifyDataSetChanged();
                            }
                        }).create().show();//deletes the tags from the database.

                        return true;
                }
                return false;
            }
        });//set on click listener for the tags menu i.e. adding and deleting tags
    }

    private void insertNewTagInput(final ArrayAdapter<Tag> tListAdapter, final String error){
        /*
        Takes the user's input and saves it in the system. Refreshes the list so that it can
        display the newly added tag.
         */
        final LayoutInflater inflater = QuestionModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_quiz, null);
        final EditText et = (v.findViewById(R.id.enter_new_name));
        et.setHint("Type here");
        if(error.compareTo("")!=0) et.setError(error); //if not empty set the error for the edit text

        Toolbar toolbar = v.findViewById(R.id.enter_new_name_toolbar);
        toolbar.setTitle("Enter New Tag");

        final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionModificationActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String usrInput = et.getText().toString();
                        try {
                            tagsManager.insertTag(new Tag(usrInput, tagsManager.getNextTagID()), CurrentUser.getCurrentUser());
                        }catch(InvalidTagException e){
                            insertNewTagInput(tListAdapter, e.getMessage());
                        }
                        tListAdapter.clear();
                        tListAdapter.addAll(tagsManager.getAllTags(CurrentUser.getCurrentUser()));
                        tListAdapter.notifyDataSetChanged();
                    }
                })//set positive button to save input
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });//set negative button to cancel
        builder.create().show();
    }

    private void getUserInput(String title, final Question selected, final String strSelected, final int mode){
        /*
        Creates the dialog for the usrInput
         */
        final LayoutInflater inflater = QuestionModificationActivity.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.add_new_quiz, null);

        final EditText et = (v.findViewById(R.id.enter_new_name));

        switch(mode){
            case UPDATE_QUESTION:
                et.setText(selected.toString());
                break;
            case UPDATE_CORRECT_ANSWER:
                et.setText(selected.getCorrectAnswer());
                break;
            case UPDATE_ANSWER:
                et.setText(strSelected);
                break;
            case ADD_ANSWER:
                et.setText("");
                break;
        }

        Toolbar toolbar = v.findViewById(R.id.enter_new_name_toolbar);
        toolbar.setTitle(title);

        if(et.getText().toString().compareTo("")==0){
            et.setError("Input can't be empty!");
        }//sets the warning for the edit text in case it's empty

        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionModificationActivity.this);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //add a quiz.
                        String usrInput = et.getText().toString();
                        switch(mode){
                            case UPDATE_QUESTION:
                                //updates the question with the given input
                                try {
                                    questionEditorManager.updateQuestion(selected.getQID(), usrInput);
                                    questionBox.setText(usrInput);
                                    return;
                                }catch (InvalidInputException i ){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(i.getMessage()).create().show();
                                }catch(PersistenceException e){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(e.getMessage()).create().show();
                                }
                                break;
                            case UPDATE_CORRECT_ANSWER:
                                //updates the correct answer of the question with the given input
                                try {
                                    questionEditorManager.updateCorrectAnswer(selected.getQID(), usrInput);
                                    correctAnswerBox.setText(usrInput);
                                    return;
                                }catch(InvalidInputException i){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(i.getMessage()).create().show();
                                }catch(PersistenceException e){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(e.getMessage()).create().show();
                                }
                                break;
                            case UPDATE_ANSWER:
                                //in order to update the multiple choice answers of the MC.
                                try {
                                    questionEditorManager.updateMCAnswer(selected.getQID(), strSelected, usrInput);
                                    mcAnswersAdapter.clear();
                                    mcAnswersAdapter.addAll(questionEditorManager.getMCAnswers(selected.getQID()));
                                    mcAnswersAdapter.notifyDataSetChanged();
                                    correctAnswerBox.setText(selected.getCorrectAnswer());
                                    return;
                                }catch(InvalidInputException i){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(i.getMessage()).create().show();
                                }catch(PersistenceException e){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(e.getMessage()).create().show();
                                }
                                break;
                            case ADD_ANSWER:
                                //to add an answer to MC question.
                                try {
                                    questionEditorManager.addMCAnswer(currrentQuestion.getQID(), usrInput);
                                    mcAnswersAdapter.clear();
                                    mcAnswersAdapter.addAll(questionEditorManager.getMCAnswers(selected.getQID()));
                                    mcAnswersAdapter.notifyDataSetChanged();
                                    return;
                                }catch (InvalidInputException i){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(i.getMessage()).create().show();
                                }catch(PersistenceException e){
                                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                                    warningBuilder.setMessage(e.getMessage()).create().show();
                                }
                                break;
                        }//switch
                    }
                })//set positive button to add the quiz
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });//set negative button to cancel
        builder.create().show();
    }

    private void showMenu(View v, final Question question, final String selected){
        /*
        This method creates and shows a menu in case a list item is clicked for the
        answer list of the MC questions
         */
        PopupMenu menu = new PopupMenu(this,v);
        menu.setGravity(Gravity.RIGHT);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.answer_select_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.answer_edit_icon:
                        getUserInput("Enter the new answer",question,selected,UPDATE_ANSWER);
                        return true;
                    case R.id.answer_delete_icon:
                        createWarningDialog(question.getQID(),selected).show();
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }//showMenu


    private AlertDialog createWarningDialog(final int questionID, final String answer){
        /*
        Warning dialog to show before deleting a MC answer.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionModificationActivity.this);
        builder.setMessage("Are you sure you want to delete this answer")//warning message.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            questionEditorManager.removeMCAnswer(questionID, answer);
                            mcAnswersAdapter.clear();
                            mcAnswersAdapter.addAll(questionEditorManager.getMCAnswers(questionID));
                            mcAnswersAdapter.notifyDataSetChanged();
                        }catch(InvalidRequestException i){
                            AlertDialog.Builder warningBuilder = new AlertDialog.Builder(QuestionModificationActivity.this);
                            warningBuilder.setMessage(i.getMessage()).create().show();
                        }
                    }
                })//set the positive button to carry out the delete operation
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });//do nothing on cancel
        // Create the AlertDialog object and return it
        return builder.create();
    }//warning dialog before deletion.

}
