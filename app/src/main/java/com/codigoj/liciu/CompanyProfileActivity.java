package com.codigoj.liciu;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Company;
import com.codigoj.liciu.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyProfileActivity extends AppCompatActivity {

    //Attributes

    private int state_favorite;
    private int state_notification;
    private String id_company;
    private String id_user;
    private AppPreferences appPreferences;
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    //UI
    private CircleImageView photo_button;
    private TextView tvNameCompany;
    private TextView tvDescriptionCompany;
    private ImageButton btn_favorite;
    private ImageButton btn_notification;
    private Button btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        if (getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("id_company")){
                id_company = getIntent().getExtras().getString("id_company");
                Log.d("funciona", "id:" +id_company);
            } else {
                startActivity(new Intent(this, TabsPublication.class));
            }
        }
        photo_button = (CircleImageView) findViewById(R.id.photo_button);
        tvNameCompany = (TextView) findViewById(R.id.tvNameCompany);
        tvDescriptionCompany = (TextView) findViewById(R.id.tvDescriptionCompany);
        btn_favorite = (ImageButton) findViewById(R.id.btn_favorite);
        btn_notification = (ImageButton) findViewById(R.id.btn_notification);
        btn_location = (Button) findViewById(R.id.btn_how_to_get);
        //Database and Auth instance
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //Data
        appPreferences = new AppPreferences(this);

        //Falta cargar el id usuario. Load from sharedPreferences th id_user
        id_user = appPreferences.getDataString(Utils.KEY_ID_USER, "");
        if (id_user.isEmpty()){
            Toast.makeText(this, "Por favor inicie sesión nuevamente", Toast.LENGTH_SHORT).show();
        }

        searchCompany();
        //The buttons starting disabled
        loadStateInitButtons(false);
        //Search the state the buttons from bd
        DatabaseReference ref1 = database.getReference().child(Utils.REF_USER_FAVORITE).child(id_user);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Load State favorite
                if (dataSnapshot.hasChild(id_company)) {
                    if (dataSnapshot.child(id_company).getValue(Boolean.class)) {
                        state_favorite = 1;
                    } else {
                        state_favorite = 0;
                    }
                } else {
                    state_favorite = 0;
                }
                Log.d("state-fav", String.valueOf(state_favorite));
                //Load State notification
                DatabaseReference ref2 = database.getReference().child(Utils.REF_NOTIFICATION_COMPANIES_TO_USER).child(id_company).child(id_user);
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ( dataSnapshot.exists() ) {
                            if (dataSnapshot.getValue(Boolean.class)) {
                                state_notification = 1;
                            } else {
                                state_notification = 0;
                            }
                        } else {
                            state_notification = 0;
                        }
                        Log.d("state-not", String.valueOf(state_notification));
                        loadStateButtons();
                        loadStateInitButtons(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state_favorite == 0){
                    state_favorite = 1;
                    //---------------------------------
                    //Verificar si el id_company y id_user carga o no al llegar a aqui.
                    //Revisar si se pueden quitar los parametros tambien
                    //----------------------------------
                    unregisteredAndRegistrerFavorite(true, id_user,id_company);
                    btn_favorite.setBackgroundResource(R.drawable.ic_favorite_active);
                } else {
                    unregisteredAndRegistrerFavorite(false, id_user,id_company);
                    state_favorite = 0;
                    btn_favorite.setBackgroundResource(R.drawable.ic_favorite_inactive);
                }

            }
        });
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state_notification == 0){
                    state_notification = 1;
                    unregisteredAndRegistrerNotification(true, id_user,id_company);
                    btn_notification.setBackgroundResource(R.drawable.ic_notifications_active);
                } else {
                    state_notification = 0;
                    unregisteredAndRegistrerNotification(false, id_user,id_company);
                    btn_notification.setBackgroundResource(R.drawable.ic_notifications_inactive);
                }
            }
        });

        //load the typeface
        Typeface berlinSansFB = Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
        tvNameCompany.setTypeface(berlinSansFB);
        tvDescriptionCompany.setTypeface(berlinSansFB);
        btn_location.setTypeface(berlinSansFB);
    }

    private void unregisteredAndRegistrerNotification(boolean b, String id_user, String id_company) {
        DatabaseReference myRef = database.getReference();
        myRef.child(Utils.REF_NOTIFICATION_COMPANIES_TO_USER).child(id_company).child(id_user).setValue(b);
    }

    private void loadStateButtons() {
        if (state_notification == 1){
            btn_notification.setBackgroundResource(R.drawable.ic_notifications_active);
        } else {
            btn_notification.setBackgroundResource(R.drawable.ic_notifications_inactive);
        }
        if (state_favorite == 1){
            btn_favorite.setBackgroundResource(R.drawable.ic_favorite_active);
        } else {
            btn_favorite.setBackgroundResource(R.drawable.ic_favorite_inactive);
        }
    }

    private void loadStateInitButtons(boolean state) {
        btn_notification.setEnabled(state);
        btn_favorite.setEnabled(state);
        btn_location.setEnabled(state);
    }

    public void unregisteredAndRegistrerFavorite(boolean value, String id_user, String id_company) {
        DatabaseReference myRef = database.getReference();
        myRef.child(Utils.REF_USER_FAVORITE).child(id_user).child(id_company).setValue(value);
    }

    public void searchCompany() {
        //Search the company
        DatabaseReference refcompany = database.getReference().child(Utils.REF_COMPANIES).child(id_company);
        refcompany.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("datos", "los datos son:"+dataSnapshot.toString());
                Company c = dataSnapshot.getValue(Company.class);
                // Fill the data in the layout
                fillData(c);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fillData(Company c) {
        Picasso.with(getApplicationContext()).load(c.getPath_image_remote()).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerCrop().into(photo_button);
        tvNameCompany.setText(c.getName());
        tvDescriptionCompany.setText(c.getDescription());
    }

    public void viewLocation(View view) {
        Intent i = new Intent(CompanyProfileActivity.this, MapsActivity.class);
        i.putExtra("id_company", id_company);
        startActivity(i);
    }
}
