package douche.com.closer.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import douche.com.closer.R;
import douche.com.closer.model.Device;

/**
 * Created by Johnny on 26/06/2017.
 */

public class ServiceEvent {

    public static String getEventName(String address, Context context){
        //GET AQUI
        return "A308 - CIVT";
    }

    public static void sendEvent(Device eventBeacon, Context context){
        try {
            Log.d("TESTE: ", WebServiceImpl.sendPost("https://posttestserver.com/post.php", eventBeacon.toJSON()));
        } catch (WebServiceImpl.GenericException e) {
            e.printStackTrace();
        }
    }
}
