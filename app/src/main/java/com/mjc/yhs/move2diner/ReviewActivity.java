package com.mjc.yhs.move2diner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mjc.yhs.move2diner.DTO.ReviewListItem;

import java.util.ArrayList;

import static com.mjc.yhs.move2diner.MainActivity.secondDatabase;

public class ReviewActivity extends AppCompatActivity {


    RecyclerViewEmptySupport recycler_review;
    ArrayList<ReviewListItem> reviewListItems = new ArrayList<>();
    ArrayList<String> reviewKeys = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_review);
        new CustomTitlebar(this, "리뷰 보기");

        recycler_review = (RecyclerViewEmptySupport) findViewById(R.id.recycle_review);

        final ReviewListAdapter reviewListAdapter = new ReviewListAdapter(ReviewActivity.this, reviewListItems, reviewKeys);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        recycler_review.setLayoutManager(manager);
        recycler_review.setEmptyView(findViewById(R.id.tv_noReviewConmment));

        recycler_review.setAdapter(reviewListAdapter);

        //데이터 찾기로직은 설명으로 한다
        Query reviewQuery = secondDatabase.getReference().child("reviews");
        reviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewListItems.clear();
                reviewKeys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(auth.getCurrentUser().getUid()).getValue() == null)
                        continue;
                    reviewListItems.add(snapshot.child(auth.getCurrentUser().getUid()).getValue(ReviewListItem.class));
                    reviewKeys.add(snapshot.child(auth.getCurrentUser().getUid()).getKey());
                }

                reviewListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
