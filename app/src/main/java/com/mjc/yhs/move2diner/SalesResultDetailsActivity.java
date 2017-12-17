package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.DTO.MenuListItem;
import com.mjc.yhs.move2diner.DTO.PosMenuListItem;
import com.mjc.yhs.move2diner.DTO.SalesInfoListItem;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Kang on 2017-12-17.
 */

public class SalesResultDetailsActivity extends AppCompatActivity {
    private RecyclerViewEmptySupport rvDetailResult;
    private SalesResultDetailsAdapter salesResultDetailsAdapter;
    private ArrayList<PosMenuListItem> properties;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();

    private TextView totalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sales_result_details);
        new CustomTitlebar(this, "판매 내역 보기");

        totalPrice = (TextView) findViewById(R.id.tv_totalPrice);

        rvDetailResult = (RecyclerViewEmptySupport)findViewById(R.id.rvDetailResult);
        rvDetailResult.setEmptyView(findViewById(R.id.tv_Empty));
        properties = new ArrayList<PosMenuListItem>();
        salesResultDetailsAdapter = new SalesResultDetailsAdapter(this, properties);
        rvDetailResult.setAdapter(salesResultDetailsAdapter);
        rvDetailResult.setLayoutManager(new LinearLayoutManager(this));

        mDatabase.child("trucks").child("salessituation").child(user.getUid()).child(getIntent().getStringExtra("SalesResultKey")).child("sell").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                properties.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    PosMenuListItem posMenuListItem = snapshot.getValue(PosMenuListItem.class);
                    properties.add(posMenuListItem);
                    total += posMenuListItem.getFoodPrice();
                }
                salesResultDetailsAdapter.notifyDataSetChanged();
                totalPrice.setText(NumberFormat.getCurrencyInstance().format(total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
