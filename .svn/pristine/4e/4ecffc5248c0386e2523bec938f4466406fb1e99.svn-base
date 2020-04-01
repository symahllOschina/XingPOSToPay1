package com.wanding.xingpos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.PayTypeBean;
import com.wanding.xingpos.bean.SummaryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date.picker.CustomDatePicker;
import com.wanding.xingpos.date.picker.TimeSelector;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.summary.util.SummaryDateUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.Utils;

/**
 * 汇总Actiivty
 */
public class SummaryActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "SummaryActivity";
	private Dialog hintDialog;// 加载数据时对话框

	private LinearLayout layoutTitle;
	private ImageView imgBack,tvTitleImg;
	private TextView tvTitle,tvFunction;
	
	private UserLoginResData loginInitData; 
	private SummaryResData summary;
	
	private CustomDatePicker datePicker;
	private String startDateTime,endDateTime,endDate,seleteDate;//
	
	private TextView tvSumMoney,tvSumNum;
	private LinearLayout aliLayout,weixinLayout,yizhifuLayout,yhkLayout,ylLayout;
	private View view1,view2,view3,view4,view5;
	private TextView 	tvAliSumMoney,tvAliSumNum,tvWeixinSumMoney,tvWeixinSumNum,
						tvYizhifuSumMoney,tvYizhifuSumNum,tvYHKSumMoney,tvYHKSumNum,
						tvYLSumMoney,tvYLSumNum;
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.summary_activity);
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){
			initPrintService();
		}
		initView();
		initListener();
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
	 * 初始化控件
	 */
	private void initView(){
		layoutTitle = (LinearLayout) findViewById(R.id.title_layoutTitle);
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitleImg = (ImageView) findViewById(R.id.title_tvTitleImg);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvFunction = (TextView) findViewById(R.id.title_tvFunction);
		
		tvTitle.setText("选择时间");
		tvTitleImg.setVisibility(View.VISIBLE);
		tvFunction.setVisibility(View.VISIBLE);
		tvFunction.setText("打印");
		
		tvSumMoney = (TextView) findViewById(R.id.summary_tvSumMoney);
		tvSumNum = (TextView) findViewById(R.id.summary_tvSumNum);
		
		aliLayout = (LinearLayout) findViewById(R.id.summary_tvAliLayout);
		weixinLayout = (LinearLayout) findViewById(R.id.summary_tvWeixinLayout);
		yizhifuLayout = (LinearLayout) findViewById(R.id.summary_tvYizhifuLayout);
		yhkLayout = (LinearLayout) findViewById(R.id.summary_tvYHKLayout);
		ylLayout = (LinearLayout) findViewById(R.id.summary_tvUNIONPAYLayout);
		view1 = findViewById(R.id.summary_view1);
		view2 = findViewById(R.id.summary_view2);
		view3 = findViewById(R.id.summary_view3);
		view4 = findViewById(R.id.summary_view4);
		view5 = findViewById(R.id.summary_view5);
		tvAliSumMoney = (TextView) findViewById(R.id.summary_tvAliSumMoney);
		tvAliSumNum = (TextView) findViewById(R.id.summary_tvAliSumNum);
		tvWeixinSumMoney = (TextView) findViewById(R.id.summary_tvWeixinSumMoney);
		tvWeixinSumNum = (TextView) findViewById(R.id.summary_tvWeixinSumNum);
		tvYizhifuSumMoney = (TextView) findViewById(R.id.summary_tvYizhifuSumMoney);
		tvYizhifuSumNum = (TextView) findViewById(R.id.summary_tvYizhifuSumNum);
		tvYHKSumMoney = (TextView) findViewById(R.id.summary_tvYHKSumMoney);
		tvYHKSumNum = (TextView) findViewById(R.id.summary_tvYHKSumNum);
		tvYLSumMoney = (TextView) findViewById(R.id.summary_tvUNIONPAYSumMoney);
		tvYLSumNum = (TextView) findViewById(R.id.summary_tvUNIONPAYSumNum);
		
		String dataJson = "";
		updateView(dataJson);
	}
	
	
	/**
	 * 初始化控件
	 */
	private void initListener(){
		imgBack.setOnClickListener(this);
		layoutTitle.setOnClickListener(this);
		tvFunction.setOnClickListener(this);
	}
	
	/**
	 * 初始化控件
	 */
	private void initData(){
		
		try {
			loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
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
		
		//起始日期为一个月前
		startDateTime = DateTimeUtil.getAMonthDateStr(-1, "yyyy-MM-dd HH:mm");
		//初始化日期时间（即系统默认时间）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        endDateTime = sdf.format(new Date());
        endDate = endDateTime.split(" ")[0];
        seleteDate = endDate;
    	tvTitle.setText(endDate);
    	//查询汇总
    	querySummary(endDate);
	}
	
	/**
	 * 更新界面数据
	 */
	private void updateView(String dataJson){
		aliLayout.setVisibility(View.GONE);
		view1.setVisibility(View.GONE);
		weixinLayout.setVisibility(View.GONE);
		view2.setVisibility(View.GONE);
		yizhifuLayout.setVisibility(View.GONE);
		view3.setVisibility(View.GONE);
		yhkLayout.setVisibility(View.GONE);
		view4.setVisibility(View.GONE);
		ylLayout.setVisibility(View.GONE);
		view5.setVisibility(View.GONE);
		
		if(Utils.isNotEmpty(dataJson)){
			Gson gson = GsonUtils.getGson();
			java.lang.reflect.Type type = new TypeToken<SummaryResData>() {}.getType();
			summary = gson.fromJson(dataJson, type);

			Integer sumTotal = summary.getSumTotal();
			Double sumAmt = summary.getSumAmt();
			//总笔数
			if(sumTotal!=null){
				int sumTotal_int = sumTotal.intValue();
				tvSumNum.setText(String.valueOf(sumTotal_int));
			}
			//总金额
			if(sumAmt!=null){
				double sumAmt_dou = sumAmt.doubleValue();
				tvSumMoney.setText(String.valueOf(sumAmt_dou));
			}



			List<PayTypeBean> lsPayType = new ArrayList<PayTypeBean>();
			lsPayType = summary.getOrderSumList();
			//银行卡总金额  = 贷记卡总金额 + 借记卡总金额
			double sumMoney = 0;
			//银行卡总笔数 = 贷记卡总笔数 + 借记卡总笔数
			int sumNum = 0;
			for (int i = 0; i < lsPayType.size(); i++) {
				PayTypeBean payType = lsPayType.get(i);
				String payWayStr = payType.getPayWay();
				//支付宝
				if(payWayStr.equals("ALI")){
					aliLayout.setVisibility(View.VISIBLE);
					view1.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvAliSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvAliSumNum.setText(String.valueOf(total_int));
				}
				//微信
				if(payWayStr.equals("WX")){
					weixinLayout.setVisibility(View.VISIBLE);
					view2.setVisibility(View.VISIBLE);

					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvWeixinSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvWeixinSumNum.setText(String.valueOf(total_int));
				}
				//翼支付
				if(payWayStr.equals("BEST")){
					yizhifuLayout.setVisibility(View.VISIBLE);
					view3.setVisibility(View.VISIBLE);

					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvYizhifuSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvYizhifuSumNum.setText(String.valueOf(total_int));
				}
				//银行卡：DEBIT= 借记卡
				if(payWayStr.equals("DEBIT")){
					yhkLayout.setVisibility(View.VISIBLE);
					view4.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Log.e("借记卡金额1",amount+"");
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					Log.e("借记卡金额2",amount_dou+"");
					Log.e("总金额1",sumMoney+"");
					sumMoney = sumMoney + amount_dou;
					Log.e("总金额2",sumMoney+"");
					int total_int = total.intValue();
					sumNum = sumNum + total_int;
					Log.e("总笔数",sumNum+"");
					tvYHKSumMoney.setText(DecimalUtil.doubletoString(sumMoney));
					tvYHKSumNum.setText(String.valueOf(sumNum));
				}
				//银行卡：    CREDIT=贷记卡
				if(payWayStr.equals("CREDIT")){
					yhkLayout.setVisibility(View.VISIBLE);
					view4.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Log.e("贷记卡金额1",amount+"");
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					Log.e("贷记卡金额2",amount_dou+"");
					Log.e("总金额1",sumMoney+"");
					sumMoney = sumMoney + amount_dou;
					Log.e("总金额2",sumMoney+"");
					int total_int = total.intValue();
					sumNum = sumNum + total_int;
					Log.e("总笔数",sumNum+"");
					tvYHKSumMoney.setText(DecimalUtil.doubletoString(sumMoney));
					tvYHKSumNum.setText(String.valueOf(sumNum));
				}
				//UNIONPAY = 银联二维码
				if(payWayStr.equals("UNIONPAY")){
					ylLayout.setVisibility(View.VISIBLE);
					view5.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvYLSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvYLSumNum.setText(String.valueOf(total_int));
				}

			}
		}

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
	 * 查询汇总
	 */
	private void querySummary(final String dateStr){
		final String url;
		if(dateStr.equals(endDate)){
			Log.e("查询日期：", "当天");
			url = NitConfig.querySummaryUrl;
		}else{
			Log.e("查询日期：", "历史");
			url = NitConfig.queryHistorySummaryUrl;
		}
		hintDialog= CustomDialog.CreateDialog(getContext(), "    查询中...");
		hintDialog.show();
		hintDialog.setCanceledOnTouchOutside(false);
		
		new Thread(){
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("eid",loginInitData.getEid());    
					userJSON.put("mid",loginInitData.getMid());     
					userJSON.put("startTime",SummaryDateUtil.getStartTimeStampTo(dateStr));    
					userJSON.put("endTime",SummaryDateUtil.getEndTimeStampTo(dateStr));    

					String content = String.valueOf(userJSON);
					Log.e("交易明细发起请求参数：", content);
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
                   Log.e("交易明细返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("交易明细返回JSON值：", jsonStr);
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
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				try {
					String jsonStr=(String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					String status = job.getString("status");
					String message = job.getString("message");
					String dataJson = job.getString("data");
					if(status.equals("200")){
						updateView(dataJson);
					}else{
						Toast.makeText(SummaryActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
				if(hintDialog!=null&&hintDialog.isShowing()){
					hintDialog.dismiss();
				}
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				break;
			}
		};
	};
	
	/**
	 *  显示日期控件
	 */
	private void setQueryDateText(){
//		datePicker = new CustomDatePicker(this, "请选择日期", new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) {
//            	seleteDate = time.split(" ")[0];
//            	tvTitle.setText(seleteDate);
//            	querySummary(seleteDate);
//
//            }
//        }, startDateTime, endDateTime);
//        datePicker.showSpecificTime(1); //不显示时和分为false
//        datePicker.setIsLoop(false);
//        datePicker.setDayIsLoop(true);
//        datePicker.setMonIsLoop(true);
//
//        datePicker.show(endDateTime);

		TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
			@Override
			public void handle(String time) {
//                        Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).0();
				seleteDate = time.split(" ")[0];
            	tvTitle.setText(seleteDate);
            	querySummary(seleteDate);
			}
		},startDateTime,endDateTime);
//		timeSelector.setMode(TimeSelector.MODE.YMDHM);//显示 年月日时分（默认）；
                timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日
		timeSelector.setIsLoop(false);//不设置时为true，即循环显示
		timeSelector.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.title_layoutTitle:
			setQueryDateText();
			break;
		case R.id.title_tvFunction:
			if(summary!=null){
				if(summary.getOrderSumList().size()>0){
					if(posProvider.equals(NEW_LAND)){
						//初始化打印机
						getPrinter();
						//打印
						NewlandPrintUtil.SummaryPrintText(getContext(), aidlPrinter, summary,loginInitData,seleteDate);
					}else if(posProvider.equals(FUYOU_SF)){
						FuyouPrintUtil.SummaryPrintText(getContext(), printService, summary,loginInitData,seleteDate);
					}

				}else{
					Toast.makeText(SummaryActivity.this, "暂无汇总信息！", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(SummaryActivity.this, "暂无汇总信息！", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}
}
