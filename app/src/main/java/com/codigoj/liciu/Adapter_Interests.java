package com.codigoj.liciu;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codigoj.liciu.modelo.Publication;

import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class Adapter_Interests extends RecyclerView.Adapter<Adapter_Interests.ViewHolder> {

    public Adapter_Interests(List<Publication> publicationList) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
