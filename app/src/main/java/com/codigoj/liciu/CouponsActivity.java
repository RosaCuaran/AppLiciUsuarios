package com.codigoj.liciu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Coupon;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CouponsActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    private AppPreferences appPreferences;

    private RecyclerView recyclerView;
    private Adapter_Coupons adapter;
    private List<Publication> publicationList;
    private Map<String, Coupon> couponsList;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_coupons);
        LinearLayoutManager layout = new LinearLayoutManager(this);

        //SharedPreferences instance
        appPreferences = new AppPreferences(this);
        //Database reference
        database = FirebaseDatabase.getInstance();
        recyclerView.setLayoutManager(layout);
        searchData();
    }

    /**
     * This method search the data about coupons reserved by the this user
     * and fill the publication list and the list the coupons reserved
     */
    private void searchData() {
        final String id_user =  appPreferences.getDataString(Utils.KEY_ID_USER,"No hay nada");
        DatabaseReference ref = database.getReference().child(Utils.REF_USERS_COUPONS).child(id_user);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                publicationList = new ArrayList<Publication>();
                couponsList = new HashMap<String, Coupon>();
                final List<String> publicationskeys = new ArrayList<String>();
                //Get all coupons reserved, if the user have
                for ( DataSnapshot coupons: dataSnapshot.getChildren() ){
                    Coupon c = coupons.getValue(Coupon.class);
                    Log.d("pub-data2", c.getType()+" "+c.getId_user());
                    if (c.getType().equals(Coupon.RESERVED)){
                        //Fill the list the keys from each publication that the user have a coupon
                        publicationskeys.add(coupons.getKey());
                        couponsList.put(coupons.getKey(),c);
                    }
                }
                //Get all publications according the publications keys

                for ( int i = 0; i < publicationskeys.size(); i++ ){
                    DatabaseReference refPub = database.getReference().child(Utils.REF_PUBLICATIONS).child(publicationskeys.get(i));
                    final int finalI = i;
                    refPub.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Publication publication = dataSnapshot.getValue(Publication.class);
                            publicationList.add(publication);
                            //If the list there isn't a next, finish the fill list publication
                            //In this point each publication only have a one coupon per user
                            if (finalI == publicationskeys.size()-1){
                                if (adapter != null){
                                    adapter.update(publicationList, couponsList);
                                } else {
                                    adapter = new Adapter_Coupons(publicationList, context, id_user, database, couponsList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
