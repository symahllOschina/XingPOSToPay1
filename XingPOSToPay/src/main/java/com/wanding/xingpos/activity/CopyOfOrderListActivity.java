package com.wanding.xingpos.activity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pull.refresh.view.XListView;
import com.pull.refresh.view.XListView.IXListViewListener;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.OrderListAdapte;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.OrderListData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;

/** 交易明细列表界面 */
public class CopyOfOrderListActivity extends BaseActivity implements OnClickListener,OnItemClickListener,IXListViewListener{
	
	private Context context = CopyOfOrderListActivity.this;
	
	private ImageView imgBack,tvTitleImg;
	private TextView tvTitle,tvTitleFunction;
	private LinearLayout layoutTitle;
    private XListView mListView;
	
	private UserLoginResData loginInitData;
	
	private int pageNum = 1;//默认加载第一页
	private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
	private String date_typeStr = "1";//（"1"=当日交易）（"2"=本月交易不含今天）
	
	private static final int REFRESH = 100;  
    private static final int LOADMORE = 200;  
    private static final int NOLOADMORE = 300;  
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，
    

    private PopupWindow popupwindow; 

    //交易列表
    private List<OrderDetailData> lsOrder = new ArrayList<OrderDetailData>();
    //交易总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private OrderListAdapte mAdapter;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider =  MainActivity.posProvider;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.copyoforder_list_activity);
		initView();
		initData();
		getOrderList(pageNum,pageNumCount);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//加载数据
//		if(pageNum==1){
//			getOrderList(pageNumCount);
//		}else{
//			lsOrder.clear();
//			pageCount = pageNum*pageNumCount;
//        	getOrderList(pageCount);
//		}
		
	}
	
	/** 初始化数据 */
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
		
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitleImg = (ImageView) findViewById(R.id.title_tvTitleImg);
		tvTitleImg.setVisibility(View.VISIBLE);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvTitleFunction = (TextView) findViewById(R.id.title_tvFunction);
		tvTitleFunction.setVisibility(View.VISIBLE);
		layoutTitle = (LinearLayout) findViewById(R.id.title_layoutTitle);
		mListView = (XListView) findViewById(R.id.mXListView);
		
		tvTitle.setText("交易明细（当天）");
		tvTitleFunction.setText("汇总");
		imgBack.setOnClickListener(this);
		layoutTitle.setOnClickListener(this);
		tvTitleFunction.setOnClickListener(this);
		
		mListView.setOnItemClickListener(this);
		mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
		mListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉
		mListView.setXListViewListener(this);//注册刷新和加载更多接口
		
	
     
	}

	/** 
	 * 获取交易明细  
	 * 入参：mid，eid，date_type（"1"=当日交易）（"2"=本月交易不含今天）
	 **/
	private void getOrderList(final int pageNum,final int pageCount){
		final String url;
		if(date_typeStr.equals("1")||date_typeStr.equals("3")){
			url = NitConfig.queryOrderDayListUrl;
		}else{
			url = NitConfig.queryOrderMonListUrl;
		}
		Log.e(TAG,"请求地址："+url);
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("pageNum",pageNum+"");   
					userJSON.put("numPerPage",pageCount+"");   
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("eid",loginInitData.getEid());
					userJSON.put("date_type",date_typeStr);    

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
				try {
					String jsonStr=(String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					if(job.getString("status").equals("200")){
						String dataJson = job.getString("data");
						Gson gjson  =  GsonUtils.getGson();
						java.lang.reflect.Type type = new TypeToken<OrderListData>() {}.getType();  
						OrderListData order = gjson.fromJson(dataJson, type);
						//获取总条数
						orderListTotalCount = order.getTotalCount();
						Log.e("总条数：", orderListTotalCount+"");
						List<OrderDetailData> orderList = new ArrayList<OrderDetailData>();
						//获取的list
						orderList = order.getOrderList();
						getMoerNum = orderList.size();
						if(pageNum == 1){
							lsOrder.clear();
						}
						lsOrder.addAll(orderList);
						Log.e("查询数据：", lsOrder.size()+""+"条");
				        //关闭上拉或下拉View，刷新Adapter
						if(loadMore.equals("0")){
							mAdapter = new OrderListAdapte(context, lsOrder,posProvider);
							mListView.setAdapter(mAdapter);
							if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
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
					}else{
						Toast.makeText(context, "查询失败！", Toast.LENGTH_LONG).show();
					}
					
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				loadMore = "0";
				break;
			case 201:
				loadMore = "0";
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				loadMore = "0";
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				loadMore = "0";
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				break;
			}
		};
	};
	
	/**
	 * 创建PopupWindow
	 */
	private void initmPopupWindowView(){
		View view = getLayoutInflater().inflate(R.layout.order_list_top_popupwindow, null,false);
		// 创建PopupWindow实例,200,150分别是宽度和高度 
		popupwindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的] 
//	    popupwindow.setAnimationStyle(R.style.AnimationFade); 
	    // 自定义view添加触摸事件 
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(popupwindow != null && popupwindow.isShowing()) { 
			         popupwindow.dismiss(); 
			         popupwindow = null; 
			    } 
				return false;
			}
		});
	    /** 在这里可以实现自定义视图的功能 */
	    TextView tvMonth = (TextView) view.findViewById(R.id.order_list_topPup_month); 
	    TextView tvYesterday = (TextView) view.findViewById(R.id.order_list_topPup_yesterday);
	    TextView tvDay = (TextView) view.findViewById(R.id.order_list_topPup_day);
	    tvMonth.setOnClickListener(this);
		tvYesterday.setOnClickListener(this);
	    tvDay.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.title_layoutTitle:
			if (popupwindow != null&&popupwindow.isShowing()) { 
				tvTitleImg.setImageDrawable(getResources().getDrawable(R.drawable.open_popupwindow_icon));
		        popupwindow.dismiss(); 
		        return; 
		      } else { 
		        initmPopupWindowView(); 
		        popupwindow.showAsDropDown(v, 0, 5); 
		        tvTitleImg.setImageDrawable(getResources().getDrawable(R.drawable.cloes_popupwindow_icon));
		      } 
			break;
		case R.id.order_list_topPup_month:
			//当前显示的不是本月的数据时才去查询本月数据
			if(!date_typeStr.equals("2")){
				tvTitle.setText("交易明细（本月）");
				pageNum = 1;
				date_typeStr = "2";
				getOrderList(pageNum,pageNumCount);
			}
			tvTitleImg.setImageDrawable(getResources().getDrawable(R.drawable.open_popupwindow_icon));
			if(popupwindow != null && popupwindow.isShowing()) { 
		         popupwindow.dismiss(); 
		         popupwindow = null; 
		    } 
			
