package com.wanding.xingpos.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.ShiftRecordResData;
import com.wanding.xingpos.bean.ShiftResData;
import com.wanding.xingpos.bean.SubReocrdSummaryResData;
import com.wanding.xingpos.bean.SubTimeSummaryResData;
import com.wanding.xingpos.bean.SubTotalSummaryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;

/**
 * 结算、交接班记录详情Activity
 */
public class ShiftRecordDetailActivity extends BaseActivity implements OnClickListener{
	private ImageView imgBack;
	private TextView tvTitle;
	
	private TextView tvSumMoney,tvDateTime;
	
	private LinearLayout aliLayout,wxLayout,yzfLayout,bankLayout,ylLayout;
	private TextView tvAliPaySumNum,tvAliPaySumMoney,tvAliRefundSumNum,tvAliRefundSumMoney;
	private TextView tvWXPaySumNum,tvWXPaySumMoney,tvWXRefundSumNum,tvWXRefundSumMoney;
	private TextView tvYZFPaySumNum,tvYZFPaySumMoney,tvYZFRefundSumNum,tvYZFRefundSumMoney;
	private TextView tvBankPaySumNum,tvBankPaySumMoney,tvBankRefundSumNum,tvBankRefundSumMoney;
	private TextView tvYLPaySumNum,tvYLPaySumMoney,tvYLRefundSumNum,tvYLRefundSumMoney;
	
	private TextView tvPrint;
	
	private UserLoginResData loginInitData;
	private ShiftRecordResData record;
	private ShiftResData summary;
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
    private String TAG = "lyc";
    
    private SharedPreferencesUtil sharedPreferencesUtil;

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
	
    private String staffName;

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
		setContentView(R.layout.shift_record_detail_activity);
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

		
		SummaryDetail();
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
	
