package com.wanding.xingpos.activity;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newland.starpos.installmentsdk.NldInstallMent;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.PosInitData;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;

/** 欢迎界面（主要初始化一些应用数据到保存到本地） */
public class WelcomeActivity extends BaseActivity {

	/**
	 * Context
	 */
	private Context context = WelcomeActivity.this;

	/**
	 * posProvider：表示pos机厂商（提供者），默认情况下为新大陆newland
	 * 				当调用新大陆SDK签到提示找不到界面时posProvider的值发生变化，改为 posProvider = "fuyousf"
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF = "fuyousf";
	private String posProvider = NEW_LAND;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
		initData();
//		setContentView(R.layout.main_pay_fragment);
	}
	
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
	
	private void initData(){
		/** wxPayServiceType默认为true，代表微信支付通道选中走默认服务，即自己后台服务，false走星POS机SDK */
		boolean wxPayServiceType = true;
		boolean aliPayServiceType = true;
		boolean ylPayServiceType = false;
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(context, "transaction");
		if(sharedPreferencesUtil.contain("wxPayServiceKey")){
			//存在
			Log.e("微信支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil.put("wxPayServiceKey", wxPayServiceType);
			Log.e("微信支付设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil.contain("aliPayServiceKey")){
			//存在
			Log.e("支付宝支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil.put("aliPayServiceKey", aliPayServiceType);
			Log.e("支付宝支付设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil.contain("ylPayServiceKey")){
			//存在
			Log.e("银联二维码支付设置：", "Key存在");
		}else{
			//保存支付通道设置的通道默认值
			sharedPreferencesUtil.put("ylPayServiceKey", ylPayServiceType);
			Log.e("银联二维码支付设置：", "Key不存在保存默认");
		}
		
		/**
		 * printNum默认为printNumNo，代表不打印， 设置界面值printNumOne表示一联，printNumTwo则为两联
		 * isDefault默认为true,字体大小为默认，false表示字体大小为大
		 */
		String printNum = "printNumOne";
		boolean isDefault = true;
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "printing");
		if(sharedPreferencesUtil1.contain("printNumKey")){
			//存在
			Log.e("打印设置：", "Key存在");
		}else{
			//保存打印设置的默认值
			sharedPreferencesUtil1.put("printNumKey", printNum);
			Log.e("打印设置：", "Key不存在保存默认");
		}
		if(sharedPreferencesUtil1.contain("isDefaultKey")){
			//存在
			Log.e("打印字体大小设置：", "Key存在");
		}else{
			//保存打印设置的默认值
			sharedPreferencesUtil1.put("isDefaultKey", isDefault);
			Log.e("打印字体大小设置：", "Key不存在保存默认");
		}
		
		/** 扫码摄像头设置参数值   默认true，代表后置摄像头,前置为false  */
		boolean cameType = true;
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(context, "scancamera");
		if(sharedPreferencesUtil3.contain("cameTypeKey")){
			//存在
			Log.e("摄像头设置：", "Key存在");
		}else{
			//不存在保存默认值
			sharedPreferencesUtil3.put("cameTypeKey", cameType);
			Log.e("摄像头设置：", "Key不存在保存默认");
		}

		/**  取出POS机厂商标示 默认为新大陆  */
		SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(context, "posInit");
		if(sharedPreferencesUtil2.contain("posProvider")){
			//存在
			Log.e("厂商标示设置：", "Key存在");
		}else{
			Log.e("厂商标示设置：", "Key不存在保存默认");
			sharedPreferencesUtil2.put("posProvider", posProvider);//保存pos提供者
		}

		//pos初始化数据
		if(sharedPreferencesUtil2.contain("posMercId")){
			PosInitData data = new PosInitData();
			data.setPosProvider((String)sharedPreferencesUtil2.getSharedPreference("posProvider", ""));
			data.setMercId_pos((String)sharedPreferencesUtil2.getSharedPreference("posMercId", ""));
			data.setTrmNo_pos((String)sharedPreferencesUtil2.getSharedPreference("posTrmNo", ""));
			data.setMername_pos((String)sharedPreferencesUtil2.getSharedPreference("posMername", ""));
			data.setBatchno_pos((String)sharedPreferencesUtil2.getSharedPreference("posBatchno", ""));
			/**
			 * 下面是调用帮助类将一个对象以序列化的方式保存
			 * 方便我们在其他界面调用，类似于Intent携带数据
			 */
			try {
				MySerialize.saveObject("PosInitData",getApplicationContext(),MySerialize.serialize(data));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//进入主界面
			intentMainActivity();
			Log.e("登录初始化信息：", "Key存在");
		}else{
			//应用初始化（获取POS机商户信息）
//			intentMainActivity();
			appDataInstance();
			Log.e("登录初始化信息：", "Key不存在");
		}
	}

	
	private void intentMainActivity(){
		Intent in = new Intent();
		in.setClass(context, MainActivity.class);
		startActivity(in);
		//跳转动画效果
		overridePendingTransition(R.anim.in_from, R.anim.to_out);
		finish();
	}
	
	/** 获取POS机本身的相关信息，获取应用初始化数据 */
	private void appDataInstance(){
		if(Utils.isNotEmpty(posProvider)){
			if(posProvider.equals(NEW_LAND)){
				try {
					ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
					Intent intent = new Intent();
					intent.setComponent(component);
					Bundle bundle = new Bundle();
					bundle.putString("msg_tp",  "0300");
					bundle.putString("pay_tp",  "2");
					bundle.putString("order_no",  "");
					bundle.putString("appid",     "com.wanding.xingpos");
					bundle.putString("reason",     "");
					bundle.putString("txndetail",     "");
					intent.putExtras(bundle);
					this.startActivityForResult(intent, 1);
				} catch(ActivityNotFoundException e) {
					//TODO:
					Log.e("Newland_Exception", "找不到界面");
					posProvider = FUYOU_SF;
					appDataInstance();
				} catch(Exception e) {
					//TODO:
					Log.e("Exception：", "异常");
				}
			}else if(posProvider.equals(FUYOU_SF)){
				try {
					ComponentName component = new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.MainActivity");
					Intent intent = new Intent();
					intent.setComponent(component);
					Bundle bundle = new Bundle();
					bundle.putString("transName", "签到");
					intent.putExtras(bundle);
					Log.e("签到Bundle的值：",bundle.toString());
					this.startActivityForResult(intent, 1);
				} catch(ActivityNotFoundException e) {
					//TODO:
					Log.e("Fouyou_Exception", "找不到界面");
					posProvider = "";
					appDataInstance();
				} catch(Exception e) {
					//TODO:
					Log.e("Exception：", "异常");
				}
			}
		}else{
			Toast.makeText(this,"找不到界面",1*1000).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle = data.getExtras();
		if (requestCode == 1&&bundle != null) {
		switch (resultCode) {
		 // 请求成功
		  case Activity.RESULT_OK:
		  	if(posProvider.equals(NEW_LAND)){
		  		newlandResult(bundle);
			}else if(posProvider.equals(FUYOU_SF)){
		  		fuyouResult(bundle);
			}
		    break;
		// 请求取消
		 case Activity.RESULT_CANCELED:
			 String reason = bundle.getString("reason");
		     if (reason != null) {
		    	 // TODO:
//				 Toast.makeText(this, reason, 0*1000).show();
		     }
		     break;
		}
		}
	}

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0310")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail获取设备商户信息：", txndetail);
			try {
				JSONObject job = new JSONObject(txndetail);
				PosInitData data = new PosInitData();
				data.setMercId_pos(job.getString("merid"));
				data.setTrmNo_pos(job.getString("termid"));
				data.setMername_pos(job.getString("mername"));
				data.setBatchno_pos(job.getString("batchno"));
				//将需要的参数传入支付请求公共类保存在本地
				saveDataLocal(data);
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(context,"数据初始化失败！",Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		String merchantIdStr = bundle.getString("merchantId");//商户号
		String terminalIdStr = bundle.getString("terminalId");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名
		String batchNoStr = "";//批次号（富友签到时不返回该字段）
        Log.e("merchantIdStr：", merchantIdStr);
        Log.e("terminalIdStr：", terminalIdStr);
        Log.e("merchantNameStr：", merchantNameStr);
        Log.e("batchNoStr：", batchNoStr);

		PosInitData data = new PosInitData();
		data.setPosProvider(posProvider);
		data.setMercId_pos(merchantIdStr);
		data.setTrmNo_pos(terminalIdStr);
		data.setMername_pos(merchantNameStr);
		data.setBatchno_pos(batchNoStr);
		//将需要的参数传入支付请求公共类保存在本地
		saveDataLocal(data);
	}
	
	private void saveDataLocal(PosInitData data){

		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("PosInitData",getApplicationContext(),MySerialize.serialize(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//将POS初始化信息保存在本地
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(context, "posInit");
		sharedPreferencesUtil.put("posProvider", posProvider);//保存pos提供者
		sharedPreferencesUtil.put("posMercId", data.getMercId_pos());
		sharedPreferencesUtil.put("posTrmNo", data.getTrmNo_pos());
		sharedPreferencesUtil.put("posMername", data.getMername_pos());
		sharedPreferencesUtil.put("posBatchno", data.getBatchno_pos());
		//进入主界面
		intentMainActivity();
			

	}
}
