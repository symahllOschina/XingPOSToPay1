package com.wanding.xingpos.activity;

import java.io.IOException;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.PosPayQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.SharedPreferencesUtil;

/**  订单详情界面 */
public class QueryOrderDetailsActivity extends BaseActivity implements OnClickListener{

	private Context context = QueryOrderDetailsActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	/** 商户订单号，交易时间，交易金额，交易渠道，交易状态 */
	private TextView tvMerchantNo,tvTerminalTime,tvTerminalTotal,tvTerminalType,tvPayState;
	
	/** 完成，重打印  */
	private TextView tvOK,tvPrint;
	
	
	private PosPayQueryResData queryResData;//查询成功返回对象
	private UserLoginResData posPublicData;
	
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

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;
	/** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
	private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_order_details_activity);
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
	
	/** 初始化数据 */
	private void initData(){
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(context, "printing");
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
		Intent in = getIntent();
		queryResData = (PosPayQueryResData) in.getSerializableExtra("queryResData");
		
		updateViewData();
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		
		tvTitle.setText("扫码查单");
		imgBack.setOnClickListener(this);
		tvMerchantNo = (TextView) findViewById(R.id.query_order_details_txMerchant_no);
		tvTerminalTime= (TextView) findViewById(R.id.query_order_details_txTerminal_time);
		tvTerminalTotal= (TextView) findViewById(R.id.query_order_details_txTerminal_total);
		tvTerminalType= (TextView) findViewById(R.id.query_order_details_txTerminal_type);
		tvPayState= (TextView) findViewById(R.id.query_order_details_txPay_state);
		
		tvOK = (TextView) findViewById(R.id.query_order_details_tvOk);
		tvPrint = (TextView) findViewById(R.id.query_order_details_tvPrint);
		
		tvOK.setOnClickListener(this);
		tvPrint.setOnClickListener(this);
	}
	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	tvMerchantNo.setText("");
    	tvTerminalTime.setText("");
    	tvTerminalTotal.setText("");
    	tvTerminalType.setText("");
    	tvPayState.setText("");
    	
    	if(queryResData!=null){
    		//商户订单号
    		tvMerchantNo.setText(queryResData.getOut_trade_no());
    		//交易时间
    		tvTerminalTime.setText(DateTimeUtil.timeStrToDateStr(queryResData.getEnd_time()));
    		//交易金额
    		tvTerminalTotal.setText(DecimalUtil.branchToElement(queryResData.getTotal_fee()));
    		//交易渠道//WX=微信，ALI=支付宝，BEST=翼支付
    		String payTypeStr = queryResData.getPay_type();
    		String payType = "";
    		if(payTypeStr.equals("WX")){
    			payType = "微信";
    		}else if(payTypeStr.equals("ALI")){
    			payType = "支付宝";
    		}else if(payTypeStr.equals("BEST")){
    			payType = "翼支付";
    		}else if(payTypeStr.equals("DEBIT")||payTypeStr.equals("CREDIT")){
				//DEBIT= 借记卡       CREDIT=贷记卡 
				payType = "银行卡";
			}else if(payTypeStr.equals("UNIONPAY")){
				//UNIONPAY = 银联二维码
				payType = "银联二维码";
			}else if(payTypeStr.equals("BANK")){
				//BANK = 银行卡
				payType = "银行卡";
			}
    		tvTerminalType.setText(payType);
//    		//交易状态 result_code	业务结果：“01”支付成功 ，02”支付失败 ，“03”支付中,"04"含退款，
    		String payStateStr = queryResData.getResult_code();
    		String payState = "";
    		if(payStateStr.equals("01")){
    			payState = "已支付";
    		}else if(payStateStr.equals("02")){
    			payState = "支付失败";
    		}else if(payStateStr.equals("03")){
    			payState = "支付中";
    		}else if(payStateStr.equals("04")){
    			payState = "全部退款";
    		}else if(payStateStr.equals("05")){
    			payState = "部分退款";
    		}
    		tvPayState.setText(payState);
    	}
    }

	/**  打印下一联提示窗口 */
	private void showPrintTwoDialog(){
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
					//打印
					NewlandPrintUtil.querySuccessPrintText(getContext(), aidlPrinter, queryResData, posPublicData,isDefault,index);
				}else if(posProvider.equals(FUYOU_SF)){
					//打印
					FuyouPrintUtil.querySuccessPrintText(getContext(), printService, queryResData, posPublicData,isDefault,index);
				}


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
		case R.id.query_order_details_tvOk:
			finish();
			break;
		case R.id.query_order_details_tvPrint:
			int index = 1;
			if(posProvider.equals(NEW_LAND)){
				//初始化打印机
				getPrinter();
				if(printNum.equals("printNumNo")){

				}else if(printNum.equals("printNumOne")){
					//打印
					NewlandPrintUtil.querySuccessPrintText(getContext(), aidlPrinter, queryResData, posPublicData,isDefault,index);

				}else if(printNum.equals("printNumTwo")){
					//打印
					NewlandPrintUtil.querySuccessPrintText(getContext(), aidlPrinter, queryResData, posPublicData,isDefault,index);
					try {
						Thread.sleep(NewlandPrintUtil.time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//弹出对话框提示打印下一联

					showPrintTwoDialog();

				}
			}else if(posProvider.equals(FUYOU_SF)){
				if(printNum.equals("printNumNo")){

				}else if(printNum.equals("printNumOne")){
					//打印
					FuyouPrintUtil.querySuccessPrintText(getContext(), printService, queryResData, posPublicData,isDefault,index);

				}else if(printNum.equals("printNumTwo")){
					//打印
					FuyouPrintUtil.querySuccessPrintText(getContext(), printService, queryResData, posPublicData,isDefault,index);
					try {
						Thread.sleep(FuyouPrintUtil.time);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//弹出对话框提示打印下一联

					showPrintTwoDialog();

				}

			}

			break;
			
		}
	}
}
