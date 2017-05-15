package douche.com.closer.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.UUID;

import douche.com.closer.constants.SampleGattAttributes;
import douche.com.closer.util.GattInfo;

/**
 * Created by johnnylee on 27/03/17.
 */

// A service that interacts with the BLE device via the Android BLE API.
public class BLECallback extends Service {
    private final static String TAG = BLECallback.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private static BLECallback mThis = null;
    private int mConnectionState = STATE_DISCONNECTED;
    private BluetoothAdapter mBtAdapter = null;
    private volatile boolean mBusy = false;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BLECallback getService() {
            return BLECallback.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        mThis = this;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public static BLECallback getInstance() {
        return mThis;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }


    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte b) {
        if (!checkGatt())
            return false;

        byte[] val = new byte[1];
        val[0] = b;
        characteristic.setValue(val);

        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, boolean b) {
        if (!checkGatt())
            return false;

        byte[] val = new byte[1];

        val[0] = (byte) (b ? 1 : 0);
        characteristic.setValue(val);
        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (!checkGatt())
            return false;

        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Retrieves the number of GATT services on the connected device. This should be invoked only after {@code BluetoothGatt#discoverServices()} completes
     * successfully.
     *
     * @return A {@code integer} number of supported services.
     */
    public int getNumServices() {
        if (mBluetoothGatt == null)
            return 0;

        return mBluetoothGatt.getServices().size();
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be invoked only after {@code BluetoothGatt#discoverServices()} completes
     * successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (!checkGatt())
            return false;

        if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
            Log.w(TAG, "setCharacteristicNotification failed");
            return false;
        }

        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        if (clientConfig == null)
            return false;

        if (enable) {
            Log.i(TAG, "enable notification");
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            Log.i(TAG, "disable notification");
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }

        mBusy = true;
        return mBluetoothGatt.writeDescriptor(clientConfig);
    }

    public boolean isNotificationEnabled(BluetoothGattCharacteristic characteristic) {
        if (!checkGatt())
            return false;

        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        if (clientConfig == null)
            return false;

        return clientConfig.getValue() == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are released properly.
     */
    public void close() {
        if (mBluetoothGatt != null) {
            Log.i(TAG, "close");
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    private boolean checkGatt() {
        if (mBtAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return false;
        }

        if (mBusy) {
            Log.w(TAG, "LeService busy");
            return false;
        }
        return true;

    }

    public int numConnectedDevices() {
        int n = 0;

        if (mBluetoothGatt != null) {
            List<BluetoothDevice> devList;
            devList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            n = devList.size();
        }
        return n;
    }

    //
    // Utility functions
    //
    public static BluetoothGatt getBtGatt() {
        return mThis.mBluetoothGatt;
    }

    public static BluetoothManager getBtManager() {
        return mThis.mBluetoothManager;
    }


    public boolean waitIdle(int i) {
        i /= 10;
        while (--i > 0) {
            if (mBusy)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            else
                break;
        }

        return i > 0;
    }

}