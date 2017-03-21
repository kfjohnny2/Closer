package douche.com.closer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import douche.com.closer.adapter.LeDeviceAdapter;

public class MainActivity extends AppCompatActivity {
    private LeDeviceAdapter mLeDeviceListAdapter;
    private ListView listDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private Handler mHandler;
    private boolean mScanning;
    private BluetoothLeScanner bleScanner;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setup();
    }

    private void setup() {
        listDevices = (ListView) findViewById(R.id.list_bluetooth_le_devices);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mLeDeviceListAdapter = new LeDeviceAdapter(getApplicationContext());
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        enableBluetooth();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scan();
            }
        });
        listDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice bDevice = (BluetoothDevice) listDevices.getItemAtPosition(i);
                Snackbar.make(view, "Power of device signal: " + String.valueOf(bDevice.getBondState()), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void enableBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            scanLeDevices(true);
            scan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                enableBluetooth();
            } else {
//                scanLeDevices(true);
                scan();
            }
        }
    }

    private void scan() {
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            mLeDeviceListAdapter.clear();
            bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
            bleScanner.startScan(mScanCallback);
            listDevices.setAdapter(mLeDeviceListAdapter);
        }
    }

    // Device scan callback.
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mLeDeviceListAdapter.addDevice(result.getDevice(), result.getRssi());
            mLeDeviceListAdapter.notifyDataSetChanged();
            swipe.setRefreshing(false);
        }
    };
}git
