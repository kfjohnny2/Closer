package douche.com.closer.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import douche.com.closer.model.Device;
import douche.com.closer.model.Person;

/**
 * Created by Johnny on 26/06/2017.
 */

public class ServiceEvent {

    public static String getEventName(String address, Context context) {
        try {
            String result = WebServiceImpl.get("devices/address/" +address, context);
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }

    public static void sendEvent(Device eventBeacon, Context context) {
        try {
            Person user = new Person(context);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", eventBeacon.getName())
                    .appendQueryParameter("address", String.valueOf(eventBeacon.getAddress()))
                    .appendQueryParameter("owner", user.getIdPref());
            Log.d("TESTE: ", WebServiceImpl.sendPost("devices", builder.build().getEncodedQuery(), context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Device> getPersonDevices(String idPerson){
        return new ArrayList<>();
    }
}
