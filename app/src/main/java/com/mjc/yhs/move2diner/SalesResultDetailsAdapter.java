package com.mjc.yhs.move2diner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mjc.yhs.move2diner.DTO.MenuListItem;

import java.util.ArrayList;

/**
 * Created by Kang on 2017-12-17.
 */

public class SalesResultDetailsAdapter extends RecyclerView.Adapter<SalesResultDetailsAdapter.MyViewHolder> {
    private Context c;
    private ArrayList<MenuListItem> properties;

    public SalesResultDetailsAdapter(Context c, ArrayList<MenuListItem> properties) {
        this.c = c;
        this.properties = properties;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_pos_result, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_foodName.setText(properties.get(position).getFoodName());
        holder.tv_foodEA.setText(properties.get(position).getFoodEA());
        holder.tv_foodPrice.setText(properties.get(position).getFoodPrice());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_foodName, tv_foodEA, tv_foodPrice;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_foodName = (TextView) itemView.findViewById(R.id.tv_foodName);
            tv_foodEA = (TextView) itemView.findViewById(R.id.tv_foodEA);
            tv_foodPrice = (TextView) itemView.findViewById(R.id.tv_foodPrice);
        }
    }
}