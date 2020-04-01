package com.wanding.xingpos.activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;

/** 主管密码管理界面 */
public class UserSetPasswdActivity extends BaseActivity implements OnClickListener{
	
	private Context context = UserSetPasswdActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private EditText etText;
	private TextView tvOk;
	
	
	private String optionStr;
	private String optionPasswd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_setpasswd_activity);
		initData();
		initView();
	}
	
	/** 初始化数据 */
	private void initData(){
		
		optionPasswd = ("888888");
		
		Intent in = getIntent();
		optionStr = in.getStringExtra("optionKey");
		
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		etText = (EditText) findViewById(R.id.user_setpasswd_etPasswd);
		tvOk = (TextView) findViewById(R.id.user_setpasswd_tvOk);
		
		tvTitle.setText("管理员密码");
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
		case R.id.user_setpasswd_tvOk:
			in = new Intent();
			String etTextStr = etText.getText().toString().trim();
			if(etTextStr!=null&&!etTextStr.equals("")){
				if(etTextStr.equals(optionPasswd)){
					if(optionStr.equals("settings")){
						in.setClass(this, SettingActivity.class);
					}else if(optionStr.equals("scanRefund")){
						in.setClass(this, ScanRefundActivity.class);
					}
					startActivity(in);
					finish();
				}else{
					Toast.makeText(context, "密码匹配错误！", Toast.LENGTH_LONG).show();
				}
				
			}else{
				Toast.makeText(context, "请输入管理密码！", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
}
