package com.mjc.yhs.move2diner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mjc.yhs.move2diner.DTO.MenuListItem;

import java.text.NumberFormat;
import java.util.ArrayList;


public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> {
    private Context c;
    ArrayList<MenuListItem> properties;
    ArrayList<String> menuKeys;
    View dialogview;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public MenuListAdapter(Context c, ArrayList<MenuListItem> properties, ArrayList<String> menuKeys) {
        this.c = c;
        this.properties = properties;
        this.menuKeys = menuKeys;
    }

    @Override
    public MenuListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_menu_info, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvRecycleFDName.setText(properties.get(position).getFoodName());
        holder.tvRecycleFDPrice.setText(NumberFormat.getCurrencyInstance().format(properties.get(position).getFoodPrice()));
        holder.tvRecycleFDDescribe.setText(properties.get(position).getFoodDescribe());
        Glide.with(c).load(properties.get(position).getFoodStoragePath()).placeholder(R.drawable.lunch_box).error(R.drawable.lunch_box).into(holder.ivFoodimg);
        holder.btnDeleteResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(c)
                        .setMessage("해당 메뉴를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteContent(position);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        });
                android.support.v7.app.AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        holder.Recyclelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogview = View.inflate(c, R.layout.menu_foodinfo, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(c);
                dlg.setTitle("메뉴 수정");
                dlg.setView(dialogview);
                final EditText txtMenuName = (EditText) dialogview.findViewById(R.id.txtMenuName);
                final EditText txtMenuPrice = (EditText) dialogview.findViewById(R.id.txtMenuPrice);
                final EditText txtMenuDescribe = (EditText) dialogview.findViewById(R.id.txtMenuDescribe);
                txtMenuName.setText(holder.tvRecycleFDName.getText().toString());
                String price = holder.tvRecycleFDPrice.getText().toString();
                price = price.replaceAll(",", "");
                txtMenuPrice.setText(price.substring(1, price.length()));
                txtMenuDescribe.setText(holder.tvRecycleFDDescribe.getText().toString());
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        MenuListItem fdDTO = new MenuListItem(txtMenuName.getText().toString(),
                                txtMenuDescribe.getText().toString(),
                                Integer.parseInt(txtMenuPrice.getText().toString())
                        );

                        mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).child(menuKeys.get(position)).setValue(fdDTO);

                    }
                });
                dlg.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void deleteContent(final int position) {

        mDatabase.child("trucks").child("menu").child(auth.getCurrentUser().getUid()).child(menuKeys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                notifyDataSetChanged();
            }
        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecycleFDName, tvRecycleFDPrice, tvRecycleFDDescribe, btnDeleteResult;
        ImageView ivFoodimg;
        ConstraintLayout Recyclelayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            Recyclelayout = (ConstraintLayout) itemView.findViewById(R.id.Recyclelayout);
            tvRecycleFDName = (TextView) itemView.findViewById(R.id.tvRecycleFDName);
            tvRecycleFDPrice = (TextView) itemView.findViewById(R.id.tvRecycleFDPrice);
            tvRecycleFDDescribe = (TextView) itemView.findViewById(R.id.tvRecycleFDDescribe);
            btnDeleteResult = (TextView) itemView.findViewById(R.id.btnDeleteResult);
            ivFoodimg = (ImageView) itemView.findViewById(R.id.ivFoodimg);
        }
    }
}
