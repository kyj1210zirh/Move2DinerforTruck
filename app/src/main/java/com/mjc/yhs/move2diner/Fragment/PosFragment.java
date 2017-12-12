package com.mjc.yhs.move2diner.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.mjc.yhs.move2diner.FoodListAdapter;
import com.mjc.yhs.move2diner.DTO.MenuListItem;
import com.mjc.yhs.move2diner.PosResultAdapter;
import com.mjc.yhs.move2diner.R;
import com.mjc.yhs.move2diner.RecyclerViewEmptySupport;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by Kang on 2017-12-10.
 */

@SuppressLint("ValidFragment")
public class PosFragment extends Fragment implements FoodListAdapter.FoodListListener, View.OnClickListener {
    private ConstraintLayout ConstraintLayout1, ConstraintLayout2;
    private ImageView iv_male, iv_female;
    private SegmentedGroup segmentedGroup;
    private PosResultAdapter posResultAdapter;

    private RecyclerViewEmptySupport rvFoodList, rvPOSResult;
    private ArrayList<MenuListItem> properties, selectProperties;
    private FoodListAdapter foodListAdapter;

    private Button tv_totalPrice, btn_cancle, btn_confirm;
    private int totalPrice;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

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
        tv_totalPrice = (Button) rootView.findViewById(R.id.tv_totalPrice);
        rootView.findViewById(R.id.btn_cancle).setOnClickListener(this);
        btn_confirm = (Button) rootView.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        ConstraintLayout1 = (ConstraintLayout)rootView.findViewById(R.id.ConstraintLayout1);
//        ConstraintLayout2 = (ConstraintLayout)rootView.findViewById(R.id.ConstraintLayout2);

        //ConstraintLayout1
        rvFoodList = (RecyclerViewEmptySupport) rootView.findViewById(R.id.rvFoodList);
        rvFoodList.setEmptyView(rootView.findViewById(R.id.tv_noMenu));
        properties = new ArrayList<>();
        foodListAdapter = new FoodListAdapter(getContext(), properties);
        foodListAdapter.registerFoodListListener(this);
        rvFoodList.setAdapter(foodListAdapter);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        rvFoodList.setLayoutManager(glm);

        //ConstraintLayout2
//        iv_male = (ImageView) rootView.findViewById(R.id.iv_male);
//        iv_male.setOnClickListener(this);
//        iv_female = (ImageView) rootView.findViewById(R.id.iv_female);
//        iv_female.setOnClickListener(this);
//        segmentedGroup = (SegmentedGroup)rootView.findViewById(R.id.segmentedGroup);
//        segmentedGroup.setTintColor(Color.parseColor("#2c3e50"));
//        selectProperties = new ArrayList<>();
//        rvPOSResult = (RecyclerViewEmptySupport) rootView.findViewById(R.id.rvPOSResult);
//        posResultAdapter = new PosResultAdapter(getContext(), selectProperties);
//        rvPOSResult.setAdapter(posResultAdapter);
//        LinearLayoutManager llm = new LinearLayoutManager(getContext());
//        rvPOSResult.setLayoutManager(llm);

        mDatabase.child("trucks").child("menu").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                properties.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuListItem menuListItem = snapshot.getValue(MenuListItem.class);
                    properties.add(menuListItem);
                }
                foodListAdapter.notifyDataSetChanged();
                GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
                if(foodListAdapter.getItemCount()<7)
                    glm.setSpanCount(2);
                else if(foodListAdapter.getItemCount()<13)
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
    public void onItemClick(int price, int position, int cnt) {
        totalPrice += price;
//        selectProperties.add(position, properties.get(position));
        tv_totalPrice.setText(NumberFormat.getCurrencyInstance().format(totalPrice));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                foodListAdapter.notifyItemRangeChanged(0, properties.size());
//                ConstraintLayout2.setVisibility(View.GONE);
//                ConstraintLayout1.setVisibility(View.VISIBLE);
                totalPrice = 0;
                tv_totalPrice.setText("금액");
//                btn_next.setText("다음 >");
                break;
            case R.id.btn_confirm:

//                if(totalPrice==0){
//                    Toast.makeText(getContext(), "1개 이상의 메뉴가 선택되어야 합니다.", Toast.LENGTH_SHORT).show();
//                }else {
//                    ConstraintLayout1.setVisibility(View.GONE);
//                    ConstraintLayout2.setVisibility(View.VISIBLE);
//                    btn_confirm.setText("확인");
//                }
                break;
//            case R.id.iv_male:
//                iv_male.setImageResource(R.drawable.icons8_businessman_check_96);
//                iv_female.setImageResource(R.drawable.icons8_businesswoman_96);
//                break;
//            case R.id.iv_female:
//                iv_female.setImageResource(R.drawable.icons8_businesswoman_check_96);
//                iv_male.setImageResource(R.drawable.icons8_businessman_96);
//                break;

        }
    }


}
