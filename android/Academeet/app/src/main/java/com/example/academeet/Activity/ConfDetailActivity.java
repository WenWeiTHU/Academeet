package com.example.academeet.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academeet.Adapter.ConfDetailAdapter;
import com.example.academeet.Fragment.ConfDetailFragment;
import com.example.academeet.Fragment.SessionListFragment;
import com.example.academeet.Item.ConferenceItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.academeet.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class ConfDetailActivity extends AppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    /**
     * @describe: 从上一级活动中获取数据，并且初始化页面
     * @param savedInstanceState 先前保存的数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_detail);
        Intent intent = getIntent();
        ConferenceItem conference = (ConferenceItem)intent.getSerializableExtra("conference");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(conference.getName());
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("conference", conference);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        // Create an instance of the tab layout from the view.
        TabLayout tabLayout = findViewById(R.id.conf_detail_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Sessions"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentList.add(new ConfDetailFragment(conference));
        fragmentList.add(new SessionListFragment(conference.getId()));

        final ViewPager viewPager = findViewById(R.id.conf_detail_view_pager);
        final ConfDetailAdapter adapter = new ConfDetailAdapter(getSupportFragmentManager(), fragmentList);
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
