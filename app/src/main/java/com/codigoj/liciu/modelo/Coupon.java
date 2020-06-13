package com.codigoj.liciu.modelo;

/**
 * Created by JHON on 17/03/2017.
 */

public class Coupon {

    //Constants
    public final static String AVAILABLE = "available";
    public final static String RESERVED = "reserved";
    public final static String REDEEMED = "redeemed";

    private String id_coupon;
    private String type;
    private String id_user =  null;

    public Coupon() {

    }

    public Coupon(String id_coupon, String type, String id_user) {
        this.id_coupon = id_coupon;
        this.type = type;
        this.id_user = id_user;
    }

    //--------------------------
    //GETTERS AND SETTERS
    //--------------------------


    public String getId_coupon() {
        return id_coupon;
    }

    public void setId_coupon(String id_coupon) {
        this.id_coupon = id_coupon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId_user() { return id_user; }

    public void setId_user(String id_user) { this.id_user = id_user; }
}
