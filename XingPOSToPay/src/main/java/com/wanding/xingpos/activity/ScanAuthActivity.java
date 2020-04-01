package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.MainBaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.PayTypeGridViewAdapter;
import com.wanding.xingpos.auth.bean.MicroPayPreAuthResponse;
import com.wanding.xingpos.auth.bean.PreLicensingResp;
import com.wanding.xingpos.auth.util.AuthReqUtil;
import com.wanding.xingpos.auth.util.ScanDisUtil;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.AuthBaseRequest;
import com.wanding.xingpos.bean.AuthRefreshOrderStateReqData;
import com.wanding.xingpos.bean.AuthResultResponse;
import com.wanding.xingpos.bean.PayWayBean;
import com.wanding.xingpos.bean.PosMemConsumeReqData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.httputils.HttpURLConUtil;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.payutil.QueryParamsReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 扫码预授权Activity
 */
@ContentView(R.layout.scan_auth_activity)
public class ScanAuthActivity extends MainBaseActivity implements OnClickListener{

	@ViewInject(R.id.menu_title_imageView)
	ImageView imgBack;
	@ViewInject(R.id.menu_title_layout)
	LinearLayout titleLayout;
	@ViewInject(R.id.menu_title_tvTitle)
	TextView tvTitle;
	@ViewInject(R.id.menu_title_imgTitleImg)
	ImageView imgTitleImg;
	@ViewInject(R.id.menu_title_tvOption)
	TextView tvOption;

	@ViewInject(R.id.pay_type_mGridView)
	GridView mGridView;
	private List<PayWayBean> list = new ArrayList<>();
	private BaseAdapter mAdapter;

	private UserLoginResData userLoginResData;

	/**
	 * 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联
	 */
	private String printNum = "printNumNo";
	/**
	 * 打印字体大小 isDefault:true默认大小，false即为大字体
	 */
	private boolean isDefault = true;
	/**
	 * cameraType为true表示打开后置摄像头，fasle为前置摄像头
	 */
	private boolean cameType = true;


	/**
	 * 输入框输入的合法金额
	 */
	private String totalFee;
	/**
	 * 支付方式
	 */
	private String payType;
	/**
	 * 打印小票第index联
	 */
	private int index = 1;

	/**
	 * 预授权成功返回
	 */
	private AuthResultResponse authResultResponse;

	private String authAction = "1";
	// 主控制类，所有合成控制方法从这个类开始
	protected MySyntherizer synthesizer;

	/**
	 *  轮询查询订单状态标示
	 */
	private int queryIndex = 1;

	AidlDeviceService aidlDeviceService = null;

	AidlPrinter aidlPrinter = null;
	public AidlScanner aidlScanner=null;
	/**
	 * 新大陆(打印机，扫码摄像头)AIDL服务
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(TAG, "bind device service");
			aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e(TAG, "unbind device service");
			aidlDeviceService = null;
		}
	};

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;
	private String scanCodeStr;//扫描返回结果
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){

		}
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
		tvTitle.setText("扫码预授权");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		synthesizer = MainActivity.synthesizer;
		try {
			userLoginResData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initData();
		initListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//清空StringBuilder，EditText恢复初始值
		//清空EditText
		pending.delete( 0, pending.length() );
		if(pending.length()<=0){
			etSumMoney.setText("￥0.00");
		}
		Log.e("onResume()方法", "已近执行");
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(activity, "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if(defMoney.equals("")||defMoney.equals("0")){
			etSumMoney.setHint("￥0.00");
			Log.e("defMoney保存金额", "为空");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
			Log.e("defMoney保存金额", etSumMoney.getText().toString()+"   pending的值："+pending.toString());
		}
		//取出设置的打印值
		SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(activity, "printing");
		//取出保存的默认值
		printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
		Log.e("取出保存的打印值", printNum);
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(activity, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}
	}
	


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(posProvider.equals(NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
			aidlScanner = null;
		}else if(posProvider.equals(FUYOU_SF)){

		}
		Log.e(TAG, "释放资源成功");

	}

	private void initData(){
		PayWayBean wxBean = new PayWayBean();
		wxBean.setImg(R.drawable.wx_pay_icon);
		wxBean.setText("微信");
		PayWayBean aliBean = new PayWayBean();
		aliBean.setImg(R.drawable.ali_pay_icon);
		aliBean.setText("支付宝");
		list.add(wxBean);
		list.add(aliBean);

		mGridView.setNumColumns(2);
		mGridView.setStackFromBottom(true);

		mAdapter = new PayTypeGridViewAdapter(activity,list);
		mGridView.setAdapter(mAdapter);
	}

	private void initListener(){
		imgBack.setOnClickListener(this);
		mGridView.setOnItemClickListener(payTypeCheckedLinstener);
	}

	/**
	 * 绑定服务
	 */
	public void bindServiceConnection() {
		bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
				Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
		Log.e("绑定服务", "绑定服务1");
	}

