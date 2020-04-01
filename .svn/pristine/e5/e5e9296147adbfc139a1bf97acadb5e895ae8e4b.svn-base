package com.wanding.xingpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;

/**  交易失败提示界面 */
public class PayErrorActivity extends BaseActivity implements OnClickListener{

	private Context context = PayErrorActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private TextView tvErrorText;
	private TextView tvOk;
	
	/** optionTypeStr:操作界面传递的值（010=支付失败，020=退款失败，030=查询失败）  */
	private String optionTypeStr = "";
	/** activityTitleStr: 界面标题 */
	private String activityTitleStr = "";
	/** errorTextStr : 错误定义text */
	private String errorTextStr = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_error_activity);
		initData();
		initView();
	}
	
	
	/** 初始化数据 */
	private void initData(){
		Intent in = getIntent();
		optionTypeStr = in.getStringExtra("optionTypeStr");
		if(optionTypeStr!=null&&!optionTypeStr.equals("")){
			if(optionTypeStr.equals("010")){
				activityTitleStr = "收银";
				errorTextStr = "交易失败！";
			}else if(optionTypeStr.equals("020")){
				activityTitleStr = "退款";
				errorTextStr = "退款失败！";
			}else if(optionTypeStr.equals("030")){
				activityTitleStr = "查询";
				errorTextStr = "查询失败！";
			}
		}else{
			activityTitleStr = "交易提示";
			errorTextStr = "未知错误！";
		}
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvErrorText = (TextView) findViewById(R.id.pay_error_tvErrorText);
		tvOk = (TextView) findViewById(R.id.pay_error_tvOk);
		
		tvTitle.setText(activityTitleStr);
		tvErrorText.setText(errorTextStr);
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
		case R.id.pay_error_tvOk:
			finish();
			break;
			
		}
	}
}
