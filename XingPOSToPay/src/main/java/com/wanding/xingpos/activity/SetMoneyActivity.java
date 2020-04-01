package com.wanding.xingpos.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.SharedPreferencesUtil;

/** 默认支付金额设置界面 */
public class SetMoneyActivity extends BaseActivity implements OnClickListener{
	private Context context = SetMoneyActivity.this;
	private ImageView imgBack;
	private TextView tvTitle;
	
	private EditText etMoney;
	private TextView tvOk;
	
	//默认金额为0
	private String defMoney = "0";
	private SharedPreferencesUtil sharedPreferencesUtil;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_money_activity);
		initView();
		initData();
	}
	
	/**  初始化数据 */
	private void initData(){
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil = new SharedPreferencesUtil(context, "defMoneyNum");
		//取出保存的默认值
		defMoney = (String) sharedPreferencesUtil.getSharedPreference("defMoneyKey", "");
		etMoney.setText(defMoney);
		
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		
		etMoney = (EditText) findViewById(R.id.setting_money_etMoney);
		EditTextUtils.setPricePoint(etMoney);
		tvOk = (TextView) findViewById(R.id.setting_money_tvOK);
		
		tvTitle.setText("设置默认金额");
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.setting_money_tvOK:
			String etMoneyStr = etMoney.getText().toString().trim();
			if(etMoneyStr.equals("")){
				//保存支付通道设置的通道值
				sharedPreferencesUtil.put("defMoneyKey", "0");
			}else if(etMoneyStr.equals("0.00")){
				sharedPreferencesUtil.put("defMoneyKey", "0");
			}else{
				//保存支付通道设置的通道值
				sharedPreferencesUtil.put("defMoneyKey", etMoneyStr);
			}
			
			finish();
			break;
		
		}
	}

	
}
