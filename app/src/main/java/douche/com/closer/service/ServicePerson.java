package douche.com.closer.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import douche.com.closer.model.Person;

/**
 * Created by Johnny on 28/06/2017.
 */

public class ServicePerson {

    public static Person getPerson(String username, String password, Context context) {
        try {
            String result = WebServiceImpl.get("persons", context);
            JSONArray jArray = new JSONArray(result);
            Person person = null;
            JSONObject jsonObject;
            for (int i = 0; i < jArray.length(); i++) {
                jsonObject = (JSONObject) jArray.get(i);
                if (jsonObject.getString("username").equals(username) && jsonObject.getString("password").equals(password)) {
                    person = new Person();
                    person.setId(jsonObject.getString("_id"));
                    person.setName(jsonObject.getString("name"));
                    person.setUserName(jsonObject.getString("username"));
                    person.setPassword(jsonObject.getString("password"));
                    person.setRole(jsonObject.getInt("role"));
//                    person.setDevices(ServiceEvent.getPersonDevices(person.getId()));
                }
            }
            return person;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
