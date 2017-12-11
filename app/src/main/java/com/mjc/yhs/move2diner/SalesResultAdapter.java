package com.mjc.yhs.move2diner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mjc.yhs.move2diner.DTO.SalesInfoListItem;

import java.text.NumberFormat;
import java.util.ArrayList;


public class SalesResultAdapter extends RecyclerView.Adapter<SalesResultAdapter.MyViewHolder> {
    Context c;
    ArrayList<SalesInfoListItem> properties;
    ArrayList<String> keys;
    View dialogView;

    FirebaseAuth auth=FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public SalesResultAdapter(Context c, ArrayList<SalesInfoListItem> properties, ArrayList<String> keys){
        this.c=c;
        this.properties=properties;
        this.keys=keys;
    }
    @Override
    public SalesResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_sales_result, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.salesLocation.setText(properties.get(position).getAddressLine());
        holder.salesDate.setText(properties.get(position).getSalesdate());
        holder.salesTime.setText(properties.get(position).getStarttime().substring(0,5) + "~");
        if(properties.get(position).getEndtime() != null && properties.get(position).getEndtime().length() != 0)
            holder.salesTime.setText(properties.get(position).getStarttime().substring(0,5) + "~" + properties.get(position).getEndtime().substring(0,5));
        holder.salesMemo.setText(properties.get(position).getMemo());
        holder.salesAccount.setText(properties.get(position).getSalesAccount());
        if(properties.get(position).getSalesAccount() != null || properties.get(position).getMemo() != null){
            holder.salesMemo.setVisibility(View.VISIBLE);
            holder.salesAccount.setVisibility(View.VISIBLE);
            holder.editReuslt.setVisibility(View.GONE);
        }
        holder.ll_editResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = View.inflate(c,R.layout.menu_sales_result,null);
                AlertDialog.Builder dlg=new AlertDialog.Builder(c);
                dlg.setTitle("매출 정보 입력");
                dlg.setView(dialogView);
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference pushid;
                        EditText editMemo=(EditText)dialogView.findViewById(R.id.editMemo);
                        EditText editResult=(EditText)dialogView.findViewById(R.id.editResult);

                        String memo,result;
                        memo=editMemo.getText().toString();
                        result=editResult.getText().toString();

                        if(TextUtils.isEmpty(memo) || TextUtils.isEmpty(result)){
                            Toast.makeText(c, "항목을 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        pushid = mDatabase.child("trucks").child("salessituation").child(properties.get(position).getTruckUid()).child(keys.get(position));
                        pushid.child("memo").setValue(editMemo.getText().toString());
                        pushid.child("salesAccount").setValue(NumberFormat.getCurrencyInstance().format(Integer.parseInt(editResult.getText().toString())));

                        holder.salesMemo.setVisibility(View.VISIBLE);
                        holder.salesAccount.setVisibility(View.VISIBLE);
                        holder.editReuslt.setVisibility(View.GONE);
                    }
                });
                dlg.show();
            }
        });

        holder.btnDeleteResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnDeleteResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(c)
                                .setMessage("정말 해당 영업정보를 삭제하시겠습니까?")
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView salesLocation, salesMemo, salesDate, salesTime, salesAccount, editReuslt, btnDeleteResult;
        LinearLayout ll_editResult;

        public MyViewHolder(View itemView){
            super(itemView);
            salesLocation =(TextView)itemView.findViewById(R.id.tv_salesLocation);
            salesMemo =(TextView)itemView.findViewById(R.id.tv_salesMemo);
            salesDate =(TextView)itemView.findViewById(R.id.tv_salesDate);
            salesTime =(TextView)itemView.findViewById(R.id.tv_salesTime);
            salesAccount =(TextView)itemView.findViewById(R.id.tv_salesAccount);
            editReuslt =(TextView)itemView.findViewById(R.id.tv_editReuslt);
            btnDeleteResult = (TextView)itemView.findViewById(R.id.btnDeleteResult);
            ll_editResult = (LinearLayout)itemView.findViewById(R.id.ll_editResult);
        }
    }

    public void deleteContent(final int position){
        mDatabase.child("trucks").child("salessituation").child(auth.getCurrentUser().getUid()).child(keys.get(position))
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(c,"삭제 완료" ,Toast.LENGTH_SHORT).show();
            }
        });
    }
}