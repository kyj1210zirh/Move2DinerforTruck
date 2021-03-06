package com.mjc.yhs.move2diner;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomTitlebar {
    private TextView tv_titlebar;
    private ImageView iv_arrow_back;
    private Activity activity;
    private final int NO_ARROW_BACK = 100;

    public CustomTitlebar(Activity _activity, String title) {
        activity = _activity;

        tv_titlebar = (TextView) activity.findViewById(R.id.tv_titlebar);
        iv_arrow_back = (ImageView) activity.findViewById(R.id.iv_arrow_back);

        tv_titlebar.setText(title);
        iv_arrow_back.setVisibility(View.VISIBLE);
        iv_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    public CustomTitlebar(Activity _activity, String title, int TAG) {
        activity = _activity;

        tv_titlebar = (TextView) activity.findViewById(R.id.tv_titlebar);
        iv_arrow_back = (ImageView) activity.findViewById(R.id.iv_arrow_back);

        tv_titlebar.setText(title);

        switch (TAG) {
            case NO_ARROW_BACK:
                iv_arrow_back.setVisibility(View.GONE);
                break;
        }
    }
}
