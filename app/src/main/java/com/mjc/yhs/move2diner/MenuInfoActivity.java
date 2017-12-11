package com.mjc.yhs.move2diner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.DTO.MenuListItem;

import java.util.ArrayList;


public class MenuInfoActivity extends AppCompatActivity {
    private RecyclerViewEmptySupport rvFoodInfo;
    private ArrayList<MenuListItem> properties = new ArrayList<>();
    private ArrayList<String> menuKeys = new ArrayList<>();
    private Button btnAddFood;
    private MenuListAdapter menuListAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menuinfo);
        new CustomTitlebar(this, "내 메뉴 보기");

        btnAddFood = (Button) findViewById(R.id.btnAddFood);
        rvFoodInfo = (RecyclerViewEmptySupport) findViewById(R.id.rvFoodInfo);
        menuListAdapter = new MenuListAdapter(MenuInfoActivity.this, properties, menuKeys);
        rvFoodInfo.setAdapter(menuListAdapter);
        rvFoodInfo.setEmptyView(findViewById(R.id.empty_menu));
        LinearLayoutManager llm = new LinearLayoutManager(MenuInfoActivity.this);
        rvFoodInfo.setLayoutManager(llm);

        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuInfoActivity.this, DMMenuActivity.class);
                startActivity(i);
            }
        });

        mDatabase.child("trucks").child("menu").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                properties.clear();
                menuKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuListItem menuListItem = snapshot.getValue(MenuListItem.class);
                    menuKeys.add(snapshot.getKey());
                    properties.add(menuListItem);
                }
                menuListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