	/**
	 * 初始化打印设备
	 */
	public void getPrinter() {
		Log.i(TAG, "获取打印机设备实例...");
		try {
			aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
//            showMsgOnTextView("初始化打印机实例");
		} catch (RemoteException e) {
			e.printStackTrace();
			ToastUtil.showText(activity,"打印机调起失败！",1);
		}

	}


	/**
	 * 初始化扫码设备
	 */
	public void initScanner() {
		try {
			if (aidlScanner == null){
				aidlScanner = AidlScanner.Stub.asInterface(aidlDeviceService.getScanner());
			}
//            showMsgOnTextView("初始化打扫码实例");
		} catch (RemoteException e) {
			e.printStackTrace();

		}
	}

	/**
	 * 前置扫码
	 */
	public void frontscan(){
		try {
			Log.i(TAG, "-------------scan-----------");
			aidlScanner= AidlScanner
					.Stub.asInterface(aidlDeviceService.getScanner());
			int time=10;//超时时间
			aidlScanner.startScan(ScanConstant.ScanType.FRONT, time, new AidlScannerListener.Stub() {

				@Override
				public void onScanResult(String[] arg0) throws RemoteException {
//                    showMsgOnTextView("onScanResult-----"+arg0[0]);
					Log.w(TAG,"onScanResult-----"+arg0[0]);
					scanCodeStr = arg0[0];
					//如果扫描的二维码为空则不执行支付请求
					if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
						String auth_no = scanCodeStr;
						Log.e("前置扫码值：", auth_no);
						int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
						String text = auth_no;
						sendMessage(msg,text);

					}else{
						Log.e("后置扫码值：", "为空");
						ToastUtil.showText(activity,"扫码取消或失败！",1);
					}

				}

				@Override
				public void onFinish() throws RemoteException {
//					ToastUtil.showText(activity,"扫码失败！",1);

				}

				@Override
				public void onError(int arg0, String arg1) throws RemoteException {
//					ToastUtil.showText(activity,"扫码出现错误！",1);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
//			ToastUtil.showText(activity,"扫码失败！",1);
		}
	}

	/**
	 * 后置扫码
	 */
	public void backscan(){
		try {
			Log.i(TAG, "-------------scan-----------");
			aidlScanner= AidlScanner
					.Stub.asInterface(aidlDeviceService.getScanner());
			aidlScanner.startScan(ScanConstant.ScanType.BACK, 10, new AidlScannerListener.Stub() {

				@Override
				public void onScanResult(String[] arg0) throws RemoteException {
//					showMsgOnTextView("onScanResult-----"+arg0[0]);
					Log.w(TAG,"onScanResult-----"+arg0[0]);

					scanCodeStr = arg0[0];
					if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
						//auth_no	授权码（及扫描二维码值）
						String auth_no = scanCodeStr;
						Log.e("后置扫码值：", auth_no);

						int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
						String text = auth_no;
						sendMessage(msg,text);

					}else{

						Log.e("后置扫码值：", "为空");
						ToastUtil.showText(activity,"扫码取消或失败！",1);
					}

				}

				@Override
				public void onFinish() throws RemoteException {
//					ToastUtil.showText(activity,"扫码失败！",1);

				}

				@Override
				public void onError(int arg0, String arg1) throws RemoteException {
//					ToastUtil.showText(activity,"扫码出现错误！",1);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
//			ToastUtil.showText(activity,"扫码失败！",1);
		}
	}

	/**
	 * speak 实际上是调用 synthesize后，获取音频流，然后播放。
	 * 获取音频流的方式见SaveFileActivity及FileSaveListener
	 * 需要合成的文本text的长度不能超过1024个GBK字节。
	 */
	private void speak(String text) {

		int result = synthesizer.speak(text);
		checkResult(result, "speak");
	}

	/**
	 * 暂停播放
	 */
	private void stop() {

		int result = synthesizer.stop();
		checkResult(result, "speak");
	}

	private void checkResult(int result, String method) {
		if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
			Log.e("error code :", result+" method:" + method );
		}
	}


	/**
	 * 消费第一步
	 */
	private void payMethodOne(){

		try {
			String totalFeeStr = pending.toString();
			Log.e("输入框金额值：", totalFeeStr);
			if(Utils.isEmpty(totalFeeStr)){
				ToastUtil.showText(activity,"请输入有效金额！",1);
				return;
			}
			totalFee =  DecimalUtil.StringToPrice(totalFeeStr);
			Log.e("金额值转换后：", totalFee);
			//金额是否合法
			int isCorrect = DecimalUtil.isEqual(totalFee);
			if(isCorrect != 1){
				ToastUtil.showText(activity,"请输入有效金额！",1);
				return;
			}

			if(posProvider.equals(Constants.NEW_LAND)){
				initScanner();
				if(cameType){
					Log.e("扫码调用：", "后置摄像头");
					backscan();
				}else{
					Log.e("扫码调用：", "前置摄像头");
					frontscan();
				}
			}else if(posProvider.equals(Constants.FUYOU_SF)){
				//开始扫码
				FuyouPosServiceUtil.scanReq(activity);
			}







		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ToastUtil.showText(activity,"金额错误！",1);
		}
	}

	/**
	 * 消费第二步
	 * auth_no：扫码结果（付款码Code）
	 */
	private void payMethodTwo(String auth_no){
		//元转分
		String total_fee = DecimalUtil.elementToBranch(totalFee);

		AuthBaseRequest request = PayRequestUtil.authReq(userLoginResData,payType,total_fee, auth_no);
		payMethodThere(request);

	}

	/**
	 * 消费第三步
	 * 调起支付
	 */
	private void payMethodThere(final AuthBaseRequest request){
		showWaitDialog();
		final String url = NitConfig.scanAuthUrl;
		Log.e(TAG,"请求地址："+url);
		new Thread(){
			@Override
			public void run() {
				try {
					String content = FastJsonUtil.toJSONString(request);
					Log.e("发起请求参数：", content);
					String content_type = HttpUtil.CONTENT_TYPE_JSON;
					String jsonStr = HttpUtil.doPos(url,content,content_type);
					Log.e("返回字符串结果：", jsonStr);
					int msg = NetworkUtils.MSG_WHAT_ONE;
					String text = jsonStr;
					sendMessage(msg,text);
				} catch (JSONException e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
				}catch (IOException e){
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
				}
			}
		}.start();

	}

	/**
	 * 支付中时查询订单状态
	 * 轮询方案：https://www.cnblogs.com/ygj0930/p/7657194.html
	 */
	private void queryOrderStatus(){
		if(authResultResponse != null){
			String orderId = authResultResponse.getOut_trade_no();
			//参数实体
			AuthRefreshOrderStateReqData reqData = QueryParamsReqUtil.refreshOrderStateReq(userLoginResData,orderId);
			//发起查询请求
			refreshOrderState(reqData);

		}else{
			ToastUtil.showText(activity,"支付中订单信息为空！",1);
		}
	}

	/**
	 * 获取订单状态
	 */
	private void refreshOrderState(final AuthRefreshOrderStateReqData reqData){

		final String url = NitConfig.refreshOrderStateUrl;
		Log.e(TAG,"获取订单状态请求地址："+url);
		new Thread(){
			@Override
			public void run() {
				try {
					String content = FastJsonUtil.toJSONString(reqData);
					Log.e("获取订单状态请求参数：", content);
					String content_type = HttpUtil.CONTENT_TYPE_JSON;
					String jsonStr = HttpUtil.doPos(url,content,content_type);
					Log.e("获取订单状态返回结果：", jsonStr);
					int msg = NetworkUtils.MSG_WHAT_THREE;
					String text = jsonStr;
					sendMessage(msg,text);
				} catch (JSONException e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
				}catch (IOException e){
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
				}
			}
		}.start();
	}

	private void sendMessage(int what,String text){
		Message msg = new Message();
		msg.what = what;
		msg.obj = text;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String errorJsonText = "";
			switch (msg.what){
				case NetworkUtils.MSG_WHAT_ONEHUNDRED:
					String auth_no = (String) msg.obj;
					payMethodTwo(auth_no);
					break;
				case NetworkUtils.MSG_WHAT_ONE:
					String scanPayJsonStr=(String) msg.obj;
					authPayJsonStr(scanPayJsonStr);
					hideWaitDialog();
					break;
				case NetworkUtils.MSG_WHAT_TWO:
					showWaitDialog("支付状态查询中！");
					queryOrderStatus();
					break;
				case NetworkUtils.MSG_WHAT_THREE:
					String refreshOrderStateStr=(String) msg.obj;
					refreshOrderStateStr(refreshOrderStateStr);
					break;
				case NetworkUtils.MSG_WHAT_FOUR:

					queryOrderStatus();

					break;
				case NetworkUtils.REQUEST_JSON_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					finish();
					break;
				case NetworkUtils.REQUEST_IO_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					finish();
					break;
				case NetworkUtils.REQUEST_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					finish();
					break;
				default:
					break;
			}
		}
	};

	private void authPayJsonStr(String jsonStr){
		try{
			Gson gjson  =  GsonUtils.getGson();
			authResultResponse = gjson.fromJson(jsonStr, AuthResultResponse.class);
			//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
			String return_codeStr = authResultResponse.getReturn_code();
			//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
			String return_msgStr = authResultResponse.getReturn_msg();
			//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
			String result_codeStr = authResultResponse.getResult_code();
			String result_msgStr = authResultResponse.getResult_msg();
			if("01".equals(return_codeStr)) {
				if("01".equals(result_codeStr)){
					speak("预授权成功"+DecimalUtil.branchToElement(authResultResponse.getTotal_amount())+"元");
					//打印小票
					startPrint();

				}else if("03".equals(result_codeStr)){
					handler.sendEmptyMessageDelayed(NetworkUtils.MSG_WHAT_TWO,0);
				}else{
					speak("预授权失败！");
					if(Utils.isNotEmpty(result_msgStr)){
						Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(activity, "预授权失败！", Toast.LENGTH_LONG).show();

					}
				}
			}else{
				speak("预授权失败！");
				if(Utils.isNotEmpty(return_msgStr)){
					Toast.makeText(activity, return_msgStr, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(activity, "预授权失败！", Toast.LENGTH_LONG).show();

				}
			}
		}catch (Exception e){
			e.printStackTrace();
			ToastUtil.showText(activity,"支付结果返回错误！",1);
			finish();
		}

	}


	private void refreshOrderStateStr(String jsonStr){
		try{
			Gson gjson  =  GsonUtils.getGson();
			com.wanding.face.bean.AuthRefreshOrderStateResData resData = gjson.fromJson(jsonStr, com.wanding.face.bean.AuthRefreshOrderStateResData.class);
			//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
			String return_codeStr = resData.getReturn_code();
			//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
			String return_msgStr = resData.getReturn_msg();
			//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
			String result_codeStr = resData.getResult_code();
			String result_msgStr = resData.getResult_msg();
			if("01".equals(return_codeStr)) {
				if("01".equals(result_codeStr)){
					authResultResponse = new AuthResultResponse();
					authResultResponse.setOut_trade_no(resData.getOut_trade_no());
					authResultResponse.setChannel_trade_no(resData.getChannel_trade_no());
					String total_amountStr = resData.getTotal_amount();
					String total_amount = "";
					if(Utils.isNotEmpty(total_amountStr)){
						total_amount = DecimalUtil.elementToBranch(total_amountStr);
					}
					authResultResponse.setTotal_amount(total_amount);
					authResultResponse.setEnd_time(resData.getEnd_time());



					speak("预授权成功"+resData.getTotal_amount()+"元");
					//打印小票
					startPrint();
					hideWaitDialog();
				}else if("03".equals(result_codeStr)){
					queryIndex ++;
					if(queryIndex <= 5 ) {

						handler.sendEmptyMessageDelayed(NetworkUtils.MSG_WHAT_FOUR,5000);

					}else{
						hideWaitDialog();
						speak("支付处理中，请等待收银员确认支付结果！");

					}

				}else{
					speak("预授权失败！");
					if(Utils.isNotEmpty(result_msgStr)){
						Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(activity, "预授权失败！", Toast.LENGTH_LONG).show();

					}

					hideWaitDialog();

				}
			}else{
				speak("预授权失败！");
				if(Utils.isNotEmpty(return_msgStr)){
					Toast.makeText(activity, return_msgStr, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(activity, "预授权失败！", Toast.LENGTH_LONG).show();

				}

				hideWaitDialog();

			}
		}catch (Exception e){
			e.printStackTrace();
			ToastUtil.showText(activity,"支付结果返回错误！",1);
			hideWaitDialog();

		}
	}

	/**
	 * 打印带优惠券的小票
	 */
	private void startPrint(){
		/** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
		index = 1;
		if(Constants.NEW_LAND.equals(posProvider)){
			//初始化打印机
			getPrinter();
			if(printNum.equals("printNumNo")){

			}else if(printNum.equals("printNumOne")){
				//打印
				NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);

			}else if(printNum.equals("printNumTwo")){
				//打印
				NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				try {
					Thread.sleep(NewlandPrintUtil.time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//弹出对话框提示打印下一联

				showPrintTwoDialog();

			}

		}else if(Constants.FUYOU_SF.equals(posProvider)){
			if(printNum.equals("printNumNo")){

				//不执行打印
//            intentToActivity(totalStr);
				finish();

			}else if(printNum.equals("printNumOne")){

				//打印一次
				String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				FuyouPosServiceUtil.printTextReq(activity,printTextStr);

			}else if(printNum.equals("printNumTwo")){
				//打印两次
				String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				FuyouPosServiceUtil.printTextReq(activity,printTextStr);
			}
		}

	}

	/**  打印下一联提示窗口 */
	private void showPrintTwoDialog(){
		View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
		Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(activity,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//打印第二联
				index = 2;
				if(posProvider.equals(NEW_LAND)){
					NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				}else if(posProvider.equals(FUYOU_SF)){
					String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
					FuyouPosServiceUtil.printTextReq(activity,printTextStr);
				}




				myDialog.dismiss();

			}
		});
		myDialog.show();
		myDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle = data.getExtras();
		/**
		 * 富友POS扫码返回
		 */
		if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
			if(bundle != null){
				Log.e(TAG,resultCode+"");
				String reason = "扫码取消";
				String traceNo = "";
				String batchNo = "";
				String ordernumber = "";
				String reason_str = (String) bundle.get("reason");
				String traceNo_str = (String)bundle.getString("traceNo");
				String batchNo_str = (String)bundle.getString("batchNo");
				String ordernumber_str = (String)bundle.getString("ordernumber");

				switch (resultCode) {
					case Activity.RESULT_OK:
						String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
						Log.e("获取扫描结果：", scanCodeStr);
						//如果扫描的二维码为空则不执行支付请求
						if(Utils.isNotEmpty(scanCodeStr)){
							//auth_no	授权码（及扫描二维码值）
							String auth_no = scanCodeStr;

							payMethodTwo(auth_no);

						}else{
							//清空StringBuilder，EditText恢复初始值
							//清空EditText
							pending.delete( 0, pending.length() );
							if(pending.length()<=0){
								etSumMoney.setText("￥0.00");
							}

							ToastUtil.showText(activity,"扫描结果为空！",1);
						}


						break;
					// 扫码取消
					case Activity.RESULT_CANCELED:

						if (Utils.isNotEmpty(reason_str)) {
							Log.e("reason", reason_str);
							reason = reason_str;
						}
						ToastUtil.showText(activity,reason,1);
						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
						break;
					default:
						break;

				}
			}else{
				ToastUtil.showText(activity,"扫码失败！",1);
			}

		}

		/**
		 * 打印返回
		 */
		if(requestCode == FuyouPosServiceUtil.PRINT_REQUEST_CODE){
			if(bundle!=null){
				Log.e(TAG,resultCode+"");
				String reason = "打印取消";
				String traceNo = "";
				String batchNo = "";
				String ordernumber = "";

				String reason_str = (String) bundle.get("reason");
				String traceNo_str = (String)bundle.getString("traceNo");
				String batchNo_str = (String)bundle.getString("batchNo");
				String ordernumber_str = (String)bundle.getString("ordernumber");
				switch (resultCode) {
					case Activity.RESULT_OK:
						if(printNum.equals("printNumTwo")){
							if(index < 2){
								//打印正常
								//弹出对话框提示打印下一联
								showPrintTwoDialog();
							}else{
								//打印完成关闭界面
								finish();
							}

						}else{
							//打印完成关闭界面
							finish();
						}

						break;
					case Activity.RESULT_CANCELED:
						if (Utils.isNotEmpty(reason_str)) {
							reason = reason_str;
							Log.e("reason", reason);
							if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
								//缺纸，不能打印
								ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
							}else {
								ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
							}
						}else{
							ToastUtil.showText(activity,reason,1);
						}
						finish();

						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
						break;
					default:
						break;

				}
			}else{
				ToastUtil.showText(activity,"打印返回数据为空！",1);
			}

		}
		/**
		 * 支付返回
		 */
		if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
			if(bundle != null){
				Log.e(TAG,resultCode+"");
				String reason = "支付取消";
				String traceNo = "";
				String batchNo = "";
				String ordernumber = "";

				String reason_str = (String) bundle.get("reason");
				String traceNo_str = (String)bundle.getString("traceNo");
				String batchNo_str = (String)bundle.getString("batchNo");
				String ordernumber_str = (String)bundle.getString("ordernumber");
				switch (resultCode) {
					// 支付成功
					case Activity.RESULT_OK:


						break;
					// 支付取消
					case Activity.RESULT_CANCELED:
						if (Utils.isNotEmpty(reason_str)) {
							reason = reason_str;
							Log.e("reason", reason);
						}
						ToastUtil.showText(activity,reason,1);
						finish();

						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

						break;
					default:
						break;
				}
			}else{
				ToastUtil.showText(activity,"支付返回数据为空！",1);
			}

		}
	}



	/**
	 * GridView的Item事件回调
	 */
	private AdapterView.OnItemClickListener payTypeCheckedLinstener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(Utils.isFastClick()){
				return;
			}
			if(position == 0&&list.get(position).getText().equals("微信")){
				payType = Constants.PAYTYPE_010WX;
				payMethodOne();
			}else if(position == 1&&list.get(position).getText().equals("支付宝")){
				payType = Constants.PAYTYPE_020ALI;
				payMethodOne();
			}else{
				payType = "";
				ToastUtil.showText(activity,"请选择付款类型",1);
			}

		}
	};


	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		super.onClick(v);
		switch (v.getId()){
			case R.id.menu_title_imageView:
				finish();
				break;
			default:
				break;

		}
	}

}
