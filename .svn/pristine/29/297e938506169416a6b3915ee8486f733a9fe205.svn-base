package com.wanding.xingpos.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.activity.AgainPrintActivity;
import com.wanding.xingpos.activity.AuthActivity;
import com.wanding.xingpos.activity.BatchSecurActivity;
import com.wanding.xingpos.activity.CardPayActivity;
import com.wanding.xingpos.activity.CardRefundActivity;
import com.wanding.xingpos.activity.CopyOfOrderListActivity;
import com.wanding.xingpos.activity.MemberManageActivity;
import com.wanding.xingpos.activity.ScanAuthActivity;
import com.wanding.xingpos.activity.ScanAuthCancelActivity;
import com.wanding.xingpos.activity.ScanAuthRecodeListActivity;
import com.wanding.xingpos.activity.ScanPayActivity;
import com.wanding.xingpos.activity.ScanQueryActivity;
import com.wanding.xingpos.activity.ShiftActivity;
import com.wanding.xingpos.activity.StaffListActivity;
import com.wanding.xingpos.activity.UserSetPasswdActivity;
import com.wanding.xingpos.activity.WriteOffActivity;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseFragment;
import com.wanding.xingpos.bean.PosInitData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
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

@SuppressLint("ValidFragment")
public class MainFunctionFragment extends BaseFragment implements OnClickListener{

	private int mCurIndex = -1;
	private static final String FRAGMENT_INDEX = "1";
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	private boolean onResume=true;//onResume()方法初始化不执行
	
	private LinearLayout skPosLoginLayout,skXiaoFeiLayout,skTuiKuanLayout,//POS签到，刷卡消费，退款
			             authLayout,authCXLayout,authWCLayout,authWCCXLayout,//预授权，预授权撤销，预授权完成，预授权完成撤销
						 byStagesLayout;//分期
	private LinearLayout ydXiaoFeiLayout,ydTuiKuanLayout,queryOrderLayout,writeOffLayout,//移动消费，退款,扫码查单,核销劵
						 ydYSQLayout,ydYSQCXLayout,ydYSQWCLayout,ydYSQWCCXLayout,authRecodeLayout,memberLayout;//预授权，预授权撤销，预授权完成,会员
	private LinearLayout summaryLayout,againPrintLayout,payOrderDetailLayout,staffAdminLayout,
			             batchSecurLayout,settingLayout;//结算，重打印，交易明细,员工,批量制卷,设置
	
	private LinearLayout zanweiLayout;
	
	private PosInitData posInitData;
	private UserLoginResData loginInitData;
	
	AidlDeviceService aidlDeviceService = null;  

    AidlPrinter aidlPrinter = null;
    private String TAG = "lyc";
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
	private MySyntherizer synthesizer;
	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.main_function_fragment, null, false);
		posProvider = MainActivity.posProvider;
		initData();
		initView(view);
