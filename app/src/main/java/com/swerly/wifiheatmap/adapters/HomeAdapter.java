package com.swerly.wifiheatmap.adapters;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.views.HeatmapViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seth on 8/20/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HeatmapViewHolder> {
    private List<HeatmapData> items;
    private HeatmapViewHolder.HeatmapCardListener listener;

    public HomeAdapter(HeatmapViewHolder.HeatmapCardListener listener){
        this.listener = listener;
        items = new ArrayList<>();
    }

    @Override
    public HeatmapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_heatmap, parent, false);
        HeatmapViewHolder heatmapViewHolder = new HeatmapViewHolder(listener, v);

        return heatmapViewHolder;
    }

    @Override
    public void onBindViewHolder(HeatmapViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateItems(final List<HeatmapData> newItems) {
        //TODO: SORT newItems HERE
        final List<HeatmapData> oldItems = new ArrayList<>(this.items);
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldItems.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }
        }).dispatchUpdatesTo(this);

    }

}
