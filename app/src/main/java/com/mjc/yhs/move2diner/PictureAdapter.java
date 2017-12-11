package com.mjc.yhs.move2diner;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mjc.yhs.move2diner.DTO.PictureDTO;

import java.util.ArrayList;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ItemHolder> {

    private ArrayList<PictureDTO> pictures;
    private Context context;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    public PictureAdapter(ArrayList<PictureDTO> pictures, Context context) {
        this.pictures = pictures;
        this.context = context;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_picture, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        final int p = position;
        if (pictures.get(position).getStoragePath() != null) {
            Glide.with(context).load(pictures.get(position).getStoragePath()).placeholder(R.drawable.loadingimage).override(100,100).fitCenter().into(holder.img_added_picture);
        }
        holder.img_del_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContent(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView img_added_picture;
        ImageView img_del_picture;

        public ItemHolder(View itemView) {
            super(itemView);
            img_del_picture = (ImageView) itemView.findViewById(R.id.img_del_picture);
            img_added_picture = (ImageView) itemView.findViewById(R.id.img_added_picture);
        }
    }

    public void deleteContent(final int position) {
        final String path = pictures.get(position).getStoragePath();
        mDatabase.child("trucks").child("pictures").child(pictures.get(position).getUserID())
                .child(pictures.get(position).getPushid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mStorage.getReferenceFromUrl(path).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"사진 삭제",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
