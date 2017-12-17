package com.mjc.yhs.move2diner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mjc.yhs.move2diner.DTO.MenuListItem;
import com.mjc.yhs.move2diner.DTO.SalesInfoListItem;

import java.util.ArrayList;

/**
 * Created by Kang on 2017-12-17.
 */

public class SalesResultDetailsActivity extends AppCompatActivity {
    private RecyclerViewEmptySupport rvResult;
    private SalesResultDetailsAdapter salesResultDetailsAdapter;
    private ArrayList<MenuListItem> properties;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sales_result_details);
        new CustomTitlebar(this, "매출 내역 보기");

        rvResult = (RecyclerViewEmptySupport)findViewById(R.id.rvResult);
        properties = new ArrayList<>();
        salesResultDetailsAdapter = new SalesResultDetailsAdapter(this, properties);
        rvResult.setAdapter(salesResultDetailsAdapter);
        rvResult.setLayoutManager(new LinearLayoutManager(this));

        SalesInfoListItem salesInfoListItem = (SalesInfoListItem)getIntent().getSerializableExtra("SalesResultRow");

    }
}
