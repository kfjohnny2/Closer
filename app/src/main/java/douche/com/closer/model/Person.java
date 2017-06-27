package douche.com.closer.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import douche.com.closer.LoginActivity;
import douche.com.closer.MainActivity;

/**
 * Created by Johnny on 26/06/2017.
 */

public class Person {


    private String id;
    private String name;
    private String userName;
    private String password;
    private List<Device> devices;
    private Integer role;

    @JsonIgnore
    SharedPreferences pref;
    @JsonIgnore
    SharedPreferences.Editor editor;
    @JsonIgnore
    private Context context;
    @JsonIgnore
    private int PRIVATE_MODE = 0;
    @JsonIgnore
    private static final String PREFER_NAME = "Reg";
    @JsonIgnore
    private static final String ID_USER = "IdUser";
    @JsonIgnore
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    @JsonIgnore
    private static final String KEY_USERNAME = "Username";
    @JsonIgnore
    private static final String KEY_PASSWORD = "txtPassword";

    public Person() {
    }

    public Person(Context context){
        this.context = context;
        pref = this.context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public Person(String name, String userName, String password, List<Device> devices, Integer role) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.devices = devices;
        this.role = role;
    }

    public void createUserLoginSession(String uName, String uPassword, String idUser){
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USERNAME, uName);
        editor.putString(KEY_PASSWORD,  uPassword);
        editor.putString(ID_USER, idUser);
        editor.commit();
    }

    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            return true;
        }
        return false;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public Integer getRolePref() {
        return pref.getInt("Admin", 0);
    }
    public Integer getRole() {
        return this.role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getIdPref() {
        return pref.getString("IdUser", "");
    }

    public void setId(String id) {
        this.id = id;
    }
}
