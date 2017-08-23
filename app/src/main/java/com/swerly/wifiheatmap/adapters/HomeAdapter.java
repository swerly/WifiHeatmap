package com.swerly.wifiheatmap.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swerly.wifiheatmap.BaseApplication;
import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.activities.ActivityMain;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.fragments.FragmentView;
import com.swerly.wifiheatmap.utils.ShareBitmap;
import com.swerly.wifiheatmap.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by Seth on 8/20/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HeatmapViewHolder> {
    private ArrayList<HeatmapData> heatmapList;
    private ActivityMain activityMain;

    public HomeAdapter( ActivityMain activityMain){
        this.activityMain = activityMain;
    }

    @Override
    public HeatmapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_heatmap, parent, false);
        HeatmapViewHolder heatmapViewHolder = new HeatmapViewHolder(v);

        return heatmapViewHolder;
    }

    @Override
    public void onBindViewHolder(HeatmapViewHolder holder, int position) {
        HeatmapData curItem = heatmapList.get(position);
        //check if this is null(will be for an in-progress heatmap)
        holder.cardImage.setImageBitmap(curItem.getFinishedImage());
        holder.name.setText(curItem.getName());
        holder.dateTime.setText(curItem.getDateTimeString());

        //shouldn't attach listers in onBind, but can't access individual card elements when getting adapter postion...

        //im probably overlooking something but this shouldn't be too resource intensive. There wont (read: shouldnt)
        //be large amounts of data in this list anyway
        attachListeners(holder, curItem, position);
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

    private void attachListeners(HeatmapViewHolder holder, final HeatmapData curItem, final int position){
        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityMain.getApp().setIndexToView(position);
                activityMain.goToFragment(new FragmentView());
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ShareBitmap(activityMain).execute(curItem.getFinishedImage());
            }
        });
    }

    public static class HeatmapViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView cardImage;
        TextView name;
        TextView dateTime;
        ImageView deleteButton;
        ImageView editButton;
        ImageView shareButton;
        ImageView viewButton;

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
