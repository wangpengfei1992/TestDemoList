package com.wpf.aptdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wpf.apt_annotation.BindView;
import com.wpf.apt_sdk.BindViewTool;


/**
 * Author: feipeng.wang
 * Time:   2022/1/11
 * Description : This is description.
 */
public class MainActivity extends AppCompatActivity {
    @BindView(value = R.id.hello)
    TextView helloText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTool.bind(this);
        if (helloText!=null){
            helloText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("wpf","111111111111111111");
                }
            });
        }
    }
}
