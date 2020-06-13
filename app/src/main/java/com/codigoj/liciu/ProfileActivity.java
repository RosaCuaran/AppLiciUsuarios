package com.codigoj.liciu;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    //Constants
    public static int CANT_CATEGORY = 14;
    public static int CANT_SUBCATEGORY = 6;


    //Attributes
    private TextView tv_title_profile;
    private TextView tv_text_profile;
    private Button btn_start_profile;

    //Categories
    private RadioButton rbRumba;
    private RadioButton rbEventos;
    private RadioButton rbEmpresarial;
    private RadioButton rbBaresyRestaurantes;
    private RadioButton rbDiversionyOcio;
    private RadioButton rbCine;
    private RadioButton rbEducacion;
    private RadioButton rbCompras;
    private RadioButton rbDeportes;
    private RadioButton rbAutos;
    private RadioButton rbMotos;
    private RadioButton rbViajes;
    private RadioButton rbSaludyBelleza;
    private RadioButton rbVivienda;

    //Subcategories
    private RadioButton rbRumbaSub0;
    private RadioButton rbEventosSub0;
    private RadioButton rbEventosSub1;
    private RadioButton rbEmpresarialSub0;
    private RadioButton rbBaresyRestaurantesSub0;
    private RadioButton rbBaresyRestaurantesSub1;
    private RadioButton rbDiversionyOcioSub0;
    private RadioButton rbCineSub0;
    private RadioButton rbEducacionSub0;
    private RadioButton rbEducacionSub1;
    private RadioButton rbEducacionSub2;
    private RadioButton rbEducacionSub3;
    private RadioButton rbComprasSub0;
    private RadioButton rbComprasSub1;
    private RadioButton rbComprasSub2;
    private RadioButton rbComprasSub3;
    private RadioButton rbDeportesSub0;
    private RadioButton rbDeportesSub1;
    private RadioButton rbDeportesSub2;
    private RadioButton rbDeportesSub3;
    private RadioButton rbDeportesSub4;
    private RadioButton rbDeportesSub5;
    private RadioButton rbAutosSub0;
    private RadioButton rbAutosSub1;
    private RadioButton rbAutosSub2;
    private RadioButton rbMotosSub0;
    private RadioButton rbMotosSub1;
    private RadioButton rbMotosSub2;
    private RadioButton rbViajesSub0;
    private RadioButton rbViajesSub1;
    private RadioButton rbSaludyBellezaSub0;
    private RadioButton rbViviendaSub0;
    private RadioButton rbViviendaSub1;

    //Layouts
    private LinearLayout cat0;
    private LinearLayout cat1;
    private LinearLayout cat2;
    private LinearLayout cat3;
    private LinearLayout cat4;
    private LinearLayout cat5;
    private LinearLayout cat6;
    private LinearLayout cat7;
    private LinearLayout cat8;
    private LinearLayout cat9;
    private LinearLayout cat10;
    private LinearLayout cat11;
    private LinearLayout cat12;
    private LinearLayout cat13;

    //Firebase
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //Preferences
    private AppPreferences appPreferences;

    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tv_title_profile = (TextView) findViewById(R.id.tv_title_profile);
        tv_text_profile = (TextView) findViewById(R.id.tv_text_profile);
        btn_start_profile = (Button) findViewById(R.id.btn_start_profile);
        //Categories
        rbRumba = (RadioButton) findViewById(R.id.rbRumba);
        rbEventos = (RadioButton) findViewById(R.id.rbEventos);
        rbEmpresarial = (RadioButton) findViewById(R.id.rbEmpresarial);
        rbBaresyRestaurantes = (RadioButton) findViewById(R.id.rbBaresyRestaurantes);
        rbDiversionyOcio = (RadioButton) findViewById(R.id.rbDiversionyOcio);
        rbCine = (RadioButton) findViewById(R.id.rbCine);
        rbEducacion = (RadioButton) findViewById(R.id.rbEducacion);
        rbCompras = (RadioButton) findViewById(R.id.rbCompras);
        rbDeportes = (RadioButton) findViewById(R.id.rbDeportes);
        rbAutos = (RadioButton) findViewById(R.id.rbAutos);
        rbMotos = (RadioButton) findViewById(R.id.rbMotos);
        rbViajes = (RadioButton) findViewById(R.id.rbViajes);
        rbSaludyBelleza = (RadioButton) findViewById(R.id.rbSaludyBelleza);
        rbVivienda = (RadioButton) findViewById(R.id.rbVivienda);
        //Subcategories
        rbRumbaSub0 = (RadioButton) findViewById(R.id.rbRumbaSub0);
        rbEventosSub0 = (RadioButton) findViewById(R.id.rbEventosSub0);
        rbEventosSub1 = (RadioButton) findViewById(R.id.rbEventosSub1);
        rbEmpresarialSub0 = (RadioButton) findViewById(R.id.rbEmpresarialSub0);
        rbBaresyRestaurantesSub0 = (RadioButton) findViewById(R.id.rbBaresyRestaurantesSub0);
        rbBaresyRestaurantesSub1 = (RadioButton) findViewById(R.id.rbBaresyRestaurantesSub1);
        rbDiversionyOcioSub0= (RadioButton) findViewById(R.id.rbDiversionyOcioSub0);
        rbCineSub0= (RadioButton) findViewById(R.id.rbCineSub0);
        rbEducacionSub0= (RadioButton) findViewById(R.id.rbEducacionSub0);
        rbEducacionSub1= (RadioButton) findViewById(R.id.rbEducacionSub1);
        rbEducacionSub2= (RadioButton) findViewById(R.id.rbEducacionSub2);
        rbEducacionSub3= (RadioButton) findViewById(R.id.rbEducacionSub3);
        rbComprasSub0= (RadioButton) findViewById(R.id.rbComprasSub0);
        rbComprasSub1= (RadioButton) findViewById(R.id.rbComprasSub1);
        rbComprasSub2= (RadioButton) findViewById(R.id.rbComprasSub2);
        rbComprasSub3= (RadioButton) findViewById(R.id.rbComprasSub3);
        rbDeportesSub0= (RadioButton) findViewById(R.id.rbDeportesSub0);
        rbDeportesSub1= (RadioButton) findViewById(R.id.rbDeportesSub1);
        rbDeportesSub2= (RadioButton) findViewById(R.id.rbDeportesSub2);
        rbDeportesSub3= (RadioButton) findViewById(R.id.rbDeportesSub3);
        rbDeportesSub4= (RadioButton) findViewById(R.id.rbDeportesSub4);
        rbDeportesSub5= (RadioButton) findViewById(R.id.rbDeportesSub5);
        rbAutosSub0= (RadioButton) findViewById(R.id.rbAutosSub0);
        rbAutosSub1= (RadioButton) findViewById(R.id.rbAutosSub1);
        rbAutosSub2= (RadioButton) findViewById(R.id.rbAutosSub2);
        rbMotosSub0= (RadioButton) findViewById(R.id.rbMotosSub0);
        rbMotosSub1= (RadioButton) findViewById(R.id.rbMotosSub1);
        rbMotosSub2= (RadioButton) findViewById(R.id.rbMotosSub2);
        rbViajesSub0= (RadioButton) findViewById(R.id.rbViajesSub0);
        rbViajesSub1= (RadioButton) findViewById(R.id.rbViajesSub1);
        rbSaludyBellezaSub0= (RadioButton) findViewById(R.id.rbSaludyBellezaSub0);
        rbViviendaSub0= (RadioButton) findViewById(R.id.rbViviendaSub0);
        rbViviendaSub1= (RadioButton) findViewById(R.id.rbViviendaSub1);

        //Layouts
        cat0 = (LinearLayout) findViewById(R.id.cat0);
        cat1 = (LinearLayout) findViewById(R.id.cat1);
        cat2 = (LinearLayout) findViewById(R.id.cat2);
        cat3 = (LinearLayout) findViewById(R.id.cat3);
        cat4 = (LinearLayout) findViewById(R.id.cat4);
        cat5 = (LinearLayout) findViewById(R.id.cat5);
        cat6 = (LinearLayout) findViewById(R.id.cat6);
        cat7 = (LinearLayout) findViewById(R.id.cat7);
        cat8 = (LinearLayout) findViewById(R.id.cat8);
        cat9 = (LinearLayout) findViewById(R.id.cat9);
        cat10 = (LinearLayout) findViewById(R.id.cat10);
        cat11 = (LinearLayout) findViewById(R.id.cat11);
        cat12 = (LinearLayout) findViewById(R.id.cat12);
        cat13 = (LinearLayout) findViewById(R.id.cat13);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((RadioButton) v).isChecked()) {
                    // If the button was already checked, uncheck them all
                    ((RadioButton) v).setChecked(false);
                    // Prevent the system from re-checking it
                    return true;
                }
                return false;
            }
        };

        //Firebase instance
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        //SharedPreferences instance
        appPreferences = new AppPreferences(getApplicationContext());

        //Events change
        rbRumba.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat0.setVisibility(View.VISIBLE);
                    rbRumbaSub0.setChecked(true);
                } else{
                    cat0.setVisibility(View.GONE);
                    rbRumbaSub0.setChecked(false);
                }
            }
        });
        rbEventos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat1.setVisibility(View.VISIBLE);
                } else{
                    cat1.setVisibility(View.GONE);
                    rbEventosSub0.setChecked(false);
                    rbEventosSub1.setChecked(false);
                }
            }
        });
        rbEmpresarial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat2.setVisibility(View.VISIBLE);
                    rbEmpresarialSub0.setChecked(true);
                } else{
                    cat2.setVisibility(View.GONE);
                    rbEmpresarialSub0.setChecked(false);
                }
            }
        });
        rbBaresyRestaurantes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat3.setVisibility(View.VISIBLE);
                } else{
                    cat3.setVisibility(View.GONE);
                    rbBaresyRestaurantesSub0.setChecked(false);
                    rbBaresyRestaurantesSub1.setChecked(false);
                }
            }
        });
        rbDiversionyOcio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat4.setVisibility(View.VISIBLE);
                    rbDiversionyOcioSub0.setChecked(true);
                } else{
                    cat4.setVisibility(View.GONE);
                    rbDiversionyOcioSub0.setChecked(false);
                }
            }
        });
        rbCine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat5.setVisibility(View.VISIBLE);
                    rbCineSub0.setChecked(true);
                } else{
                    cat5.setVisibility(View.GONE);
                    rbCineSub0.setChecked(false);
                }
            }
        });
        rbEducacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat6.setVisibility(View.VISIBLE);
                } else{
                    cat6.setVisibility(View.GONE);
                    rbEducacionSub0.setChecked(false);
                    rbEducacionSub1.setChecked(false);
                    rbEducacionSub2.setChecked(false);
                    rbEducacionSub3.setChecked(false);
                }
            }
        });
        rbCompras.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat7.setVisibility(View.VISIBLE);
                } else{
                    cat7.setVisibility(View.GONE);
                    rbComprasSub0.setChecked(false);
                    rbComprasSub1.setChecked(false);
                    rbComprasSub2.setChecked(false);
                    rbComprasSub3.setChecked(false);
                }
            }
        });
        rbDeportes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat8.setVisibility(View.VISIBLE);
                } else{
                    cat8.setVisibility(View.GONE);
                    rbDeportesSub0.setChecked(false);
                    rbDeportesSub1.setChecked(false);
                    rbDeportesSub2.setChecked(false);
                    rbDeportesSub3.setChecked(false);
                    rbDeportesSub4.setChecked(false);
                    rbDeportesSub5.setChecked(false);
                }
            }
        });
        rbAutos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat9.setVisibility(View.VISIBLE);
                } else{
                    cat9.setVisibility(View.GONE);
                    rbAutosSub0.setChecked(false);
                    rbAutosSub1.setChecked(false);
                    rbAutosSub2.setChecked(false);
                }
            }
        });
        rbMotos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat10.setVisibility(View.VISIBLE);
                } else{
                    cat10.setVisibility(View.GONE);
                    rbMotosSub0.setChecked(false);
                    rbMotosSub1.setChecked(false);
                    rbMotosSub2.setChecked(false);
                }
            }
        });
        rbViajes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat11.setVisibility(View.VISIBLE);
                } else{
                    cat11.setVisibility(View.GONE);
                    rbViajesSub0.setChecked(false);
                    rbViajesSub1.setChecked(false);
                }
            }
        });
        rbSaludyBelleza.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat12.setVisibility(View.VISIBLE);
                    rbSaludyBellezaSub0.setChecked(true);
                } else{
                    cat12.setVisibility(View.GONE);
                    rbSaludyBellezaSub0.setChecked(false);
                }
            }
        });
        rbVivienda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cat13.setVisibility(View.VISIBLE);
                } else{
                    cat13.setVisibility(View.GONE);
                    rbViviendaSub0.setChecked(false);
                    rbViviendaSub1.setChecked(false);
                }
            }
        });

        //Set the TouchListener
        rbRumba.setOnTouchListener(onTouchListener);
        rbEventos.setOnTouchListener(onTouchListener);
        rbEmpresarial.setOnTouchListener(onTouchListener);
        rbBaresyRestaurantes.setOnTouchListener(onTouchListener);
        rbDiversionyOcio.setOnTouchListener(onTouchListener);
        rbCine.setOnTouchListener(onTouchListener);
        rbEducacion.setOnTouchListener(onTouchListener);
        rbCompras.setOnTouchListener(onTouchListener);
        rbDeportes.setOnTouchListener(onTouchListener);
        rbAutos.setOnTouchListener(onTouchListener);
        rbMotos.setOnTouchListener(onTouchListener);
        rbViajes.setOnTouchListener(onTouchListener);
        rbSaludyBelleza.setOnTouchListener(onTouchListener);
        rbVivienda.setOnTouchListener(onTouchListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //load the typeface
        loadFonts();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null ){
                    id_user = user.getUid();
                } else {
                    goLoginScreen();
                }
            }
        };
    }

    private void loadFonts() {
        Typeface berlinSansFB= Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
        tv_title_profile.setTypeface(berlinSansFB);
        tv_text_profile.setTypeface(berlinSansFB);
        btn_start_profile.setTypeface(berlinSansFB);
        //Categories
        rbRumba.setTypeface(berlinSansFB);
        rbEventos.setTypeface(berlinSansFB);
        rbEmpresarial.setTypeface(berlinSansFB);
        rbBaresyRestaurantes.setTypeface(berlinSansFB);
        rbDiversionyOcio.setTypeface(berlinSansFB);
        rbCine.setTypeface(berlinSansFB);
        rbEducacion.setTypeface(berlinSansFB);
        rbCompras.setTypeface(berlinSansFB);
        rbDeportes.setTypeface(berlinSansFB);
        rbAutos.setTypeface(berlinSansFB);
        rbMotos.setTypeface(berlinSansFB);
        rbViajes.setTypeface(berlinSansFB);
        rbSaludyBelleza.setTypeface(berlinSansFB);
        rbVivienda.setTypeface(berlinSansFB);
        //Subcategories
        rbRumbaSub0.setTypeface(berlinSansFB);
        rbEventosSub0.setTypeface(berlinSansFB);
        rbEventosSub1.setTypeface(berlinSansFB);
        rbEmpresarialSub0.setTypeface(berlinSansFB);
        rbBaresyRestaurantesSub0.setTypeface(berlinSansFB);
        rbBaresyRestaurantesSub1.setTypeface(berlinSansFB);
        rbDiversionyOcioSub0.setTypeface(berlinSansFB);
        rbCineSub0.setTypeface(berlinSansFB);
        rbEducacionSub0.setTypeface(berlinSansFB);
        rbEducacionSub1.setTypeface(berlinSansFB);
        rbEducacionSub2.setTypeface(berlinSansFB);
        rbEducacionSub3.setTypeface(berlinSansFB);
        rbComprasSub0.setTypeface(berlinSansFB);
        rbComprasSub1.setTypeface(berlinSansFB);
        rbComprasSub2.setTypeface(berlinSansFB);
        rbComprasSub3.setTypeface(berlinSansFB);
        rbDeportesSub0.setTypeface(berlinSansFB);
        rbDeportesSub1.setTypeface(berlinSansFB);
        rbDeportesSub2.setTypeface(berlinSansFB);
        rbDeportesSub3.setTypeface(berlinSansFB);
        rbDeportesSub4.setTypeface(berlinSansFB);
        rbDeportesSub5.setTypeface(berlinSansFB);
        rbAutosSub0.setTypeface(berlinSansFB);
        rbAutosSub1.setTypeface(berlinSansFB);
        rbAutosSub2.setTypeface(berlinSansFB);
        rbMotosSub0.setTypeface(berlinSansFB);
        rbMotosSub1.setTypeface(berlinSansFB);
        rbMotosSub2.setTypeface(berlinSansFB);
        rbViajesSub0.setTypeface(berlinSansFB);
        rbViajesSub1.setTypeface(berlinSansFB);
        rbSaludyBellezaSub0.setTypeface(berlinSansFB);
        rbViviendaSub0.setTypeface(berlinSansFB);
        rbViviendaSub1.setTypeface(berlinSansFB);
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void btnStart(View view) {
        //Check if there is any category selected
        String[][] subCategories = new String[CANT_CATEGORY][CANT_SUBCATEGORY];
        if (isSelectedCategory(subCategories)){
            final DatabaseReference myRef = database.getReference().child(Utils.REF_USERS)
                    .child(id_user).child(Utils.REF_CATEGORY);
            //Get the categories selected and get the subcategories selected
            final Map<String,Object> allCategories = new HashMap();
            String categories = "";
            for (int i=0; i<CANT_CATEGORY; i++){
                String subCat = "";
                for (int j=0; j<CANT_SUBCATEGORY; j++){
                    if (!(subCategories[i][j] == null)) {
                        subCat += subCategories[i][j];
                        if (j < CANT_SUBCATEGORY-1 ) {
                            if (subCategories[i][j + 1] != null)
                                subCat += ",";
                        }
                    }
                }
                if (!subCat.isEmpty()){
                    allCategories.put("/"+i+"/"+Utils.REF_SUBCATEGORY, subCat);
                    categories += i +",";
                }
            }
            //Remove the last character from the categories, it mean the ","
            final String category = categories.substring(0,categories.length()-1);
            //Remove the data exists in database to rewrite
            myRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    //Store in db in firebase
                    myRef.updateChildren(allCategories, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Gson gson = new Gson();
                            String subCategoriesJSON = gson.toJson(allCategories);
                            Log.d("pruebaJsonCat", subCategoriesJSON);
                            Log.d("pruebaJsonCat", category);
                            appPreferences.saveDataString(Utils.KEY_CATEGORY, category);
                            appPreferences.saveDataString(Utils.KEY_SUBCATEGORY, subCategoriesJSON);

                            startActivity(new Intent(ProfileActivity.this, TabsPublication.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    });
                }
            });
        } else {
            Toast.makeText(this, "Por favor selecione al menos una categoria.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isSelectedCategory(String[][] subCategories){
        boolean value = false;
        if (rbRumba.isChecked()) {
            if (rbRumbaSub0.isChecked())
                subCategories[0][0] = "0";
            value = true;
        } if (rbEventos.isChecked()) {
            if (rbEventosSub0.isChecked())
                subCategories[1][0] = "0";
            if (rbEventosSub1.isChecked())
                subCategories[1][1] = "1";
            value = true;
        } if (rbEmpresarial.isChecked()){
            if (rbEmpresarialSub0.isChecked())
                subCategories[2][0] = "0";
            value = true;
        } if (rbBaresyRestaurantes.isChecked()){
            if (rbBaresyRestaurantesSub0.isChecked())
                subCategories[3][0] = "0";
            if (rbBaresyRestaurantesSub1.isChecked())
                subCategories[3][1] = "1";
            value = true;
        } if (rbDiversionyOcio.isChecked()){
            if (rbDiversionyOcioSub0.isChecked())
                subCategories[4][0] = "0";
            value = true;
        } if (rbCine.isChecked()){
            if (rbCineSub0.isChecked())
                subCategories[5][0] = "0";
            value = true;
        } if (rbEducacion.isChecked()){
            if (rbEducacionSub0.isChecked())
                subCategories[6][0] = "0";
            if (rbEducacionSub1.isChecked())
                subCategories[6][1] = "1";
            if (rbEducacionSub2.isChecked())
                subCategories[6][2] = "2";
            if (rbEducacionSub3.isChecked())
                subCategories[6][3] = "3";
            value = true;
        } if (rbCompras.isChecked()){
            if (rbComprasSub0.isChecked())
                subCategories[7][0] = "0";
            if (rbComprasSub1.isChecked())
                subCategories[7][1] = "1";
            if (rbComprasSub2.isChecked())
                subCategories[7][2] = "2";
            if (rbComprasSub3.isChecked())
                subCategories[7][3] = "3";
            value = true;
        } if (rbDeportes.isChecked()){
            if (rbDeportesSub0.isChecked())
                subCategories[8][0] = "0";
            if (rbDeportesSub1.isChecked())
                subCategories[8][1] = "1";
            if (rbDeportesSub2.isChecked())
                subCategories[8][2] = "2";
            if (rbDeportesSub3.isChecked())
                subCategories[8][3] = "3";
            if (rbDeportesSub4.isChecked())
                subCategories[8][4] = "4";
            if (rbDeportesSub5.isChecked())
                subCategories[8][5] = "5";
            value = true;
        } if (rbAutos.isChecked()){
            if (rbAutosSub0.isChecked())
                subCategories[9][0] = "0";
            if (rbAutosSub1.isChecked())
                subCategories[9][1] = "1";
            if (rbAutosSub2.isChecked())
                subCategories[9][2] = "2";
            value = true;
        } if (rbMotos.isChecked()){
            if (rbMotosSub0.isChecked())
                subCategories[10][0] = "0";
            if (rbMotosSub1.isChecked())
                subCategories[10][1] = "1";
            if (rbMotosSub2.isChecked())
                subCategories[10][2] = "2";
            value = true;
        } if (rbViajes.isChecked()){
            if (rbViajesSub0.isChecked())
                subCategories[11][0] = "0";
            if (rbViajesSub1.isChecked())
                subCategories[11][1] = "1";
            value = true;
        } if (rbSaludyBelleza.isChecked()){
            if (rbSaludyBellezaSub0.isChecked())
                subCategories[12][0] = "0";
            value = true;
        } if (rbVivienda.isChecked()) {
            if (rbViviendaSub0.isChecked())
                subCategories[13][0] = "0";
            if (rbViviendaSub1.isChecked())
                subCategories[13][1] = "1";
            value = true;
        }
        return value;
    }
}
