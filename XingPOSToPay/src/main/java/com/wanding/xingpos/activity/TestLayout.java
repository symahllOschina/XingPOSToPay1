package com.wanding.xingpos.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.util.SharedPreferencesUtil;

public class TestLayout extends BaseActivity{

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        initView();

        initData();

    }

    /**
     * 初始化控件
     * 注释
     */
    private void initView(){

        editText = findViewById(R.id.test_layout_edittext);

    }


    /**
     * 初始化Data
     **/
    private void initData(){
        Intent in = getIntent();
        String req = in.getStringExtra("req");
        String res = in.getStringExtra("res");


        editText.setText("发起请求参数："+req+"返回状态吗或json字符串："+res);
    }
}
