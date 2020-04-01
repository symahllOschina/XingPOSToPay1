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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pull.refresh.view.XListView;
import com.pull.refresh.view.XListView.IXListViewListener;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.OrderListAdapte;
import com.wanding.xingpos.adapter.ShiftRecordAdapte;
import com.wanding.xingpos.adapter.ShiftRecordAdapte.OnSetRecordDetail;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.OrderListData;
import com.wanding.xingpos.bean.ShiftRecordListResData;
import com.wanding.xingpos.bean.ShiftRecordResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date.picker.CustomDatePicker;
import com.wanding.xingpos.date.picker.TimeSelector;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.summary.util.SettlementDateUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;

/**
 * 结算、交接班Activity
 */
public class ShiftRecordActivity extends BaseActivity implements OnClickListener,OnSetRecordDetail,IXListViewListener{
	private ImageView imgBack;
	private TextView tvTitle;
	
	private TextView tvStartDateTime,tvEndDateTime;
	private TextView tvOk;
	private XListView mListView;
	
	private CustomDatePicker datePicker;
	private String pickerStartDateTime,pickerEndDateTime,pickerSeleteDateTime;//日期选择控件的选择范围，起始日期和结束日期,以及选择的日期时间
	private String startDateTimeStr,endDateTimeStr,dateTimeStr,dateStr,timeStr;//
	
	private UserLoginResData loginInitData;
	
	private List<ShiftRecordResData> lsRecord = new ArrayList<ShiftRecordResData>();
	private ShiftRecordAdapte mAdapter;
	
	private String startTime = "";
	private String endTime = "";
	
	private int pageNo = 1;//默认加载第一页
	private static final int pageSize = 20;//默认一页加载xx条数据（死值不变）
	
	private static final int REFRESH = 100;  
    private static final int LOADMORE = 200;  
    private static final int NOLOADMORE = 300;  
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，
    //交易总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shift_record_activity);
		
		initView();
		initListener();
		initData();
		
