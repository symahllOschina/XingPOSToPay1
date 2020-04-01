package com.wanding.xingpos.activity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.newland.newpaysdk.model.NldRefundResult;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.PosRefundReqData;
import com.wanding.xingpos.bean.PosRefundResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.RandomStringGenerator;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/** 扫码退款界面 */
public class ScanRefundActivity extends BaseActivity implements OnClickListener{
	public static final int REQUEST_CODE = 1;
	
	
	
	private ImageView imgBack;
	private TextView tvTitle;
	private RelativeLayout layoutOrderId;
	private LinearLayout layoutMoney;
	private EditText etOrderId,etOrderMoney;
	private ImageView imagScan;
	private TextView tvOk;
	
	private Dialog hintDialog;// 加载数据时对话框
	private String scanCodeStr;//扫描返回结果
	
	private Context context = ScanRefundActivity.this;
	private UserLoginResData posPublicData;

//	private String etMoneyTextStr;
	AidlDeviceService aidlDeviceService = null;
    AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
	private boolean cameType = true;
    private String TAG = "lyc";
    // 主控制类，所有合成控制方法从这个类开始
    public static MySyntherizer synthesizer;
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

    /** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
    private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;

	/** 退款步骤 refundOption = 1表示查询订单，2表示输入金额退款  */
	private int refundOption = 1;
	private JSONObject dataJsonObj;
	/**  退款时查单返回的支付方式（仅支持富友POS机，打印小票时用）  */
	private String refund_payTypeStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_refund_activity);
		synthesizer = MainActivity.synthesizer;
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){
			initPrintService();
		}
		initView();
		initData();
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

		if(posProvider.equals(NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
			aidlScanner = null;
		}else if(posProvider.equals(FUYOU_SF)){
			if(null != printReceiver){
				unregisterReceiver(printReceiver);
			}
			unbindService(printServiceConnection);
		}
		Log.e(TAG, "释放资源成功");
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
        }

    }
    
    /**
     * 初始化扫码设备
     */
    public void initScanner() {
        try {
            if (aidlScanner == null)
			{
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
    				if(scanCodeStr!=null&&!scanCodeStr.equals("")){
    					Log.e("扫描返回扫描结果：", scanCodeStr);

    					etOrderId.setText(scanCodeStr);
    			    	
    				}else{
    					
    				}

                }

                @Override
                public void onFinish() throws RemoteException {
//                    showMsgOnTextView("onFinish...");

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
//                    showMsgOnTextView("onError..。。"+arg0+"-----"+arg1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
					if(scanCodeStr!=null&&!scanCodeStr.equals("")){
						//auth_no	授权码（及扫描二维码值）
						String auth_no = scanCodeStr;
						Log.e("后置扫码值：", auth_no);


						Message msg = new Message();
						msg.obj = auth_no;
						msg.what = 100;
						mHandler.sendMessage(msg);



					}else{

						Log.e("后置扫码值：", "为空");
					}

				}

				@Override
				public void onFinish() throws RemoteException {
//					showMsgOnTextView("onFinish...");

				}

				@Override
				public void onError(int arg0, String arg1) throws RemoteException {
//					showMsgOnTextView("onError..。。"+arg0+"-----"+arg1);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void initData(){
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(context, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}

		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(context, "printing");
        //取出保存的默认值
        printNum = (String) sharedPreferencesUtil.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil.getSharedPreference("isDefaultKey", isDefault);
		try {
			posPublicData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		layoutOrderId = findViewById(R.id.scan_refund_layoutRefundOrderId);
		layoutMoney = findViewById(R.id.scan_refund_layoutRefundMoney);
		//先隐藏退款金额输入框
		layoutMoney.setVisibility(View.GONE);

		etOrderId = (EditText) findViewById(R.id.scan_refund_etOrderId);
		etOrderMoney = (EditText) findViewById(R.id.scan_refund_etOrderMoney);
		EditTextUtils.setPricePoint(etOrderMoney);
		imagScan = (ImageView) findViewById(R.id.scan_refund_imagScan);
		tvOk = (TextView) findViewById(R.id.scan_refund_tvOk);
		
		tvTitle.setText("扫码退款");
		imgBack.setOnClickListener(this);
		imagScan.setOnClickListener(this);
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
    
    	
        String text = "扫码退货"+total+"元";
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
	
	/** 获取自己生成的订单号   */
	private void getRefundOrderId(final String etOrderIdTextStr){
		hintDialog=CustomDialog.CreateDialog(getContext(), "    查询中...");
		hintDialog.show();
		hintDialog.setCanceledOnTouchOutside(false);
		final String url = NitConfig.getPosPayOrderId;
		new Thread(){
			@Override
			public void run() {
				
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("refundCode",etOrderIdTextStr);
					if(posProvider.equals(NEW_LAND)){
						userJSON.put("SDKType","NEWLAND_SDK");
					}else if(posProvider.equals(FUYOU_SF)){
						userJSON.put("SDKType","FUIOU_SDK");
					}
					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
					

                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();  
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();  
                   connection.setConnectTimeout(60000);
                   connection.setReadTimeout(60000);
                   connection.setRequestMethod("POST");  
                   connection.setDoOutput(true);  
                   connection.setRequestProperty("User-Agent", "Fiddler");  
                   connection.setRequestProperty("Content-Type", "application/json");  
                   connection.setRequestProperty("Charset", "UTF-8");
                   connection.setChunkedStreamingMode(0);//请求超时时会导致自动重新请求，这里设置不重新请求
                   OutputStream os = connection.getOutputStream();  
                   os.write(content.getBytes());  
                   os.close();  
                   int  code = connection.getResponseCode();
                   Log.e("返回状态吗：", code+"");
                   if(code == 200){
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("返回JSON值：", jsonStr);
                	   Message msg=new Message();
   					   msg.what=2;
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
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				}   catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				} 
				
			};
		}.start();
	}
	
	/** 扫描结果发起退款请求  */
	private void payRequestMethood(final String url,final PosRefundReqData posBean){
		hintDialog=CustomDialog.CreateDialog(getContext(), "    退款中...");
		hintDialog.show();
		hintDialog.setCanceledOnTouchOutside(false);
		new Thread(){
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("pay_ver",posBean.getPay_ver());   
					userJSON.put("pay_type",posBean.getPay_type());   
					userJSON.put("service_id",posBean.getService_id());   
					userJSON.put("merchant_no",posBean.getMerchant_no());   
					userJSON.put("terminal_id",posBean.getTerminal_id());   
					userJSON.put("terminal_trace",posBean.getTerminal_trace());   
					userJSON.put("terminal_time",posBean.getTerminal_time());
					userJSON.put("refund_fee",posBean.getRefund_fee());   
					userJSON.put("out_trade_no",posBean.getOut_trade_no());   
					userJSON.put("operator_id",posBean.getOperator_id());   
					userJSON.put("key_sign",posBean.getKey_sign());   

					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();  
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();  
                   connection.setConnectTimeout(60000);
                   connection.setReadTimeout(60000);
                   connection.setRequestMethod("POST");  
                   connection.setDoOutput(true);  
                   connection.setRequestProperty("User-Agent", "Fiddler");  
                   connection.setRequestProperty("Content-Type", "application/json");  
                   connection.setRequestProperty("Charset", "UTF-8");
                   connection.setChunkedStreamingMode(0);//请求超时时会导致自动重新请求，这里设置不重新请求
                   OutputStream os = connection.getOutputStream();  
                   os.write(content.getBytes());  
                   os.close();  
                   int  code = connection.getResponseCode();
                   Log.e("返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("返回JSON值：", jsonStr);
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
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				}

			};
		}.start();
	}

	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				String auth_no = (String) msg.obj;
				etOrderId.setText(auth_no);
				break;
			case 1:
				String jsonStr=(String) msg.obj;
				//然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}  
         	   	Gson gjson  =  GsonUtils.getGson();
        		PosRefundResData posResult = gjson.fromJson(jsonStr, PosRefundResData.class);
        		//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        		String return_codeStr = posResult.getReturn_code();
        		//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        		String return_msgStr = posResult.getReturn_msg();
        		//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        		String result_codeStr = posResult.getResult_code();
        		if(return_codeStr.equals("01")){
        			if(result_codeStr.equals("01")){
        				
        				/**
        				 * 下面是调用帮助类将一个对象以序列化的方式保存
        				 * 方便我们在其他界面调用，类似于Intent携带数据
        				 */
        				try {
        					MySerialize.saveObject("refundOrder",getContext(),MySerialize.serialize(posResult));
        				} catch (IOException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				
        				//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
        				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
        				Boolean scanPayValue = true;
        				Boolean payServiceType = true;
        				String scanPayTypeValue = "refund";//pay:支付，refund:退款
        				sharedPreferencesUtil.put("scanPayYes", scanPayValue);
        				sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
        				sharedPreferencesUtil.put("payServiceType", payServiceType);
        				
        				
        				startPrint(posResult);
//        				Toast.makeText(getContext(), "退款成功!", Toast.LENGTH_LONG).show();
        				
        			}else if(result_codeStr.equals("03")){
        				Toast.makeText(getContext(), "退款中!", Toast.LENGTH_LONG).show();
        			}else{
        				intentActivity();
        				
//        				Toast.makeText(getContext(), "退款失败!", Toast.LENGTH_LONG).show();
        			}
        		}else{
        			intentActivity();
        			Toast.makeText(getContext(), return_msgStr, Toast.LENGTH_LONG).show();
        		}
				if(hintDialog!=null){
					hintDialog.dismiss();
				}
        		etOrderId.setText("");
				break;
			case 2:
				String orderIdJSONStr=(String) msg.obj;
				//{"data":{"orderId":"20180813180710457012455681976813","refundfCode":"000079","payWay":"WX","way":"SDK"}}
				try {
					JSONObject job = new JSONObject(orderIdJSONStr);
					boolean isSuccess = job.getBoolean("isSuccess");
					if(isSuccess){
						String dataStr = job.getString("data");
						JSONObject dataJob = new JSONObject(dataStr);
						String way = dataJob.getString("way");
						String refundfCode = dataJob.getString("refundfCode");
						String total_feeStr = "";
						dataJsonObj = dataJob;
						refundOption = 2;
						//判断支付通道
						if(way.equals("SDK")){
							Log.e("二维码支付服务类型：", "第三方");
							if(posProvider.equals(NEW_LAND)){
								Log.e("二维码支付服务类型：", "星POS");
								layoutOrderId.setVisibility(View.GONE);
								layoutMoney.setVisibility(View.VISIBLE);


							}else if(posProvider.equals(FUYOU_SF)){
								Log.e("二维码支付服务类型：", "富友POS");
								layoutOrderId.setVisibility(View.GONE);
								layoutMoney.setVisibility(View.VISIBLE);
								etOrderMoney.setEnabled(false);
								etOrderMoney.setBackgroundColor(getResources().getColor(R.color.gray_e5e5e5));
								etOrderMoney.setHint("   该订单只支持默认全额退");
								etOrderMoney.setHintTextColor(getResources().getColor(R.color.red_d05450));


							}

//							if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
//								//金额为元
//								total_feeStr = DecimalUtil.scaleNumber(etMoneyTextStr);
//							}
//							if(posProvider.equals(NEW_LAND)){
//								Log.e("二维码支付服务类型：", "星POS");
//								NewPosServiceUtil.refundReq(ScanRefundActivity.this, refundfCode,total_feeStr, posPublicData);
//							}else if(posProvider.equals(FUYOU_SF)){
//								Log.e("二维码支付服务类型：", "富友POS");
//								//支付方式
//								String payTypeStr = dataJob.getString("payWay");
//								//金额
//								total_feeStr = FieldTypeUtil.makeFieldAmount(etMoneyTextStr);
//								//凭证号
//								String oldTraceStr = dataJob.getString("refundfCode");
//								//设备号
//								String deviceNum = posPublicData.getTrmNo_pos();
//								String pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
//								FuyouPosServiceUtil.refundReq(ScanRefundActivity.this,payTypeStr,total_feeStr,oldTraceStr,pos_order_noStr);
//
//							}

						}else{
							Log.e("二维码支付服务类型：", "默认自己的");
							layoutOrderId.setVisibility(View.GONE);
							layoutMoney.setVisibility(View.VISIBLE);


//							if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
//								//金额为分
//								total_feeStr = DecimalUtil.elementToBranch(etMoneyTextStr);
//							}
//							PosRefundReqData posBean = PayRequestUtil.refundReq(total_feeStr,refundfCode,posPublicData,posProvider);
//							//付款二维码内容(发起退款请求)
//							String url = NitConfig.refundUrl;
//							payRequestMethood(url,posBean);

						}
					}else{



						String errorMessage = job.getString("errorMessage");
						Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(hintDialog!=null){
					hintDialog.dismiss();
				}
				etOrderId.setText("");
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				if(hintDialog!=null){
					hintDialog.dismiss();
				}
        		etOrderId.setText("");
				break;
			}
		};
	};
	
	private void startPrint(PosRefundResData refundResData){
		String totalStr = DecimalUtil.branchToElement(refundResData.getRefund_fee());
		//播放语音
		speak(totalStr);
		/**
		 * 根据pos机厂商不同调用不同的打印设备
		 */
		int index = 1;
		if(posProvider.equals(NEW_LAND)){
			//初始化打印机
			getPrinter();

            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if(printNum.equals("printNumNo")){
                intentToActivity();
            }else if(printNum.equals("printNumOne")){
                //打印一次
                NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                intentToActivity();
            }else if(printNum.equals("printNumTwo")){
                //打印两次
                NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(NewlandPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPrintTwoDialog(refundResData);
            }

		}else if(posProvider.equals(FUYOU_SF)){

            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if(printNum.equals("printNumNo")){
                intentToActivity();
            }else if(printNum.equals("printNumOne")){
                //打印一次
                FuyouPrintUtil.refundSuccessPrintText(getContext(), printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                intentToActivity();
            }else if(printNum.equals("printNumTwo")){
                //打印两次
                FuyouPrintUtil.refundSuccessPrintText(getContext(), printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(FuyouPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPrintTwoDialog(refundResData);
            }
		}


		
	}

    /**  打印下一联提示窗口 */
    private void showPrintTwoDialog(final PosRefundResData refundResData){
        View view = LayoutInflater.from(context).inflate(R.layout.printtwo_dialog_activity, null);
        TextView btok = (TextView) view.findViewById(R.id.printtwo_dialog_tvOk);
        final Dialog myDialog = new Dialog(context,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = 2;
                if(posProvider.equals(NEW_LAND)){
                    NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                }else if(posProvider.equals(FUYOU_SF)){
                    FuyouPrintUtil.refundSuccessPrintText(getContext(), printService, refundResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                }

                intentToActivity();
                myDialog.dismiss();

            }
        });
        myDialog.show();
    }
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.scan_refund_imagScan:
			etOrderId.setText("");
			if(posProvider.equals(NEW_LAND)){
				if(cameType){
					Log.e("扫码调用：", "后置摄像头");
//				in = new Intent();
//				in.setClass(this, CaptureActivity.class);
//				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivityForResult(in, REQUEST_CODE);
					initScanner();
					backscan();
				}else{
					Log.e("扫码调用：", "前置摄像头");
					initScanner();
					frontscan();
				}
			}else if(posProvider.equals(FUYOU_SF)){
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.fuyousf.android.fuious",
						"com.fuyousf.android.fuious.NewSetScanCodeActivity"));
				intent.putExtra("flag", "true");
				startActivityForResult(intent, 11);
			}

			
			break;
		case R.id.scan_refund_tvOk:
			if(Utils.isFastClick()){
				Log.e("连续点击","return连续点击");
				return;
			}
			Log.e("连续点击","连续点击结束");
			if(refundOption == 1){
				refundStepOne();
			}else if(refundOption == 2){
				refundStepTwo();
			}
			break;
		}
	}
	
	/** 退款第一步：（查询订单判断订单来源）  */
	private void refundStepOne(){
		String etOrderIdTextStr = etOrderId.getText().toString().trim();
//		etMoneyTextStr = etOrderMoney.getText().toString().trim();
		if(Utils.isEmpty(etOrderIdTextStr)){
			Toast.makeText(context, "退款单号不能为空！", Toast.LENGTH_LONG).show();
			return;
		}
		getRefundOrderId(etOrderIdTextStr);


		/*if(etOrderIdTextStr!=null&&!etOrderIdTextStr.equals("")){
			//请求类型，“010”微信，“020”支付宝，“060”qq钱包
//			String pay_type = "010";//客户端不传类型，由服务端判断
	    	//auth_no	授权码
	    	String auth_no = etOrderIdTextStr;
	    	//total_fee	金额，单位分
	    	String total_feeStr = "";
	    	//{"transactionId":"UD180412A01175300820185846285503"}

	    	if(PayUtils.getPayService(auth_no)){
				if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
					//金额为元
					total_feeStr = DecimalUtil.scaleNumber(etMoneyTextStr);
				}
	    		//调用星POS SDK
	    		XingPosServiceRefund(auth_no,total_feeStr);
	    		Log.e("二维码支付服务类型：", "星POS");
//	    		Toast.makeText(context, "二维码支付服务类型是星POS的！", Toast.LENGTH_LONG).show();
		    	
	    	}else{
	    		if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
	    			////金额为分
		    		total_feeStr = DecimalUtil.elementToBranch(etMoneyTextStr);
		    	}
	    		PosRefundReqData posBean = PayRequestUtil.refundReq(total_feeStr,auth_no,posPublicData);
		    	//付款二维码内容(发起退款请求)
		    	String url = NitConfig.refundUrl;
		    	payRequestMethood(url,posBean);
		    	Log.e("二维码支付服务类型：", "默认自己的");
//		    	Toast.makeText(context, "二维码支付服务类型是默认自己的！", Toast.LENGTH_LONG).show();
	    	}
	    	
		}else{
			Toast.makeText(context, "退款单号不能为空！", Toast.LENGTH_LONG).show();
		}*/
	}

	/** 退款第二步：（输入金额退款）  */
	private void refundStepTwo(){
		String etMoneyTextStr = etOrderMoney.getText().toString().trim();
		try {
			String way = dataJsonObj.getString("way");
			String refundfCode = dataJsonObj.getString("refundfCode");
			String total_feeStr = "";
			if(way.equals("SDK")){
				if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
					//金额为元
					total_feeStr = DecimalUtil.scaleNumber(etMoneyTextStr);
				}
				if(posProvider.equals(NEW_LAND)){
					Log.e("二维码支付服务类型：", "星POS");
					NewPosServiceUtil.refundReq(ScanRefundActivity.this, refundfCode,total_feeStr, posPublicData);
				}else if(posProvider.equals(FUYOU_SF)){
					Log.e("二维码支付服务类型：", "富友POS");
					//支付方式
					refund_payTypeStr = dataJsonObj.getString("payWay");
					//金额
					total_feeStr = FieldTypeUtil.makeFieldAmount(etMoneyTextStr);
					//凭证号
					String oldTraceStr = dataJsonObj.getString("refundfCode");
					//设备号
					String deviceNum = posPublicData.getTrmNo_pos();
					String pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
					FuyouPosServiceUtil.refundReq(ScanRefundActivity.this,refund_payTypeStr,total_feeStr,oldTraceStr,pos_order_noStr);



				}
			}else{
				if(etMoneyTextStr!=null&&!etMoneyTextStr.equals("")){
					//金额为分
					total_feeStr = DecimalUtil.elementToBranch(etMoneyTextStr);
				}
				PosRefundReqData posBean = PayRequestUtil.refundReq(total_feeStr,refundfCode,posPublicData,posProvider);
				//付款二维码内容(发起退款请求)
				String url = NitConfig.refundUrl;
				payRequestMethood(url,posBean);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}
	
	/** 跳转目标Activity */
	private void intentToActivity(){
		startActivity(new Intent(context, RefundSuccessActivity.class));
		finish();
	}
	
	
	private void intentActivity(){
		Intent in = new Intent();
		in.setClass(context, PayErrorActivity.class);
		in.putExtra("optionTypeStr", "020");
		startActivity(in);
		finish();
	};

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail退款成功返回信息：", txndetail);
			Gson gjson  =  GsonUtils.getGson();
			ScanPaymentDate posResult = gjson.fromJson(txndetail, ScanPaymentDate.class);
			/**
			 * 下面是调用帮助类将一个对象以序列化的方式保存
			 * 方便我们在其他界面调用，类似于Intent携带数据
			 */
			try {
				MySerialize.saveObject("refundOrder",getContext(),MySerialize.serialize(posResult));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
			Boolean scanPayValue = true;
			Boolean payServiceType = false;
			String scanPayTypeValue = "refund";//pay:支付，refund:退款
			sharedPreferencesUtil.put("scanPayYes", scanPayValue);
			sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
			sharedPreferencesUtil.put("payServiceType", payServiceType);

			try {
				JSONObject job = new JSONObject(txndetail);
				String transamount = job.getString("transamount");
				speak(transamount);
				intentToActivity();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		String amountStr = bundle.getString("amount");//金额
		String traceNoStr = bundle.getString("traceNo");//凭证号
		String batchNoStr = bundle.getString("batchNo");//批次号
		String referenceNoStr = bundle.getString("referenceNo");//参考号
		String cardNoStr = bundle.getString("cardNo");//卡号
		String typeStr = bundle.getString("type");//卡类型
		String issueStr = bundle.getString("issue");//发卡行
		String dateStr = bundle.getString("date");//日期
		String timeStr = bundle.getString("time");//时间
		String oldTraceStr = bundle.getString("oldTrace");//原凭证号
		String merchantldStr = bundle.getString("merchantld");//商户号
		String terminalldStr = bundle.getString("terminalld");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名称
		String transactionTypeStr = bundle.getString("transactionType");//交易类型

		Log.e("返回的支付金额信息：", amountStr);
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

		ScanPaymentDate posResult = new ScanPaymentDate();
		posResult.setOrderid_scan(cardNoStr);
		posResult.setTranslocaldate(dateStr);
		posResult.setTranslocaltime(timeStr);
		posResult.setTransamount(totalStr);
		posResult.setPay_tp(refund_payTypeStr);

		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("refundOrder",getContext(),MySerialize.serialize(posResult));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
		Boolean scanPayValue = true;
		Boolean payServiceType = false;
		String scanPayTypeValue = "refund";//pay:支付，refund:退款
		sharedPreferencesUtil.put("scanPayYes", scanPayValue);
		sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
		sharedPreferencesUtil.put("payServiceType", payServiceType);
		intentToActivity();

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();  
		if(requestCode == REQUEST_CODE){
			if(resultCode == CaptureActivity.RESULT_CODE){
				scanCodeStr=bundle.getString("ScanCode");
				Log.e("扫描返回扫描结果：", scanCodeStr);
				etOrderId.setText(scanCodeStr);
				
//				refundStepOne();
				
			}
		}
		if (requestCode == 11&&bundle != null) {
			switch (resultCode) {
				// 退款成功
				case Activity.RESULT_OK:
					if(posProvider.equals(NEW_LAND)){

					}else if(posProvider.equals(FUYOU_SF)){
						scanCodeStr = bundle.getString("return_txt");//扫码返回数据
						Log.e("扫描返回扫描结果：", scanCodeStr);
						etOrderId.setText(scanCodeStr);
					}

					break;
				// 支付取消
				case Activity.RESULT_CANCELED:
					if(posProvider.equals(NEW_LAND)){
						String reason = bundle.getString("reason");
						if (reason != null) {
							// TODO:
						}
						intentActivity();
						Toast.makeText(context, "退款失败！", Toast.LENGTH_LONG).show();
					}else if(posProvider.equals(FUYOU_SF)){

					}

					break;

			}
		}

		if (requestCode == 1&&bundle != null) {
			switch (resultCode) {
			 // 退款成功
			  case Activity.RESULT_OK:
				  if(posProvider.equals(NEW_LAND)){
					  newlandResult(bundle);
				  }else if(posProvider.equals(FUYOU_SF)){
					  fuyouResult(bundle);
				  }

			    break;
			// 支付取消
			 case Activity.RESULT_CANCELED:
			 	if(posProvider.equals(NEW_LAND)){
					String reason = bundle.getString("reason");
					if (reason != null) {
						// TODO:
					}
					intentActivity();
					Toast.makeText(context, "退款失败！", Toast.LENGTH_LONG).show();
				}else if(posProvider.equals(FUYOU_SF)){

				}
			     break;
			}
		}
	}
}
