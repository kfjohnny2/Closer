package douche.com.closer.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import douche.com.closer.MainActivity;
import douche.com.closer.R;

/**
 * Created by johnnylee on 13/03/17.
 */

public class LeDeviceAdapter extends BaseAdapter{
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    public LeDeviceAdapter(Context context) {
        super();
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }


    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflator.inflate(R.layout.list_item_devices, null);
        // General ListView optimization code.
        TextView txtDeviceName = (TextView) view.findViewById(R.id.txt_device_name);
        TextView txtDeviceAdress = (TextView) view.findViewById(R.id.txt_device_adress);

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            txtDeviceName.setText(deviceName);
        else
            txtDeviceName.setText("UNKNOWN DEVICE");
        txtDeviceAdress.setText(device.getAddress());

        return view;
    }
}
