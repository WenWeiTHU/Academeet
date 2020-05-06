package com.example.academeet.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.academeet.R;

import butterknife.ButterKnife;

public class RegisterFragment extends Fragment {
    private int layout_id;

    public RegisterFragment(int layout_id) {
        super();
        this.layout_id = layout_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(layout_id, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public View getRegisterView(int id) {
        Activity activity = getActivity();
        return activity.findViewById(id);
    }


}
