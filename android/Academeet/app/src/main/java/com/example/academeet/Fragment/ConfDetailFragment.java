package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.academeet.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ConfDetailFragment extends Fragment {


    public ConfDetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_conf_detail, container, false);
        return view;
    }
}