//		//获得索引值
//		Bundle bundle = getArguments();
//		if (bundle != null) {
//			mCurIndex = bundle.getInt(FRAGMENT_INDEX);
//		}
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
	
	public MainFunctionFragment(MySyntherizer synthesizer) {
		super();
		this.synthesizer = synthesizer;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(onResume){
			//加载数据
			
		}
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		if(posProvider.equals(NEW_LAND)){
			getActivity().unbindService(serviceConnection);
			aidlPrinter=null;
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
	 * 重写父类方法（fragment可见时加载界面数据）
	 */
	@Override
	protected void lazyLoad() {

		if (!isPrepared || !isVisible||mHasLoadedOnce) {
			return;
		}
		//请求数据
		
	}
	
	private void initData(){

	}
	
	/**  初始化控件*/
	private void initView(View view){
		skPosLoginLayout = (LinearLayout) view.findViewById(R.id.main_function_skPosLogin);
		skXiaoFeiLayout = (LinearLayout) view.findViewById(R.id.main_function_skXiaoFei);
		skTuiKuanLayout = (LinearLayout) view.findViewById(R.id.main_function_skTuiKuan);
		
		authLayout = (LinearLayout) view.findViewById(R.id.main_function_authLayout);
		authCXLayout = (LinearLayout) view.findViewById(R.id.main_function_authCXLyout);
		authWCLayout = (LinearLayout) view.findViewById(R.id.main_function_authWCLayout);
		authWCCXLayout = (LinearLayout) view.findViewById(R.id.main_function_authWCCXLayout);

		byStagesLayout = (LinearLayout) view.findViewById(R.id.main_function_byStagesLayout);

		ydXiaoFeiLayout = (LinearLayout) view.findViewById(R.id.main_function_ydXiaoFei);
		ydTuiKuanLayout = (LinearLayout) view.findViewById(R.id.main_function_ydTuiKuan);
		queryOrderLayout = (LinearLayout) view.findViewById(R.id.main_function_queryOrderLayout);
		writeOffLayout = (LinearLayout) view.findViewById(R.id.main_function_writeOffLayout);

		ydYSQLayout = view.findViewById(R.id.main_function_ydYSQLayout);
		ydYSQCXLayout = view.findViewById(R.id.main_function_ydYSQCXLayout);
		ydYSQWCLayout = view.findViewById(R.id.main_function_ydYSQWCLayout);
        ydYSQWCCXLayout = view.findViewById(R.id.main_function_ydYSQWCCXLayout);
		authRecodeLayout = view.findViewById(R.id.main_function_authRecodeLayout);
		memberLayout = view.findViewById(R.id.main_function_memberLayout);

		summaryLayout = (LinearLayout) view.findViewById(R.id.main_function_summaryLayout);
		againPrintLayout = (LinearLayout) view.findViewById(R.id.main_function_againPrint);
		payOrderDetailLayout = (LinearLayout) view.findViewById(R.id.main_function_payOrderDetail);
		staffAdminLayout = (LinearLayout) view.findViewById(R.id.main_function_staffAdmin);
		batchSecurLayout = (LinearLayout) view.findViewById(R.id.main_function_batchSecurLayout);
		settingLayout = (LinearLayout) view.findViewById(R.id.main_function_settingLayout);

		zanweiLayout = (LinearLayout) view.findViewById(R.id.main_function_zanweiLayout);



		skPosLoginLayout.setOnClickListener(this);
		skXiaoFeiLayout.setOnClickListener(this);
		skTuiKuanLayout.setOnClickListener(this);
		//预授权
		authLayout.setOnClickListener(this);
		authCXLayout.setOnClickListener(this);
		authWCLayout.setOnClickListener(this);
		authWCCXLayout.setOnClickListener(this);

		//分期
		byStagesLayout.setOnClickListener(this);
		
		ydTuiKuanLayout.setOnClickListener(this);
		ydXiaoFeiLayout.setOnClickListener(this);
		queryOrderLayout.setOnClickListener(this);

		zanweiLayout.setVisibility(View.GONE);
		if(posProvider.equals(NEW_LAND)){
//			writeOffLayout.setVisibility(View.INVISIBLE);
//			ydYSQLayout.setVisibility(View.VISIBLE);
//			ydYSQCXLayout.setVisibility(View.VISIBLE);
//			ydYSQWCLayout.setVisibility(View.VISIBLE);
//            ydYSQWCCXLayout.setVisibility(View.VISIBLE);
//			authRecodeLayout.setVisibility(View.VISIBLE);
			memberLayout.setVisibility(View.INVISIBLE);
			batchSecurLayout.setVisibility(View.GONE);
			zanweiLayout.setVisibility(View.VISIBLE);


			ydYSQLayout.setOnClickListener(this);
			ydYSQCXLayout.setOnClickListener(this);
			ydYSQWCLayout.setOnClickListener(this);
			ydYSQWCCXLayout.setOnClickListener(this);
			authRecodeLayout.setOnClickListener(this);
		}else if(posProvider.equals(FUYOU_SF)){

//			writeOffLayout.setVisibility(View.VISIBLE);
//			ydYSQLayout.setVisibility(View.VISIBLE);
//			ydYSQCXLayout.setVisibility(View.VISIBLE);
//			ydYSQWCLayout.setVisibility(View.VISIBLE);
//            ydYSQWCCXLayout.setVisibility(View.VISIBLE);
//			authRecodeLayout.setVisibility(View.VISIBLE);
			memberLayout.setVisibility(View.VISIBLE);
			batchSecurLayout.setVisibility(View.VISIBLE);
			zanweiLayout.setVisibility(View.GONE);

			memberLayout.setOnClickListener(this);
			batchSecurLayout.setOnClickListener(this);
		}

        writeOffLayout.setOnClickListener(this);
        ydYSQLayout.setOnClickListener(this);
        ydYSQCXLayout.setOnClickListener(this);
        ydYSQWCLayout.setOnClickListener(this);
        ydYSQWCCXLayout.setOnClickListener(this);
        authRecodeLayout.setOnClickListener(this);

		summaryLayout.setOnClickListener(this);
		againPrintLayout.setOnClickListener(this);
		payOrderDetailLayout.setOnClickListener(this);
		staffAdminLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
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
					startActivityForResult(intent, 1);
				} catch(ActivityNotFoundException e) {
                    e.printStackTrace();
					//TODO:
					Log.e("Newland_Exception", "找不到界面");
					posProvider = FUYOU_SF;
					appDataInstance();
				} catch(Exception e) {
                    e.printStackTrace();
					//TODO:
					Log.e("Exception：", "异常");
				}
			}else if(posProvider.equals(FUYOU_SF)){
				try {
					ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
					Intent intent = new Intent();
					intent.setComponent(component);
					Bundle bundle = new Bundle();
					bundle.putString("transName", "签到");
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				} catch(ActivityNotFoundException e) {
                    e.printStackTrace();
					//TODO:
					Log.e("Fouyou_Exception", "找不到界面");
					posProvider = "";
					appDataInstance();
				} catch(Exception e) {
				    e.printStackTrace();
					//TODO:
					Log.e("Exception：", "异常");
				}
			}
		}else{
			Toast.makeText(getContext(),"找不到界面",1*1000).show();
		}

	}
	
	private void saveDataLocal(PosInitData data){

		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("PosInitData",getContext(),MySerialize.serialize(data));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e("保存POS机初始化信息：","成功！");
		//将POS初始化信息保存在本地
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "posInit");
		sharedPreferencesUtil.put("posMercId", posInitData.getMercId_pos());
		sharedPreferencesUtil.put("posTrmNo", posInitData.getTrmNo_pos());
		sharedPreferencesUtil.put("posMername", posInitData.getMername_pos());
		sharedPreferencesUtil.put("posBatchno", posInitData.getBatchno_pos());
		//执行登陆功能
		getLoginData();
			

	}
	
	/** 获取登录信息（签到）  */
	private void getLoginData(){
		final String url = NitConfig.loginUrl;
		new Thread(){
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
                   Log.e("签到返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("签到返回JSON值：", jsonStr);
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
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
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
						

						
						Toast.makeText(getContext(), "签到成功！", Toast.LENGTH_LONG).show();
						//打印商户信息
						if(posProvider.equals(NEW_LAND)){
							//初始化打印机
							getPrinter();
							//打印
							NewlandPrintUtil.businessInfoPrintText(getContext(), aidlPrinter,loginInitData);
						}else if(posProvider.equals(FUYOU_SF)){
							//打印
							FuyouPrintUtil.businessInfoPrintText(getContext(),printService,loginInitData);
						}

//						Intent intent=new Intent();
//						intent.setClass(getContext(), WelcomeActivity.class);
//						startActivity(intent);
//						//关闭应用
//						BaseApplication.getInstance().exit();
						
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
							logResData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
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
			case 201:
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				
				break;
			}
		};
	};


	@Override
	public void onClick(View v) {
		Intent in = null;
		String authType = "";
		switch (v.getId()) {
		case R.id.main_function_skPosLogin://POS签到
			//应用初始化（获取POS机商户信息）
			appDataInstance();
			break;
		case R.id.main_function_skXiaoFei://刷卡消费
			in = new Intent();
			in.setClass(getActivity(), CardPayActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_skTuiKuan://刷卡退款
			in = new Intent();
			in.setClass(getContext(), CardRefundActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_authLayout://预授权
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			//1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
			in = new Intent();
			in.setClass(getContext(), AuthActivity.class);
			authType = "1";
			in.putExtra("authType", authType);
			startActivity(in);
			break;
		case R.id.main_function_authCXLyout://预授权撤销
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			//1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
			in = new Intent();
			in.setClass(getContext(), AuthActivity.class);
			authType = "2";
			in.putExtra("authType", authType);
			startActivity(in);
			break;
		case R.id.main_function_authWCLayout://预授权完成
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			//1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
			in = new Intent();
			in.setClass(getContext(), AuthActivity.class);
			authType = "3";
			in.putExtra("authType", authType);
			startActivity(in);
			break;
		case R.id.main_function_authWCCXLayout://预授权完成撤销
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			//1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
			in = new Intent();
			in.setClass(getContext(), AuthActivity.class);
			authType = "4";
			in.putExtra("authType", authType);
			startActivity(in);
			break;
		case R.id.main_function_byStagesLayout://分期
			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
//			in = new Intent();
//			in.setClass(getActivity(), InstalmentActivity.class);
//			startActivity(in);
			break;
		case R.id.main_function_ydXiaoFei://移动消费
			in = new Intent();
			in.setClass(getActivity(), ScanPayActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_ydTuiKuan://移动退款
			in = new Intent();
			in.setClass(getActivity(), UserSetPasswdActivity.class);
			in.putExtra("optionKey", "scanRefund");
			startActivity(in);
			break;
		case R.id.main_function_queryOrderLayout://扫码查单
			in = new Intent();
			in.setClass(getActivity(), ScanQueryActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_writeOffLayout://核劵
			String sign = "2";
			in = new Intent();
			in.setClass(getActivity(), WriteOffActivity.class);
			in.putExtra("sign",sign);
			startActivity(in);
			break;
		case R.id.main_function_ydYSQLayout://预授权
//			ToastUtil.showText(getActivity(),"此功能暂未开通",1);
			authType = "1";
			in = new Intent();
			in.setClass(getActivity(), ScanAuthActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_ydYSQCXLayout://预授权撤销
//			ToastUtil.showText(getActivity(),"此功能暂未开通",1);
			authType = "2";
			in = new Intent();
			in.setClass(getActivity(), ScanAuthCancelActivity.class);
			in.putExtra("authAction",authType);
			startActivity(in);
			break;
		case R.id.main_function_ydYSQWCLayout://预授权完成
//			ToastUtil.showText(getActivity(),"此功能暂未开通",1);
			authType = "3";
			in = new Intent();
			in.setClass(getActivity(), ScanAuthCancelActivity.class);
			in.putExtra("authAction",authType);
			startActivity(in);
			break;
		case R.id.main_function_ydYSQWCCXLayout://预授权完成撤销
//			ToastUtil.showText(getActivity(),"此功能暂未开通",1);
			authType = "4";
			in = new Intent();
			in.setClass(getActivity(), ScanAuthCancelActivity.class);
			in.putExtra("authAction",authType);
			startActivity(in);
			break;
		case R.id.main_function_authRecodeLayout://预授权记录
//			ToastUtil.showText(getActivity(),"此功能暂未开通",1);
			in = new Intent();
			in.setClass(getActivity(), ScanAuthRecodeListActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_memberLayout://会员
//			Toast.makeText(getContext(), "会员！", Toast.LENGTH_LONG).show();
			in = new Intent();
			in.setClass(getActivity(), MemberManageActivity.class);
			startActivity(in);

			break;
		case R.id.main_function_summaryLayout://交接班
			
//			try {
//				loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			SummaryOrder();
			
			in = new Intent();
			in.setClass(getActivity(), ShiftActivity.class);
			startActivity(in);
			
			
			break;
		case R.id.main_function_againPrint://重打印
			in = new Intent();
			in.setClass(getActivity(), AgainPrintActivity.class);
			startActivity(in);
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			break;
		case R.id.main_function_payOrderDetail://交易明细
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			in = new Intent();
//			in.setClass(getActivity(), OrderListActivity.class);
			in.setClass(getActivity(), CopyOfOrderListActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_staffAdmin://员工
			in = new Intent();
			in.setClass(getActivity(), StaffListActivity.class);
			startActivity(in);
			break;
		case R.id.main_function_batchSecurLayout://批量制劵
			in = new Intent();
			in.setClass(getActivity(), BatchSecurActivity.class);
			startActivity(in);
//			Toast.makeText(getContext(), "此功能暂未开通！", Toast.LENGTH_LONG).show();
			break;
		case R.id.main_function_settingLayout://设置
			in = new Intent();
			in.setClass(getActivity(), UserSetPasswdActivity.class);
			in.putExtra("optionKey", "settings");
			startActivity(in);
			break;
		default:
			break;
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
				posInitData = new PosInitData();
				posInitData.setMercId_pos(job.getString("merid"));
				posInitData.setTrmNo_pos(job.getString("termid"));
				posInitData.setMername_pos(job.getString("mername"));
				posInitData.setBatchno_pos(job.getString("batchno"));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(),"POS签到失败！请联系技术！",Toast.LENGTH_LONG).show();
			}
            //将需要的参数传入支付请求公共类保存在本地
			saveDataLocal(posInitData);
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

		posInitData = new PosInitData();
		posInitData.setPosProvider(posProvider);
		posInitData.setMercId_pos(merchantIdStr);
		posInitData.setTrmNo_pos(terminalIdStr);
		posInitData.setMername_pos(merchantNameStr);
		posInitData.setBatchno_pos(batchNoStr);
		//将需要的参数传入支付请求公共类保存在本地
		saveDataLocal(posInitData);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		     }
		     break;
		}
		}
	}
}
