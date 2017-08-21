package com.swerly.wifiheatmap;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Seth on 8/20/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HeatmapViewHolder> {
    ArrayList<HeatmapData> heatmapList;

    public HomeAdapter(){
    }

    @Override
    public HeatmapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_heatmap, parent, false);
        HeatmapViewHolder heatmapViewHolder = new HeatmapViewHolder(v);

        //todo: click listeners for the buttons

        return heatmapViewHolder;
    }

    @Override
    public void onBindViewHolder(HeatmapViewHolder holder, int position) {
        HeatmapData curItem = heatmapList.get(position);
        //check if this is null(will be for an in-progress heatmap)
        holder.cardImage.setImageBitmap(curItem.getFinishedImage());
        holder.name.setText(curItem.getName());
        holder.dateTime.setText(curItem.getDateTimeString());
    }

    @Override
    public int getItemCount() {
        return heatmapList == null ? 0 : heatmapList.size();
    }

    public void setNewData(ArrayList<HeatmapData> newList){
        //should switch to notifyItemInserted at some point if I want animations...
        heatmapList = newList;
        this.notifyDataSetChanged();
    }

    public static class HeatmapViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView cardImage;
        TextView name;
        TextView dateTime;
        ImageButton deleteButton;
        ImageButton editButton;
        ImageButton shareButton;
        ImageButton viewButton;

        public HeatmapViewHolder(View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            cardImage = itemView.findViewById(R.id.card_image_view);
            name = itemView.findViewById(R.id.title_text_view);
            dateTime = itemView.findViewById(R.id.subtitle_text_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
            shareButton = itemView.findViewById(R.id.share_button);
            viewButton = itemView.findViewById(R.id.view_button);
        }
    }
}
