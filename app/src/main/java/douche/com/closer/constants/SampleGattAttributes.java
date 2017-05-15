package douche.com.closer.constants;

/**
 * Created by johnnylee on 27/03/17.
 */

import android.util.Log;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String TX_POWER_LEVEL = "00002a07-0000-1000-8000-00805f9b34fb";
    public static String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb";
    public static String SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    public static String MODEL_NUMBER_STRING = "00002a24-0000-1000-8000-00805f9b34fb";
    public static String SERIAL_NUMBER_STRING = "00002a25-0000-1000-8000-00805f9b34fb";
    public static String FIRMWARE_REVISION_STRING = "00002a26-0000-1000-8000-00805f9b34fb";
    public static String HARDWARE_REVISION_STRING = "00002a27-0000-1000-8000-00805f9b34fb";
    public static String SOFTWARE_REVISION_STRING = "00002a28-0000-1000-8000-00805f9b34fb";
    public static String MANUFACTURER_REVISION_STRING = "00002a29-0000-1000-8000-00805f9b34fb";
    public static String PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");

        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(TX_POWER_LEVEL, "Tx Power Level");
        attributes.put(DEVICE_NAME, "Device Name");
        attributes.put(APPEARANCE, "Appearance");
        attributes.put(PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS, "Peripheral Preferred Connection Parameters");
        attributes.put(SYSTEM_ID, "System ID");
        attributes.put(MODEL_NUMBER_STRING, "Model Number String");
        attributes.put(SERIAL_NUMBER_STRING, "Serial Number String");
        attributes.put(FIRMWARE_REVISION_STRING, "Firmware Revision String");
        attributes.put(HARDWARE_REVISION_STRING, "Hardware Revision String");
        attributes.put(SOFTWARE_REVISION_STRING, "Software Revision String");
        attributes.put(MANUFACTURER_REVISION_STRING, "Manufacturer Name String");
        attributes.put(PNP_ID, "PnP ID");


    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        if(name != null)
            Log.d("NAME: ", name);
        return name == null ? defaultName : name;
    }
}

