package com.example.academeet.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.academeet.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingTitleComponent extends RelativeLayout {

    @BindView(R.id.title)
    TextView sub_menu_text;

    @BindView(R.id.submit)
    TextView submit_text;

    private View mView;

    public SettingTitleComponent(Context context) {
        this(context, null);
    }

    public SettingTitleComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SettingTitleComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.component_setting_titlebar,this,true);
        ButterKnife.bind(mView);
        @SuppressLint({"Recycle", "CustomViewStyleable"})
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.submenu);
        sub_menu_text.setText(a.getString(R.styleable.submenu_text));
    }

    public void updateText(String info, int type) {
        if (type == 0)
            sub_menu_text.setText(info);
        else submit_text.setText(info);
    }
}
