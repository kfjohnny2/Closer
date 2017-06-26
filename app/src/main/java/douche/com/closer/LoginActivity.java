package douche.com.closer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import douche.com.closer.model.User;

public class LoginActivity extends AppCompatActivity {
    private EditText edUser;
    private EditText edPass;
    private Button btLogin;
    private Button btSignUp;
    private User session;
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
                    String uName = null;
                    String uPassword = null;

                    if (sharedPreferences.contains("Name")) {
                        uName = sharedPreferences.getString("Name", "");

                    }

                    if (sharedPreferences.contains("edPassword")) {
                        uPassword = sharedPreferences.getString("edPassword", "");

                    }

                    // Object uName = null;
                    // Object uEmail = null;
                    if (username.equals(uName) && password.equals(uPassword)) {

                        session.createUserLoginSession(uName,
                                uPassword);

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

    private void setup() {

        edUser = (EditText) findViewById(R.id.edName);
        edPass = (EditText) findViewById(R.id.edPass);
        btLogin = (Button) findViewById(R.id.btLogin);
        btSignUp = (Button) findViewById(R.id.btSignup);

        // User Session Manager
        session = new User(getApplicationContext());

        // get Email, Password input text
        edUser = (EditText) findViewById(R.id.edName);
        edPass = (EditText) findViewById(R.id.edPass);

        Toast.makeText(getApplicationContext(),
                "User Login Status: " + session.isUserLoggedIn(),
                Toast.LENGTH_LONG).show();


        // User Login button
        btLogin = (Button) findViewById(R.id.btLogin);

        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);


        btLogin.setOnClickListener(btLoginListener);
    }
}
