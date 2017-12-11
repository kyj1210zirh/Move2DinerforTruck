package com.mjc.yhs.move2diner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.mjc.yhs.move2diner.DTO.ReviewListItem;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.mjc.yhs.move2diner.MainActivity.secondDatabase;


public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ItemHolder> {

    private ArrayList<ReviewListItem> reviewListItems = new ArrayList<ReviewListItem>();
    private ArrayList<String> reviewKeys = new ArrayList<>();
    private Context context;

    ReviewListAdapter(Context context, ArrayList<ReviewListItem> reviewListItems, ArrayList<String> reviewKeys) {
        this.context = context;
        this.reviewListItems = reviewListItems;
        this.reviewKeys = reviewKeys;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_review, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {

        Glide.with(context).load(reviewListItems.get(position).getThumbnail()).placeholder(R.drawable.loadingimage).error(R.drawable.loadingimage)
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(holder.review_row_thumbnail);
        holder.review_row_nick.setText(reviewListItems.get(position).getUserNick());
        holder.review_row_time.setText(reviewListItems.get(position).getReviewTime());
        holder.review_row_content.setText(reviewListItems.get(position).getContent());
        holder.btnDeleteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                        .setMessage("정말 사용자의 리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteReview(position);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewListItems.size();
    }

    public void deleteReview(final int position) {

        secondDatabase.getReference().child("reviews").child(reviewListItems.get(position).getUserId()).child(reviewKeys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                notifyDataSetChanged();
            }
        });
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView review_row_nick, review_row_content, review_row_time, btnDeleteReview;
        ImageView review_row_thumbnail;

        public ItemHolder(View itemView) {
            super(itemView);
            review_row_nick = (TextView) itemView.findViewById(R.id.review_row_nick);
            review_row_content = (TextView) itemView.findViewById(R.id.review_row_content);
            review_row_time = (TextView) itemView.findViewById(R.id.review_row_time);
            btnDeleteReview = (TextView) itemView.findViewById(R.id.btnDeleteReview);
            review_row_thumbnail = (ImageView) itemView.findViewById(R.id.review_row_thumbnail);
        }
    }
}
