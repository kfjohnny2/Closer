package douche.com.closer.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import douche.com.closer.model.Person;

/**
 * Created by Johnny on 28/06/2017.
 */

public class ServicePerson {

    public static ArrayList<Person> getPerson(Context context) {
        try {
            String result = WebServiceImpl.get("persons", context);
            ArrayList<Person> listdata = new ArrayList<Person>();
            JSONArray jArray = new JSONArray(result);
            Person person;
            JSONObject jsonObject;
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    jsonObject = (JSONObject) jArray.get(i);
                    person = new Person();
                    person.setId(jsonObject.getString("_id"));
                    person.setName(jsonObject.getString("name"));
                    person.setUserName(jsonObject.getString("username"));
                    person.setPassword(jsonObject.getString("password"));
                    person.setRole(jsonObject.getInt("role"));
                    listdata.add(person);
                }
            }
            return listdata;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
