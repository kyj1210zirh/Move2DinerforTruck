package com.mjc.yhs.move2diner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.mjc.yhs.move2diner.Fragment.OthersFragment;
import com.mjc.yhs.move2diner.Fragment.PosFragment;
import com.mjc.yhs.move2diner.Fragment.SalesSituationFragment;

public class FragmentTab extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private SwipeViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private MenuItem prevMenuItem;
    private BackPressCloseHandler backPressCloseHandler;

    public static Boolean isSalesSituation = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragement_tab);
        new CustomTitlebar(this, "Move 2 Diner", 100); // NO_ARROW_BACK
        initView();
    }

    private void initView() {
        viewPager = (SwipeViewPager) findViewById(R.id.viewPager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        setupViewPager(viewPager);
        backPressCloseHandler = new BackPressCloseHandler(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);

                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupViewPager(SwipeViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SalesSituationFragment.newInstance());
        adapter.addFragment(PosFragment.newInstance());
        adapter.addFragment(OthersFragment.newInstance());
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.btm_business:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btm_revenue:
                if (isSalesSituation == false) {
                    Toast.makeText(this, "영업을 먼저 시작해 주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                viewPager.setCurrentItem(1);
                break;
            case R.id.btm_others:
                viewPager.setCurrentItem(2);
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        backPressCloseHandler.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
