package douche.com.closer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import douche.com.closer.model.Device;
import douche.com.closer.model.Person;
import douche.com.closer.service.ServiceUser;

public class SignupActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button buttonReg2;
    EditText edName, edUsername, edPassword;
    CheckBox cbAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edUsername = (EditText) findViewById(R.id.edUsernameSignup);
        edPassword = (EditText) findViewById(R.id.edPassSignup);
        edName = (EditText) findViewById(R.id.edName);
        cbAdmin = (CheckBox) findViewById(R.id.cbAdminSignup);

        buttonReg2 = (Button) findViewById(R.id.btSignup);

// creating an shared Preference file for the information to be stored
// first argument is the name of file and second is the mode, 0 is private mode

        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
// get editor to edit in file
        editor = sharedPreferences.edit();

        buttonReg2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String username = edUsername.getText().toString();
                String pass = edPassword.getText().toString();
                String name = edName.getText().toString();
                Integer isAdmin  = cbAdmin.isChecked() ? 1 : 0;
                if (edUsername.getText().length() <= 0) {
                    Toast.makeText(SignupActivity.this, "Enter username", Toast.LENGTH_SHORT).show();
                } else if (edPassword.getText().length() <= 0) {
                    Toast.makeText(SignupActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else if (edName.getText().length() <= 0) {
                    Toast.makeText(SignupActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                }else {
                    // as now we have information in string. Lets stored them with the help of editor
                    editor.putString("Username", username);
                    editor.putString("edPassword", pass);
                    editor.putInt("Admin", isAdmin);

                    Person person = new Person(name, username, pass, new ArrayList<Device>(), isAdmin);
                    new SendUserAsyncTask().execute(person);
                }   // commit the values

                // after saving the value open next activity
                Intent ob = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(ob);
            }
        });
    }

    private class SendUserAsyncTask extends AsyncTask<Person, Void, String> {

        @Override
        protected String doInBackground(Person... params) {
            return ServiceUser.postUser(params[0], getApplicationContext());
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject=new JSONObject(s);
                String idUser = jsonObject.getString("_id");
                editor.putString("IdUser", idUser);
                editor.commit();
                } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
