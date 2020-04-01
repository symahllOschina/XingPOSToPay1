package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.RandomStringGenerator;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;

/** 
 * 刷卡消费Activity
 */
public class CardPayActivity extends BaseActivity implements OnClickListener{
	
	private Context context = CardPayActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	private EditText etSumMoney;
	private ImageButton imagEliminate;
	private TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight,tvNine,tvZero,tvSpot;
	private TextView tvOk;
	private StringBuilder pending = new StringBuilder();
	
	private String payType = "";//分别顺序对应：040,010,020,030,050
	
	public static final int REQUEST_CODE = 1;
	private Dialog hintDialog;// 加载数据时对话框
	
	private UserLoginResData loginInitData;
	private CardPaymentDate cardPaymentDate;
	private String pos_order_noStr;
	/** POS支付成功返回的订单号，金额  */
	private String orderid_scan = "";
	private String transamount = "";//
	/** 是否为测试环境，默认为测试环境 */
    private boolean testState = true;
    

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private static final String TAG = "CardPayActivity";
//    protected Handler mainHandler;

	/**
	 * 富友(打印机)AIDL服务
	 */
	private PrintInterface printService = null;
	private PrintReceiver printReceiver;
	private ServiceConnection printServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			printService = PrintInterface.Stub.asInterface(arg1);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}

	};

	class PrintReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("result");
//			etBack.setText("reason："+result);
		}
	}

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;

