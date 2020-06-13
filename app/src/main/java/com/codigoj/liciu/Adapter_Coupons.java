package com.codigoj.liciu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codigoj.liciu.data.AppPreferences;
import com.codigoj.liciu.modelo.Coupon;
import com.codigoj.liciu.modelo.Publication;
import com.codigoj.liciu.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jhon Martinez on 19/07/17.
 */

public class Adapter_Coupons extends RecyclerView.Adapter<Adapter_Coupons.ViewHolder> {

    private List<Publication> publicationList;
    private Map<String, Coupon> couponsList;
    private Context context;
    private String id_user;
    private FirebaseDatabase database;

    public Adapter_Coupons(List<Publication> publications, Context c, String id_user, FirebaseDatabase database, Map<String, Coupon> couponsList) {
        this.context = c;
        this.publicationList = publications;
        this.id_user = id_user;
        this.database = database;
        this.couponsList = couponsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_coupon_publication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.dateStart.setText(publicationList.get(position).getDate_start());
        holder.description.setText(publicationList.get(position).getDescription());
        holder.name.setText(publicationList.get(position).getName());
        Picasso.with(context).load(publicationList.get(position).getPath_image_pub()).fit().centerCrop().into(holder.image);
        final Publication pub = publicationList.get(position);
        holder.btn_charge_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("INFORMACIÓN")
                        .setMessage(R.string.msg_confirm_charge_coupon)
                        .setPositiveButton("Cobrar cupón",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //search the coupon
                                        Coupon c = couponsList.get(publicationList.get(position).getId());
                                        c.setType(Coupon.REDEEMED);

                                        //Redeemed the coupon, change the state to "redeemed"
                                        //Update the first reference
                                        database.getReference().child(Utils.REF_PUBLICATIONS).child(pub.getId()).child(Utils.REF_COUPONS).child(c.getId_coupon()).setValue(c);
                                        //Update the second reference
                                        database.getReference().child(Utils.REF_USERS_COUPONS).child(id_user).child(pub.getId()).setValue(c);
                                        String code = c.getId_coupon().substring(c.getId_coupon().length()-8,c.getId_coupon().length());
                                        //Send notification
                                        sendNotification(pub.getId());
                                        //Show message dialog
                                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                                        builder2.setTitle(code)
                                                .setMessage(R.string.msg_charge_coupon)
                                                .setPositiveButton("Aceptar",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //Coupon redemeed
                                                            }
                                                        });
                                        builder2.create();
                                        builder2.show();
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                builder.create();
                builder.show();
            }
        });

        //load the typeface
        Typeface berlinSansFB= Typeface.createFromAsset(context.getAssets(),"fonts/BRLNSR.TTF");
        holder.name.setTypeface(berlinSansFB);
        holder.description.setTypeface(berlinSansFB);
        holder.dateStart.setTypeface(berlinSansFB);
        holder.btn_charge_coupon.setTypeface(berlinSansFB);
    }

    /**
     * This method publish a new notification request, the server take the request and send the notification
     * to the company that belongs to publication
     */
    public void sendNotification(String id_publication) {

        //Search the company by the id_publication
        Query search = database.getReference().child(Utils.REF_SEARCH).child(id_publication)
                .child(Utils.REF_ID_COMPANY);
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("results",dataSnapshot.toString());
                    if (dataSnapshot.exists()){
                        //Get the company's id and send the notification
                        String id_company = dataSnapshot.getValue(String.class);
                        DatabaseReference myRef = database.getReference().child("notification_request_user");
                        String idNotification = myRef.push().getKey();

                        Map<String, Object> childUpdates = new HashMap<>();
                        HashMap<String, Object> notification = new HashMap<>();
                        //Data for the notification
                        notification.put("id_company", id_company);
                        notification.put("message","Un usuario ha redimido un cupón");
                        notification.put("title","Lici");
                        childUpdates.put("/"+idNotification, notification);
                        myRef.updateChildren(childUpdates);
                    }

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("results",databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicationList.size();
    }

    public void update(List<Publication> publicationList, Map<String, Coupon> couponsList) {
        this.couponsList = couponsList;
        this.publicationList = publicationList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView name;
        TextView dateStart;
        ImageView image;
        TextView description;
        Button btn_charge_coupon;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) itemView.findViewById(R.id.card);
            name = (TextView) itemView.findViewById(R.id.cardName);
            image = (ImageView) itemView.findViewById(R.id.cardImage);
            description = (TextView) itemView.findViewById(R.id.cardDescription);
            dateStart = (TextView) itemView.findViewById(R.id.cardDate);
            btn_charge_coupon = (Button) itemView.findViewById(R.id.btn_charge_coupon);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
