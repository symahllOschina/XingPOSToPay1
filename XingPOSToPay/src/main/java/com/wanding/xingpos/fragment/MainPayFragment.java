package com.wanding.xingpos.fragment;

import android.annotation.SuppressLint;
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
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.client.TtsMode;
import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.igexin.sdk.PushManager;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.activity.PayErrorActivity;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.baidu.tts.util.OfflineResource;
import com.wanding.xingpos.base.BaseFragment;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.PosInitData;
import com.wanding.xingpos.bean.PosPayQueryReqData;
import com.wanding.xingpos.bean.PosPayQueryResData;
import com.wanding.xingpos.bean.PosScanpayReqData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.FastJsonUtil;
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

@SuppressLint("ValidFragment")
public class MainPayFragment extends BaseFragment implements OnClickListener{

	private int mCurIndex = -1;
	private static final String FRAGMENT_INDEX = "1";
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	private boolean onResume=true;//onResume()方法初始化不执行
	
	private EditText etSumMoney;
	private ImageButton imagEliminate;
	private TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight,tvNine,tvZero,tvSpot;
	private StringBuilder pending = new StringBuilder();

	private LinearLayout paySK,payWX,payAL,payYL,payDX;//刷卡，微信，支付宝，银联，翼支付
	private String payType = "";//分别顺序对应：040,010,020,030,050
	//SDK调支付界面请求码
	public static final int PAY_REQUEST_CODE = 1;
	//调扫码界面请求码
	public static final int FU_REQUEST_CODE = 11;
	private Dialog hintDialog;// 加载数据时对话框
	
	/** 银联二维码，支付宝微信支付通道识别码 */
	private boolean wxPayServiceType = true;
	private boolean aliPayServiceType = true;
	private boolean ylPayServiceType = true;
	/** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
	private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;
	
	private PosInitData posInitData;//POS初始化信息
	private UserLoginResData loginInitData;
	private boolean testState = true;//是否为测试环境(默认测试环境)
	private String pos_order_noStr = "";
	private PosScanpayResData payResData;//支付成功返回
	private CardPaymentDate cardPaymentDate;//支付成功返回
	private ScanPaymentDate scanPaymentDate;//支付成功返回
	private PosPayQueryReqData posPayQueryReqData;;//支付未知查询返回
	/** POS支付成功返回的订单号，金额  */
	private String orderid_scan = "";
	private String scanPayMoneyStr = null;
	private String transamount = "";//支付成功返回的金额
	/**
	 * 打印小票第index联
	 */
	private int index = 1;
	/**
	 * 是否为API支付，isApi = true表示是API支付，false表示为SDK支付
	 */
	private boolean isApi = true;
	
	// ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 本demo的包名是com.baidu.tts.sample，定义在build.gradle中。
     */
    protected String appId = "11072721";

    protected String appKey = "eZGGWmPXBYCbTBrcxZWkGX7B";

    protected String secretKey = "a336b0c83f57cc5a878489f372ecfe9a";
    
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;
    
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_MALE;
    
    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private static final String TAG = "MainActivity";
    protected Handler mainHandler;
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
	private boolean cameType = true;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;

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

//	private boolean isSettlement = false;//是否调结算SDK，是为true

