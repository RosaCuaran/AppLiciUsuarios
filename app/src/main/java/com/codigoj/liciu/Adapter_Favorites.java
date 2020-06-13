package com.codigoj.liciu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codigoj.liciu.modelo.Publication;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jhon Martinez on 19/07/17.
 */

public class Adapter_Favorites extends RecyclerView.Adapter<Adapter_Favorites.ViewHolder> {

    private List<Publication> publicationList;
    private Context context;

    public Adapter_Favorites(List<Publication> publications, Context c) {
        this.context = c;
        this.publicationList = publications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.publication, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dateStart.setText(publicationList.get(position).getDate_start());
        holder.description.setText(publicationList.get(position).getDescription());
        holder.name.setText(publicationList.get(position).getName());
        Picasso.with(context).load(publicationList.get(position).getPath_image_pub()).fit().centerCrop().into(holder.image);
        final Publication pub = publicationList.get(position);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PublicationActivity.class);
                i.putExtra("id_publication", pub.getId());
                context.startActivity(i);
            }
        });
        //load the typeface
        Typeface berlinSansFB= Typeface.createFromAsset(context.getAssets(),"fonts/BRLNSR.TTF");
        holder.name.setTypeface(berlinSansFB);
        holder.description.setTypeface(berlinSansFB);
        holder.dateStart.setTypeface(berlinSansFB);
    }

    @Override
    public int getItemCount() {
        return publicationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView dateStart;
        ImageView image;
        TextView description;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) itemView.findViewById(R.id.card);
            name = (TextView) itemView.findViewById(R.id.cardName);
            image = (ImageView) itemView.findViewById(R.id.cardImage);
            description = (TextView) itemView.findViewById(R.id.cardDescription);
            dateStart = (TextView) itemView.findViewById(R.id.cardDate);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
