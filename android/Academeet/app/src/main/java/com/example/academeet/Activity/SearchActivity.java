package com.example.academeet.Activity;

import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.academeet.Fragment.SearchListFragment;
import com.example.academeet.R;


public class SearchActivity extends AppCompatActivity {
    SearchView searchView;
    SearchListFragment searchListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // When text clicked
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchListFragment.searchConference(query);
                Toast toast = Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

            // When text changed
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                }else{
                }
                return false;
            }
        });

        searchListFragment = new SearchListFragment();
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container,searchListFragment)
                .commit();
    }
}
