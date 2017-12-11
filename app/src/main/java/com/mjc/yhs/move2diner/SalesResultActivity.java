package com.mjc.yhs.move2diner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.DTO.SalesInfoListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SalesResultActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rvResult;
    SalesResultAdapter salesResultAdapter;
    ArrayList<SalesInfoListItem> properties;
    ArrayList<String> uidKeys;
    Query situationQuery;
    Button btnStartDate, btnEndDate;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();
    private long limitdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sales_result);

        rvResult = (RecyclerView) findViewById(R.id.rvResult);
        properties = new ArrayList<SalesInfoListItem>();
        uidKeys = new ArrayList<String>();
        salesResultAdapter = new SalesResultAdapter(this, properties, uidKeys);
        rvResult.setAdapter(salesResultAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        rvResult.setLayoutManager(llm);

        findViewById(R.id.btnAll).setOnClickListener(this);
        findViewById(R.id.btnLastSeven).setOnClickListener(this);
        findViewById(R.id.btnLastfifteen).setOnClickListener(this);
        findViewById(R.id.btnSelectDate).setOnClickListener(this);
        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnStartDate.setOnClickListener(this);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnEndDate.setOnClickListener(this);
        findViewById(R.id.btnDateSearch).setOnClickListener(this);

        onClick(findViewById(R.id.btnLastSeven));
    }

    public void onClick(View v) {
        String uid = user.getUid();
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uidKeys.clear();
                properties.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SalesInfoListItem salesInfoListItem = snapshot.getValue(SalesInfoListItem.class);
                    if (salesInfoListItem.getOnBusiness() == true) {
                        continue;
                    }
                    uidKeys.add(snapshot.getKey());
                    properties.add(salesInfoListItem);
                }
                salesResultAdapter.notifyDataSetChanged();
                rvResult.scrollToPosition(salesResultAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        switch (v.getId()) {
            case R.id.btnAll:
                situationQuery = mDatabase.child("trucks").child("salessituation").child(uid).orderByKey();
                situationQuery.removeEventListener(vel);
                situationQuery.addValueEventListener(vel);
                break;
            case R.id.btnLastSeven:
                situationQuery = mDatabase.child("trucks").child("salessituation").child(uid).orderByKey().limitToLast(7);
                situationQuery.removeEventListener(vel);
                situationQuery.addValueEventListener(vel);
                break;
            case R.id.btnLastfifteen:
                situationQuery = mDatabase.child("trucks").child("salessituation").child(uid).orderByKey().limitToLast(15);
                situationQuery.removeEventListener(vel);
                situationQuery.addValueEventListener(vel);
                break;
            case R.id.btnSelectDate:
                if (findViewById(R.id.llSelectDate).getVisibility() == View.GONE) {
                    findViewById(R.id.llSelectDate).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.llSelectDate).setVisibility(View.GONE);
                }
                break;
            case R.id.btnStartDate:
                Calendar cal = Calendar.getInstance();
                DatePickerDialog startdlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date date = new Date(year - 1900, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        limitdate = date.getTime();
                        btnStartDate.setText(sdf.format(date));
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                startdlg.getDatePicker().setMaxDate(new Date().getTime());
                startdlg.show();
                break;
            case R.id.btnEndDate:
                if (limitdate != 0) {
                    Calendar cal2 = Calendar.getInstance();
                    DatePickerDialog enddlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Date date = new Date(year - 1900, month, dayOfMonth);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            btnEndDate.setText(sdf.format(date));
                        }
                    }, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH));
                    enddlg.getDatePicker().setMaxDate(new Date().getTime());
                    enddlg.getDatePicker().setMinDate(limitdate);
                    enddlg.show();
                } else {
                    Toast.makeText(this, "시작일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDateSearch:

                situationQuery = mDatabase.child("trucks").child("salessituation").child(uid).orderByChild("salesdate").startAt(btnStartDate.getText().toString()).endAt(btnEndDate.getText().toString());
                situationQuery.removeEventListener(vel);
                situationQuery.addValueEventListener(vel);
                break;
        }
    }
}
