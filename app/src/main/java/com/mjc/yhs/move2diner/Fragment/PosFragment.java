package com.mjc.yhs.move2diner.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.DTO.PosMenuListItem;
import com.mjc.yhs.move2diner.FoodListAdapter;
import com.mjc.yhs.move2diner.DTO.MenuListItem;
import com.mjc.yhs.move2diner.FragmentTab;
import com.mjc.yhs.move2diner.R;
import com.mjc.yhs.move2diner.RecyclerViewEmptySupport;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Kang on 2017-12-10.
 */

@SuppressLint("ValidFragment")
public class PosFragment extends Fragment implements FoodListAdapter.FoodListListener, View.OnClickListener {
    private RecyclerViewEmptySupport rvFoodList;
    private ArrayList<PosMenuListItem> properties;
    public HashMap<String, PosMenuListItem> selectedItem;
    private FoodListAdapter foodListAdapter;

    private Button tv_totalPrice;
    private int totalPrice, OrderPrice;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference pushid;

    public PosFragment() {
    }

    public static PosFragment newInstance() {
        return new PosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_pos, container, false);
        totalPrice = 0;
        OrderPrice = 0;
        tv_totalPrice = (Button) rootView.findViewById(R.id.tv_totalPrice);
        rootView.findViewById(R.id.btn_cancle).setOnClickListener(this);
        rootView.findViewById(R.id.btn_confirm).setOnClickListener(this);

        rvFoodList = (RecyclerViewEmptySupport) rootView.findViewById(R.id.rvFoodList);
        rvFoodList.setEmptyView(rootView.findViewById(R.id.tv_noMenu));
        properties = new ArrayList<>();
        selectedItem = new HashMap<>();
        foodListAdapter = new FoodListAdapter(getContext(), properties);
        foodListAdapter.registerFoodListListener(this);
        rvFoodList.setAdapter(foodListAdapter);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        rvFoodList.setLayoutManager(glm);

        mDatabase.child("trucks").child("menu").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                properties.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PosMenuListItem posMenuListItem = snapshot.getValue(PosMenuListItem.class);
                    posMenuListItem.setFoodID(snapshot.getKey());
                    properties.add(posMenuListItem);
                }
                foodListAdapter.notifyDataSetChanged();
                GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
                if (foodListAdapter.getItemCount() < 5)
                    glm.setSpanCount(2);
                else if (foodListAdapter.getItemCount() < 10)
                    glm.setSpanCount(3);
                else
                    glm.setSpanCount(4);
                rvFoodList.setLayoutManager(glm);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(int price, int position) {
        OrderPrice += price;
        tv_totalPrice.setText(NumberFormat.getCurrencyInstance().format(OrderPrice));
        selectedItem.put(properties.get(position).getFoodID(), properties.get(position));
        System.out.println(selectedItem.get(properties.get(position).getFoodID()).getFoodEA());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                foodListAdapter.notifyItemRangeChanged(0, properties.size());
                OrderPrice = 0;
                tv_totalPrice.setText("주문 누적액 : " + NumberFormat.getCurrencyInstance().format(totalPrice));
                break;
            case R.id.btn_confirm:
                if (OrderPrice == 0) {
                    Toast.makeText(getContext(), "1개 이상의 메뉴가 선택되어야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //정상적인 판매완료 로직
                    foodListAdapter.notifyItemRangeChanged(0, properties.size());
                    pushid = ((FragmentTab) getActivity()).databaseReference.child("sell");
                    for (final String key : selectedItem.keySet()) {
                        final int EA = selectedItem.get(key).getFoodEA();
                        pushid.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    System.out.println("아이템 존재함" + EA);
                                    dataSnapshot.child("foodEA").getRef().setValue(Integer.parseInt(dataSnapshot.child("foodEA").getValue().toString()) + EA);
                                } else {
                                    dataSnapshot.child("foodName").getRef().setValue(selectedItem.get(key).getFoodName());
                                    dataSnapshot.child("foodPrice").getRef().setValue(selectedItem.get(key).getFoodPrice());
                                    dataSnapshot.child("foodEA").getRef().setValue(EA);
                                }
                                selectedItem.remove(key);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    totalPrice += OrderPrice;
                    OrderPrice = 0;
                    tv_totalPrice.setText("주문 누적액 : " + NumberFormat.getCurrencyInstance().format(totalPrice));
                }
                break;
        }
    }
}