	/**
	 *  轮询查询订单状态标示
	 */
	private int queryIndex = 1;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.main_pay_fragment, null, false);
		mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handle(msg);
            }

        };
		posProvider = MainActivity.posProvider;
		initView(view);
		initData();

		isPrepared = true;
		lazyLoad();
		
		//因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
		ViewGroup parent = (ViewGroup)view.getParent();
		if(parent != null) {
			parent.removeView(view);
		}
		onResume=false;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){
			initPrintService();
		}
		return view;
	}
	
	
	
	public MainPayFragment(MySyntherizer synthesizer) {
		super();
		this.synthesizer = synthesizer;
	}



	@Override
	public void onResume() {
		super.onResume();
		if(onResume){
			//加载数据
		}
		//清空StringBuilder，EditText恢复初始值
		 //清空EditText
		 pending.delete( 0, pending.length() );
		 if(pending.length()<=0){
			 etSumMoney.setText("￥0.00");
		 }
		//transaction ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "transaction");
		//取出保存的值
		wxPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("wxPayServiceKey", true);
		aliPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("aliPayServiceKey", true);
		ylPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("ylPayServiceKey", true);
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(getContext(), "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if(defMoney.equals("")||defMoney.equals("0")){
			etSumMoney.setHint("￥0.00");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
		}
		
		//取出设置的打印值
		SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(getContext(), "printing");
		//取出保存的默认值
		printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
		Log.e("取出保存的打印值", printNum);
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(getContext(), "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}

		try {
			posInitData =(PosInitData) MySerialize.deSerialization(MySerialize.getObject("PosInitData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("onResume方法已调用：","--------------");
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if(posProvider.equals(NEW_LAND)){
			getActivity().unbindService(serviceConnection);
			aidlPrinter=null;
			aidlScanner = null;
		}else if(posProvider.equals(FUYOU_SF)){
			if(null != printReceiver){
				getActivity().unregisterReceiver(printReceiver);
			}
			getActivity().unbindService(printServiceConnection);
		}
        Log.i(TAG, "释放资源成功");
    }
	
	/**
     * 绑定服务
     */
    public void bindServiceConnection() {
        getActivity().bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
        Log.e("绑定服务", "绑定服务1");
    }

	private void initPrintService(){
		printRegisterReceiver();
		Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
		printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
		printIntent.setPackage("com.fuyousf.android.fuious");
		getContext().bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void printRegisterReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.fuyousf.android.fuious.service.print");
		printReceiver = new PrintReceiver();
		getContext().registerReceiver(printReceiver, intentFilter);
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
                    String scanCodeStr = arg0[0];
                    //如果扫描的二维码为空则不执行支付请求
    				if(scanCodeStr!=null&&!scanCodeStr.equals("")){
    			    	//auth_no	授权码（及扫描二维码值）
    			    	String auth_no = scanCodeStr;
    			    	Log.e("前置扫码值：", auth_no);
    			    	
    			    	
    			    	Message msg = new Message();
    			    	msg.obj = auth_no;
    			    	msg.what = 100;
    			    	mHandler.sendMessage(msg);
    			    	
    			    	
    			    	
    				}else{
    					//清空StringBuilder，EditText恢复初始值
    					//清空EditText
    					pending.delete( 0, pending.length() );
    					if(pending.length()<=0){
    			        	etSumMoney.setText("￥0.00");
    			        }
    					Log.e("前置扫码值：", "为空");
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
					String scanCodeStr = arg0[0];
					//如果扫描的二维码为空则不执行支付请求
					if(scanCodeStr!=null&&!scanCodeStr.equals("")){
						//auth_no	授权码（及扫描二维码值）
						String auth_no = scanCodeStr;
						Log.e("后置扫码值：", auth_no);


						Message msg = new Message();
						msg.obj = auth_no;
						msg.what = 100;
						mHandler.sendMessage(msg);

					}else{
						//清空StringBuilder，EditText恢复初始值
						//清空EditText
						pending.delete( 0, pending.length() );
						if(pending.length()<=0){
							etSumMoney.setText("￥0.00");
						}
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
	
	/**
	 * 重写父类方法（fragment可见时加载界面数据）
	 */
	@Override
	protected void lazyLoad() {

		Log.e("lazyLoad方法重载：","--------------");
		if (!isPrepared || !isVisible||mHasLoadedOnce) {
			return;
		}

		try {
			posInitData =(PosInitData) MySerialize.deSerialization(MySerialize.getObject("PosInitData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//请求数据
		getLoginData();
		
		
	}
	
	private void initData(){
		//加载数据
		//transaction ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "transaction");
		//取出保存的值
		wxPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("wxPayServiceKey", true);
		aliPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("aliPayServiceKey", true);
		ylPayServiceType = (Boolean) sharedPreferencesUtil.getSharedPreference("ylPayServiceKey", true);
		//取出保存的默认支付金额
		//defMoneyNum ：交易设置值存储应用本地的文件名称
		SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(getContext(), "defMoneyNum");
		//取出保存的默认值
		String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
		if(defMoney.equals("")||defMoney.equals("0")){
			etSumMoney.setHint("￥0.00");
		}else{
			etSumMoney.setText("￥"+defMoney);
			pending.append(defMoney);
		}
		//取出设置的打印值
		SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(getContext(), "printing");
		//取出保存的默认值
		printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
		Log.e("取出保存的打印值", printNum);
		
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(getContext(), "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}
		
		try {
			posInitData =(PosInitData) MySerialize.deSerialization(MySerialize.getObject("PosInitData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**  初始化控件*/
	private void initView(View view){
		
		etSumMoney = (EditText) view.findViewById(R.id.content_layout_etSumMoney);
		EditTextUtils.setPricePoint(etSumMoney);
		//强制隐藏Android输入法窗体 
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//EditText始终不弹出软件键盘 
		etSumMoney.setInputType(InputType.TYPE_NULL); 
        imm.hideSoftInputFromWindow(etSumMoney.getWindowToken(),0); 
		tvOne = (TextView) view.findViewById(R.id.content_layout_tvOne);
		tvTwo = (TextView) view.findViewById(R.id.content_layout_tvTwo);
		tvThree = (TextView) view.findViewById(R.id.content_layout_tvThree);
		tvFour = (TextView) view.findViewById(R.id.content_layout_tvFour);
		tvFive = (TextView) view.findViewById(R.id.content_layout_tvFive);
		tvSix = (TextView) view.findViewById(R.id.content_layout_tvSix);
		tvSeven = (TextView) view.findViewById(R.id.content_layout_tvSeven);
		tvEight = (TextView) view.findViewById(R.id.content_layout_tvEight);
		tvNine = (TextView) view.findViewById(R.id.content_layout_tvNine);
		tvZero = (TextView) view.findViewById(R.id.content_layout_tvZero);
		tvSpot = (TextView) view.findViewById(R.id.content_layout_tvSpot);
		imagEliminate = (ImageButton) view.findViewById(R.id.content_layout_imagEliminate);
		
		paySK = (LinearLayout) view.findViewById(R.id.main_bottom_paySK);
		payWX = (LinearLayout) view.findViewById(R.id.main_bottom_payWX);
		payAL = (LinearLayout) view.findViewById(R.id.main_bottom_payAli);
		payYL = (LinearLayout) view.findViewById(R.id.main_bottom_payYL);
		payDX = (LinearLayout) view.findViewById(R.id.main_bottom_payDX);
		
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
		
		paySK.setOnClickListener(this);
		payWX.setOnClickListener(this);
		payAL.setOnClickListener(this);
		payYL.setOnClickListener(this);
		payDX.setOnClickListener(this);
		
		
	}
	
	protected void handle(Message msg) {
        int what = msg.what;
        switch (what) {
        }
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

	private void speakText(String text) {

		int result = synthesizer.speak(text);
		checkResult(result, "speak");
	}

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        	Log.e("error code :", result+" method:" + method );
        }
    }
    

    
    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(getContext(), voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
//            toPrint("【error】:copy files from assets failed." + e.getMessage());
              // 可以用下面一行替代，在logcat中查看代码
              Log.e("【error】:", e.getMessage());
        }
        return offlineResource;
    }
	
	/** 获取登录信息（签到）  */
	private void getLoginData(){
		final String url = NitConfig.loginUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("thirdMid",posInitData.getMercId_pos());   //商户号
					userJSON.put("terminal_id",posInitData.getTrmNo_pos()); //  设备号，终端号
					if(posProvider.equals(FUYOU_SF)){
						userJSON.put("type","5"); //设备识别类型：5表示POS机为富友厂商
					}
					String content = String.valueOf(userJSON);
					Log.e("签到发起请求参数：", content); 
					  
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();  
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();  
                   connection.setConnectTimeout(HttpJsonReqUtil.CONNECTTIME);
                   connection.setReadTimeout(HttpJsonReqUtil.READTIME);
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
                   Log.e("签到返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("签到返回JSON值：", jsonStr);
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
				}catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("Exception", "Exception");

                }
			};
		}.start();
	}
	
	
	/** 扫描结果发起请求  */
	private void payRequestMethood(final String url,final PosScanpayReqData posBean){
//		Looper.prepare();
		hintDialog=CustomDialog.CreateDialog(getContext(), "    正在支付...");
		hintDialog.show();
		hintDialog.setCancelable(false);
		new Thread(){
			@Override
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
					userJSON.put("auth_no",posBean.getAuth_no());   
					userJSON.put("total_fee",posBean.getTotal_fee());   
					userJSON.put("order_body",posBean.getOrder_body());   
					userJSON.put("key_sign",posBean.getKey_sign());   

					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content); 
					  
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();  
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();

					connection.setConnectTimeout(HttpJsonReqUtil.CONNECTTIME);
					connection.setReadTimeout(HttpJsonReqUtil.READTIME);
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
	
	/** 测试环境POS接口支付保存订单号 */
	private void saveOrderNoToService(){
		//insertChannelIdTest，入参：goodsPrice = 金额，transactionId = 支付返回的UD或9开头的 ，refundCode = order_no(自己生成的订单号)
		final String url = NitConfig.insertChannelIdTestUrl;
		new Thread(){
			@Override
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

					connection.setConnectTimeout(HttpJsonReqUtil.CONNECTTIME);
					connection.setReadTimeout(HttpJsonReqUtil.READTIME);
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
                   Log.e("保存测试数据返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("保存测试数据返回JSON值：", jsonStr);
                	   Message msg=new Message();
   					   msg.what=3;
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
	private void getCardStock(final boolean isApi){

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
					if(isApi){
						userJSON.put("amount",DecimalUtil.branchToElement(payResData.getTotal_fee()));
						userJSON.put("orderId",payResData.getOut_trade_no());
						userJSON.put("payWay",FuyouPrintUtil.getPayWay(payResData.getPay_type()));
						userJSON.put("payTime",DateTimeUtil.dateToStamp(payResData.getEnd_time()));
					}else{
						userJSON.put("orderId",pos_order_noStr);
						//银行卡
						if(payType.equals("040")){
							userJSON.put("amount",cardPaymentDate.getTransamount());
							userJSON.put("payWay",FuyouPrintUtil.getPayWay("0"));
							userJSON.put("payTime",DateTimeUtil.dateToStamp(cardPaymentDate.getTranslocaldate()+cardPaymentDate.getTranslocaltime()));
						}else{
							userJSON.put("amount",scanPaymentDate.getTransamount());
							userJSON.put("payWay",FuyouPrintUtil.getPayWay(scanPaymentDate.getPay_tp()));
							userJSON.put("payTime",DateTimeUtil.dateToStamp(scanPaymentDate.getTranslocaldate()+scanPaymentDate.getTranslocaltime()));
						}
					}


					String content = String.valueOf(userJSON);
					Log.e("查询卡劵发起请求参数：", content);

					HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
					//HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();

					connection.setConnectTimeout(HttpJsonReqUtil.CONNECTTIME);
					connection.setReadTimeout(HttpJsonReqUtil.READTIME);
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
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	/**
	 * 查询个推别名绑定状态
	 */
	private void queryAliasStatus(){
		SharedPreferencesUtil shp = new SharedPreferencesUtil(getActivity(),"clientid");
		final String clientid = (String) shp.getSharedPreference("cid","");

		final String url = NitConfig.queryAliasStatusUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					// 拼装JSON数据，向服务端发起请求
					JSONObject userJSON = new JSONObject();
					userJSON.put("clientId",clientid);
					String content = String.valueOf(userJSON);
					Log.e("充值发起请求参数：", content);
					String content_type = HttpUtil.CONTENT_TYPE_JSON;
					String jsonStr = HttpUtil.doPos(url,content,content_type);
					Log.e("充值返回字符串结果：", jsonStr);
					Message msg=new Message();
					msg.what=5;
					msg.obj=jsonStr;
					mHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);
				}catch (IOException e){
					e.printStackTrace();
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					Message msg=new Message();
					msg.what=404;
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	/**
	 * 支付中时查询订单状态
	 * 轮询方案：https://www.cnblogs.com/ygj0930/p/7657194.html
	 */
	private void queryOrderStatus(){
		if(payResData != null){
			posPayQueryReqData = PayRequestUtil.paymentStateQueryReq(payType,payResData,loginInitData);
			//发起查询请求
			getPayOrderStatus();

		}else{
			ToastUtil.showText(getActivity(),"支付中订单信息为空！",1);
		}
	}

	/**
	 * 获取订单状态
	 */
	private void getPayOrderStatus(){

		final String url = NitConfig.queryOrderStatusUrl;
//		final String url = "http://192.168.2.63:8080/payment/query";
		Log.e(TAG,"获取订单状态接口调用地址："+url);
		new Thread(){
			@Override
			public void run() {
				try {

					String content = FastJsonUtil.toJSONString(posPayQueryReqData);
					Log.e("获取订单状态请求参数：", content);
					String content_type = HttpUtil.CONTENT_TYPE_JSON;
					String jsonStr = HttpUtil.doPos(url,content,content_type);
					Log.e("获取订单状态返回结果：", jsonStr);
					int msg = NetworkUtils.MSG_WHAT_EIGHT;
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
	 *  星POS 内置接口支付
	 *  payToatl:支付金额（最终提交以元为单位）
	 */
	private void XingPosServicePay(String payToatl){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
		    //String payType = "";//分别顺序对应：040,010,020,030,050
		    if(payType.equals("040")){
		    	bundle.putString("pay_tp",  "0");//银行卡
		    	bundle.putString("proc_cd",  "000000");
		    }else if(payType.equals("010")){
		    	bundle.putString("pay_tp",  "11");//微信
		    	bundle.putString("proc_cd",  "660000");
		    }else if(payType.equals("020")){
		    	bundle.putString("proc_cd",  "660000");
		    	bundle.putString("pay_tp",  "12");//支付宝
		    }else if(payType.equals("030")){
		    	bundle.putString("pay_tp",  "13");//银联
		    	bundle.putString("proc_cd",  "660000");
		    }  
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  payToatl);
		    
		    //设备号
		    String deviceNum = posInitData.getTrmNo_pos();
		    pos_order_noStr = RandomStringGenerator.getNlRandomNum(deviceNum);
		    bundle.putString("order_no",  pos_order_noStr);
		    Log.e("生成的订单号：", pos_order_noStr);
		    bundle.putString("batchbillno", "");//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
		    //TODO:
			Log.e("Exception：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
		    //TODO:
			Log.e("Exception：", "异常");
		}
	}

	/**
	 * 富友POS 内置支付
	 * payTotal:支付金额（最终提交以分为单位）
	 */
	private void FuyouPosServicePay(String payTotal){
        try {
    //		String total_feeStr = DecimalUtil.elementToBranch(payTotal);
    //		Log.e("元转分",total_feeStr);
            String total_feeStr = FieldTypeUtil.makeFieldAmount(payTotal);
            Log.e("SDK提交带规则金额",total_feeStr);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.MainActivity"));

            //包名     包名+类名（全路径）
            if(payType.equals("040")){
                intent.putExtra("transName", "消费");
            }else if(payType.equals("010")){
                intent.putExtra("transName", "微信消费");
            }else if(payType.equals("020")){
                intent.putExtra("transName", "支付宝消费");
            }else if(payType.equals("030")){
                intent.putExtra("transName", "银联二维码消费");
            }

    //		intent.putExtra("amount", "000000000001");
            intent.putExtra("amount", total_feeStr);
            //设备号
            String deviceNum1 = posInitData.getTrmNo_pos();
            pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum1);
            intent.putExtra("orderNumber", pos_order_noStr);
            if(!payType.equals("040")){
                intent.putExtra("isPrintTicket", "true");//为true时调用打印；为false时不调用打印
                if (cameType) {
                    intent.putExtra("isFrontCamera", "false");//是否打开前置摄像头(传true时，打开前置。传false不打开前置)
                }else{
                    intent.putExtra("isFrontCamera", "true");//是否打开前置摄像头(传true时，打开前置。传false不打开前置)
                }
            }
            intent.putExtra("version", "1.0.7");
            startActivityForResult(intent, PAY_REQUEST_CODE);

        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Exception：", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }

	}

	public void sendMessage(int what,String text){
		Message msg = new Message();
		msg.what = what;
		msg.obj = text;
		mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				String auth_no = (String) msg.obj;
				//total_fee	金额，单位分(金额需乘以100)
		    	String etTextStr = scanPayMoneyStr;
		    	Log.e("前置输入框文本text值：", etTextStr);
		    	String total_feeStr = DecimalUtil.elementToBranch(etTextStr);
		    	scanPayMethodTwo(auth_no, total_feeStr);
				break;
			case 1:
				String jsonStr=(String) msg.obj;
				//然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}  
         	   	Gson gjson  =  GsonUtils.getGson();
                payResData = gjson.fromJson(jsonStr, PosScanpayResData.class);
        		//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        		String return_codeStr = payResData.getReturn_code();
        		//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        		String return_msgStr = payResData.getReturn_msg();
        		//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        		String result_codeStr = payResData.getResult_code();
				synchronized (MainPayFragment.class){
					if(return_codeStr.equals("01")){
						if(result_codeStr.equals("01")){

							/**
							 * 下面是调用帮助类将一个对象以序列化的方式保存
							 * 方便我们在其他界面调用，类似于Intent携带数据
							 */
							try {
								MySerialize.saveObject("scanPayOrder",getContext(),MySerialize.serialize(payResData));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
							SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
							Boolean scanPayValue = true;
							Boolean payServiceType = true;
							String scanPayTypeValue = "pay";//pay:支付，refund:退款
							sharedPreferencesUtil.put("scanPayYes", scanPayValue);
							sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
							sharedPreferencesUtil.put("payServiceType", payServiceType);


							startPrint();
							if(hintDialog!=null&&hintDialog.isShowing()){
								hintDialog.dismiss();
							}
//        				Toast.makeText(getContext(), "支付成功!", Toast.LENGTH_LONG).show();
						}else if(result_codeStr.equals("03")){
							if(hintDialog!=null&&hintDialog.isShowing()){
								hintDialog.dismiss();
							}
//							Toast.makeText(getContext(), "支付中!", Toast.LENGTH_LONG).show();
							mHandler.sendEmptyMessageDelayed(NetworkUtils.MSG_WHAT_SIX,0);

						}else{
							intentActivity();
							Toast.makeText(getContext(), "支付失败!", Toast.LENGTH_LONG).show();
						}
					}else{
						intentActivity();
						Toast.makeText(getContext(), return_msgStr+"!", Toast.LENGTH_LONG).show();
					}
				}

        		//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }


				break;
			case 2:
				String logJsonStr=(String) msg.obj;
				try {
					JSONObject job = new JSONObject(logJsonStr);
					String statusStr = job.getString("status");
					if(statusStr.equals("200")){
						Log.e("签到查询状态：", "成功！");
						String dataJsonStr = job.getString("data");
						JSONObject dataJob = new JSONObject(dataJsonStr);
						UserLoginResData logResData = new UserLoginResData();
						//json返回信息
						logResData.setAccess_token(dataJob.getString("accessToken"));
						logResData.setMerchant_no(dataJob.getString("merchant_no"));
						logResData.setTerminal_id(dataJob.getString("terminal_id"));
						logResData.setMid(dataJob.getString("mid"));
						logResData.setEid(dataJob.getString("eid"));

//						logResData.setAccess_token("wwh88pdhkqps1xvhxb0fcqa61bs7awyz");
//						logResData.setMerchant_no("1000179");
//						logResData.setTerminal_id("10108");
//						logResData.setMid("344");
//						logResData.setEid("132");

						logResData.setEname(dataJob.getString("ename"));
						//pos初始化信息
						logResData.setMercId_pos(posInitData.getMercId_pos());
						logResData.setTrmNo_pos(posInitData.getTrmNo_pos());
						logResData.setMername_pos(posInitData.getMername_pos());
						logResData.setBatchno_pos(posInitData.getBatchno_pos());
						logResData.setQueryCoupons(dataJob.getBoolean("isQueryCoupons"));

						/**
						 * 下面是调用帮助类将一个对象以序列化的方式保存
						 * 方便我们在其他界面调用，类似于Intent携带数据
						 */
						try {
							MySerialize.saveObject("UserLoginResData",getContext(),MySerialize.serialize(logResData));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
						
//						//款台名称作为默认的员工名称
//						List<StaffData> lsStaff = new ArrayList<StaffData>();
//						StaffData staff = new StaffData();
//						staff.setName(loginInitData.getEname());
//						lsStaff.add(staff);
//						
//						//保存
//				        try {
//				            String listStr = MySerialize.serialize(lsStaff);
//				            MySerialize.saveObject("staff",getContext(),listStr);
//				        } catch (IOException e) {
//				            e.printStackTrace();
//				        }

						/**
						 * 别名绑定查询
						 */
						queryAliasStatus();
						
					}else{
						UserLoginResData logResData = new UserLoginResData();
						//json返回信息
						logResData.setAccess_token("");
						logResData.setMerchant_no("");
						logResData.setTerminal_id("");
						logResData.setMid("");
						logResData.setEid("");
						logResData.setEname("");
						//pos初始化信息
						logResData.setMercId_pos(posInitData.getMercId_pos());
						logResData.setTrmNo_pos(posInitData.getTrmNo_pos());
						logResData.setMername_pos(posInitData.getMername_pos());
						logResData.setBatchno_pos(posInitData.getBatchno_pos());
						logResData.setQueryCoupons(false);

						/**
						 * 下面是调用帮助类将一个对象以序列化的方式保存
						 * 方便我们在其他界面调用，类似于Intent携带数据
						 */
						try {
							MySerialize.saveObject("UserLoginResData",getContext(),MySerialize.serialize(logResData));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
						Toast.makeText(getContext(), "签到失败！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				break;
			case 3:
				String saveTestJsonStr=(String) msg.obj;
				//{"isSuccess":true,"errorCode":null,"errorMessage":null,"data":"UD180413A01175300610011747021785"}
				Log.e("返回的支付金额信息111：", transamount);
				String transamountStr = transamount;
				Log.e("返回的支付金额信息222：", transamountStr);
				//播放语音
				speak(transamountStr);
//				intentToActivity();
				break;
			case 4:
				String cardStockJson = (String) msg.obj;
				cardStockJson(cardStockJson);
				if(hintDialog!=null&&hintDialog.isShowing()){

					hintDialog.dismiss();
				}
				break;
			case 5:
				//{"data":"1000145","message":"查询成功","status":200}
				String jsonStr_2 = (String) msg.obj;
				queryAliasStatusJSON(jsonStr_2);
				break;
			case NetworkUtils.MSG_WHAT_SIX:
				hintDialog=CustomDialog.CreateDialog(getContext(), "支付状态查询中！");
				hintDialog.show();
				hintDialog.setCancelable(false);
				queryOrderStatus();
				break;
			case NetworkUtils.MSG_WHAT_SEVEN:
				getPayOrderStatus();
				break;
			case NetworkUtils.MSG_WHAT_EIGHT:
				String queryOrderStatusStr=(String) msg.obj;
				queryOrderStatusStr(queryOrderStatusStr);
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				if(hintDialog!=null&&hintDialog.isShowing()){
					hintDialog.dismiss();
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
						if(Utils.isNotEmpty(url)){
							if(isApi){
								FuyouPrintUtil.cardStockPrintText(getActivity(),printService,url);
								payResData.setUrl(url);
								/**
								 * 下面是调用帮助类将一个对象以序列化的方式保存
								 * 方便我们在其他界面调用，类似于Intent携带数据
								 */
								try {
									MySerialize.saveObject("scanPayOrder",getContext(),MySerialize.serialize(payResData));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
								SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
								Boolean scanPayValue = true;
								Boolean payServiceType = true;
								String scanPayTypeValue = "pay";//pay:支付，refund:退款
								sharedPreferencesUtil.put("scanPayYes", scanPayValue);
								sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
								sharedPreferencesUtil.put("payServiceType", payServiceType);


							}else{
								if(payType.equals("040")){
									FuyouPrintUtil.cardStockPrintText(getActivity(),printService,url);
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

									FuyouPrintUtil.cardStockPrintText(getActivity(),printService,url);
									scanPaymentDate.setUrl(url);
									/**
									 * 下面是调用帮助类将一个对象以序列化的方式保存
									 * 方便我们在其他界面调用，类似于Intent携带数据
									 */
									try {
										MySerialize.saveObject("scanPayOrder",getContext(),MySerialize.serialize(scanPaymentDate));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
									SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
									Boolean scanPayValue = true;
									Boolean payServiceType = false;
									String scanPayTypeValue = "pay";//pay:支付，refund:退款
									sharedPreferencesUtil.put("scanPayYes", scanPayValue);
									sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
									sharedPreferencesUtil.put("payServiceType", payServiceType);
								}
							}
						}


					}else{
						ToastUtil.showText(getActivity(),subMsg,1);
					}
				}else{
					ToastUtil.showText(getActivity(),msg,1);
				}
			}else{
				ToastUtil.showText(getActivity(),"查询失败！",1);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}



	}

	private void queryAliasStatusJSON(String str){
		//{"data":"1000145","message":"查询成功","status":200}
		try {
			JSONObject job = new JSONObject(str);
			String status = job.getString("status");
			String data = job.getString("data");
			String message = job.getString("message");
			String terminal_id = loginInitData.getTerminal_id();

//			PushManager.getInstance().bindAlias(getActivity(), terminal_id);
			if(status.equals("200")){
				if(!data.equals(terminal_id)){

					PushManager.getInstance().bindAlias(getActivity(), terminal_id);
				}
			}else{
				PushManager.getInstance().bindAlias(getActivity(), terminal_id);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 查询支付结果
	 */
	private void queryOrderStatusStr(String json){
		try{
			Gson gjson  =  GsonUtils.getGson();
			PosPayQueryResData posResult = gjson.fromJson(json, PosPayQueryResData.class);
			//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
			String return_codeStr = posResult.getReturn_code();
			//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
			String return_msgStr = posResult.getReturn_msg();
			//result_code	业务结果：“01”支付成功 ，02”支付失败 ，“03”支付中,"04"含退款，
			String result_codeStr = posResult.getResult_code();
			if(return_codeStr.equals("01")){
				if(result_codeStr.equals("01")){
					payResData = new PosScanpayResData();
					payResData.setPay_type(posResult.getPay_type());
					payResData.setMerchant_name(posResult.getMerchant_name());
					payResData.setMerchant_no(posResult.getMerchant_no());
					payResData.setTerminal_id(posResult.getTerminal_id());
					payResData.setTerminal_trace(posResult.getTerminal_trace());
					payResData.setTerminal_time(posResult.getTerminal_time());
					payResData.setTotal_fee(posResult.getTotal_fee());
					payResData.setEnd_time(posResult.getEnd_time());
					payResData.setOut_trade_no(posResult.getOut_trade_no());

					startPrint();

					if(hintDialog!=null&&hintDialog.isShowing()){
						hintDialog.dismiss();
					}
				}else if(result_codeStr.equals("02")){

					if(hintDialog!=null&&hintDialog.isShowing()){
						hintDialog.dismiss();
					}

					intentActivity();
					ToastUtil.showText(getActivity(),"支付失败",1);
				}else if(result_codeStr.equals("03")){
					queryIndex ++;
					if(queryIndex <= 5 ) {

						mHandler.sendEmptyMessageDelayed(NetworkUtils.MSG_WHAT_SEVEN,5000);

					}else{
						if(hintDialog!=null&&hintDialog.isShowing()){
							hintDialog.dismiss();
						}
						speakText("支付处理中，请等待收银员确认支付结果！");

					}

				}else{

					if(hintDialog!=null&&hintDialog.isShowing()){
						hintDialog.dismiss();
					}

					intentActivity();
					ToastUtil.showText(getActivity(),"支付失败",1);
				}
			}else{

				if(hintDialog!=null&&hintDialog.isShowing()){
					hintDialog.dismiss();
				}

				intentActivity();
				ToastUtil.showText(getActivity(),return_msgStr,1);
			}
		}catch (Exception e){
			e.printStackTrace();
			if(hintDialog!=null&&hintDialog.isShowing()){
				hintDialog.dismiss();
			}

			intentActivity();
			ToastUtil.showText(getActivity(),"支付返回结果错误！",1);
		}

	}

	
	/** 跳转目标Activity */
	private void startPrint(){

		String totalStr = DecimalUtil.branchToElement(payResData.getTotal_fee());
		//播放语音
		speak(totalStr);
		index = 1;
		if(posProvider.equals(NEW_LAND)){
            //初始化打印机
            getPrinter();
            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if(printNum.equals("printNumNo")){

                //不执行打印
//                intentToActivity();

            }else if(printNum.equals("printNumOne")){



                //打印一次dfdfd
                NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
//                intentToActivity();

            }else if(printNum.equals("printNumTwo")){
                //打印两次
                NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
				try {
					Thread.sleep(NewlandPrintUtil.time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                //弹出对话框提示打印下一联
                showPrintTwoDialog(payResData);

            }
        }else if(posProvider.equals(FUYOU_SF)){

			if(printNum.equals("printNumNo")){

				//不执行打印
//				intentToActivity();

			}else if(printNum.equals("printNumOne")){

				//打印一次
				String printTextStr = FuyouPrintUtil.paySuccessPrintText(getContext(), payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.CustomPrinterActivity");
                Intent intent = new Intent();
                intent.setComponent(component);
                Bundle bundle = new Bundle();
                bundle.putString("data", printTextStr);
                bundle.putString("isPrintTicket", "true");
                intent.putExtras(bundle);
                startActivityForResult(intent, FuyouPosServiceUtil.PRINT_REQUEST_CODE);

			}else if(printNum.equals("printNumTwo")){
				//打印两次
				String printTextStr = FuyouPrintUtil.paySuccessPrintText(getContext(), payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.CustomPrinterActivity");
                Intent intent = new Intent();
                intent.setComponent(component);
                Bundle bundle = new Bundle();
                bundle.putString("data", printTextStr);
                bundle.putString("isPrintTicket", "true");
                intent.putExtras(bundle);
                startActivityForResult(intent, FuyouPosServiceUtil.PRINT_REQUEST_CODE);

			}

        }


	}



	/** 金额：transamountStr ，交易类型  */
//	private void intentToActivity(){
//		Intent in = new Intent();
//		in.setClass(getActivity(), PaySuccessActivity.class);
//		in.putExtra("payTypeKey", payType);
//		if(NitConfig.isTest.equals("test")){
//			in.putExtra("pos_order", pos_order_noStr);
//		}
//		startActivity(in);
//	}
	private void intentActivity(){
		if(hintDialog!=null&&hintDialog.isShowing()){
			hintDialog.dismiss();
		}
		Intent in = new Intent();
		in.setClass(getActivity(), PayErrorActivity.class);
		in.putExtra("optionTypeStr", "010");
		startActivity(in);
	};
	
	/**  打印下一联提示窗口 */
	@SuppressLint("InflateParams")
    private void showPrintTwoDialog(final PosScanpayResData payResData){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.printtwo_dialog_activity, null);
		Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(getActivity(),R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				index = 2;
				if(posProvider.equals(NEW_LAND)){
					NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
				}else if(posProvider.equals(FUYOU_SF)){
					String printTextStr = FuyouPrintUtil.paySuccessPrintText(getContext(), payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                    ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.CustomPrinterActivity");
                    Intent intent = new Intent();
                    intent.setComponent(component);
                    Bundle bundle = new Bundle();
                    bundle.putString("data", printTextStr);
                    bundle.putString("isPrintTicket", "true");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, FuyouPosServiceUtil.PRINT_REQUEST_CODE);
				}

				myDialog.dismiss();
				
			}
		});
		myDialog.show();
		myDialog.setCanceledOnTouchOutside(false);
	}
	
	/** 判断金额数值是否大于零  */
	private boolean setSumMoney(){
		boolean flag = false;
		String sumMoney = etSumMoney.getText().toString();
		if(sumMoney!=null&&sumMoney.length()>0){
			double num = Double.valueOf(sumMoney);
			if(num>0){
				return true;
			}
		}
		return flag;
	}
	
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
		String total_feeStr = "0.00";
		int last = 0;
        if(pending.length()!=0)
        {
            last = pending.codePointAt(pending.length()-1);

        }
		
		switch (v.getId()) {
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
		case R.id.main_bottom_paySK://刷卡
			String cid = PushManager.getInstance().getClientid(getActivity());
			Log.e("获取CID",cid);
			try {
				payType = "040";
				Log.e("输入框文本text值：", pending.toString());
				total_feeStr = DecimalUtil.scaleNumber(pending.toString());
				if(DecimalUtil.isEqual(total_feeStr)==1){
					scanPayMethodOne(payType);
				}else{
					Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_bottom_payWX://微信
			try {
				payType = "010";
				Log.e("输入框文本text值：", pending.toString());
				total_feeStr = DecimalUtil.scaleNumber(pending.toString());
				if(DecimalUtil.isEqual(total_feeStr)==1){
					scanPayMethodOne(payType);
				}else{
					Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_bottom_payAli://支付宝
			try {
				payType = "020";
				Log.e("输入框文本text值：", pending.toString());
				total_feeStr = DecimalUtil.scaleNumber(pending.toString());
				if(DecimalUtil.isEqual(total_feeStr)==1){
					scanPayMethodOne(payType);
				}else{
					Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_bottom_payYL://银联
			try {
				payType = "030";
				Log.e("输入框文本text值：", pending.toString());
				total_feeStr = DecimalUtil.scaleNumber(pending.toString());
				if(DecimalUtil.isEqual(total_feeStr)==1){
					scanPayMethodOne(payType);
				}else{
					Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.main_bottom_payDX://翼支付
			try {
				payType = "050";
				Log.e("输入框文本text值：", pending.toString());
				total_feeStr = DecimalUtil.scaleNumber(pending.toString());
				if(DecimalUtil.isEqual(total_feeStr)==1){
					scanPayMethodOne(payType);
				}else{
					Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "请输入有效金额！", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
	
	private void scanPayMethodOne(String payType){
		try {
			//auth_no	授权码（及扫描二维码值）
			scanPayMoneyStr = pending.toString();
			Log.e("输入框文本text值：", scanPayMoneyStr);
			if(!scanPayMoneyStr.equals(".")){
				String total_feeStr = DecimalUtil.scaleNumber(scanPayMoneyStr);
				if(payType.equals("010")){
					Log.e("支付类型：", "微信支付！");
					/** 根据支付通道进行请求服务  */
					if(wxPayServiceType){
						Log.e("支付通道：", "默认");
						if(posProvider.equals(NEW_LAND)){
							if(cameType){
								Log.e("扫码调用：", "后置摄像头");
//							Intent in = new Intent();
//			    			in.setClass(getActivity(), CaptureActivity.class);
//			    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    			startActivityForResult(in, REQUEST_CODE);
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
							startActivityForResult(intent, FU_REQUEST_CODE);
						}
					}else
					{
						if(posProvider.equals(NEW_LAND)){
							Log.e("支付通道：", "星POS");
							XingPosServicePay(total_feeStr);
						}else if(posProvider.equals(FUYOU_SF)){
							Log.e("支付通道：", "富友POS");
							FuyouPosServicePay(total_feeStr);
						}
					}
					
					
				}else if(payType.equals("020")){
					Log.e("支付类型：", "支付宝支付！");
					/** 根据支付通道进行请求服务  */
					if(aliPayServiceType){
						Log.e("支付通道：", "默认");

						if(posProvider.equals(NEW_LAND)){
							if(cameType){
								Log.e("扫码调用：", "后置摄像头");
//							Intent in = new Intent();
//			    			in.setClass(getActivity(), CaptureActivity.class);
//			    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    			startActivityForResult(in, REQUEST_CODE);
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
							startActivityForResult(intent, FU_REQUEST_CODE);
						}
						
					}else
					{
						if(posProvider.equals(NEW_LAND)){
							Log.e("支付通道：", "星POS");
							XingPosServicePay(total_feeStr);
						}else if(posProvider.equals(FUYOU_SF)){
							Log.e("支付通道：", "富友POS");
							FuyouPosServicePay(total_feeStr);
						}

					}
				}else if(payType.equals("050")){
					Log.e("支付类型：", "翼支付！");
					
					Log.e("支付通道：", "默认");
					if(posProvider.equals(NEW_LAND)){
						if(cameType){
							Log.e("扫码调用：", "后置摄像头");
//							Intent in = new Intent();
//			    			in.setClass(getActivity(), CaptureActivity.class);
//			    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    			startActivityForResult(in, REQUEST_CODE);
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
						startActivityForResult(intent, FU_REQUEST_CODE);
					}
					
				}else if(payType.equals("030")){
					/** 根据支付通道进行请求服务  */
					if(ylPayServiceType){
						Log.e("支付通道：", "默认");
						if(posProvider.equals(NEW_LAND)){
							if(cameType){
								Log.e("扫码调用：", "后置摄像头");
//							Intent in = new Intent();
//			    			in.setClass(getActivity(), CaptureActivity.class);
//			    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    			startActivityForResult(in, REQUEST_CODE);
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
							startActivityForResult(intent, FU_REQUEST_CODE);
						}
					}else{
						Log.e("支付通道：", "第三方");
						if(posProvider.equals(NEW_LAND)){
							Log.e("支付通道：", "星POS");
							XingPosServicePay(total_feeStr);
						}else if(posProvider.equals(FUYOU_SF)){
							Log.e("支付通道：", "富友POS");
							FuyouPosServicePay(total_feeStr);
						}
					}




				}else if(payType.equals("040")){
					if(posProvider.equals(NEW_LAND)){
						Log.e("支付通道：", "星POS");
						XingPosServicePay(total_feeStr);
					}else if(posProvider.equals(FUYOU_SF)){
						Log.e("支付通道：", "富友POS");
						FuyouPosServicePay(total_feeStr);
					}
				}
				
				
			}else{
				Log.e("输入金额有误！", "false");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	};
	
	private void scanPayMethodTwo(String auth_no,String total_feeStr){
		try {
			/** 根据支付通道进行请求服务  */
			if(payType.equals("010")){
				Log.e("支付类型：", "微信支付");
				if(wxPayServiceType){
					Log.e("微信支付通道：", "默认");
					PosScanpayReqData posBean = PayRequestUtil.payReq(payType, auth_no, total_feeStr,loginInitData,posProvider);
			    	//付款二维码内容(发起支付请求)
			    	String url = NitConfig.barcodepayUrl;
			    	payRequestMethood(url,posBean);
				}else
				{
					Log.e("微信支付通道：", "星POS");
					XingPosServicePay(total_feeStr);
				}
			}else if(payType.equals("020")){
				Log.e("支付类型：", "支付宝支付");
				if(aliPayServiceType){
					Log.e("支付宝支付通道：", "默认");
					PosScanpayReqData posBean = PayRequestUtil.payReq(payType, auth_no, total_feeStr,loginInitData,posProvider);
			    	//付款二维码内容(发起支付请求)
			    	String url = NitConfig.barcodepayUrl;
			    	payRequestMethood(url,posBean);
				}else
				{
					Log.e("支付宝支付通道：", "星POS");
					XingPosServicePay(total_feeStr);
				}
			}else if(payType.equals("030")){
				Log.e("支付类型：", "银联二维码支付");
				if(ylPayServiceType){
					Log.e("银联二维码支付通道：", "默认");
					String payTypeStr = "060";
					PosScanpayReqData posBean = PayRequestUtil.payReq(payTypeStr, auth_no, total_feeStr,loginInitData,posProvider);
					//付款二维码内容(发起支付请求)
					String url = NitConfig.barcodepayUrl;
					payRequestMethood(url,posBean);
				}else
				{
					Log.e("银联二维码支付通道：", "星POS");
					XingPosServicePay(total_feeStr);
				}
			}else if(payType.equals("050")){
				Log.e("支付类型：", "翼支付");
				PosScanpayReqData posBean = PayRequestUtil.payReq(payType, auth_no, total_feeStr,loginInitData,posProvider);
				//付款二维码内容(发起支付请求)
				String url = NitConfig.barcodepayUrl;
				payRequestMethood(url,posBean);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				if(!payType.equals("040")){
					orderid_scan = job.getString("orderid_scan");
				}
				transamount = job.getString("transamount");
				Log.e("返回的支付金额信息：", transamount);
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
			}else{
				Log.e("扫码支付成功：", txndetail);
				Gson gjson  =  GsonUtils.getGson();
				ScanPaymentDate posResult = gjson.fromJson(txndetail, ScanPaymentDate.class);
				//手动录入交易类型微信，支付宝，银联010,020,030赋值微信，支付宝，银联为11,12,13
				if(payType.equals("010")){
					posResult.setPay_tp("11");
				}else if(payType.equals("020")){
					posResult.setPay_tp("12");
				}else if(payType.equals("030")){
					posResult.setPay_tp("13");
				}
				/**
				 * 下面是调用帮助类将一个对象以序列化的方式保存
				 * 方便我们在其他界面调用，类似于Intent携带数据
				 */
				try {
					MySerialize.saveObject("scanPayOrder",getContext(),MySerialize.serialize(posResult));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
				Boolean scanPayValue = true;
				Boolean payServiceType = false;
				String scanPayTypeValue = "pay";//pay:支付，refund:退款
				sharedPreferencesUtil.put("scanPayYes", scanPayValue);
				sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
				sharedPreferencesUtil.put("payServiceType", payServiceType);
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
//				intentToActivity();
			}
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		//如果是银行卡消费保存消费返回信息
		if(payType.equals("040")){
			String jsonStr = bundle.getString("json");
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

		}else{
			String amountStr = bundle.getString("amount");//金额
			String traceNoStr = bundle.getString("traceNo");//凭证号
			String batchNoStr = bundle.getString("batchNo");//批次号
			String referenceNoStr = bundle.getString("referenceNo");//参考号
			String cardNoStr = bundle.getString("cardNo");//卡号(扫码支付返回订单号)
			String typeStr = bundle.getString("type");//卡类型
			String issueStr = bundle.getString("issue");//发卡行
			String dateStr = bundle.getString("date");//日期
			String timeStr = bundle.getString("time");//时间
			String zfbOrderNumberStr = bundle.getString("zfbOrderNumber");//商户自定义的订单号
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

			scanPaymentDate = new ScanPaymentDate();
			//手动录入交易类型微信，支付宝，银联010,020,030赋值微信，支付宝，银联为11,12,13
			if(payType.equals("010")){
				scanPaymentDate.setPay_tp("11");
			}else if(payType.equals("020")){
				scanPaymentDate.setPay_tp("12");
			}else if(payType.equals("030")){
				scanPaymentDate.setPay_tp("13");
			}
			scanPaymentDate.setOrderid_scan(cardNoStr);
			scanPaymentDate.setTranslocaldate(dateStr);
			scanPaymentDate.setTranslocaltime(timeStr);
			scanPaymentDate.setTransamount(totalStr);//此处金额是分转元之后的金额

			/**
			 * 下面是调用帮助类将一个对象以序列化的方式保存
			 * 方便我们在其他界面调用，类似于Intent携带数据
			 */
			try {
				MySerialize.saveObject("scanPayOrder",getContext(),MySerialize.serialize(scanPaymentDate));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "scanPay");
			Boolean scanPayValue = true;
			Boolean payServiceType = false;
			String scanPayTypeValue = "pay";//pay:支付，refund:退款
			sharedPreferencesUtil.put("scanPayYes", scanPayValue);
			sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
			sharedPreferencesUtil.put("payServiceType", payServiceType);
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
			isApi = false;
			getCardStock(isApi);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			Bundle bundle = data.getExtras();
			if (requestCode == FU_REQUEST_CODE&&bundle != null) {
				switch (resultCode) {
					// 扫码成功
					case Activity.RESULT_OK:
						if(posProvider.equals(NEW_LAND)){

						}else if(posProvider.equals(FUYOU_SF)){
							String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
							Log.e("Fragment界面获取扫描结果：", scanCodeStr);
							//如果扫描的二维码为空则不执行支付请求
							if(scanCodeStr!=null&&!scanCodeStr.equals("")){
								//auth_no	授权码（及扫描二维码值）
								String auth_no = scanCodeStr;
								//total_fee	金额，单位分(金额需乘以100)
								String etTextStr = pending.toString();
								Log.e("输入框文本text值：", etTextStr);
								String total_feeStr = DecimalUtil.elementToBranch(etTextStr);

								scanPayMethodTwo(auth_no,total_feeStr);

							}else{
								//清空StringBuilder，EditText恢复初始值
								//清空EditText
								pending.delete( 0, pending.length() );
								if(pending.length()<=0){
									etSumMoney.setText("￥0.00");
								}
								Toast.makeText(getContext(),"扫描结果为空！",Toast.LENGTH_LONG).show();
							}
						}

						break;
					// 扫码取消
					case Activity.RESULT_CANCELED:
						if(posProvider.equals(NEW_LAND)){

						}else if(posProvider.equals(FUYOU_SF)){
							String reason = "";
							if (data != null) {
								Bundle b = data.getExtras();
								if (b != null) {
									reason = (String) b.get("reason");
								}
							}
							if (Utils.isNotEmpty(reason)) {
								Log.d("reason", reason);
								Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
							}
						}
						break;
				}
			}

			if (requestCode == PAY_REQUEST_CODE&&bundle != null) {
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
					 if(posProvider.equals(NEW_LAND)){
						 String reason = bundle.getString("reason");
						 Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
					 }else if(posProvider.equals(FUYOU_SF)){
						 String reason = "";
						 if (data != null) {
							 Bundle b = data.getExtras();
							 if (b != null) {
								 reason = (String) b.get("reason");
							 }
						 }
						 if (reason != null) {
							 Log.d("reason", reason);
							 Toast.makeText(getContext(), reason, Toast.LENGTH_SHORT).show();
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
							if(printNum.equals("printNumOne")){
								boolean isQueryCoupons = loginInitData.isQueryCoupons();
								if(isQueryCoupons){
									isApi = true;
									getCardStock(isApi);
								}


							}else if(printNum.equals("printNumTwo")){
								if(index < 2){
									//打印正常
									//弹出对话框提示打印下一联
									showPrintTwoDialog(payResData);
								}else{
									boolean isQueryCoupons = loginInitData.isQueryCoupons();
									if(isQueryCoupons){
										isApi = true;
										getCardStock(isApi);
									}
								}

							}

							break;
						case Activity.RESULT_CANCELED:
							if (Utils.isNotEmpty(reason_str)) {
								reason = reason_str;
								Log.e("reason", reason);
								if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
									//缺纸，不能打印
									ToastUtil.showText(getContext(),"打印机缺纸，打印中断！",1);
								}else {
									ToastUtil.showText(getContext(),"打印机出现故障错误码为："+reason,1);
								}
							}else{
								ToastUtil.showText(getContext(),reason,1);
							}

							Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
							break;
						default:
							break;

					}
				}else{
					ToastUtil.showText(getContext(),"打印返回数据为空！",1);
				}

			}

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
