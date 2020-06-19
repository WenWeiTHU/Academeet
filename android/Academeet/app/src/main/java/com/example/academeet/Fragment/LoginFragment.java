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

    /**
     * @describe: 创建Fragment，初始化界面
     * @param inflater Layout解析器
     * @param container View 容器
     * @param savedInstanceState 之前存在的实例
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    /**
     * @describe: 获取Fragment中的控件
     * @param id 控件的ID
     * @return 对应的控件或者 null
     */
    public View getLoginView(int id) {
        return getActivity().findViewById(id);
    }
}
