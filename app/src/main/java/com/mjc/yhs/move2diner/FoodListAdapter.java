package com.mjc.yhs.move2diner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mjc.yhs.move2diner.DTO.MenuListItem;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Kang on 2017-12-10.
 */

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder>{
    Context c;
    ArrayList<MenuListItem> properties;
    FoodListListener foodListListener;

    public FoodListAdapter(Context c, ArrayList<MenuListItem> properties) {
        this.c = c;
        this.properties = properties;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_food_view, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MenuListItem menuListItem = properties.get(position);
        Glide.with(c).load(menuListItem.getFoodStoragePath()).placeholder(R.drawable.lunch_box).error(R.drawable.lunch_box).into(holder.iv_foodImage);
        holder.tv_foodName.setText(menuListItem.getFoodName());
        holder.tv_foodPrice.setText(NumberFormat.getCurrencyInstance().format(menuListItem.getFoodPrice()));
        holder.tv_foodPrice.setHint(String.valueOf(properties.get(position).getFoodPrice()));
        holder.cl_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tv_foodName.bringToFront();
                holder.tv_foodPrice.bringToFront();
                holder.tv_foodName.setTextColor(Color.WHITE);
                holder.tv_foodPrice.setTextColor(Color.WHITE);
                if(holder.tv_itemCount.getVisibility()==View.GONE)
                    holder.tv_itemCount.setVisibility(View.VISIBLE);
                int cnt = Integer.parseInt(holder.tv_itemCount.getText().toString())+1;
                holder.tv_itemCount.setText(String.valueOf(cnt));

                int price = Integer.parseInt(holder.tv_foodPrice.getHint().toString());
                System.out.println("눌린 아이템 " + position);
                foodListListener.onItemClick(price, position);
            }
        });
    }

    public interface FoodListListener{
        void onItemClick(int price, int position);
    }

    public void registerFoodListListener(FoodListListener foodListListener){
        this.foodListListener = foodListListener;
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_foodImage;
        TextView tv_foodName, tv_foodPrice, tv_itemCount;
        ConstraintLayout cl_food;

        public MyViewHolder(View itemView) {
            super(itemView);
            cl_food = (ConstraintLayout)itemView.findViewById(R.id.cl_food);
            iv_foodImage = (ImageView)itemView.findViewById(R.id.iv_foodImage);
            tv_foodName = (TextView)itemView.findViewById(R.id.tv_foodName);
            tv_foodPrice = (TextView)itemView.findViewById(R.id.tv_foodPrice);
            tv_itemCount = (TextView)itemView.findViewById(R.id.tv_itemCount);
        }
    }
}
