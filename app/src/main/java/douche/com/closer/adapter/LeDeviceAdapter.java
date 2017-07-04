package douche.com.closer.adapter;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import douche.com.closer.R;
import douche.com.closer.model.Device;
import douche.com.closer.model.Person;
import douche.com.closer.service.ServiceEvent;
import douche.com.closer.service.ServiceUser;

/**
 * Created by johnnylee on 13/03/17.
 */

public class LeDeviceAdapter extends BaseAdapter{
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    private Context context;
    private int rssi;
    private String deviceName;
    private TextView txtEventName;
    ProgressDialog dialog;
    public LeDeviceAdapter(Context context) {
        super();
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLeDevices = new ArrayList<BluetoothDevice>();
        this.context = context;
//        dialog = ProgressDialog.show(context, "",
//                "Loading. Please wait...", true);
    }

    public void addDevice(ScanResult scanResult) {
        if(!mLeDevices.contains(scanResult.getDevice())) {
            mLeDevices.add(scanResult.getDevice());
            this.rssi = scanResult.getRssi();
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
        txtEventName = (TextView) view.findViewById(R.id.txt_device_name);
        TextView txtDeviceAdress = (TextView) view.findViewById(R.id.txt_device_adress);

        BluetoothDevice device = mLeDevices.get(i);
        txtEventName.setText("Searching event...");
        new EventAsyncTask().execute(device.getAddress());
//        txtDeviceAdress.setText(String.valueOf(rssi));
        txtDeviceAdress.setText(device.getAddress());

        return view;
    }

    private class EventAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
//            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return ServiceEvent.getEventName(params[0], context);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtEventName.setText(s);
        }
    }
}