		//查询交接班记录参数设置
		setQueryRecordParams();
	}
	
	private void initData(){
		try {
			loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//起始日期为一个月前
		pickerStartDateTime = DateTimeUtil.getAMonthDateStr(-1, "yyyy-MM-dd HH:mm");
		//初始化日期时间（即系统默认时间）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        dateTimeStr = sdf.format(new Date());
        pickerEndDateTime = dateTimeStr;
        pickerSeleteDateTime = dateTimeStr;
        dateStr = dateTimeStr.split(" ")[0];
        timeStr = dateTimeStr.split(" ")[1];
        //初始化显示的开始时间，结束时间
        String startTimeStr = "00:00";
        startDateTimeStr = dateStr + " "+startTimeStr;
        endDateTimeStr = dateTimeStr;
        
        tvStartDateTime.setText(startDateTimeStr);
        tvEndDateTime.setText(endDateTimeStr);
	}

	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvTitle.setText("交接班");
		
		tvStartDateTime = (TextView) findViewById(R.id.select_datetime_tvStartTime);
		tvEndDateTime = (TextView) findViewById(R.id.select_datetime_tvEndTime);
		tvOk = (TextView) findViewById(R.id.select_datetime_tvOk);
		mListView = (XListView) findViewById(R.id.settlement_record_listView);
		
		
		
	}
	
	private void initListener() {
		imgBack.setOnClickListener(this);
		tvStartDateTime.setOnClickListener(this);
		tvEndDateTime.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		
		mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
		mListView.setPullRefreshEnable(false);//是否可以下拉刷新,默认可以下拉
		mListView.setXListViewListener(this);//注册刷新和加载更多接口
	}
	
	private void setQueryRecordParams(){
		String startTimeStr = tvStartDateTime.getText().toString();
		String endTimeStr = tvEndDateTime.getText().toString();
		Log.e("起始时间：", startTimeStr);
		Log.e("结束时间：", endTimeStr);
		startTime = SettlementDateUtil.getStartTimeStampTo(startTimeStr);
		endTime = SettlementDateUtil.getEndTimeStampTo(endTimeStr);
		if(Long.parseLong(startTime)<=Long.parseLong(endTime)){
			
			queryRecord(startTime,endTime,pageNo,pageSize);
		}else{
			Toast.makeText(ShiftRecordActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	/**
	 *  查询交接班记录
	 */
	private void queryRecord(final String startTime,final String endTime,final int pageNo,final int pageSize){
		final String url = NitConfig.settlementRecordUrl;
		new Thread(){
			public void run() {
				try {
					JSONObject userJSON = new JSONObject(); 
					//mid,eid
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("eid",loginInitData.getEid()); 
					userJSON.put("startTime",startTime); 
					userJSON.put("endTime",endTime); 
					userJSON.put("pageNo",pageNo+""); 
					userJSON.put("pageSize",pageSize+""); 

					String content = String.valueOf(userJSON);
					Log.e("结算记录发起请求参数：", content); 
					  
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
                   Log.e("结算记录返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("结算记录返回JSON值：", jsonStr);
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
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			case REFRESH:
				mAdapter.notifyDataSetChanged();
                //更新完毕
				onLoad();
				break;
			case LOADMORE:
				mListView.setPullLoadEnable(true);//是否可以上拉加载更多
				mAdapter.notifyDataSetChanged();
                // 加载更多完成
				onLoad();
				break;
			case NOLOADMORE:
				mListView.setPullLoadEnable(false);//是否可以上拉加载更多
				// 加载更多完成-->>已没有更多
				onLoad();
				break;
			case 1:
				String jsonStr;
				try {
					jsonStr = (String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					if(job.getBoolean("isSuccess")){
						String dataJson = job.getString("data");
						settlementRecordJson(dataJson);
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
	
	/**
	 * 交班记录成功返回
	 */
	private void settlementRecordJson(String dataJson){
		
		Gson gjson  =  GsonUtils.getGson();
		java.lang.reflect.Type type = new TypeToken<ShiftRecordListResData>() {}.getType();  
		ShiftRecordListResData order = gjson.fromJson(dataJson, type);
		//获取总条数
		orderListTotalCount = order.getTotalCount();
		Log.e("总条数：", orderListTotalCount+"");
		List<ShiftRecordResData> orderList = new ArrayList<ShiftRecordResData>();
		//获取的list
		orderList = order.getOrderList();
		getMoerNum = orderList.size();
		Log.e("每次获取条数：", getMoerNum+"");
		if(pageNo == 1){
			lsRecord.clear();
		}
		lsRecord.addAll(orderList); 
		Log.e("查询数据：", lsRecord.size()+""+"条");
		//关闭上拉或下拉View，刷新Adapter
		if(loadMore.equals("0")){
			mAdapter = new ShiftRecordAdapte(this, lsRecord);
			mAdapter.ItemOnSetRecordDetail(this);
			mListView.setAdapter(mAdapter);
			if(lsRecord.size()<=orderListTotalCount&&getMoerNum==pageSize){
				Message msg1 = new Message();  
                msg1.what = LOADMORE;  
                mHandler.sendEmptyMessageDelayed(LOADMORE, 0);  
			}else{
				Message msg1 = new Message();  
                msg1.what = NOLOADMORE;  
                mHandler.sendEmptyMessageDelayed(NOLOADMORE, 0); 
			}
		}else if(loadMore.equals("1")){
			Message msg1 = new Message();  
            msg1.what = REFRESH;  
            mHandler.sendEmptyMessageDelayed(REFRESH, 2000); 
		}else if(loadMore.equals("2")){
			Message msg1 = new Message();  
            msg1.what = LOADMORE;  
            mHandler.sendEmptyMessageDelayed(LOADMORE, 2000);  
		}
		
		
	}

	/**
	 *  显示日期控件
	 */
	private void setQueryDateText(final TextView tvText){
//		datePicker = new CustomDatePicker(this, "请选择日期", new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) {
//            	pickerSeleteDateTime = time;
//            	tvText.setText(pickerSeleteDateTime);
//            }
//        }, pickerStartDateTime, pickerEndDateTime);
//        datePicker.showSpecificTime(2); //不显示时和分为false
//        datePicker.setIsLoop(false);
//        datePicker.setDayIsLoop(true);
//        datePicker.setMonIsLoop(true);
//
//        datePicker.show(pickerEndDateTime);

		TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
			@Override
			public void handle(String time) {
//                        Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
				tvText.setText(time);
			}
		},pickerStartDateTime,pickerEndDateTime);
		timeSelector.setMode(TimeSelector.MODE.YMDHM);//显示 年月日时分（默认）；
//                timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日
		timeSelector.setIsLoop(false);//不设置时为true，即循环显示
		timeSelector.show();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.select_datetime_tvStartTime://开始时间
			setQueryDateText(tvStartDateTime);
			break;
		case R.id.select_datetime_tvEndTime://结束时间
			setQueryDateText(tvEndDateTime);
			break;
		case R.id.select_datetime_tvOk://确定
			//查询交接班记录参数设置
			pageNo = 1;
			setQueryRecordParams();
			break;
		}
	}

	/**  Adapter的Item暴露的查看按钮实现接口  */
	@Override
	public void getDetail(ShiftRecordResData record) {
		Intent in = new Intent();
		in.setClass(this, ShiftRecordDetailActivity.class);
		in.putExtra("record", record);
		startActivity(in);
	}
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(new Date().toLocaleString());
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		loadMore = "2";
		if(lsRecord.size()<=orderListTotalCount&&getMoerNum==pageSize){
        	//已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
        	if(Long.parseLong(startTime)<=Long.parseLong(endTime)){
        		pageNo = pageNo + 1;
    			queryRecord(startTime,endTime,pageNo,pageSize);
    		}else{
    			Toast.makeText(ShiftRecordActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_LONG).show();
    		}
        }else{
        	//没有数据执行两秒关闭view
        	mHandler.postDelayed(new Runnable() {  
                @Override  
                public void run() {  
                	Message msg = new Message();  
                    msg.what = NOLOADMORE;  
                    mHandler.sendMessage(msg);  
                }  
            }, 1000); 
        	
        }
	}
	
}
