package com.wanding.xingpos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**  订单详情界面 */
public class OrderDetailsActivity extends BaseActivity implements OnClickListener{

	private Context context = OrderDetailsActivity.this;
	private Dialog hintDialog;// 加载数据时对话框
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	/** 商户订单号，渠道订单号，交易时间，交易金额，交易渠道，交易状态 */
	private TextView tvMerchantNo,tvTransactionId,tvTerminalTime,tvTerminalTotal,tvTerminalType,tvPayState;
	
	/** 完成，重打印  */
	private TextView tvOK,tvPrint;
	
	
	private OrderDetailData order;//订单对象
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
		setContentView(R.layout.order_details_activity);
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
			loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent in = getIntent();
		order = (OrderDetailData) in.getSerializableExtra("order");
		
		updateViewData();
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		
		tvTitle.setText("交易详情");
		imgBack.setOnClickListener(this);
		tvMerchantNo = (TextView) findViewById(R.id.order_details_txMerchant_no);
		tvTransactionId = (TextView) findViewById(R.id.order_details_tvTransactionId);
		tvTerminalTime= (TextView) findViewById(R.id.order_details_txTerminal_time);
		tvTerminalTotal= (TextView) findViewById(R.id.order_details_txTerminal_total);
		tvTerminalType= (TextView) findViewById(R.id.order_details_txTerminal_type);
		tvPayState= (TextView) findViewById(R.id.order_details_txPay_state);
		
		tvOK = (TextView) findViewById(R.id.order_details_tvOk);
		tvPrint = (TextView) findViewById(R.id.order_details_tvPrint);
		
