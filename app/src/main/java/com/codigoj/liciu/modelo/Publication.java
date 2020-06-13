package com.codigoj.liciu.modelo;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by JHON on 17/02/2017.
 */

public class Publication {
    //Constants
    public final static String TYPE_EVENT = "EVENTO";
    public final static String TYPE_PROMOTION = "PROMOCION";
    public final static String PUBLICATION_ID = "id";
    public final static String PUBLICATION_NAME = "name";
    public final static String PUBLICATION_PATH_IMAGE_PUB = "path_image_pub";
    public final static String PUBLICATION_DESCRIPTION = "description";
    public final static String PUBLICATION_TYPE_PUBLICATION = "type_publication";
    public final static String PUBLICATION_NUMCUPOS = "numCupos";
    public final static String PUBLICATION_DATE_START = "date_start";
    public final static String PUBLICATION_DATE_END = "date_end";
    public final static String PUBLICATION_POBLATIONM = "poblationM";
    public final static String PUBLICATION_POBLATIONF = "poblationF";
    public final static String PUBLICATION_SUBCATEGORY = "subcategory";
    public final static String PUBLICATION_COUPONS = "coupons";

    //attribute of class
    private String id;
    private String name;
    private String path_image_pub;
    private String description;
    private String type_publication;
    private int numCupos;
    private String date_start;
    private String date_end;
    private boolean poblationM;
    private boolean poblationF;
    private String subcategory;
    private ArrayList<Coupon> listCoupons = null;
    Date hoy = new Date();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public Publication() {

    }

    public Publication(String id, String name, String path_image_pub, String description, String type_publication, int numCupos, String date_end, boolean poblationM, boolean poblationF, String subcategory) {
        this.id = id;
        this.name = name;
        this.path_image_pub = path_image_pub;
        this.description = description;
        this.type_publication = type_publication;
        this.numCupos = -1;
        this.date_start = format.format(hoy);
        this.date_end = date_end;
        this.poblationM = poblationM;
        this.poblationF = poblationF;
        this.subcategory = subcategory;
    }

    /**
     * Add the coupon to the list
     * @param myCoupon
     */
    public void addCoupon(Coupon myCoupon){
        if (listCoupons == null){
            listCoupons = new ArrayList<Coupon>();
        }
        listCoupons.add(myCoupon);
    }

    /**
     * This method get the coupons count in state AVAILABLE
     * @return cont If return cont==-1 there are not available coupons , else yes.
     */
    public int getCountListAvailable(){
        int cont = -1;
        if (listCoupons != null) {
            cont = 0;
            for (Coupon c : listCoupons) {
                if (c.getType().equals(Coupon.AVAILABLE)) {
                    cont++;
                }
            }
        }
        return cont;
    }

    /**
     * This method get the coupons count in state RESERVED
     * @return cont If return cont==-1 there are not reserved coupons, else yes.
     */
    public int getCountListReserved(){
        int cont = -1;
        if (listCoupons != null) {
            cont = 0;
            for (Coupon c : listCoupons) {
                if (c.getType().equals(Coupon.RESERVED)) {
                    cont++;
                }
            }
        }
        return cont;
    }

    /**
     * This method get the coupons count in state REDEEMED
     * @return cont If return cont==-1 there are not redeemed coupons, else yes.
     */
    public int getCountListRedeemed(){
        int cont = -1;
        if (listCoupons != null) {
            cont = 0;
            for (Coupon c : listCoupons) {
                if (c.getType().equals(Coupon.REDEEMED)) {
                    cont++;
                }
            }
        }
        return cont;
    }

    /**
     * Compare this date_start with the date_start from publication sent by parameter
     * <b><pre>Pre:</pre></b> this.date_start != null || this.date_start != "". The
     * publication have been a initialized && p.getDate_start() != null || p.getDate_start() != ""
     * @param p is the publication sent by parameter
     * @return 1 if this publication is newer than the other ||
     * -1 if this publication is older than the other ||
     * 0 if both publications have a same date.
     */
    public int compareDateStart(Publication p){
        Date temp1 = parseToDate(this.date_start);
        Date temp2 = parseToDate(p.getDate_start());
            if (temp1.after(temp2))
                return 1;
            else if (temp1.before(temp2))
                return -1;
            else return 0;
    }

    /**
     * Transform the date sent by parameter like String and return a object Date if is possible.
     * @param pDate is the parameter to change to Date. pDate != null && pDate != ""
     * @return a Date object if is possible; otherwise, return null
     */
    public Date parseToDate(String pDate){
        Date date = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            date = format.parse(pDate);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Get the list amount
     * @return
     */
    public int getCountList(){
        return listCoupons.size();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(PUBLICATION_ID, id);
        result.put(PUBLICATION_NAME, name);
        result.put(PUBLICATION_PATH_IMAGE_PUB, path_image_pub);
        result.put(PUBLICATION_DESCRIPTION, description);
        result.put(PUBLICATION_TYPE_PUBLICATION, type_publication);
        result.put(PUBLICATION_NUMCUPOS, numCupos);
        result.put(PUBLICATION_DATE_START, date_start);
        result.put(PUBLICATION_DATE_END, date_end);
        result.put(PUBLICATION_POBLATIONM, poblationM);
        result.put(PUBLICATION_POBLATIONF, poblationF);
        result.put(PUBLICATION_SUBCATEGORY, subcategory);
        result.put(PUBLICATION_COUPONS, listCoupons);

        return result;
    }
    //--------------------------
    //GETTERS AND SETTERS
    //--------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getType_publication() {
        return type_publication;
    }

    public void setType_publication(String type_publication) { this.type_publication = type_publication; }

    public int getNumCupos() {
        return numCupos;
    }

    public void setNumCupos(int numCupos) { this.numCupos = numCupos; }

    public String getPath_image_pub() { return path_image_pub; }

    public void setPath_image_pub(String path_image_pub) { this.path_image_pub = path_image_pub; }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public boolean isPoblationM() { return poblationM; }

    public void setPoblationM(boolean poblationM) { this.poblationM = poblationM; }

    public boolean isPoblationF() { return poblationF; }

    public void setPoblationF(boolean poblationF) { this.poblationF = poblationF; }

    public String getSubcategory() { return subcategory; }

    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public ArrayList<Coupon> getListCoupons() { return listCoupons; }

    public void setListCoupons(ArrayList<Coupon> listCoupons) { this.listCoupons = listCoupons; }
}
