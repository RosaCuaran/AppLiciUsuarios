package com.codigoj.liciu.dummy;

import android.app.Activity;
import android.util.Log;

import com.codigoj.liciu.modelo.Company;
import com.codigoj.liciu.modelo.Publication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Content {

    private static final String TAG = Activity.class.getName();

    //Firebase
    private FirebaseAuth firebaseAuth;
    private StorageReference storage;
    private DatabaseReference database;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Publication> ITEMS = new ArrayList<Publication>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Publication> ITEM_MAP = new HashMap<String, Publication>();

    private static final int COUNT = 25;

    public Content() {
        //SharedPreferences instance
        //appPreferences = new AppPreferences(getContext());
        //Database reference
        database = FirebaseDatabase.getInstance().getReference();
        //Storage reference
        storage = FirebaseStorage.getInstance().getReference();

    }

    private static void addItem(Publication item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }
    //Method

    private static void crearListItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * Method for the filtrer and add to the list the publications by category and subcategory
     * @return Query with the interest publications
     */
    public Query queryInterest(){
        //Query for the work
        Query query = database.child("companies").getRef();
        Log.d(TAG, "Query ref-key: " + query.getRef().getKey());
        Log.d(TAG, "Query ref-String: " + query.getRef().toString());
        Log.d(TAG, "Query ref-Root: " + query.getRef().getRoot().toString());
        Log.d(TAG, "Query ref-Parent: " + query.getRef().getParent().toString());
        Log.d(TAG, "Query ref-equalsCompany: " + query.getRef().getKey().equals(Company.class));

        ChildEventListener event = new ChildEventListener() {
            //The next two param should load from the AppPreferences class
            //Select the category (only one)
            String category = "3";
            //Select the subcatagory (several)
            String subcategory = "0,1";
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Values
                Log.d(TAG, "onChildAdded-Key: " + dataSnapshot.getKey());//id company
                Log.d(TAG, "onChildAdded-Children: " + dataSnapshot.getChildren());// Get one child //AVERIGUAR SI SE PUEDE OBTENER EL ULTIMO HIJO
                Log.d(TAG, "onChildAdded-ChildrenCount: " + dataSnapshot.getChildrenCount());//count the attributes in the company
                Log.d(TAG, "onChildAdded-Value: " + dataSnapshot.getValue());//Array of Values of the company
                Company c = dataSnapshot.getValue(Company.class);
                if (c.getCategory() == Integer.parseInt(category) && c.isValidity()){
                    for (DataSnapshot objectpublication: dataSnapshot.child("publications").getChildren()){
                        Publication p = objectpublication.getValue(Publication.class);
                        if (p.getSubcategory().equals(subcategory)){
                            addItem(p);
                        }
                    }
                }

                //Test
                /*for (PublicationActivity p : ITEMS){
                    Log.d(TAG, "publication-Key: " + p.getId());// id company
                }*/
                /*for (DataSnapshot company: dataSnapshot.getChildren() ){ //Take one company from the list
                    Log.d(TAG, "company-Key: " + company.getKey());// id company
                    Log.d(TAG, "company-Children: " + company.getChildren()); // Get one child
                    Log.d(TAG, "company-ChildrenCount: " + company.getChildrenCount()); // count the attributes in the company
                    Log.d(TAG, "company-Value: " + company.getValue()); // object type Company
                }*/
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addChildEventListener(event);
        return query;
    }

}
