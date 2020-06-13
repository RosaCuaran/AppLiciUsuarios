package com.codigoj.liciu;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    //Attributes
    private RecyclerView recyclerView;
    private LinearLayoutManager layout;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.search));
        actionBar.setDisplayHomeAsUpEnabled(true);
        String keywords;

        //Database reference
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_search);
        layout = new LinearLayoutManager(getApplicationContext());

        if (getIntent().getExtras() != null){
            //Value for the company
            if(getIntent().getExtras().containsKey("sbc")){
                keywords = getIntent().getExtras().getString("sbc");
                searchByCompany(keywords);
            } else if(getIntent().getExtras().containsKey("sbp")){
                keywords = getIntent().getExtras().getString("sbp");
                searchByPublication(convertString(keywords));
            } else if(getIntent().getExtras().containsKey("sbi")){
                keywords = getIntent().getExtras().getString("sbi");
                searchByCategory(convertString(keywords));
            }
        }

    }

    public String convertString(String cadena){
        cadena = cadena.toLowerCase();
        cadena = cadena.replace("á","a");
        cadena = cadena.replace("é","e");
        cadena = cadena.replace("í","i");
        cadena = cadena.replace("ó","o");
        cadena = cadena.replace("ú","u");
        return cadena;
    }

    /**
     * This Method search the keywords in the categories names availables
     * @param keywords
     */
    public void searchByCategory(String keywords) {
        String category = "-1";
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("rumba");
        categories.add("eventos");
        categories.add("empresarial");
        categories.add("bares y restaurantes");
        categories.add("diversion y ocio");
        categories.add("cine");
        categories.add("educacion");
        categories.add("compras");
        categories.add("deportes");
        categories.add("autos");
        categories.add("motos");
        categories.add("viajes");
        categories.add("salud y belleza");
        categories.add("vivienda");
        for (int i= 0; i <categories.size();i++) {
            if (categories.get(i).equalsIgnoreCase(keywords)){
                category = String.valueOf(i);
                break;
            } else if (categories.get(i).contains(keywords) ) {
                category = String.valueOf(i);
                break;
            }
        }
        Query ref = database.getReference().child(Utils.REF_CATEGORY_PUBLICATIONS).child(category);
        loadInsideReciclerView(ref);
    }

    /**
     * This method search the keywords in the publications name from database
     * @param keywords
     */
    public void searchByPublication(String keywords) {
        Query ref = database.getReference().child(Utils.REF_PUBLICATIONS).orderByChild(Utils.REF_NAME)
                .startAt(keywords).endAt(keywords+"\uf8ff");
        loadInsideReciclerView(ref);
    }

    public void searchByCompany(String keywords) {
        Query ref = database.getReference().child(Utils.REF_PUBLICATIONS).orderByChild(Utils.REF_NAME_COMPANY)
                .startAt(keywords).endAt(keywords+"\uf8ff");
        loadInsideReciclerView(ref);
    }

    /**
     * Load the Query for showing it in the recicler view
     * @param ref
     */
    public void loadInsideReciclerView(Query ref){
        FirebaseRecyclerAdapter<Publication, Fragment_Interests.PublicationViewHolder> adapter = new FirebaseRecyclerAdapter<Publication, Fragment_Interests.PublicationViewHolder>
                (Publication.class,
                        R.layout.publication,
                        Fragment_Interests.PublicationViewHolder.class,
                        ref)
        {
            @Override
            protected void populateViewHolder(Fragment_Interests.PublicationViewHolder viewHolder, final Publication model, int position) {
                Picasso.with(getApplicationContext()).load(model.getPath_image_pub()).fit().centerCrop().into(viewHolder.image);
                viewHolder.name.setText(model.getName());
                viewHolder.description.setText(model.getDescription());
                viewHolder.dateStart.setText(model.getDate_start());
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(SearchActivity.this, PublicationActivity.class);
                        i.putExtra("id_publication", model.getId());
                        startActivity(i);
                    }
                });
                //load the typeface
                Typeface berlinSansFB= Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
                viewHolder.name.setTypeface(berlinSansFB);
                viewHolder.description.setTypeface(berlinSansFB);
                viewHolder.dateStart.setTypeface(berlinSansFB);
            }

            @Override
            public Publication getItem(int position) {
                return super.getItem(getItemCount() - (position + 1));
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
    }

}
