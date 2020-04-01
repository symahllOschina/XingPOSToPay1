package com.wanding.xingpos.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.AutoCheck;
import com.wanding.xingpos.baidu.tts.util.InitConfig;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.baidu.tts.util.NonBlockSyntherizer;
import com.wanding.xingpos.baidu.tts.util.OfflineResource;
import com.wanding.xingpos.baidu.tts.util.UiMessageListener;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.util.NitConfig;

/**  支付成功提示界面 */
public class PaySuccessActivity extends BaseActivity implements OnClickListener{

	private Context context = PaySuccessActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private TextView tvPayOrderId;
	private TextView tvOk;
	
	/** 交易金额，交易类型分别顺序对应：刷卡，微信，支付宝，银联，翼支付 040,010,020,030,050 */

	private String payType,payTypeStr;
	private String pos_order_noStr;
	

	
    
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_success_activity);

        initView();
		initData();


	}
	


    /**
     * 初始化界面控件
     */
    private void initView(){
        imgBack = (ImageView) findViewById(R.id.title_imageBack);
        tvTitle = (TextView) findViewById(R.id.title_tvTitle);
        tvPayOrderId = (TextView) findViewById(R.id.pay_success_payOrderIdText);
        tvOk = (TextView) findViewById(R.id.pay_success_tvOK);

        tvTitle.setText("收银");
        if(NitConfig.isTest.equals("test")){
            tvPayOrderId.setVisibility(View.VISIBLE);

        }
        imgBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }
	
	/** 初始化数据 */
	private void initData(){
		Intent in = getIntent();
		payType = in.getStringExtra("payTypeKey");
		//刷卡，微信，支付宝，银联，翼支付，分别顺序对应：040,010,020,030,050
		if(payType.equals("040")){
			payTypeStr = "银行卡消费收款";
		}else if(payType.equals("010")){
			payTypeStr = "微信支付收款";
		}else if(payType.equals("020")){
			payTypeStr = "支付宝支付收款";
		}else if(payType.equals("030")){
			payTypeStr = "银联支付收款";
		}else if(payType.equals("050")){
			payTypeStr = "翼支付收款";
            Log.e("传值订单号：",pos_order_noStr);
        }
        if(NitConfig.isTest.equals("test")){
            pos_order_noStr = in.getStringExtra("pos_order");
            tvPayOrderId.setText(pos_order_noStr);
        }

		
	}
	


	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.pay_success_tvOK:
			finish();
			break;
			
		}
	}
}
