package comp3350.project16.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import comp3350.project16.Exceptions.InvalidInputException;
import comp3350.project16.Exceptions.PersistenceException;
import comp3350.project16.Exceptions.UserNotFoundException;
import comp3350.project16.R;
import comp3350.project16.application.Main;
import comp3350.project16.application.Services;
import comp3350.project16.logic.LoginManager;

public class LoginActivity extends AppCompatActivity {

    private LoginManager loginManager;
    private long lastTimeClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!Services.getMode().equals("TESTING")){
            copyDatabaseToDevice();
        }

        loginManager =  new LoginManager(Services.getUserPersistence());

        final Button loginButton = findViewById(R.id.loginButton);
        final Button registerButton = findViewById(R.id.registerNewAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(SystemClock.elapsedRealtime() - lastTimeClicked > 3000){

                    lastTimeClicked = SystemClock.elapsedRealtime();

                    // Code here executes on main thread after user presses button
                    String loginCredentials = ((TextView) findViewById(R.id.loginCredentials)).getText().toString();
                    String password = ((TextView) findViewById(R.id.passwordInput)).getText().toString();

                    try {

                        String user = loginManager.attemptLogin(loginCredentials, password);
                        CurrentUser.setUser(user);
                        Intent mainView = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainView);
                        finish();

                    }catch(InvalidInputException e){
                        errorDialog("Invalid input", e.getMessage());
                    }catch (PersistenceException e){
                        errorDialog("We couldn't log you in", "User not found");
                    }catch(UserNotFoundException e){
                        errorDialog("Login Failed", e.getMessage());
                    }
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                //Intent registerView = new Intent(LoginActivity.this, Register Activity);
                //startActivity(registerView);
                //finish();
            }
        });
    }

    private void errorDialog(String title, String reason){
        //Display an alert dialog saying the reasoning and has an OK button
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(reason)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }}).show();
    }

    private void copyDatabaseToDevice() {

        final String DB_PATH = "db";

        String[] assetNames;
        Context context = getApplicationContext();
        File dataDirectory = context.getDir(DB_PATH, Context.MODE_PRIVATE);
        AssetManager assetManager = getAssets();

        try {

            assetNames = assetManager.list(DB_PATH);
            for (int i = 0; i < assetNames.length; i++) {
                assetNames[i] = DB_PATH + "/" + assetNames[i];
            }

            copyAssetsToDirectory(assetNames, dataDirectory);

            Main.setDBPathName(dataDirectory.toString() + "/" + Main.getDBPathName());

        } catch (final IOException ioe) {
            System.out.println("Unable to access application data: " + ioe.getMessage());
        }
    }

    public void copyAssetsToDirectory(String[] assets, File directory) throws IOException {
        AssetManager assetManager = getAssets();

        for (String asset : assets) {

            String[] components = asset.split("/");
            String copyPath = directory.toString() + "/" + components[components.length - 1];

            char[] buffer = new char[1024];
            int count;

            File outFile = new File(copyPath);

            if (!outFile.exists()) {
                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                FileWriter out = new FileWriter(outFile);

                count = in.read(buffer);
                while (count != -1) {
                    out.write(buffer, 0, count);
                    count = in.read(buffer);
                }

                out.close();
                in.close();
            }
        }
    }
}
