package douche.com.closer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import douche.com.closer.model.Device;
import douche.com.closer.service.ServiceEvent;

public class RegisterBeaconActivity extends AppCompatActivity {
    private EditText edEventName;
    private EditText edAddress;
    private Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_beacon);
        setup();
    }

    private void setup(){
        edAddress = (EditText) findViewById(R.id.edBeaconAddress);
        edEventName = (EditText) findViewById(R.id.edEventName);
        btRegister = (Button) findViewById(R.id.btRegister);

        btRegister.setOnClickListener(btRegisterClickListener);
    }

    private View.OnClickListener btRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Device eventBeacon = null;
            if (edEventName.getText().length() <= 0 || edAddress.getText().length() <= 0 )
                Toast.makeText(RegisterBeaconActivity.this, "Enter the name of the event and the beacon address", Toast.LENGTH_SHORT).show();
            else
                 eventBeacon = new Device(edEventName.getText().toString(), edAddress.getText().toString());
            new SendEventAsyncTask().execute(eventBeacon);
        }
    };

    private class SendEventAsyncTask extends AsyncTask<Device, Void, Void>{

        @Override
        protected Void doInBackground(Device... params) {
            ServiceEvent.sendEvent(params[0], getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(RegisterBeaconActivity.this, "Beacon successfully registered", Toast.LENGTH_SHORT).show();
            new RegisterBeaconActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    edAddress.setText("");
                    edEventName.setText("");
                }
            });
        }
    }
}
