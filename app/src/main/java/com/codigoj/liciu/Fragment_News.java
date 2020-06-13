package com.codigoj.liciu;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Fragment_News extends Fragment {

    public static final String REF_CATEGORY_PUBLICATIONS = "category_publications";
    public static final String REF_SUBCATEGORY = "subcategory";
    //Attributes
    private AppPreferences appPreferences;
    private RecyclerView recyclerView;

    //Firebase
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_news);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());

        //SharedPreferences instance
        appPreferences = new AppPreferences(getContext());
        //Database reference
        database = FirebaseDatabase.getInstance();

        //The next two param should load from the AppPreferences class
        //Select the category (only one)
        String category = appPreferences.getDataString(Utils.KEY_CATEGORY,"No hay nada");
        //Select the subcatagory (several or one)
        String subcategory = appPreferences.getDataString(Utils.KEY_SUBCATEGORY,"No hay nada");

        Query ref = database.getReference().child(REF_CATEGORY_PUBLICATIONS).child(category).orderByChild(REF_SUBCATEGORY).equalTo(subcategory);
        final FirebaseRecyclerAdapter<Publication, Fragment_Interests.PublicationViewHolder> adapter = new FirebaseRecyclerAdapter<Publication, Fragment_Interests.PublicationViewHolder>(
                Publication.class,
                R.layout.publication,
                Fragment_Interests.PublicationViewHolder.class,
                ref
        ) {
            @Override
            protected void populateViewHolder(Fragment_Interests.PublicationViewHolder viewHolder, Publication model, int position) {
                Log.d("date-end", model.getDate_end());
                Picasso.with(getContext()).load(model.getPath_image_pub()).fit().centerCrop().into(viewHolder.image);
                viewHolder.name.setText(model.getName());
                viewHolder.description.setText(model.getDescription());
                viewHolder.dateStart.setText(model.getDate_start());
                final Publication pub = model;
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), PublicationActivity.class);
                        i.putExtra("id_publication", pub.getId());
                        startActivity(i);
                    }
                });

                //load the typeface
                Typeface berlinSansFB= Typeface.createFromAsset(getActivity().getAssets(),"fonts/BRLNSR.TTF");
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
        return view;
    }

}
