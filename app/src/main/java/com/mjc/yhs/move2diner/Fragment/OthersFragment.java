package com.mjc.yhs.move2diner.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mjc.yhs.move2diner.LoginDefaultActivity;
import com.mjc.yhs.move2diner.MenuInfoActivity;
import com.mjc.yhs.move2diner.ProfileActivity;
import com.mjc.yhs.move2diner.R;
import com.mjc.yhs.move2diner.ReviewActivity;
import com.mjc.yhs.move2diner.SalesResultActivity;


public class OthersFragment extends Fragment implements View.OnClickListener {
    Intent i;

    public static OthersFragment newInstance() {
        return new OthersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_others, container, false);

        rootView.findViewById(R.id.btnProfile).setOnClickListener(this);
        rootView.findViewById(R.id.btnFoodInfo).setOnClickListener(this);
        rootView.findViewById(R.id.btnReview).setOnClickListener(this);
        rootView.findViewById(R.id.btnChangeAuth).setOnClickListener(this);
        rootView.findViewById(R.id.btnSalesResult).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnProfile:
                i = new Intent(getActivity(), ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.btnFoodInfo:
                i = new Intent(getActivity(), MenuInfoActivity.class);
                startActivity(i);
                break;
            case R.id.btnReview:
                i = new Intent(getActivity(), ReviewActivity.class);
                startActivity(i);
                break;
            case R.id.btnChangeAuth:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                        .setMessage("로그 아웃 하시겠습니까?")
                        .setPositiveButton("로그 아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logOut();

                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                break;
            case R.id.btnSalesResult:
                i = new Intent(getActivity(), SalesResultActivity.class);
                startActivity(i);
                break;
        }
    }

    private void logOut() {
        Toast.makeText(getContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginDefaultActivity.class));
        getActivity().finish();
    }
}
