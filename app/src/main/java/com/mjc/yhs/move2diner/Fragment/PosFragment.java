package com.mjc.yhs.move2diner.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.FoodListAdapter;
import com.mjc.yhs.move2diner.DTO.MenuListItem;
import com.mjc.yhs.move2diner.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Kang on 2017-12-10.
 */

@SuppressLint("ValidFragment")
public class PosFragment extends Fragment implements FoodListAdapter.FoodListListener, View.OnClickListener {
    private RecyclerView rv;
    private List<Integer> postions;
    private ArrayList<MenuListItem> properties;
    private FoodListAdapter foodListAdapter;

    private Button tv_totalPrice;
    private int totalPrice;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public PosFragment() {
    }

    public static PosFragment newInstance() {
        return new PosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_pos, container, false);

        postions = new ArrayList<Integer>();
        rootView.findViewById(R.id.btn_cancle).setOnClickListener(this);
        tv_totalPrice = (Button) rootView.findViewById(R.id.tv_totalPrice);
        rv = (RecyclerView) rootView.findViewById(R.id.rvFoodList);
        properties = new ArrayList<MenuListItem>();
        foodListAdapter = new FoodListAdapter(getContext(), properties);
        foodListAdapter.registerFoodListListener(this);
        rv.setAdapter(foodListAdapter);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        rv.setLayoutManager(glm);

        totalPrice = 0;

        firebaseDatabase.getReference().child("trucks").child("menu").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                properties.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuListItem menuListItem = snapshot.getValue(MenuListItem.class);
                    properties.add(menuListItem);
                }
                foodListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(int price, int position) {
        totalPrice += price;
//        System.out.println("눌린 아이템2 " + position);
        postions.add(position);
        tv_totalPrice.setText(NumberFormat.getCurrencyInstance().format(totalPrice));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
//                firebaseDatabase.getReference().child("trucks").child("menu").child(user.getUid()).addListenerForSingleValueEvent(valueEventListener);
                totalPrice = 0;
                tv_totalPrice.setText("금액");
                for(int i : postions){
                    foodListAdapter.notifyDataSetChanged();
                }
                postions.clear();
                break;
        }
    }
}
