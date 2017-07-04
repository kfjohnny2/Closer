package douche.com.closer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import douche.com.closer.model.Person;
import douche.com.closer.service.ServicePerson;
import douche.com.closer.service.ServiceUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private Button btLogin;
    private Button btSignUp;
    private Person session;
    private SharedPreferences sharedPreferences;
    private static final String PREFER_NAME = "Reg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        setup();
        btSignUp = (Button) findViewById(R.id.btSignup);
        btSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);

            }
        });

    }

    private View.OnClickListener btLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = edUser.getText().toString();
            String password = edPass.getText().toString();
            try {
                // Validate if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    if(sharedPreferences.contains("Username")){
                        loginViaPrefs();
                    } else{
                        new PersonTask().execute(username.trim(), password.trim());
                    }
                } else {
                    // user didn't entered username or password
                    Toast.makeText(getApplicationContext(),
                            "Please enter username and password",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void loginViaPrefs(){
        String username = edUser.getText().toString();
        String password = edPass.getText().toString();
        // Validate if username, password is filled
            String uName = null;
            String uPassword = null;
            String idUser = null;
            if (sharedPreferences.contains("Username")) {
                uName = sharedPreferences.getString("Username", "");
            }
            if (sharedPreferences.contains("edPassword")) {
                uPassword = sharedPreferences.getString("edPassword", "");

            }
            if (sharedPreferences.contains("IdUser")) {
                idUser = sharedPreferences.getString("IdUser", "");
            }
            // Object uName = null;
            // Object uEmail = null;
            if (username.equals(uName) && password.equals(uPassword)) {
                session.createUserLoginSession(uName,
                        uPassword, idUser);
                // Starting MainActivity
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            } else {
                // username / password doesn't match&
                Toast.makeText(getApplicationContext(),
                        "Username/Password is incorrect",
                        Toast.LENGTH_LONG).show();
            }
    }
    private void setup() {

        edUser = (EditText) findViewById(R.id.edName);
        edPass = (EditText) findViewById(R.id.edPass);
        btLogin = (Button) findViewById(R.id.btLogin);
        btSignUp = (Button) findViewById(R.id.btSignup);

        // Person Session Manager
        session = new Person(getApplicationContext());

        // get Email, Password input text
        edUser = (EditText) findViewById(R.id.edName);
        edPass = (EditText) findViewById(R.id.edPass);
        btLogin.setOnClickListener(btLoginListener);
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);

        if (session.isUserLoggedIn()) {
            // Starting MainActivity
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

            finish();
        }
    }

    private class PersonTask extends AsyncTask<String, Void, Person> {

        @Override
        protected Person doInBackground(String... params) {
            return ServicePerson.getPerson(params[0], params[1], getApplicationContext());
        }

        @Override
        protected void onPostExecute(Person person) {
            super.onPostExecute(person);
            if(person != null){
                if (edUser.getText().toString().equals(person.getUserName()) &&  edPass.getText().toString().equals(person.getPassword())){
                    session.createUserLoginSession(person.getUserName(),
                            person.getPassword(), person.getId());
                    // Starting MainActivity
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            } else{
                Toast.makeText(getApplicationContext(),
                        "Username/Password is incorrect",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
