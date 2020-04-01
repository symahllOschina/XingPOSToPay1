package com.wanding.xingpos.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.util.Utils;

/** 设置界面 */
public class SettingActivity extends BaseActivity implements OnClickListener{
	private ImageView imgBack;
	private TextView tvTitle;
	private LinearLayout transactionlayout,printLayout,defMoneyLayout;//交易设置,打印设置,金额设置
	private LinearLayout businessLayout,clearWaterLayout,versionlayout;//商户信息，清空流水，关于悦收银


	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		posProvider = MainActivity.posProvider;
		initView();
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		transactionlayout = (LinearLayout) findViewById(R.id.setting_transaction_layout);
		printLayout = (LinearLayout) findViewById(R.id.setting_print_layout);
		defMoneyLayout = (LinearLayout) findViewById(R.id.setting_money_layout);
		businessLayout = (LinearLayout) findViewById(R.id.setting_business_layout);
		clearWaterLayout = (LinearLayout) findViewById(R.id.setting_clearWater_layout);
		versionlayout = (LinearLayout) findViewById(R.id.setting_version_layout);
		
		tvTitle.setText("设置");
		imgBack.setOnClickListener(this);
		transactionlayout.setOnClickListener(this);
		printLayout.setOnClickListener(this);
		defMoneyLayout.setOnClickListener(this);
		businessLayout.setOnClickListener(this);
		clearWaterLayout.setOnClickListener(this);
		versionlayout.setOnClickListener(this);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();
		if (requestCode == 1&&bundle != null) {
			switch (resultCode) {
				// 支付成功
				case Activity.RESULT_OK:

					break;
				// 支付取消
				case Activity.RESULT_CANCELED:
					String reason = bundle.getString("reason");
					Log.e("失败返回值", reason);
					if (Utils.isNotEmpty(reason)) {
                        if(reason.equals("无交易记录,无需结算")){
                            Toast.makeText(SettingActivity.this,"无交易记录！",Toast.LENGTH_LONG).show();
                        }
					}

					break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.setting_transaction_layout://交易设置
			in = new Intent();
			in.setClass(this, SetPayServiceActivity.class);
			startActivity(in);
			break;
		case R.id.setting_print_layout://打印设置
			in = new Intent();
			in.setClass(this, SetPrintNumActivity.class);
			startActivity(in);
			break;
		case R.id.setting_money_layout://默认支付金额设置
			in = new Intent();
			in.setClass(this, SetMoneyActivity.class);
			startActivity(in);
			break;
		case R.id.setting_business_layout://商户信息
			in = new Intent();
			in.setClass(this, BusinessDetailsActivity.class);
			startActivity(in);
			break;
		case R.id.setting_clearWater_layout://清空流水
			if(posProvider.equals(NEW_LAND)){
				NewPosServiceUtil.settleReq(SettingActivity.this);
			}else if(posProvider.equals(FUYOU_SF)){
				FuyouPosServiceUtil.settleReq(SettingActivity.this);
			}
			break;
		case R.id.setting_version_layout://版本检测
			in = new Intent();
			in.setClass(this, SetAboutAppActivity.class);
			startActivity(in);
			break;
		
		}
	}
}