	private void initData(){
		
		Intent in = getIntent();
		record = (ShiftRecordResData) in.getSerializableExtra("record");
		String staffNameStr = record.getReserve1();
		if(Utils.isNotEmpty(staffNameStr)){
			staffName = staffNameStr;
		}else{
			staffName = "";
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
	

	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvTitle.setText("交接班");
		
		tvSumMoney = (TextView) findViewById(R.id.settlement_content_tvSumMoney);
		tvDateTime = (TextView) findViewById(R.id.settlement_content_tvDateTime);
		
		aliLayout = (LinearLayout) findViewById(R.id.settlment_content_aliLayout);
		tvAliPaySumNum = (TextView) findViewById(R.id.settlment_content_alipaySumNum);
		tvAliPaySumMoney = (TextView) findViewById(R.id.settlment_content_alipaySumMoney);
		tvAliRefundSumNum = (TextView) findViewById(R.id.settlment_content_alirefundSumNum);
		tvAliRefundSumMoney = (TextView) findViewById(R.id.settlment_content_alirefundSumMoney);
		
		wxLayout = (LinearLayout) findViewById(R.id.settlment_content_wxLayout);
		tvWXPaySumNum = (TextView) findViewById(R.id.settlment_content_wxpaySumNum);
		tvWXPaySumMoney = (TextView) findViewById(R.id.settlment_content_wxpaySumMoney);
		tvWXRefundSumNum = (TextView) findViewById(R.id.settlment_content_wxrefundSumNum);
		tvWXRefundSumMoney = (TextView) findViewById(R.id.settlment_content_wxrefundSumMoney);
		
		yzfLayout = (LinearLayout) findViewById(R.id.settlment_content_yzfLayout);
		tvYZFPaySumNum = (TextView) findViewById(R.id.settlment_content_yzfpaySumNum);
		tvYZFPaySumMoney = (TextView) findViewById(R.id.settlment_content_yzfpaySumMoney);
		tvYZFRefundSumNum = (TextView) findViewById(R.id.settlment_content_yzfrefundSumNum);
		tvYZFRefundSumMoney = (TextView) findViewById(R.id.settlment_content_yzfrefundSumMoney);
		
		bankLayout = (LinearLayout) findViewById(R.id.settlment_content_bankLayout);
		tvBankPaySumNum = (TextView) findViewById(R.id.settlment_content_bankpaySumNum);
		tvBankPaySumMoney = (TextView) findViewById(R.id.settlment_content_bankpaySumMoney);
		tvBankRefundSumNum = (TextView) findViewById(R.id.settlment_content_bankrefundSumNum);
		tvBankRefundSumMoney = (TextView) findViewById(R.id.settlment_content_bankrefundSumMoney);
		
		ylLayout = (LinearLayout) findViewById(R.id.settlment_content_ylLayout);
		tvYLPaySumNum = (TextView) findViewById(R.id.settlment_content_ylpaySumNum);
		tvYLPaySumMoney = (TextView) findViewById(R.id.settlment_content_ylpaySumMoney);
		tvYLRefundSumNum = (TextView) findViewById(R.id.settlment_content_ylrefundSumNum);
		tvYLRefundSumMoney = (TextView) findViewById(R.id.settlment_content_ylrefundSumMoney);
		
		tvPrint = (TextView) findViewById(R.id.shift_record_detail_tvPrint);
		
		updateView();
	}
	
	private void initListener() {
		imgBack.setOnClickListener(this);
		tvPrint.setOnClickListener(this);
	}
	
	/**
	 * 更新界面数据
	 */
	private void updateView(){
		aliLayout.setVisibility(View.GONE);
		wxLayout.setVisibility(View.GONE);
		yzfLayout.setVisibility(View.GONE);
		bankLayout.setVisibility(View.GONE);
		ylLayout.setVisibility(View.GONE);
		if(summary != null){
			//结算总金额
	        ArrayList<SubTotalSummaryResData> totalList = summary.getTotalList();
	        SubTotalSummaryResData total = null;
	        for (int i = 0; i < totalList.size(); i++) {
	        	total = totalList.get(i);
			}
	        tvSumMoney.setText("￥"+total.getMoney());
			//结算时间周期
	        ArrayList<SubTimeSummaryResData> timeList = summary.getTimeList();
	        SubTimeSummaryResData subStartTime = null;
	        SubTimeSummaryResData subEndTime = null;
	        for (int i = 0; i < timeList.size(); i++) {
	        	subStartTime = timeList.get(0);
	        	subEndTime = timeList.get(1);
			}
	        String startTimeStr = "";
	        String endTimeStr = "";
	        startTimeStr = subStartTime.getType();
	        endTimeStr = subEndTime.getType();
	        if(startTimeStr.contains(".")){
	        	startTimeStr = startTimeStr.substring(0,startTimeStr.indexOf("."));
	        }
	        if(endTimeStr.contains(".")){
	        	endTimeStr = endTimeStr.substring(0,endTimeStr.indexOf("."));
	        }
	        tvDateTime.setText(startTimeStr+"至"+endTimeStr);
	        //交易明细
	        ArrayList<SubReocrdSummaryResData> reocrdList = summary.getReocrdList();
	        for (int i = 0; i < reocrdList.size(); i++) {
	        	SubReocrdSummaryResData reocrd = reocrdList.get(i);
	        	String mode = reocrd.getMode();
	        	if(mode.equals("WX")){
	        		wxLayout.setVisibility(View.VISIBLE);
	        		String type = reocrd.getType();
	        		if(type.equals("noRefund")){
	        			tvWXPaySumNum.setText(reocrd.getTotalCount());
	        			tvWXPaySumMoney.setText(reocrd.getMoney());
	        		}else if(type.equals("refund")){
	        			tvWXRefundSumNum.setText(reocrd.getTotalCount());
	        			tvWXRefundSumMoney.setText(reocrd.getMoney());
	        		}
	        	}else if(mode.equals("ALI")){
	        		aliLayout.setVisibility(View.VISIBLE);
	        		String type = reocrd.getType();
	        		if(type.equals("noRefund")){
	        			tvAliPaySumNum.setText(reocrd.getTotalCount());
	        			tvAliPaySumMoney.setText(reocrd.getMoney());
	        		}else if(type.equals("refund")){
	        			tvAliRefundSumNum.setText(reocrd.getTotalCount());
	        			tvAliRefundSumMoney.setText(reocrd.getMoney());
	        		}
	        	}else if(mode.equals("BEST")){
	        		yzfLayout.setVisibility(View.VISIBLE);
	        		String type = reocrd.getType();
	        		if(type.equals("noRefund")){
	        			tvYZFPaySumNum.setText(reocrd.getTotalCount());
	        			tvYZFPaySumMoney.setText(reocrd.getMoney());
	        		}else if(type.equals("refund")){
	        			tvYZFRefundSumNum.setText(reocrd.getTotalCount());
	        			tvYZFRefundSumMoney.setText(reocrd.getMoney());
	        		}
	        	}else if(mode.equals("BANK")){
	        		bankLayout.setVisibility(View.VISIBLE);
	        		String type = reocrd.getType();
	        		if(type.equals("noRefund")){
	        			tvBankPaySumNum.setText(reocrd.getTotalCount());
	        			tvBankPaySumMoney.setText(reocrd.getMoney());
	        		}else if(type.equals("refund")){
	        			tvBankRefundSumNum.setText(reocrd.getTotalCount());
	        			tvBankRefundSumMoney.setText(reocrd.getMoney());
	        		}
	        	}else if(mode.equals("UNIONPAY")){
	        		ylLayout.setVisibility(View.VISIBLE);
	        		String type = reocrd.getType();
	        		if(type.equals("noRefund")){
	        			tvYLPaySumNum.setText(reocrd.getTotalCount());
	        			tvYLPaySumMoney.setText(reocrd.getMoney());
	        		}else if(type.equals("refund")){
	        			tvYLRefundSumNum.setText(reocrd.getTotalCount());
	        			tvYLRefundSumMoney.setText(reocrd.getMoney());
	        		}
	        	}
			}
		}
	}

	
	/** 结算请求  */
	private void SummaryDetail(){
		final String url = NitConfig.settlementRecordDetailUrl;
		new Thread(){
			public void run() {
				try {
					JSONObject userJSON = new JSONObject(); 
					//mid,eid
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("eid",loginInitData.getEid()); 
					userJSON.put("worId",record.getId()); 

					String content = String.valueOf(userJSON);
					Log.e("交接班记录详情发起请求参数：", content); 
					  
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
                   Log.e("交接班记录详情返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("交接班记录详情返回JSON值：", jsonStr);
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
				String jsonStr;
				try {
					jsonStr = (String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					if(job.getBoolean("isSuccess")){
						summaryJson(jsonStr);
					}else{
						Toast.makeText(getContext(), "查询失败！", Toast.LENGTH_LONG).show();
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
	
	/** 结算请求返回JSON数据处理 */
	private void summaryJson(String jsonStr){
		try {
			JSONObject job = new JSONObject(jsonStr);
			summary = new ShiftResData();
			JSONObject obj = job.getJSONObject("data");
			JSONArray timeArray = obj.getJSONArray("time");
			ArrayList<SubTimeSummaryResData> timeList = new ArrayList<SubTimeSummaryResData>();
			for (int i = 0; i < timeArray.length(); i++) {
				JSONObject time_obj = timeArray.optJSONObject(i);
				SubTimeSummaryResData time = new SubTimeSummaryResData();
				
				String time_typeStr = time_obj.getString("type");
				Log.e("time_typeStr:", time_typeStr);
				if(time_typeStr==null||time_typeStr.equals("null")){
					time.setType("");
				}else{
					time.setType(time_typeStr);
				}
				Log.e("timeType:", time.getType());
				
				String time_totalCountStr = time_obj.getString("totalCount");
				Log.e("time_totalCountStr:", time_totalCountStr);
				if(time_totalCountStr==null||time_totalCountStr.equals("null")){
					time.setTotalCount("");
				}else{
					time.setTotalCount(time_totalCountStr);
					
				}
				Log.e("timeTotalCount:", time.getTotalCount());
				
				String time_moneyStr = time_obj.getString("money");
				if(time_moneyStr==null||time_moneyStr.equals("null")){
					time.setMoney("");
				}else{
					
					time.setMoney(time_moneyStr);
				}
				Log.e("timeMoney:", time.getMoney());
				
				String time_modeStr = time_obj.getString("mode");
				if(time_modeStr==null||time_modeStr.equals("null")){
					
					time.setMode("");
					
				}else{

					time.setMode(time_modeStr);
				}
				Log.e("timeMode:", time.getMode());
				timeList.add(time);
			}
			summary.setTimeList(timeList);
			
			JSONArray reocrdArray = obj.getJSONArray("reocrd");
			ArrayList<SubReocrdSummaryResData> reocrdList = new ArrayList<SubReocrdSummaryResData>();
			for (int i = 0; i < reocrdArray.length(); i++) {
				JSONObject reocrd_obj = reocrdArray.optJSONObject(i);
				SubReocrdSummaryResData reocrd = new SubReocrdSummaryResData();
				
				String reocrd_typeStr = reocrd_obj.getString("type");
				if(reocrd_typeStr==null||reocrd_typeStr.equals("null")){
					
					reocrd.setType("");
					
				}else{
					reocrd.setType(reocrd_typeStr);
				}
				
				String reocrd_totalCountStr = reocrd_obj.getString("totalCount");
				if(reocrd_totalCountStr==null||reocrd_totalCountStr.equals("null")){
					reocrd.setTotalCount("0");
				}else{
					reocrd.setTotalCount(reocrd_totalCountStr);
					
				}
				
				String reocrd_moneyStr = reocrd_obj.getString("money");
				if(reocrd_moneyStr ==null||reocrd_moneyStr.equals("null")){
					reocrd.setMoney("0.00");
				}else{
					
					reocrd.setMoney(reocrd_moneyStr);
				}
				
				String reocrd_modeStr = reocrd_obj.getString("mode");
				if(reocrd_modeStr==null||reocrd_modeStr.equals("null")){
					reocrd.setMode("");
				}else{
					reocrd.setMode(reocrd_modeStr);
				}
				reocrdList.add(reocrd);
			}
			summary.setReocrdList(reocrdList);
			
			JSONArray totalArray = obj.getJSONArray("total");
			ArrayList<SubTotalSummaryResData> totalList = new ArrayList<SubTotalSummaryResData>();
			for (int i = 0; i < totalArray.length(); i++) {
				JSONObject total_obj = totalArray.optJSONObject(i);
				SubTotalSummaryResData total = new SubTotalSummaryResData();
				
				
				String total_typeStr = total_obj.getString("type");
				if(total_typeStr==null||total_typeStr.equals("null")){
					total.setType("");
				}else{
					
					total.setType(total_typeStr);
				}
				
				String total_totalCountStr = total_obj.getString("totalCount");
				if(total_totalCountStr==null||total_totalCountStr.equals("null")){
					total.setTotalCount("0");
				}else{
					
					total.setTotalCount(total_totalCountStr);
				}
				
				String total_moneyStr = total_obj.getString("money");
				if(total_moneyStr==null||total_moneyStr.equals("null")){
					total.setMoney("0.00");
				}else{
					
					total.setMoney(total_moneyStr);
				}
				
				String total_modeStr = total_obj.getString("mode");
				if(total_modeStr==null||total_modeStr.equals("null")){
					total.setMode("total");
				}else{
					
					total.setMode(total_modeStr);
				}
				totalList.add(total);
			}
			summary.setTotalList(totalList);
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateView();
		
	}
	
	
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.shift_record_detail_tvPrint://打印		
			if(posProvider.equals(NEW_LAND)){
				//初始化打印机
				getPrinter();
				//打印
				NewlandPrintUtil.SettlementPrintText(this, aidlPrinter, summary,loginInitData,staffName);
			}else if(posProvider.equals(FUYOU_SF)){
				//打印
				FuyouPrintUtil.SettlementPrintText(this,printService,summary,loginInitData,staffName);
			}
			break;
		}
	}
	
}
