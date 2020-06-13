package com.codigoj.liciu;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.codigoj.liciu.utils.Utils.REF_PUBLICATIONS;

/**
 * This fragment show the publications filtered by the category and subcategory selected by the user
 */
public class Fragment_Interests extends Fragment  {

    public static final String REF_CATEGORY_PUBLICATIONS = "category_publications";
    public static final String REF_SUBCATEGORY = "subcategory";
    //Attributes
    private AppPreferences appPreferences;
    private RecyclerView recyclerView;
    private LinearLayoutManager layout;
    private ProgressBar progressBar;
    private List<Publication> publicationList;
    private ArrayList<String> idPublicationList;

    //Firebase
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_interest);
        layout = new LinearLayoutManager(getActivity());

        //SharedPreferences instance
        appPreferences = new AppPreferences(getContext());
        //Database reference
        database = FirebaseDatabase.getInstance();

        //The next two param should load from the AppPreferences class
        //Select the category (several or one)
        String category = appPreferences.getDataString(Utils.KEY_CATEGORY,"No hay nada");
        //Select the subcatagory (several or one)
        String subcategory = appPreferences.getDataString(Utils.KEY_SUBCATEGORY,"No hay nada");
        String[] categories = category.split(",");
        //Create the list for the queries needed and the list for the publications
        ArrayList<Query> queries = new ArrayList<>();
        publicationList = new ArrayList<>();
        idPublicationList = new ArrayList<>();
        //Gson for the parser data
        Gson json = new Gson();
        JsonElement element = new JsonParser().parse(subcategory).getAsJsonObject();
        JsonObject jsonObject = element.getAsJsonObject();
        for (int i=0; i< categories.length;i++){
            String subCategories = jsonObject.getAsJsonPrimitive("/"+categories[i]+"/subcategory").getAsString();
            Log.d("jsondeserilized-c", categories[i]+":"+subCategories);
            //This is when the user only select a one category and one subcategory
            if (subCategories.length()<2){
                Query ref = database.getReference().child(REF_CATEGORY_PUBLICATIONS)
                        .child(categories[i]).child(Utils.REF_SUBCATEGORY)
                        .child(subCategories).child(REF_PUBLICATIONS);
                queries.add(ref);

            } //This is when the user use more than one categories and subcategories.
            else {
                String[] subCategory = subCategories.split(",");
                for (int j = 0; j < subCategory.length; j++) {
                    Query ref = database.getReference().child(REF_CATEGORY_PUBLICATIONS)
                            .child(categories[i]).child(Utils.REF_SUBCATEGORY)
                            .child(subCategory[j]).child(REF_PUBLICATIONS);
                    queries.add(ref);
                }
            }
        }
        toDoQuery(queries);

        return view;
    }

    private void toDoQuery(final ArrayList<Query> queries) {
        Log.d("TestPub-Queries-SIZE", String.valueOf(queries.size()));
        for (int i = 0; i < queries.size(); i++) {
            Query query = queries.get(i);
            Log.d("TestPub-Queries-REF", query.getRef().toString());
            final int finalI = i;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot publication : dataSnapshot.getChildren()) {
                            boolean actived = ((Boolean)(publication.getValue())).booleanValue();
                            if (actived){
                                addList(publication.getKey());
                            }
                        }
                        if (finalI == queries.size()-1){
                            loadPublications();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    /**
     * Load the publications
     */
    private void loadPublications() {
        Log.d("TestPub-Size", "tamano list to search: "+ idPublicationList.size());
        String timeServerString = appPreferences.getDataString(Utils.KEY_UPDATE_AT, "");
        final Date timeServer = new Date(Long.parseLong(timeServerString));
        //Format the date
        final DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        for (int i = 0; i < idPublicationList.size(); i++) {
            Query query = database.getReference().child(REF_PUBLICATIONS)
                    .child(idPublicationList.get(i));
            final int finalI = i;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Publication pub = dataSnapshot.getValue(Publication.class);
                    //Check the limit date publication and compare
                    Date pub_date = null;
                    Log.d("TestPub-server-data", dataSnapshot.toString());
                    Log.d("TestPub-OB", "publication: "+ pub.getId());
                    Log.d("TestPub-OB", "publication: "+ pub.getDate_start());
                    try {
                         pub_date = format.parse(pub.getDate_end());
                    } catch (ParseException e) {
                        Log.d("Datelici-convertion", e.getMessage());
                    }
                    if (pub_date != null){
                        //if (timeServer.before(pub_date) || timeServer.getDay() == pub_date.getDay()){
                            Log.d("TestPub-AddS", "publication: "+ pub.getId()+" aniadida");
                            publicationList.add(pub);
                        //}
                        if (finalI == idPublicationList.size()-1){
                            //Apply the order by the date
                            orderByDate();
                            //Start the layout
                            Adapter_Interests adapter = new Adapter_Interests(publicationList);
                            recyclerView.setLayoutManager(layout);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("TestPub-Er", databaseError.getMessage());
                }
            });

        }
        //Listen for every publications
        DatabaseReference queryEvent = database.getReference().child(REF_PUBLICATIONS);
        ChildEventListener child = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Publication pub = dataSnapshot.getValue(Publication.class);
                if (searchById(pub.getId()))
                    publicationList.add(pub);
                    //update the interface
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("TestPub-OnCCh", "publication: "+ dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("TestPub-OnCR", "publication: "+ dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("TestPub-OnCM", "publication: "+ dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TestPub-OnCC", "publication: "+ databaseError.getMessage());
            }
        };
    }

    /**
     * This method order by the publication date start. Order desc
     */
    private void orderByDate() {
        ArrayList<Publication> order = new ArrayList<>();
        for (int i = 0; i < publicationList.size(); i++) {
            Publication pub = publicationList.get(i);
            for (int j = i+1; j < publicationList.size()-1 ; j++) {
                Publication pubToOrder = publicationList.get(j);
                if (pub.compareDateStart(pubToOrder) > -1) {
                    order.add(i, pub);
                    order.add(j, pubToOrder);
                }else {
                    order.add(i, pubToOrder);
                    order.add(j, pub);
                }
            }
        }

        for (int i = 0; i < order.size(); i++) {
            Log.d("TestPub-order", order.get(i).getId() + " "+ order.get(i).getDate_start());
        }
    }

    /**
     * Add to the idPublicationList the new id of publication if this is not
     * @param idPublication
     */
    public void addList(String idPublication) {
        if (!searchById(idPublication)) {
            Timestamp time = new Timestamp(new Date().getTime());
            idPublicationList.add(idPublication);
            Log.d("TestPub-idtest", "Time:"+time+ " add:"+ idPublication);
            //Actualiza la vista para que se cargue la nueva publicacion.
        }
    }

    /**
     * Search if inside idPublicationList exist a idPublication send by parameter
     * @param idPublication is the id of publication to search
     * @return true if the idPublication is repeted in the list, otherwise return false.
     */
    public boolean searchById(String idPublication){
        boolean end = false;
        for (int i = 0; i < idPublicationList.size() && !end; i++) {
            String pub = idPublicationList.get(i);
            if (idPublication.equals(pub)) {
                end = true;
                return true;
            }
        }
        return false;
    }


        public class PublicationViewHolder extends RecyclerView.ViewHolder{
            public TextView name;
            public TextView description;
            public TextView dateStart;
            public ImageView image;

            public PublicationViewHolder(View itemView) {
                super(itemView);
            }
        }

}
