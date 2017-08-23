package com.swerly.wifiheatmap.views;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.HeatmapData;

public class HeatmapViewHolder extends RecyclerView.ViewHolder{
    HeatmapData item;
    CardView cardView;
    ImageView cardImage;
    TextView name;
    TextView dateTime;
    ImageView deleteButton;
    ImageView editButton;
    ImageView shareButton;
    ImageView viewButton;

    public HeatmapViewHolder(final HeatmapCardListener listener, View itemView){
        super(itemView);
        cardView = itemView.findViewById(R.id.card_view);
        cardImage = itemView.findViewById(R.id.card_image_view);
        name = itemView.findViewById(R.id.title_text_view);
        dateTime = itemView.findViewById(R.id.subtitle_text_view);
        deleteButton = itemView.findViewById(R.id.delete_button);
        editButton = itemView.findViewById(R.id.edit_button);
        shareButton = itemView.findViewById(R.id.share_button);
        viewButton = itemView.findViewById(R.id.view_button);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onViewPressed(item);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onViewPressed(item);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSharePressed(item);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeletePressed(item);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDeletePressed(item);
            }
        });
    }

    public void setItem(HeatmapData item){
        this.item = item;
        //check if this is null(will be for an in-progress heatmap)
        cardImage.setImageBitmap(item.getFinishedImage());
        name.setText(item.getName());
        dateTime.setText(item.getDateTimeString());
    }

    public interface HeatmapCardListener{
        void onViewPressed(HeatmapData item);
        void onSharePressed(HeatmapData item);
        void onEditPressed(HeatmapData item);
        void onDeletePressed(HeatmapData item);
    }
}