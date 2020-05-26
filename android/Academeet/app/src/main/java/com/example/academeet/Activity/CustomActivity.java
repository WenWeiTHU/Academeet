package com.example.academeet.Activity;

import android.os.Bundle;
import com.example.academeet.Adapter.HomePagerAdapter;
import com.example.academeet.Fragment.CustomFragment;
import com.example.academeet.R;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;

public class CustomActivity extends AppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    ArrayList<String> titles = new ArrayList<>();
    @BindView(R.id.custom_view_pager)
    ViewPager mCustomViewerPager;
    @BindView(R.id.custom_tab_layout)
    TabLayout mCustomTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ButterKnife.bind(this);
        titles.add("Favorite");
        titles.add("Reminder");
        //titles.add("Dislike");

        fragmentList.add(new CustomFragment("Favors"));
        fragmentList.add(new CustomFragment("Reminds"));
        //fragmentList.add(new CustomFragment("Dislikes"));

        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, titles);
        mCustomViewerPager.setAdapter(pagerAdapter);
        mCustomTabLayout.setupWithViewPager(mCustomViewerPager);
    }
}