package com.codigoj.liciu.modelo;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JHON on 17/02/2017.
 */

public class Company {

    private String id;
    private String email;
    private String path_image_local;
    private String path_image_remote;
    private String name;
    private String description;
    private String direction;
    private int category;
    private double longitud;
    private double latitud;
    private boolean validity;
    private HashMap<String, Publication> publications;

    public Company() {
        publications = new HashMap<String, Publication>();
    }

    public Company(String id, String email, String path_image_local, String path_image_remote, String name, String description, String direction, int category, double longitud, double latitud, boolean validity, ArrayList<Publication> publications) {
        this.id = id;
        this.email = email;
        this.path_image_local = path_image_local;
        this.path_image_remote = path_image_remote;
        this.name = name;
        this.description = description;
        this.direction = direction;
        this.category = category;
        this.longitud = longitud;
        this.latitud = latitud;
        this.validity = validity;
        this.publications = new HashMap<String, Publication>();
    }

    public Company(String id, String email, String path_image_local, String name, String description, int category, boolean validity){
        this.id = id;
        this.email = email;
        this.path_image_local = path_image_local;
        this.name = name;
        this.description = description;
        this.category = category;
        this.validity = validity;
        this.publications = new HashMap<String, Publication>();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("email", email);
        result.put("path_image_local", path_image_local);
        result.put("path_image_remote", path_image_remote);
        result.put("name", name);
        result.put("description", description);
        result.put("direction", direction);
        result.put("category", category);
        result.put("longitud", longitud);
        result.put("latitud", latitud);
        result.put("publications", publications);

        return result;
    }

    //--------------------------
    //GETTERS AND SETTERS
    //--------------------------

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPath_image_local() {
        return path_image_local;
    }

    public void setPath_image_local(String path_image_local) { this.path_image_local = path_image_local; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public int getCategory() { return category; }

    public void setCategory(int category) { this.category = category; }

    public String getPath_image_remote() { return path_image_remote; }

    public void setPath_image_remote(String path_image_remote) { this.path_image_remote = path_image_remote; }

    public boolean isValidity() { return validity; }

    public void setValidity(boolean validity) { this.validity = validity; }

    public HashMap<String, Publication> getPublications() { return publications; }

    public void setPublications(HashMap<String, Publication> publications) { this.publications = publications; }
}
