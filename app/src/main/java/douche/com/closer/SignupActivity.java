package douche.com.closer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button buttonReg2;
    EditText edUsername, edPassword, edAddress;
    CheckBox cbAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edUsername = (EditText) findViewById(R.id.edNameSignup);
        edPassword = (EditText) findViewById(R.id.edPassSignup);
        edAddress = (EditText) findViewById(R.id.edAddress);
        cbAdmin = (CheckBox) findViewById(R.id.cbAdminSignup);

        buttonReg2 = (Button) findViewById(R.id.btSignup);

// creating an shared Preference file for the information to be stored
// first argument is the name of file and second is the mode, 0 is private mode

        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
// get editor to edit in file
        editor = sharedPreferences.edit();

        buttonReg2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String name = edUsername.getText().toString();
                String pass = edPassword.getText().toString();
                if (edUsername.getText().length() <= 0) {
                    Toast.makeText(SignupActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                } else if (edPassword.getText().length() <= 0) {
                    Toast.makeText(SignupActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else {
                    // as now we have information in string. Lets stored them with the help of editor
                    editor.putString("Name", name);
                    editor.putString("edPassword", pass);
                    editor.putInt("Admin", cbAdmin.isChecked() ? 1 : 0);
                    editor.commit();
                }   // commit the values

                // after saving the value open next activity
                Intent ob = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(ob);

            }
        });

        cbAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    edAddress.setVisibility(View.VISIBLE);
                else
                    edAddress.setVisibility(View.GONE);
            }
        });
    }
}
