/*
 * Copyright (c) 2017 Seth Werly.
 *
 * This file is part of WifiHeatmap.
 *
 *     WifiHeatmap is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     WifiHeatmap is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with WifiHeatmap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.swerly.wifiheatmap.adapters;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swerly.wifiheatmap.R;
import com.swerly.wifiheatmap.data.HeatmapData;
import com.swerly.wifiheatmap.views.HeatmapDataViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Seth on 8/20/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HeatmapDataViewHolder> {
    private List<HeatmapData> items;
    private HeatmapDataViewHolder.HeatmapCardListener listener;
    private Comparator<HeatmapData> comparatorToUse;

    public HomeAdapter(HeatmapDataViewHolder.HeatmapCardListener listener){
        this.listener = listener;
        items = new ArrayList<>();
        comparatorToUse = HeatmapData.getComparator(HeatmapData.HeatmapDataComparator.NAME_SORT);
    }

    @Override
    public HeatmapDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_heatmap, parent, false);
        HeatmapDataViewHolder heatmapDataViewHolder = new HeatmapDataViewHolder(listener, v);

        return heatmapDataViewHolder;
    }

    @Override
    public void onBindViewHolder(HeatmapDataViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateItems(final List<HeatmapData> newItems, Comparator<HeatmapData> comparator) {
        if (comparator == comparatorToUse){
            return;
        }

        final List<HeatmapData> oldItems = new ArrayList<>(this.items);
        this.items.clear();

        if (newItems != null) {
            this.items.addAll(newItems);
        }

        if (comparator != null) {
            comparatorToUse = comparator;
        }

        Collections.sort(items, comparatorToUse);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
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
        });
        diffResult.dispatchUpdatesTo(this);

    }

}
