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

public class MenuComponent extends RelativeLayout {

    @BindView(R.id.sub_menu_text)
    TextView sub_menu_text;

    @BindView(R.id.sub_content_text)
    TextView sub_content_text;

    private View mView;

    public MenuComponent(Context context) {
        this(context, null);
    }

    public MenuComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setContent(String content) {
        sub_content_text.setText(content);
    }

    private void init(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.component_menu,this,true);
        ButterKnife.bind(mView);
//        @SuppressLint({"Recycle", "CustomViewStyleable"})
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.submenu);
        sub_menu_text.setText(a.getString(R.styleable.submenu_text));
        sub_content_text.setText(a.getString(R.styleable.submenu_text_content));
    }
}
