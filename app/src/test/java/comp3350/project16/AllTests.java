package comp3350.project16;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.project16.logic.DoQuizManagerTest;
import comp3350.project16.logic.LoginManager;
import comp3350.project16.logic.LoginManagerTest;
import comp3350.project16.logic.QuestionEditorManager;
import comp3350.project16.logic.QuestionEditorManagerTest;
import comp3350.project16.logic.QuestionManagerTest;
import comp3350.project16.logic.QuizManagerTest;
import comp3350.project16.logic.TagManagerTest;
import comp3350.project16.logic.ResultManagerTest;
import comp3350.project16.objects.MCQuestionTest;
import comp3350.project16.objects.QuestionTest;
import comp3350.project16.objects.QuizTest;
import comp3350.project16.objects.ResultTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        QuestionTest.class,
        MCQuestionTest.class,
        QuestionManagerTest.class,
        DoQuizManagerTest.class,
        LoginManagerTest.class,
        QuizTest.class,
        QuestionEditorManagerTest.class,
        QuizManagerTest.class,
        TagManagerTest.class,
        ResultTest.class,
        ResultManagerTest.class
})
public class AllTests
{

}
