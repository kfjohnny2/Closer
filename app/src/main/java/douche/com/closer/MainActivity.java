package douche.com.closer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

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
    private BluetoothGatt bluetoothGatt;
    private SwipeRefreshLayout swipe;
    private ScanResult mScanResult;
    private int minRssi = 0;

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

        listDevices.setOnItemClickListener(deviceListener);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        mLeDeviceListAdapter = new LeDeviceAdapter(getApplicationContext());
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        enableBluetooth();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent beaconIntent= new Intent(getApplicationContext(), RegisterBeaconActivity.class);
                startActivity(beaconIntent);
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scan();
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
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (int i = 0; i < results.size(); i++) {
                Log.d("SCAN RESULT: ", results.get(i).getDevice().getName());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("SCAN ERROR: ", String.valueOf(errorCode));
        }

        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ScanRecord sr = result.getScanRecord();
                    double distance;
                    if (sr != null) {
                        Integer txPowerLevel = DeviceControlActivity.getTxPowerLevel(sr.getBytes());
                        if(txPowerLevel != null){
                            distance = Math.pow(10, (txPowerLevel-result.getRssi())/20);
                            Log.i("DISTANCE ", distance + "mt");
                        }
                    }
                    mLeDeviceListAdapter.addDevice(result);
                    mLeDeviceListAdapter.notifyDataSetChanged();

                }
            });

            if (mDevice != null) {
                if (result.getDevice() != null && result.getDevice().getName() != null) {
                    if (result.getDevice().getName().equals(mDevice.getName()) && result.getRssi() < -minRssi) {
                        ScanRecord sr = result.getScanRecord();

                        sendNotification("OUT OF SIGHT", "YOUR BEACON IS TOO FAR");
                    }
                }
            }
            swipe.setRefreshing(false);
        }
    };

    private BluetoothDevice mDevice;
    AdapterView.OnItemClickListener deviceListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            setMinRssi(view, i);
        }
    };

    private void setMinRssi(final View view, final int i) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Setup your min distance for the beacon monitor. \n Ex.: Less is for more close distante, higher for bigger distances");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                minRssi = Integer.parseInt(input.getText().toString());
                mDevice = (BluetoothDevice) mLeDeviceListAdapter.getItem(i);
//                Snackbar.make(view, "Device for monitoring: " + String.valueOf(mDevice.getName()) + " for min rssi "+minRssi, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(getApplicationContext(),"Device for monitoring: " + String.valueOf(mDevice.getName()) + " for min rssi "+minRssi, Toast.LENGTH_LONG ).show();
                final Intent intent = new Intent(getApplicationContext(), DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mDevice.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mDevice.getAddress());
                if (mScanning) {
                    bleScanner.stopScan(mScanCallback);
                    mScanning = false;
                }
                dialog.cancel();
                startActivity(intent);
            }
        });

        builder.show();
    }

    public void sendNotification(String title, String content) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSmallIcon(android.R.drawable.ic_notification_overlay)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }



    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                        Toast.makeText(getApplicationContext(), "Peripheral connected", Toast.LENGTH_LONG).show();
                    } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Toast.makeText(getApplicationContext(), "Peripheral Disconnected", Toast.LENGTH_LONG).show();
                    } else if (status != BluetoothGatt.GATT_SUCCESS) {
                        gatt.disconnect();
                    }
                }
            });
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (rssi < -60) {
                sendNotification("OUT OF SIGHT", "YOUR BEACON IS TOO FAR");
            }
        }
    };

//    private boolean mConnected;
//    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BLECallback.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
//                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
//            } else if (BLECallback.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
//                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                clearUI();
//            } else if (BLECallback.
//                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics on the
//                // user interface.
//                displayGattServices(mBluetoothLeService.getSupportedGattServices());
//            } else if (BLECallback.ACTION_DATA_AVAILABLE.equals(action)) {
//                displayData(intent.getStringExtra(BLECallback.EXTRA_DATA));
//            }
//        }
//    };
}
