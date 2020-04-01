package com.wanding.xingpos.instalment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.InstalmentQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.BitmapUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.PhotoModule;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  退款说明
 */
public class InstalmentRefundHintActivity extends BaseActivity implements View.OnClickListener
{

    private ImageView imgBack;
    private TextView tvTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instalment_refund_hint_activity);


        initView();
        initListener();


    }

    /**  初始化控件 */
    private void initView(){
        imgBack = findViewById(R.id.title_imageBack);
        tvTitle = findViewById(R.id.title_tvTitle);
        tvTitle.setText("退款说明");


    }

    /** 事件监听注册 */
    private void initListener(){
        imgBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
        }
    }
}
