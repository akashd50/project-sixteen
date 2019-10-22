package comp3350.project16.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import comp3350.project16.R;
import comp3350.project16.application.Main;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar toolbar = getSupportActionBar();

        configureNavigationDrawer();
        toolbar.setHomeAsUpIndicator(R.drawable.ic_menu_icon);
        toolbar.setDisplayHomeAsUpEnabled(true);

        final TextView logout = findViewById(R.id.main_activity_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUser.setUser("");
                finishAffinity();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            // Android home
            case android.R.id.home:

                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawers();
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.main_activity_questions){
                    startActivity(new Intent(MainActivity.this, QuestionViewActivity.class));
                }else if(menuItem.getItemId() == R.id.main_activity_quizzes){
                    startActivity(new Intent(MainActivity.this, QuizViewActivity.class));
                }else if(menuItem.getItemId() == R.id.main_activity_results){
                    startActivity(new Intent(MainActivity.this, ViewPastResultsActivity.class));
                }

                drawerLayout.closeDrawers();

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
