package com.wanding.xingpos.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.util.SharedPreferencesUtil;

/** 打印设置界面(选择打印联数) */
public class SetPrintNumActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	private Context context = SetPrintNumActivity.this;
	private ImageView imgBack;
	private TextView tvTitle;
	
	private RadioGroup numRadioGroup,textSizeRadioGroup;
	private RadioButton numButNo,numButOne,numButTwo,textButDefault,textButLarge;
	private TextView tvOk;
	
	//默认是一联printNumOne
	private String printNum = "printNumNo";
	//字体大小默认
	private boolean isDefault = true;
	private SharedPreferencesUtil sharedPreferencesUtil;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_printnum_activity);
		initView();
		initData();
	}
	
	/**  初始化数据 */
	private void initData(){
		//printing ：交易设置值存储应用本地的文件名称
		sharedPreferencesUtil = new SharedPreferencesUtil(context, "printing");
		//取出保存的值
		printNum = (String) sharedPreferencesUtil.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil.getSharedPreference("isDefaultKey", isDefault);
		if(printNum.equals("printNumNo")){
			numButNo.setChecked(true);
			numButOne.setChecked(false);
			numButTwo.setChecked(false);
		}else if(printNum.equals("printNumOne"))
		{
			numButNo.setChecked(false);
			numButOne.setChecked(true);
			numButTwo.setChecked(false);
		}else{
			numButNo.setChecked(false);
			numButOne.setChecked(false);
			numButTwo.setChecked(true);
		}

		if(isDefault){
			textButDefault.setChecked(true);
			textButLarge.setChecked(false);
		}else{
			textButDefault.setChecked(false);
			textButLarge.setChecked(true);
		}

		
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		numRadioGroup = (RadioGroup) findViewById(R.id.setting_printnum_numRadioGroup);
		numButNo = (RadioButton) findViewById(R.id.setting_printnum_numButNo);
		numButOne = (RadioButton) findViewById(R.id.setting_printnum_numButOne);
		numButTwo = (RadioButton) findViewById(R.id.setting_printnum_numButTwo);
		textSizeRadioGroup = (RadioGroup) findViewById(R.id.setting_printnum_textSizeRadioGroup);
		textButDefault = (RadioButton) findViewById(R.id.setting_printnum_textButDefault);
		textButLarge = (RadioButton) findViewById(R.id.setting_printnum_textButLarge);
		tvOk = (TextView) findViewById(R.id.setting_printnum_tvOK);
		
		tvTitle.setText("打印设置");
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		numRadioGroup.setOnCheckedChangeListener(this);
		textSizeRadioGroup.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.setting_printnum_tvOK:
			if(printNum.equals("printNumNo")){
				Log.e("打印联数设置为：", "printNumNo");
			}else if(printNum.equals("printNumOne")){
				Log.e("打印联数设置为：", "printNumOne");
			}else{
				Log.e("打印联数设置为：", "printNumTwo");
			}

			if(isDefault){
				Log.e("打印字体大小设置为：", "isDefault");
			}else{
				Log.e("打印字体大小设置为：", "isDefault");
			}
			//保存支付通道设置的通道值
			sharedPreferencesUtil.put("printNumKey", printNum);
			sharedPreferencesUtil.put("isDefaultKey", isDefault);
			finish();
			break;
		
		}
	}

	/** RadioGroup选中事件监听 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (group.getId()) {
		case R.id.setting_printnum_numRadioGroup://
			if(checkedId == R.id.setting_printnum_numButNo){
				printNum = "printNumNo";
			}else if(checkedId == R.id.setting_printnum_numButOne){
				printNum = "printNumOne";
			}else{
				printNum = "printNumTwo";
			}
			break;
		case R.id.setting_printnum_textSizeRadioGroup://
			if(checkedId == R.id.setting_printnum_textButDefault){
				isDefault = true;
			}else{
				isDefault = false;
			}
			break;
		}
	}
}