		tvOK.setOnClickListener(this);
		tvPrint.setOnClickListener(this);
	}
	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	try {
			tvMerchantNo.setText("");
			tvTransactionId.setText("");
			tvTerminalTime.setText("");
			tvTerminalTotal.setText("");
			tvTerminalType.setText("");
			tvPayState.setText("");
			
			if(order!=null){
				//商户订单号
				tvMerchantNo.setText(order.getOrderId());
				//渠道订单号
				String transactionIdStr = order.getTransactionId();
				String transactionId = "";
				if(Utils.isNotEmpty(transactionIdStr)){
					transactionId = transactionIdStr;
				}
				tvTransactionId.setText(transactionId);
				//交易时间
				tvTerminalTime.setText(DateTimeUtil.stampToDate(Long.parseLong(order.getPayTime())));
				//交易金额
				tvTerminalTotal.setText(DecimalUtil.StringToPrice(order.getGoodsPrice()));
				//交易渠道//WX=微信，ALI=支付宝，BEST=翼支付
				String payTypeStr = order.getPayWay();
				String payType = "";
				if(payTypeStr.equals("WX")){
					payType = "微信";
				}else if(payTypeStr.equals("ALI")){
					payType = "支付宝";
				}else if(payTypeStr.equals("BEST")){
					payType = "翼支付";
				}else if(payTypeStr.equals("DEBIT")){
					//DEBIT= 借记卡       CREDIT=贷记卡 
					payType = "银行卡(借记卡)";
				}else if(payTypeStr.equals("CREDIT")){
					//DEBIT= 借记卡       CREDIT=贷记卡
					payType = "银行卡(贷记卡)";
				}else if(payTypeStr.equals("UNIONPAY")){
					//UNIONPAY = 银联二维码
					payType = "银联二维码";
				}else if(payTypeStr.equals("BANK")){
					//BANK = 银行卡
					payType = "银行卡";
				}else{
					payType = "未知";
				}
				tvTerminalType.setText(payType);
				String orderStatus = "未知状态";
				String orderTypeStr = order.getOrderType();
				if(orderTypeStr!=null&&!orderTypeStr.equals("")&&!orderTypeStr.equals("null")){
					//先判断是支付交易还是退款交易 0正向 ,1退款
					if(orderTypeStr.equals("0")){
						//判断交易状态状态status 状态为支付、预支付、退款等	0准备支付1支付完成2支付失败3.包括退款5.支付未知
						String statusStr = order.getStatus();
						if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("1")){
							orderStatus = "支付成功";
						}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("3")){
							orderStatus = "包含退款";
						}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("4")){
							orderStatus = "全部退款";
						}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("5")){
							orderStatus = "支付未知";
						}else{
							orderStatus = "支付失败";
						}
					}else if(orderTypeStr.equals("1")){
						//判断退款状态
						String refund_statusStr = order.getStatus();
						if(refund_statusStr!=null&&!refund_statusStr.equals("null")&&refund_statusStr.equals("1")){
							orderStatus = "退款成功";
						}else{
							orderStatus = "退款失败";
						}
					}
				}
				tvPayState.setText(orderStatus);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
	 * 查询是否有优惠券
	 */
	private void getCardStock(){
		hintDialog=CustomDialog.CreateDialog(getContext(), "    查询中...");
		hintDialog.show();
		hintDialog.setCancelable(false);
		final String url = NitConfig.queryPosConsumOrderUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("orderId",order.getOrderId());

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
			}
		}.start();
	}

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					String cardStockJson = (String) msg.obj;
					cardStockJson(cardStockJson);

					hintDialog.dismiss();
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
					startPrintText();
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
						order.setUrl(url);
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
		startPrintText();
	}

    /**
	 *  开始打印小票
	 */
    private void startPrintText(){
		//交易未知不打印小票
		boolean orderStatus = true;
		String orderTypeStr = order.getOrderType();
		if(orderTypeStr!=null&&!orderTypeStr.equals("")&&!orderTypeStr.equals("null")){
			//先判断是支付交易还是退款交易 0正向 ,1退款
			if(orderTypeStr.equals("0")){
				//判断交易状态状态status 状态为支付、预支付、退款等	0准备支付1支付完成2支付失败3.包括退款5.支付未知
				String statusStr = order.getStatus();
				if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("1")){

				}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("3")){

				}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("4")){

				}else if(statusStr!=null&&!statusStr.equals("null")&&statusStr.equals("5")){
					orderStatus = false;
				}else{
					orderStatus = false;
				}
			}
		}


		String payTypeStr = order.getPayWay();
		if(orderStatus){
			if(!payTypeStr.equals("DEBIT")&&!payTypeStr.equals("CREDIT")&&!payTypeStr.equals("BANK")){
				int index = 1;
				if(posProvider.equals(NEW_LAND)){
					//初始化打印机
					getPrinter();
					if(printNum.equals("printNumNo")){

					}else if(printNum.equals("printNumOne")){
						//打印
						NewlandPrintUtil.orderDetailsPrintText(getContext(), aidlPrinter, order, loginInitData,isDefault,index);

					}else if(printNum.equals("printNumTwo")){
						//打印
						NewlandPrintUtil.orderDetailsPrintText(getContext(), aidlPrinter, order, loginInitData,isDefault,index);
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
						FuyouPrintUtil.orderDetailsPrintText(getContext(), printService, order, loginInitData,isDefault,index);

					}else if(printNum.equals("printNumTwo")){
						//打印
						FuyouPrintUtil.orderDetailsPrintText(getContext(), printService, order, loginInitData,isDefault,index);
						try {
							Thread.sleep(FuyouPrintUtil.time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//弹出对话框提示打印下一联
						showPrintTwoDialog();

					}

				}
			}else{
				Toast.makeText(OrderDetailsActivity.this, "银行卡交易不支持明细补打！", Toast.LENGTH_LONG).show();
			}

		}else{
			Toast.makeText(OrderDetailsActivity.this, "该订单状态未知，不打印小票！", Toast.LENGTH_LONG).show();
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
					NewlandPrintUtil.orderDetailsPrintText(getContext(), aidlPrinter, order, loginInitData,isDefault,index);
				}else if(posProvider.equals(FUYOU_SF)){
					FuyouPrintUtil.orderDetailsPrintText(getContext(), printService, order, loginInitData,isDefault,index);
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
		case R.id.order_details_tvOk:
			finish();
			break;
		case R.id.order_details_tvPrint:
			if(Utils.isFastClick()){
				return;
			}
			//直接打印小票
//			startPrintText();

			//查询是否有优惠券
			getCardStock();



			break;
			
		}
	}
}
