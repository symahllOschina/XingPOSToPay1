package com.wanding.xingpos.activity;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.MySerialize;

/** 商户信息展示Activity  */
public class BusinessDetailsActivity extends BaseActivity implements OnClickListener{

	private Context context = BusinessDetailsActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	/** */
	private TextView tvMerchantName,tvMerchantNo,tvKuanTaiName,tvTerminalId,tvMercId_pos,tvTrmNo_pos,tvBatchno_pos;
	
	/** 完成，重打印  */
	private TextView tvBack;
	
	
	
	private UserLoginResData posPublicData;
	
	
    private String TAG = "商户信息展示界面";
    
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_details_activity);
		initView();
		initData();
		
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	
    
    
	
	/** 初始化数据 */
	private void initData(){
		try {
			posPublicData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		updateViewData();
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		
		tvTitle.setText("商户信息");
		imgBack.setOnClickListener(this);
		tvMerchantName = (TextView) findViewById(R.id.business_details_tvMerchantName);
		tvMerchantNo = (TextView) findViewById(R.id.business_details_tvMerchant_no);
		tvKuanTaiName = findViewById(R.id.business_details_tvKuanTaiName);
		tvTerminalId= (TextView) findViewById(R.id.business_details_tvTerminal_id);
		tvMercId_pos= (TextView) findViewById(R.id.business_details_tvMercId_pos);
		tvTrmNo_pos= (TextView) findViewById(R.id.business_details_tvTrmNo_pos);
		tvBatchno_pos= (TextView) findViewById(R.id.business_details_tvBatchno_pos);
		
		tvBack = (TextView) findViewById(R.id.business_details_tvBack);
		
		
		tvBack.setOnClickListener(this);
	}
	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	try {
    		tvMerchantName.setText("");
			tvMerchantNo.setText("");
			tvKuanTaiName.setText("");
			tvTerminalId.setText("");
			tvMercId_pos.setText("");
			tvTrmNo_pos.setText("");
			tvBatchno_pos.setText("");
			
			if(posPublicData!=null){
				//商户名称
				tvMerchantName.setText(posPublicData.getMername_pos());
				//商户号
				tvMerchantNo.setText(posPublicData.getMerchant_no());
				//款台名称
				tvKuanTaiName.setText(posPublicData.getEname());
				//设备号
				tvTerminalId.setText(posPublicData.getTerminal_id());
				//pos商户号
				tvMercId_pos.setText(posPublicData.getMercId_pos());
				//pos设备号
				tvTrmNo_pos.setText(posPublicData.getTrmNo_pos());
				//批次号
				tvBatchno_pos.setText(posPublicData.getBatchno_pos());
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.business_details_tvBack:
			finish();
			break;
			
		}
	}
}
