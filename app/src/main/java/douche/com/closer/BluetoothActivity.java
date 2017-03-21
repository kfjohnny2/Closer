
package douche.com.closer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private ListView listDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private SwipeRefreshLayout swipe;
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);

        listDevices = (ListView) findViewById(R.id.list_bluetooth_devices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetooth();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isRefreshing)
                    setup();
                swipe.setRefreshing(isRefreshing);
            }
        });
    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            setup();
        }
    }

    private void setup() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<>();
        for (BluetoothDevice b : pairedDevices) {
            s.add(b.getName() + ", " +String.valueOf(b.getBondState()));
        }

        listDevices.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, s));
        isRefreshing = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                enableBluetooth();
            } else {
                setup();
            }
        }
    }
}
