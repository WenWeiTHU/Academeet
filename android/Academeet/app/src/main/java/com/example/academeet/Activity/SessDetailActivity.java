package com.example.academeet.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.academeet.Adapter.ConfDetailAdapter;
import com.example.academeet.Adapter.SessDetailAdapter;
import com.example.academeet.Fragment.PaperListFragment;
import com.example.academeet.Fragment.SessDetailFragment;
import com.example.academeet.Item.SessionItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.View;

import com.example.academeet.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SessDetailActivity extends AppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sess_detail);
        Intent intent = getIntent();
        SessionItem session = (SessionItem)intent.getSerializableExtra("session");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(session.getName());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                Context context = view.getContext();
//                Intent intent = new Intent(context, SearchActivity.class);
//
//
//                context.startActivity(intent);
//            }
//        });

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = findViewById(R.id.sess_detail_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Papers"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentList.add(new SessDetailFragment(session));
        fragmentList.add(new PaperListFragment(session.getId()));


        final ViewPager viewPager = findViewById(R.id.sess_detail_view_pager);
        final SessDetailAdapter adapter = new SessDetailAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                }
        );

    }
}
