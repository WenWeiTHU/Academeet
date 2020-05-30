package com.example.academeet.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.academeet.Adapter.HomePagerAdapter;
import com.example.academeet.Fragment.CustomFragment;
import com.example.academeet.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
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
//    @BindView(R.id.calendar_fab)
//    FloatingActionButton fab;
    CustomFragment favorsFragment;
    CustomFragment remindsFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_export){
            if(mCustomViewerPager.getCurrentItem() == 0){
                favorsFragment.exportCalendar("Favorite conferences");
            } else if(mCustomViewerPager.getCurrentItem() == 1) {
                remindsFragment.exportCalendar("Reminded conferences");
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        ButterKnife.bind(this);
        titles.add("Favorite");
        titles.add("Reminder");
        //titles.add("Dislike");

        favorsFragment = new CustomFragment("Favors");
        remindsFragment = new CustomFragment("Reminds");
        fragmentList.add(favorsFragment);
        fragmentList.add(remindsFragment);
        //fragmentList.add(new CustomFragment("Dislikes"));

        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager(),
                fragmentList, titles);
        mCustomViewerPager.setAdapter(pagerAdapter);
        mCustomTabLayout.setupWithViewPager(mCustomViewerPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_favorites_toolbar);
        toolbar.setTitle("Favorites");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener((view) -> {finish();});

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mCustomViewerPager.getCurrentItem() == 0){
//                    favorsFragment.exportCalendar("Favorite Conferences");
//                } else if(mCustomViewerPager.getCurrentItem() == 1) {
//                    remindsFragment.exportCalendar("Reminded Conferences");
//                }
//            }
//        });
    }
}