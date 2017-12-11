package com.mjc.yhs.move2diner.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.mjc.yhs.move2diner.R;
import com.mjc.yhs.move2diner.SalesResultAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SalesResultFragment extends Fragment implements View.OnClickListener {
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

    public static SalesResultFragment newInstance() {
        return new SalesResultFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_sales_result, container, false);

        rvResult = (RecyclerView) rootView.findViewById(R.id.rvResult);
        properties = new ArrayList<SalesInfoListItem>();
        uidKeys = new ArrayList<String>();
        salesResultAdapter = new SalesResultAdapter(getContext(), properties, uidKeys);
        rvResult.setAdapter(salesResultAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        rvResult.setLayoutManager(llm);

        rootView.findViewById(R.id.btnAll).setOnClickListener(this);
        rootView.findViewById(R.id.btnLastSeven).setOnClickListener(this);
        rootView.findViewById(R.id.btnLastfifteen).setOnClickListener(this);
        rootView.findViewById(R.id.btnSelectDate).setOnClickListener(this);
        btnStartDate = (Button) rootView.findViewById(R.id.btnStartDate);
        btnStartDate.setOnClickListener(this);
        btnEndDate = (Button) rootView.findViewById(R.id.btnEndDate);
        btnEndDate.setOnClickListener(this);
        rootView.findViewById(R.id.btnDateSearch).setOnClickListener(this);

        onClick(rootView.findViewById(R.id.btnLastSeven));

        return rootView;
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
                if (getView().findViewById(R.id.llSelectDate).getVisibility() == View.GONE) {
                    getView().findViewById(R.id.llSelectDate).setVisibility(View.VISIBLE);
                } else {
                    getView().findViewById(R.id.llSelectDate).setVisibility(View.GONE);
                }
                break;
            case R.id.btnStartDate:
                Calendar cal = Calendar.getInstance();
                DatePickerDialog startdlg = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
                    DatePickerDialog enddlg = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
                    Toast.makeText(getContext(), "시작일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
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
