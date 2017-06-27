package douche.com.closer.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import douche.com.closer.model.Device;
import douche.com.closer.model.Person;

/**
 * Created by Johnny on 26/06/2017.
 */

public class ServiceEvent {

    public static String getEventName(String address, Context context) {
//        try {
//            String result = WebServiceImpl.get("https://behere-api-eltonvs1.c9users.io:8080/devices?address=" +address);
//            JSONObject jsonObject = new JSONObject(result);
//            return jsonObject.getString("address");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return "A308 - CIVT";
    }

    public static void sendEvent(Device eventBeacon, Context context) {
        try {
            Person user = new Person(context);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", eventBeacon.getName())
                    .appendQueryParameter("address", String.valueOf(eventBeacon.getAddress()))
                    .appendQueryParameter("owner", user.getIdPref());
            Log.d("TESTE: ", WebServiceImpl.sendPost("https://behere-api-eltonvs1.c9users.io:8080/devices", builder.build().getEncodedQuery()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