//			Toast.makeText(context, "选择当月", Toast.LENGTH_LONG).show();
			break;
			case R.id.order_list_topPup_yesterday:
				if(!date_typeStr.equals("3")){
					tvTitle.setText("交易明细（昨天）");
					pageNum = 1;
					date_typeStr = "3";
					getOrderList(pageNum,pageNumCount);
				}
				tvTitleImg.setImageDrawable(getResources().getDrawable(R.drawable.open_popupwindow_icon));
				if(popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}
				break;
		case R.id.order_list_topPup_day:
			//当前显示的不是当天的数据时才去查询当天数据
			if(!date_typeStr.equals("1")){
				tvTitle.setText("交易明细（当天）");
				pageNum = 1;
				date_typeStr = "1";
				getOrderList(pageNum,pageNumCount);
			}
			tvTitleImg.setImageDrawable(getResources().getDrawable(R.drawable.open_popupwindow_icon));
			if(popupwindow != null && popupwindow.isShowing()) { 
		         popupwindow.dismiss(); 
		         popupwindow = null; 
		    } 
//			Toast.makeText(context, "选择当天", Toast.LENGTH_LONG).show();
			break;
		case R.id.title_tvFunction:
			in = new Intent();
			in.setClass(CopyOfOrderListActivity.this, SummaryActivity.class);
			startActivity(in);
			break;
		
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		//这里因为添加了头部，所以数据Item的索引值发生变化，即对应item应为：position-1；
		OrderDetailData order = lsOrder.get(position-1);
		Intent in = new Intent();
		in.setClass(context, OrderDetailsActivity.class);
		in.putExtra("order", order);
		startActivity(in);
	}
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(new Date().toLocaleString());
	}

	@Override
	public void onRefresh() {
		loadMore = "1";
		pageNum = 1;
		getOrderList(pageNum,pageNumCount);
	}

	@Override
	public void onLoadMore() {
		loadMore = "2";
		if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
        	//已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
        	pageNum = pageNum + 1;
        	getOrderList(pageNum,pageNumCount);
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
