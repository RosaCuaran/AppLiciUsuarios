package com.codigoj.liciu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Coupon;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PublicationActivity extends AppCompatActivity {

    public static final String REF_SEARCH = "search";
    public static final String REF_ID_COMPANY = "id_company";
    public static final String REF_PUBLICATIONS = "publications";
    public static final String REF_COUPONS = "coupons";


    public static final int INITIAL_LOAD = 0;
    public static final int UPDATE = 1;

    //UI references
    private TextView coupons_number;
    private Button btn_reserve_coupon;
    private Button btn_how_to_get;
    private Button btn_company_profile;
    private TextView name;
    private TextView dateStart;
    private ImageView image;
    private TextView description;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private ValueEventListener listener;
    private AppPreferences appPreferences;

    //Local data
    String id_publication = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        appPreferences = new AppPreferences(this);

        if (getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("id_publication")){
                 id_publication = getIntent().getExtras().getString("id_publication");
            }
        }
        coupons_number = (TextView) findViewById(R.id.tv_coupons_number);
        btn_reserve_coupon = (Button) findViewById(R.id.btn_reserve_coupon);
        btn_how_to_get = (Button) findViewById(R.id.btn_how_to_get);
        btn_company_profile = (Button) findViewById(R.id.btn_company_profile);
        name = (TextView) findViewById(R.id.cardName);
        image = (ImageView) findViewById(R.id.cardImage);
        description = (TextView) findViewById(R.id.cardDescription);
        dateStart = (TextView) findViewById(R.id.cardDate);
        //Database and Auth instance
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (id_publication == ""){
            finish();
            startActivity(new Intent(this, TabsPublication.class));
        }
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        loadData();
    }

    /**
     * This method load or update the data shown in the view
     */
    private void loadData() {
        DatabaseReference REF_pub = database.getReference().child(REF_PUBLICATIONS).child(id_publication);
        REF_pub.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("datos", "Las publicaciones:" + dataSnapshot.toString());
                Publication publication = dataSnapshot.getValue(Publication.class);

                for (DataSnapshot couponlist : dataSnapshot.getChildren()) {
                    if (couponlist.getKey().equals(REF_COUPONS)) {
                        //Log.d("datos", "Los cupones:" + couponlist.toString());
                        for (DataSnapshot coupon : couponlist.getChildren()) {
                            publication.addCoupon(coupon.getValue(Coupon.class));
                        }
                    }
                }
                //Log.d("datos", "Los cupones-count:" + publication.getCountList());
                //Filtrer by the publication type
                if (publication.getType_publication().equals(Publication.TYPE_EVENT)){
                    btn_reserve_coupon.setVisibility(View.GONE);
                    coupons_number.setVisibility(View.GONE);

                }
                //and else Publication.TYPE_PROMOTION
                else{
                    //The coupons are a infite
                    String number = "";
                    if (publication.getNumCupos() == 0){
                        number = "Cupones ilimitados";
                    }
                    else
                    {   //Filtrer by the disponibility
                        if (publication.getCountListAvailable() == -1 || publication.getCountListAvailable() == 0){
                            number = "Cupones agotados";
                            btn_reserve_coupon.setEnabled(false);
                        }
                        else
                        {
                            number = getString(R.string.prompt_coupons_available, publication.getCountListAvailable());
                        }
                    }
                    coupons_number.setText(number);
                }
                name.setText(publication.getName());
                dateStart.setText(publication.getDate_start());
                description.setText(publication.getDescription());
                Picasso.with(getApplicationContext()).load(publication.getPath_image_pub()).fit().centerCrop().into(image);

                //load the typeface
                Typeface berlinSansFB= Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
                name.setTypeface(berlinSansFB);
                description.setTypeface(berlinSansFB);
                dateStart.setTypeface(berlinSansFB);
                coupons_number.setTypeface(berlinSansFB);
                btn_reserve_coupon.setTypeface(berlinSansFB);
                btn_how_to_get.setTypeface(berlinSansFB);
                btn_company_profile.setTypeface(berlinSansFB);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * This method search and change to type reserved the first coupon of type available, if exist.
     * @param view
     */
    public void viewReserveCoupons(View view) {
        final String id_user =  appPreferences.getDataString(Utils.KEY_ID_USER,"No hay nada");
        Query ref = database.getReference().child(Utils.REF_PUBLICATIONS).child(id_publication)
                .child(Utils.REF_COUPONS).orderByChild(Utils.REF_ID_USER).equalTo(id_user);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("datasnapshot-", "los datos son:"+dataSnapshot.toString());
                if (dataSnapshot.exists()){
                    //The user already have a coupon from this publication
                    Log.d("datasnapshot-", "los datos son:"+dataSnapshot.toString());
                    Toast.makeText(PublicationActivity.this, "Ya tienes un cupón reservado", Toast.LENGTH_SHORT).show();
                } else {
                    //Register the data, it mean that the user receives a coupon
                    Query ref = database.getReference().child(Utils.REF_PUBLICATIONS).child(id_publication)
                            .child(Utils.REF_COUPONS).orderByChild(Utils.REF_TYPE).equalTo(Utils.REF_AVAILABLE);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int cont = 0;
                            for (DataSnapshot coupon : dataSnapshot.getChildren()) {
                                if (cont == 0){
                                    //Update the first reference
                                    Coupon c = coupon.getValue(Coupon.class);
                                    c.setId_user(id_user);
                                    c.setType(Utils.REF_RESERVED);
                                    coupon.getRef().setValue(c);
                                    //Update the second reference
                                    database.getReference().child(Utils.REF_USERS_COUPONS)
                                        .child(id_user).child(id_publication).setValue(c);
                                    cont++;
                                    //update the view
                                    loadData();
                                    //show the notification to user
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PublicationActivity.this);
                                    builder.setMessage(R.string.promt_message_cuopon_reserved);
                                    builder.setPositiveButton("ir ahora", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(PublicationActivity.this, CouponsActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }
                                    });
                                    builder.show();
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



    public void viewLocation(View view) {
        DatabaseReference refcompany = database.getReference().child(REF_SEARCH).child(id_publication).child(REF_ID_COMPANY);
        refcompany.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("datos", "los datos son:"+dataSnapshot.toString());
                String id_company = dataSnapshot.getValue(String.class);
                Intent i = new Intent(PublicationActivity.this, MapsActivity.class);
                i.putExtra("id_company", id_company);
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void viewProfile(View view) {
        DatabaseReference refcompany = database.getReference().child(REF_SEARCH).child(id_publication).child(REF_ID_COMPANY);
        refcompany.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("datos", "los datos son:"+dataSnapshot.toString());
                String id_company = dataSnapshot.getValue(String.class);
                Intent i = new Intent(PublicationActivity.this, CompanyProfileActivity.class);
                i.putExtra("id_publication", id_publication);
                i.putExtra("id_company", id_company);
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference ref = database.getReference().child(Utils.REF_PUBLICATIONS).child(id_publication);
        ref.removeEventListener(listener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref = database.getReference().child(Utils.REF_PUBLICATIONS).child(id_publication);
        ref.addValueEventListener(listener);
    }
}
