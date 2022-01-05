package com.wpf.aptdemo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wpf.apt_annotation.BindView;

/**
 * Author: feipeng.wang
 * Time:   2021/7/28
 * Description : This is description.
 */
public class MainActivity extends AppCompatActivity{
    @BindView(value = R.id.hello)
    TextView helloText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