//	private boolean isSettlement = false;//是否调用结算SDK，是为true；
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_pay_activity);
		initView();
		initData();
		synthesizer = MainActivity.synthesizer;
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
		}else if(posProvider.equals(FUYOU_SF)){
			initPrintService();
		}

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
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if(defMoney.equals("")||defMoney.equals("0")){
			etSumMoney.setHint("￥0.00");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(posProvider.equals(NEW_LAND)){

		}else if(posProvider.equals(FUYOU_SF)){
			if(null != printReceiver){
				unregisterReceiver(printReceiver);
			}
			unbindService(printServiceConnection);
		}
	}

   
	
	private void initData(){
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if(defMoney.equals("")||defMoney.equals("0")){
			etSumMoney.setHint("￥0.00");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
		}
		try {
			loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**  初始化控件 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		etSumMoney = (EditText) findViewById(R.id.content_layout_etSumMoney);
		//强制隐藏Android输入法窗体 
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//EditText始终不弹出软件键盘 
		etSumMoney.setInputType(InputType.TYPE_NULL); 
		EditTextUtils.setPricePoint(etSumMoney);
        imm.hideSoftInputFromWindow(etSumMoney.getWindowToken(),0); 
		tvOne = (TextView) findViewById(R.id.content_layout_tvOne);
		tvTwo = (TextView) findViewById(R.id.content_layout_tvTwo);
		tvThree = (TextView) findViewById(R.id.content_layout_tvThree);
		tvFour = (TextView) findViewById(R.id.content_layout_tvFour);
		tvFive = (TextView) findViewById(R.id.content_layout_tvFive);
		tvSix = (TextView) findViewById(R.id.content_layout_tvSix);
		tvSeven = (TextView) findViewById(R.id.content_layout_tvSeven);
		tvEight = (TextView) findViewById(R.id.content_layout_tvEight);
		tvNine = (TextView) findViewById(R.id.content_layout_tvNine);
		tvZero = (TextView) findViewById(R.id.content_layout_tvZero);
		tvSpot = (TextView) findViewById(R.id.content_layout_tvSpot);
		imagEliminate = (ImageButton) findViewById(R.id.content_layout_imagEliminate);
		tvOk = (TextView) findViewById(R.id.card_pay_tvOk);
		
		
		
		tvTitle.setText("消费");
		imgBack.setOnClickListener(this);
		tvOne.setOnClickListener(this);
		tvTwo.setOnClickListener(this);
		tvThree.setOnClickListener(this);
		tvFour.setOnClickListener(this);
		tvFive.setOnClickListener(this);
		tvSix.setOnClickListener(this);
		tvSeven.setOnClickListener(this);
		tvEight.setOnClickListener(this);
		tvNine.setOnClickListener(this);
		tvZero.setOnClickListener(this);
		tvSpot.setOnClickListener(this);
		imagEliminate.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		
	}
	
	
    
    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String total) {
//        String text = mInput.getText().toString();
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(mInput.getText())) {
//            text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
//            mInput.setText(text);
//        }
//        String text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
    	//刷卡，微信，支付宝，银联，翼支付，分别顺序对应：040,010,020,030,050
    	String payTypeStr = "";
		if(payType.equals("040")){
			payTypeStr = "银行卡收款";
		}else if(payType.equals("010")){
			payTypeStr = "微信收款";
		}else if(payType.equals("020")){
			payTypeStr = "支付宝收款";
		}else if(payType.equals("030")){
			payTypeStr = "银联收款";
		}else if(payType.equals("050")){
			payTypeStr = "翼支付收款";
		}
    	
        String text = payTypeStr+total+"元";
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        	Log.e("error code :", result+" method:" + method );
        }
    }

	private void initPrintService(){
		printRegisterReceiver();
		Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
		printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
		printIntent.setPackage("com.fuyousf.android.fuious");
		bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void printRegisterReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.fuyousf.android.fuious.service.print");
		printReceiver = new PrintReceiver();
		registerReceiver(printReceiver, intentFilter);
	}

	/** 测试环境POS接口支付保存订单号 */
	private void saveOrderNoToService(){
		//insertChannelIdTest，入参：goodsPrice = 金额，transactionId = 支付返回的UD或9开头的 ，refundCode = order_no(自己生成的订单号)
		final String url = NitConfig.insertChannelIdTestUrl;
		new Thread(){
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("goodsPrice","0.01");  
					userJSON.put("transactionId",orderid_scan); 
					userJSON.put("refundCode",pos_order_noStr);  

					String content = String.valueOf(userJSON);
					Log.e("保存测试数据发起请求参数：", content); 
					  
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();  
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();  
                   connection.setConnectTimeout(5000);  
                   connection.setRequestMethod("POST");  
                   connection.setDoOutput(true);  
                   connection.setRequestProperty("User-Agent", "Fiddler");  
                   connection.setRequestProperty("Content-Type", "application/json");  
                   connection.setRequestProperty("Charset", "UTF-8");  
                   OutputStream os = connection.getOutputStream();  
                   os.write(content.getBytes());  
                   os.close();  
                   int  code = connection.getResponseCode();
                   Log.e("保存测试数据返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("保存测试数据返回JSON值：", jsonStr);
                	   Message msg=new Message();
   					   msg.what=1;
   					   msg.obj=jsonStr;
   				       mHandler.sendMessage(msg);
                	    
                   }else{
                	   Message msg=new Message();
   				       msg.what=404;
   				       mHandler.sendMessage(msg);
                   }
                   
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("JSONException", "JSONException");
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("IOException", "IOException");
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				}  
				
			};
		}.start();
	}

	/**
	 * 查询是否有优惠券
	 * isApi:是否为API支付，还是SDK支付
	 */
	private void getCardStock(){

			hintDialog=CustomDialog.CreateDialog(getContext(), "    查询优惠券...");
			hintDialog.show();
			hintDialog.setCancelable(false);


		final String url = NitConfig.getCardStockUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("terminalId",loginInitData.getTerminal_id());
					userJSON.put("merchantNo",loginInitData.getMerchant_no());
//					userJSON.put("terminalId","11407");
//					userJSON.put("merchantNo","1000853");

					userJSON.put("orderId",pos_order_noStr);
					userJSON.put("amount",cardPaymentDate.getTransamount());
					userJSON.put("payWay",FuyouPrintUtil.getPayWay("0"));
					userJSON.put("payTime",DateTimeUtil.dateToStamp(cardPaymentDate.getTranslocaldate()+cardPaymentDate.getTranslocaltime()));




					String content = String.valueOf(userJSON);
					Log.e("查询卡劵发起请求参数：", content);

					HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
					//HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();
					connection.setConnectTimeout(60000);
					connection.setReadTimeout(60000);
					connection.setRequestMethod("POST");
					connection.setDoOutput(true);
					connection.setRequestProperty("User-Agent", "Fiddler");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty("Charset", "UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write(content.getBytes());
					os.close();
					int  code = connection.getResponseCode();
					Log.e("查询卡劵返回状态吗：", code+"");
					if(code == 200){

						InputStream is = connection.getInputStream();
						//下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型
						String jsonStr = HttpJsonReqUtil.readString(is);
						Log.e("查询卡劵返回JSON值：", jsonStr);
						Message msg=new Message();
						msg.what=4;
						msg.obj=jsonStr;
						mHandler.sendMessage(msg);


					}else{
						Message msg=new Message();
						msg.what=404;
						mHandler.sendMessage(msg);
					}


				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("JSONException", "JSONException");
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e("IOException", "IOException");
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String saveTestJsonStr=(String) msg.obj;
				//{"isSuccess":true,"errorCode":null,"errorMessage":null,"data":"UD180413A01175300610011747021785"}
				String transamountStr = transamount;
				//播放语音
				speak(transamountStr);
//				intentToActivity(transamountStr);
				break;
			case 4:
				String cardStockJson = (String) msg.obj;
				cardStockJson(cardStockJson);
				if(hintDialog!=null&&hintDialog.isShowing()){

					hintDialog.dismiss();
				}
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				hintDialog.dismiss();
				//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
				break;
			}
		};
	};

	private void cardStockJson(String json){
		/*{
			"code": "000000",
				"msg": "SUCCESS",
				"data": {
			"url": "http://beta.soar.xin/ledticket/index.html?outer=CONSUME&orderId=20190628155108388039616656326375"
		},
			"timestamp": "1561708332261",
				"subCode": "000000",
				"subMsg": "查询成功"
		}*/
		try {
			if(Utils.isNotEmpty(json)){
				JSONObject job = new JSONObject(json);
				String code = job.getString("code");
				String msg = job.getString("msg");
				if("000000".equals(code)){
					String subCode = job.getString("subCode");
					String subMsg = job.getString("subMsg");
					if("000000".equals(subCode)){
						JSONObject dataJob = new JSONObject(job.getString("data"));
						String url = dataJob.getString("url");


						FuyouPrintUtil.cardStockPrintText(activity,printService,url);
						cardPaymentDate.setUrl(url);

						/**
						 * 下面是调用帮助类将一个对象以序列化的方式保存
						 * 方便我们在其他界面调用，类似于Intent携带数据
						 */
						try {
							MySerialize.saveObject("cardPayOrder",getContext(),MySerialize.serialize(cardPaymentDate));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
						Boolean cardPayValue = true;
						int cardOption = 11;
						sharedPreferencesUtil.put("cardPayYes", cardPayValue);
						sharedPreferencesUtil.put("cardPayType", "pay");
						sharedPreferencesUtil.put("cardOption", cardOption);


					}else{
						ToastUtil.showText(activity,subMsg,1);
					}
				}else{
					ToastUtil.showText(activity,msg,1);
				}
			}else{
				ToastUtil.showText(activity,"查询失败！",1);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}



	}
	
	/**
	 *  星POS 内置接口支付
	 *  payToatl:支付金额（最终提交以元为单位）
	 */
	private void XingPosServicePay(String payType,String total_fee){
		//设备号
		String deviceNum = loginInitData.getTrmNo_pos();
	    pos_order_noStr = RandomStringGenerator.getNlRandomNum(deviceNum);
	    Log.e("生成的订单号：", pos_order_noStr);
		NewPosServiceUtil.payReq(CardPayActivity.this, payType, total_fee,pos_order_noStr,loginInitData);
		
	}

	/**
	 * 富友POS 内置支付
	 * payTotal:支付金额（最终提交以分为单位）
	 */
	private void FuyouPosServicePay(String payTotal){
//		String total_feeStr = DecimalUtil.elementToBranch(payTotal);
		String total_feeStr = FieldTypeUtil.makeFieldAmount(payTotal);
		Log.e("SDK提交带规则金额",total_feeStr);
		//设备号
		String deviceNum = loginInitData.getTrmNo_pos();
		pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
		Log.e("生成的订单号：", pos_order_noStr);
		//boolean isFrontCamera = false;//是否开启前置摄像头
		boolean isFrontCamera = false;
		FuyouPosServiceUtil.payReq(CardPayActivity.this, payType, total_feeStr,pos_order_noStr,isFrontCamera);
	}

	/**
	 * 调结算SDK（清空终端流水）

	private void emptyPosOrder(){
		isSettlement = true;
		FuyouPosServiceUtil.settleReq(CardPayActivity.this);

	}
	 */
	
	/** 金额：transamountStr ，交易类型  */
//	private void intentToActivity(String transamountStr){
//		Intent in = new Intent();
//		in.setClass(context, PaySuccessActivity.class);
//		in.putExtra("totalKey", transamountStr);
//		in.putExtra("payTypeKey", payType);
//		if(NitConfig.isTest.equals("test")){
//			in.putExtra("pos_order", pos_order_noStr);
//		}
//		startActivity(in);
//	}
	
	private void intentActivity(){
		Intent in = new Intent();
		in.setClass(context, PayErrorActivity.class);
		in.putExtra("optionTypeStr", "010");
		startActivity(in);
	};
	

	
	private boolean judje1() {
        String a = "+-*/.";
        int[] b = new int[a.length()];
        int max;
        for (int i = 0; i < a.length(); i++) {
            String c = "" + a.charAt(i);
            b[i] = pending.lastIndexOf(c);
        }
        Arrays.sort(b);
        if (b[a.length() - 1] == -1) {
            max = 0;
        } else {
            max = b[a.length() - 1];
        }
        if (pending.indexOf(".", max) == -1) {
            return true;
        } else {
            return false;
        }
    }
	 
	 private String getTextNum(StringBuilder sbu){
		 String str = "";
		 //判断是否整数
		 
		 return str;
	 } 
	
	@Override
	public void onClick(View v) {
		Intent in = null;
		String etTextStr,total_feeStr;
		int last = 0;
        if(pending.length()!=0)
        {
            last = pending.codePointAt(pending.length()-1);

        }
		
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.content_layout_tvOne:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("1");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvTwo:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("2");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvThree:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("3");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvFour:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("4");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvFive:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("5");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSix:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("6");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSeven:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("7");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvEight:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("8");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvNine:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("9");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvZero:
			if(pending.toString().equals("0.0")){
				return;
			}
        	pending = pending.append("0");
        	if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
        	//输入内容头为0的情况下，只能输入小数点
        	if (pending.toString().startsWith("0") && pending.toString().trim().length() > 1) {
                if (!pending.toString().substring(1, 2).equals(".")) {
                	pending = pending.deleteCharAt(pending.length()-1); 
                    return;
                }
            }
        	etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSpot:
			if(pending.length()>0){
				if (judje1()) {
	                pending = pending.append(".");
	                if (pending.toString().contains(".")) {
	    	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	    	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	    	            	pending = pending.deleteCharAt(pending.length()-1);  
	    	            }
	    	        }
	                etSumMoney.setText("￥"+pending);
	            }
			}
			
			break;
		case R.id.content_layout_imagEliminate:
			//删除
			if (pending.length() != 0) {
                pending = pending.delete(pending.length() - 1, pending.length());
                etSumMoney.setText("￥"+pending);
                if(pending.toString().equals("0")){
                	//清空pending
                	pending.delete( 0, pending.length() );
                }
                if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
            }
			//清空
//			pending = pending.delete(0, pending.length());
//			tvSumMoney.setText(pending);
			break;
		case R.id.card_pay_tvOk://确认
			try {
				payType = "040";
				etTextStr = pending.toString();
				Log.e("输入框文本text值：", etTextStr);
				if(!etTextStr.equals(".")){
					Log.e("输入框文本text值：", pending.toString());
					total_feeStr = DecimalUtil.scaleNumber(etTextStr);
					if(DecimalUtil.isEqual(total_feeStr)==1){
						if(posProvider.equals(NEW_LAND)){
							Log.e("支付通道：", "星POS");
							XingPosServicePay(payType,total_feeStr);
						}else if(posProvider.equals(FUYOU_SF)){
							Log.e("支付通道：", "富友POS");
							FuyouPosServicePay(total_feeStr);
						}

				    }else{
				    	Toast.makeText(getContext(), "请输出有效金额！", Toast.LENGTH_LONG).show();
				    }
					
				}else{
					Log.e("输入金额有误！", "false");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		
		}
	}

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail支付返回信息：", txndetail);
			try {
				JSONObject job = new JSONObject(txndetail);
				if(!payType.equals("040")&&!payType.equals("030")){
					orderid_scan = job.getString("orderid_scan");
				}
				transamount = job.getString("transamount");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//清空StringBuilder，EditText恢复初始值
			//清空EditText
			pending.delete( 0, pending.length() );
			if(pending.length()<=0){
				etSumMoney.setText("￥0.00");
			}
			//如果是银行卡消费保存消费返回信息
			if(payType.equals("040")){
				Gson gjson  =  GsonUtils.getGson();
				CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);
				/**
				 * 下面是调用帮助类将一个对象以序列化的方式保存
				 * 方便我们在其他界面调用，类似于Intent携带数据
				 */
				try {
					MySerialize.saveObject("cardPayOrder",getContext(),MySerialize.serialize(posResult));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
				Boolean cardPayValue = true;
				int cardOption = 11;
				sharedPreferencesUtil.put("cardPayYes", cardPayValue);
				sharedPreferencesUtil.put("cardPayType", "pay");
				sharedPreferencesUtil.put("cardOption", cardOption);
			}
			//测试环境将订单保存服务器
			if(NitConfig.isTest.equals("test")){
				Log.e("服务环境：", "测试环境");
				saveOrderNoToService();
			}else{
				String transamountStr = transamount;
				Log.e("服务环境：", "生产环境");
				Log.e("返回的支付金额信息222：", transamountStr);
				//播放语音
				speak(transamountStr);
//				intentToActivity(transamountStr);
			}

		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		//如果是银行卡消费保存消费返回信息
		if(payType.equals("040")){
			String amountStr = bundle.getString("amount");//金额
			String traceNoStr = bundle.getString("traceNo");//凭证号
			String batchNoStr = bundle.getString("batchNo");//批次号
			String referenceNoStr = bundle.getString("referenceNo");//参考号
			String cardNoStr = bundle.getString("cardNo");//卡号
			String typeStr = bundle.getString("type");//卡类型
			String issueStr = bundle.getString("issue");//发卡行
			String dateStr = bundle.getString("date");//日期
			String timeStr = bundle.getString("time");//时间
			String orderNumberStr = bundle.getString("orderNumber");//订单流水号
			String merchantldStr = bundle.getString("merchantld");//商户号
			String terminalldStr = bundle.getString("terminalld");//终端号
			String merchantNameStr = bundle.getString("merchantName");//商户名称
			String transactionTypeStr = bundle.getString("transactionType");//交易类型

			Log.e("返回的支付金额信息：", amountStr);
			Log.e("交易参考号：", referenceNoStr);
			Log.e("返回的卡类型：", typeStr);
			String totalStr = DecimalUtil.branchToElement(amountStr);
			//播放语音
			speak(totalStr);

			//获取系统年份
			Calendar date = Calendar.getInstance();
			String year = String.valueOf(date.get(Calendar.YEAR));
			String dateTimeStr = year + dateStr +  timeStr;
			dateStr = year + dateStr;
			Log.e("手动拼接日期时间：",dateTimeStr);
			Log.e("手动拼接日期：",dateStr);

			cardPaymentDate =  new CardPaymentDate();
			cardPaymentDate.setPriaccount(cardNoStr);
			cardPaymentDate.setAcqno("");
			cardPaymentDate.setIisno(issueStr);
			cardPaymentDate.setSystraceno(traceNoStr);
			cardPaymentDate.setTranslocaldate(dateStr);
			cardPaymentDate.setTranslocaltime(timeStr);
			cardPaymentDate.setTransamount(totalStr);//此处金额是分转元之后的金额

			/**
			 * 下面是调用帮助类将一个对象以序列化的方式保存
			 * 方便我们在其他界面调用，类似于Intent携带数据
			 */
			try {
				MySerialize.saveObject("cardPayOrder",getContext(),MySerialize.serialize(cardPaymentDate));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
			Boolean cardPayValue = true;
			int cardOption = 11;
			sharedPreferencesUtil.put("cardPayYes", cardPayValue);
			sharedPreferencesUtil.put("cardPayType", "pay");
			sharedPreferencesUtil.put("cardOption", cardOption);

		}

		//清空StringBuilder，EditText恢复初始值
		//清空EditText
		pending.delete( 0, pending.length() );
		if(pending.length()<=0){
			etSumMoney.setText("￥0.00");
		}

		//查询商家优惠券信息
		boolean isQueryCoupons = loginInitData.isQueryCoupons();
		if(isQueryCoupons){
			getCardStock();
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			Bundle bundle=data.getExtras();
			if (requestCode == 1&&bundle != null) {
				switch (resultCode) {
				 // 支付成功
				  case Activity.RESULT_OK:
					  if(posProvider.equals(NEW_LAND)){
						  newlandResult(bundle);
					  }else if(posProvider.equals(FUYOU_SF)){

					  	fuyouResult(bundle);

					  }
				    break;
				// 支付取消
				 case Activity.RESULT_CANCELED:
					 String reason = bundle.getString("reason");
					 Log.e("失败返回值", reason);
					 if(posProvider.equals(NEW_LAND)){

					 }else if(posProvider.equals(FUYOU_SF)){
						 if (Utils.isNotEmpty(reason)) {
							 if(reason.equals("交易流水满,请结算")){
//								Toast.makeText(this, "交易流水已满，请结算啊啊啊啊啊", 0*100).show();
//								 //调结算SDK（清空终端流水）
//								 emptyPosOrder();
							 }
						 }
					 }


				    //清空StringBuilder，EditText恢复初始值
					//清空EditText
					pending.delete( 0, pending.length() );
					if(pending.length()<=0){
			        	etSumMoney.setText("￥0.00");
			        }
				     break;
				
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
