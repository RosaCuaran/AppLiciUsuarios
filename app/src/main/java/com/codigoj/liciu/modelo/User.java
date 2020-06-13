package com.codigoj.liciu.modelo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 4/05/17.
 */

public class User {

    //Constants
    public final static String USER_ID = "id";
    public final static String USER_NAME = "name";
    public final static String USER_BIRTHDATE = "birthdate";
    public final static String USER_GENDER = "gender";
    public final static String USER_EMAIL = "email";

    //Attributes
    private String id_user;
    private String name;
    private String birthdate;
    private String gender;
    private String email;

    public User() {
        this.birthdate = null;
    }

    public User(String id_user, String name, String gender, String email) {
        this.id_user = id_user;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.birthdate = null;
    }

    public User(String id_user, String name, String birthdate, String gender, String email) {
        this.id_user = id_user;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(USER_ID, id_user);
        result.put(USER_NAME, name);
        result.put(USER_GENDER, gender);
        result.put(USER_BIRTHDATE, birthdate);
        result.put(USER_EMAIL, email);
        return result;
    }

    //--------------------------
    //GETTERS AND SETTERS
    //--------------------------

    public String getId_user() { return id_user; }

    public void setId_user(String id_user) { this.id_user = id_user; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getBirthdate() { return birthdate; }

    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

}
