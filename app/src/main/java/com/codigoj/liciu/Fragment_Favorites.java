package com.codigoj.liciu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * This fragment show the company's publications that the user have selected like favorites
 */
public class Fragment_Favorites extends Fragment {

    private List<Publication> publicationList;
    //Attributes
    private AppPreferences appPreferences;
    private RecyclerView recyclerView;
    private Adapter_Favorites adapter;
    //Firebase
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_favorites);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        //SharedPreferences instance
        appPreferences = new AppPreferences(getContext());
        //Database reference
        database = FirebaseDatabase.getInstance();
        publicationList = new ArrayList<Publication>();
        searchData();
        // Inflate the layout for this fragment
        return view;
    }

    private void searchData() {
        final String id_user =  appPreferences.getDataString(Utils.KEY_ID_USER,"No hay nada");
        DatabaseReference ref1 = database.getReference().child(Utils.REF_USER_FAVORITE).child(id_user);
        ref1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    final List<String> listCompanies= new ArrayList();
                    if (dataSnapshot.getValue(Boolean.class) == true) {
                        listCompanies.add(dataSnapshot.getKey());
                    }
                    for (int i=0 ; i<listCompanies.size(); i++) {
                        DatabaseReference ref2 = database.getReference().child(Utils.REF_COMPANIES).child(listCompanies.get(i));
                        final int finalI = i;
                        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                //For the get the publications, I must ask if the company have validity and have publications
                                //First I must check if there are the fields in db
                                boolean validity = false;
                                DataSnapshot publist = null;
                                for (DataSnapshot attributes : dataSnapshot2.getChildren()) {
                                    if (attributes.getKey().equals(Utils.REF_VALIDITY)) {
                                        validity = attributes.getValue(Boolean.class);
                                    }
                                    if (attributes.getKey().equals(Utils.REF_PUBLICATIONS)) {
                                        publist = attributes;
                                    }
                                }
                                if (validity){ //The company does not accomplish the validity or the company does not have publications
                                    if (publist != null) {
                                        for (DataSnapshot keyPub : publist.getChildren()) {
                                            //Add each publication to the list
                                            publicationList.add(keyPub.getValue(Publication.class));
                                        }
                                    }
                                }
                                if (finalI == listCompanies.size()-1){
                                    //Finish the search
                                    //Order the publications
                                    adapter = new Adapter_Favorites(publicationList, getActivity());
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    // The user does not have favorites
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    addPublications(dataSnapshot.getKey());
                } else {
                    removePublications(dataSnapshot.getKey());
                }
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
        });
    }

    private void removePublications(String id_company) {
        DatabaseReference refSearch = database.getReference().child(Utils.REF_COMPANIES).child(id_company)
                .child(Utils.REF_PUBLICATIONS);
        refSearch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    for (int i = 0; i < publicationList.size(); i++) {
                        if (publicationList.get(i).getId().equals(key.getKey())){
                            publicationList.remove(i);
                            adapter.notifyItemRemoved(i);
                            adapter.notifyItemRangeChanged(i, publicationList.size());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addPublications(String id_company) {
        DatabaseReference refSearch = database.getReference().child(Utils.REF_COMPANIES).child(id_company)
                .child(Utils.REF_PUBLICATIONS);
        refSearch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    if (!searchPublicationById(key.getKey())){
                        publicationList.add(key.getValue(Publication.class));
                        //Verificar posicion de insercion, el cliente no ha dicho nada pero se puede organizar
                        //Segun sus paramentros
                        adapter = new Adapter_Favorites(publicationList, getActivity());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean searchPublicationById(String id_publication) {
        for (int i = 0; i < publicationList.size(); i++) {
            if (publicationList.get(i).getId().equals(id_publication)){
                return true;
            }
        }
        return false;
    }

}
