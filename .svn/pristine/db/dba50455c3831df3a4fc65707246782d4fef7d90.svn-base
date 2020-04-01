package com.wanding.xingpos.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.util.SharedPreferencesUtil;

/** 交易设置界面(选择支付通道) */
public class SetPayServiceActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	
	private Context context = SetPayServiceActivity.this;
	private ImageView imgBack;
	private TextView tvTitle;

	private LinearLayout ylewmLayout;
	private RadioGroup wxRadioGroup,aliRadioGroup,ylRadioGroup,cameRadioGroup;
	private RadioButton wxBtnDefault,wxBtnOther,aliBtnDefault,aliBtnOther,ylBtnDefault,ylBtnOther,frontCameBtn,postCameBtn;
	private TextView tvOk;
	
	private boolean wxPayServiceType = true;
	private boolean aliPayServiceType = true;
	private boolean ylPayServiceType = true;

	/** 扫码摄像头设置参数值   默认true，代表后置摄像头,前置为false  */
	private boolean cameType = true;
	
	private SharedPreferencesUtil sharedPreferencesUtil1;
	private SharedPreferencesUtil sharedPreferencesUtil2;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider = MainActivity.posProvider;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_payservice_activity);
		initView();
		initData();
	}
	
	/**  初始化数据 */
	private void initData(){
		//transaction ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "transaction");
		//取出保存的值
		wxPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("wxPayServiceKey", true);
		aliPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("aliPayServiceKey", true);
		ylPayServiceType = (Boolean) sharedPreferencesUtil1.getSharedPreference("ylPayServiceKey", true);
		if(wxPayServiceType)
		{
			wxBtnDefault.setChecked(true);
			wxBtnOther.setChecked(false);
		}else{
			wxBtnDefault.setChecked(false);
			wxBtnOther.setChecked(true);
		}
		if(aliPayServiceType)
		{
			aliBtnDefault.setChecked(true);
			aliBtnOther.setChecked(false);
		}else{
			aliBtnDefault.setChecked(false);
			aliBtnOther.setChecked(true);
		}
		if(ylPayServiceType)
		{
			ylBtnDefault.setChecked(true);
			ylBtnOther.setChecked(false);
		}else{
			ylBtnDefault.setChecked(false);
			ylBtnOther.setChecked(true);
		}
		
		//取出保存的摄像头参数值
		sharedPreferencesUtil2 = new SharedPreferencesUtil(context, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil2.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			frontCameBtn.setChecked(false);
			postCameBtn.setChecked(true);
		}else{
			frontCameBtn.setChecked(true);
			postCameBtn.setChecked(false);
		}
	}
	
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		ylewmLayout = findViewById(R.id.setting_payservice_ylewmLayout);
		wxRadioGroup = (RadioGroup) findViewById(R.id.setting_payservice_wxRadioGroup);
		aliRadioGroup = (RadioGroup) findViewById(R.id.setting_payservice_aliRadioGroup);
		ylRadioGroup = (RadioGroup) findViewById(R.id.setting_payservice_ylewmRadioGroup);
		cameRadioGroup = (RadioGroup) findViewById(R.id.setting_payservice_cameraRadioGroup);
		wxBtnDefault = (RadioButton) findViewById(R.id.setting_payservice_wxBtnDefault);
		wxBtnOther = (RadioButton) findViewById(R.id.setting_payservice_wxBtnOther);
		aliBtnDefault = (RadioButton) findViewById(R.id.setting_payservice_aliBtnDefault);
		aliBtnOther = (RadioButton) findViewById(R.id.setting_payservice_aliBtnOther);
		ylBtnDefault = (RadioButton) findViewById(R.id.setting_payservice_ylewmBtnDefault);
		ylBtnOther = (RadioButton) findViewById(R.id.setting_payservice_ylewmBtnOther);
		frontCameBtn = (RadioButton) findViewById(R.id.setting_payservice_frontCamera);
		postCameBtn = (RadioButton) findViewById(R.id.setting_payservice_postCamera);
		tvOk = (TextView) findViewById(R.id.setting_payservice_tvOK);
		
		tvTitle.setText("交易设置");
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		wxRadioGroup.setOnCheckedChangeListener(this);
		aliRadioGroup.setOnCheckedChangeListener(this);
		ylRadioGroup.setOnCheckedChangeListener(this);
		cameRadioGroup.setOnCheckedChangeListener(cameRGCheckedListener);
		ylewmLayout.setVisibility(View.GONE);
		if(posProvider.equals(FUYOU_SF)){
			ylewmLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.setting_payservice_tvOK:
			
			if(wxPayServiceType){
				Log.e("微信选则的支付通道为：", "默认");
			}else{
				Log.e("微信选则的支付通道为：", "第三方");
			}
			if(aliPayServiceType){
				Log.e("支付宝选则的支付通道为：", "默认");
			}else{
				Log.e("支付宝选则的支付通道为：", "第三方");
			}
			if(ylPayServiceType){
				Log.e("银联二维码选则的支付通道为：", "默认");
			}else{
				Log.e("银联二维码选则的支付通道为：", "第三方");
			}
			if(cameType){
				Log.e("摄像头选择的为：", "后置");
			}else{
				Log.e("摄像头选择的为：", "前置");
			}
			//保存支付通道设置的通道值
			sharedPreferencesUtil1.put("wxPayServiceKey", wxPayServiceType);
			sharedPreferencesUtil1.put("aliPayServiceKey", aliPayServiceType);
			sharedPreferencesUtil1.put("ylPayServiceKey", ylPayServiceType);
			//保存调用摄像头的值
			sharedPreferencesUtil2.put("cameTypeKey", cameType);
			finish();
			break;
		
		}
	}

	/** RadioGroup选中事件监听 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (group.getId()) {
		case R.id.setting_payservice_wxRadioGroup://微信选择监听
			if(checkedId == R.id.setting_payservice_wxBtnDefault){
				wxPayServiceType = true;
			}else{
				wxPayServiceType = false;
			}
			break;
		case R.id.setting_payservice_aliRadioGroup://支付宝选择监听
			if(checkedId == R.id.setting_payservice_aliBtnDefault){
				aliPayServiceType = true;
			}else{
				aliPayServiceType = false;
			}
			break;
		case R.id.setting_payservice_ylewmRadioGroup://银联二维码选择监听
			if(checkedId == R.id.setting_payservice_ylewmBtnDefault){
				ylPayServiceType = true;
			}else{
				ylPayServiceType = false;
			}
			break;
		}
	}

	private OnCheckedChangeListener cameRGCheckedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId == R.id.setting_payservice_postCamera){
				cameType = true;
			}else{
				cameType = false;
			}
		}
	};

}
