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

    /**
     * @describe: 初始化数据
     * @param layout_id 注册界面的 layout id
     */
    public RegisterFragment(int layout_id) {
        super();
        this.layout_id = layout_id;
    }

    /**
     * @describe: 初始化界面
     * @param inflater Layout解析器
     * @param container Fragment容器
     * @param savedInstanceState 先前保存的实例
     * @return 创建好的 Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(layout_id, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    /**
     * @describe: 查找 Register Fragment中的 View
     * @param id View 的 id
     * @return 查找到的 View
     */
    public View getRegisterView(int id) {
        Activity activity = getActivity();
        return activity.findViewById(id);
    }


}
