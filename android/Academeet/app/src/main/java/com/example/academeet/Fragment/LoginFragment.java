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

public class LoginFragment extends Fragment {
    @BindView(R.id.login_type_radio)
    RadioGroup mLoginTypeRadio;
    @BindView(R.id.login_type_scholar_radio)
    RadioButton mLoginTypeScholarRadio;
    @BindView(R.id.login_type_admin_radio)
    RadioButton mLoginTypeAdminRadio;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.register_button)
    Button mRegisterButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
