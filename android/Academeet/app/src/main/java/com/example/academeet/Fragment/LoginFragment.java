package com.example.academeet.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.example.academeet.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

public class LoginFragment extends Fragment {
    RadioButton mLoginTypeAdminRadio;
    @BindView(R.id.login_button)
    FancyButton mLoginButton;
    @BindView(R.id.register_button)
    FancyButton mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public View getLoginView(int id) {
        return getActivity().findViewById(id);
    }
}
