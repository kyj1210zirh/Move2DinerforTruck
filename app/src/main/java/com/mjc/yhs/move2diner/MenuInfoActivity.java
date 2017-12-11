package com.mjc.yhs.move2diner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    RecyclerViewEmptySupport rvFoodInfo;
    ArrayList<MenuListItem> properties = new ArrayList<>();
    ArrayList<String> menuKeys = new ArrayList<>();
    Button btnAddFood;
    MenuListAdapter menuListAdapter;
    View dialogView;
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
                Intent i = new Intent(MenuInfoActivity.this, NewMenuActivity.class);
                startActivity(i);
            }
        });
//        btnAddFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogView = View.inflate(MenuInfoActivity.this, R.layout.menu_foodinfo, null);
//                AlertDialog.Builder dlg = new AlertDialog.Builder(MenuInfoActivity.this);
//                dlg.setTitle("메뉴 추가");
//                dlg.setView(dialogView);
//                dlg.setNegativeButton("취소", null);
//                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText txtMenuName = (EditText) dialogView.findViewById(R.id.txtMenuName);
//                        EditText txtMenuPrice = (EditText) dialogView.findViewById(R.id.txtMenuPrice);
//                        EditText txtMenuDescribe = (EditText) dialogView.findViewById(R.id.txtMenuDescribe);
//
//                        try{
//                            MenuListItem fdDTO = new MenuListItem(txtMenuName.getText().toString(),
//                                    txtMenuDescribe.getText().toString(),
//                                    Integer.parseInt(txtMenuPrice.getText().toString())
//                            );
//
//                            mDatabase.child("trucks").child("menu").child(user.getUid()).push().setValue(fdDTO);
//                        } catch (Exception e){
//                            Log.e("Add Menu Exception ", e.getMessage());
//                            Toast.makeText(getApplicationContext(), "메뉴 추가 실패", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//                dlg.show();
//            }
//        });


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
