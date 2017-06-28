package douche.com.closer.service;

import android.content.Context;
import android.net.Uri;

import java.net.URL;
import java.net.URLEncoder;

import douche.com.closer.model.Person;

/**
 * Created by Johnny on 27/06/2017.
 */

public class ServiceUser {

    public static String postUser(Person user, Context context){
        try {
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("name", user.getName())
                    .appendQueryParameter("username", user.getUserName())
                    .appendQueryParameter("password", user.getPassword())
                    .appendQueryParameter("role", String.valueOf(user.getRole()));

//            URL url = new URL("https://behere-api-eltonvs1.c9users.io:8080/persons?name="+ URLEncoder.encode(user.getName(), "UTF-8")+"&username=" + URLEncoder.encode(user.getUserName(), "UTF-8")+"&password="+ URLEncoder.encode(user.getPassword(), "UTF-8")+"&role="+user.getRole());
            return WebServiceImpl.sendPost("persons", builder.build().getEncodedQuery(), context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
