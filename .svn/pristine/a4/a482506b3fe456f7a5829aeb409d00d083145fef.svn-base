package com.wanding.xingpos.activity;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** 交易明细列表界面 */
public class OrderListActivity extends BaseActivity implements OnClickListener,OnItemClickListener{
	
	private Context context = OrderListActivity.this;
	
	private ImageView imgBack,tvTitleImg;
	private TextView tvTitle,tvTitleFunction;
	private LinearLayout layoutTitle;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView mListView;
	
	private UserLoginResData loginInitData;
	
	private int pageNum = 1;//默认加载第一页
	private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
	private String date_typeStr = "1";//（"1"=当日交易）（"2"=本月交易不含今天）
	
	private static final int REFRESH = 100;  
    private static final int LOADMORE = 200;  
    private boolean loadMore = false;//loadMore为true表示加载更多操作，false表示刷新操作
    private View loadMoreView;  
    int visibleLastIndex = 0;    //最后的可视项索引  
    int visibleItemCountNum;        // 当前窗口可见项总数  
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
		setContentView(R.layout.order_list_activity);
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
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_list_mSwipeRefreshLayout);
		mListView = (ListView) findViewById(R.id.order_list_mListView);
		
		tvTitle.setText("交易明细（当天）");
		tvTitleFunction.setText("汇总");
		imgBack.setOnClickListener(this);
		layoutTitle.setOnClickListener(this);
		tvTitleFunction.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		
		/** 获取上拉加载布局并初始化view  */
		loadMoreView = getLayoutInflater().inflate(R.layout.load_more, null);  
        loadMoreView.setVisibility(View.GONE);  
        mListView.addFooterView(loadMoreView);  
        mListView.setFooterDividersEnabled(false);  
  
        //设置进度圈的大小;(这里面只有两个值SwipeRefreshLayout.LARGE和DEFAULT，后者是默认效果)  
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);  
        //设置进度圈的背景色。这里随便给他设置了一个颜色：浅绿色  
//        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.CYAN);
//        mSwipeRefreshLayout.setProgressBackgroundColor(Color.CYAN);
        //设置进度动画的颜色。这里面最多可以指定四个颜色，先随机设置的
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark  
                , android.R.color.holo_blue_dark  
                , android.R.color.holo_red_dark  
                , android.R.color.widget_edittext_dark);  
        
        //设置手势监听  (下拉刷新)
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {  
            @Override  
            public void onRefresh() {  
            	loadMore = false;//记录该操作为刷新操作还是加载更多操作
//            	//刷新数据(刷新主要获取第一页的新数据)
            	if(pageNum == 1){
            		getOrderList(pageNum,pageNumCount);
            	}else{
            		pageNum = 1;
                	getOrderList(pageNum,lsOrder.size());
            	}
            	
            }  
        });  
        //给listview设置一个滑动的监听  
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {  
        	
  
            //当滑动状态发生改变的时候执行  
            @Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {  
                    //当不滚动的时候  
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:  
                    	
                        int itemsLastIndex = mAdapter.getCount() - 1;    //数据集最后一项的索引  
                        int lastIndex = itemsLastIndex + 1;                //加上底部的loadMoreView项  
                        //判断是否是最底部  
                         //if (view.getLastVisiblePosition() == (view.getCount()) - 1) { //或者  
                        if (visibleLastIndex == lastIndex) {  
                        	loadMoreView.setVisibility(View.VISIBLE);
                            //加载数据
                        	loadMore = true;//记录该操作为刷新操作还是加载更多操作
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
                                        msg.what = LOADMORE;  
                                        msg.arg1 = visibleLastIndex - visibleItemCountNum + 1;  
                                        mHandler.sendMessage(msg); 
                                        
                                    }  
                                }, 2000); 
                            	
                            }
                        }  
                        break;  
                }  
            }  
  
            //正在滑动的时候执行  
            @Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                visibleItemCountNum = visibleItemCount;  
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  
            }  
        });  
	}

	/** 
	 * 获取交易明细  
	 * 入参：mid，eid，date_type（"1"=当日交易）（"2"=本月交易不含今天）
	 **/
	private void getOrderList(final int pageNum,final int pageCount){
		final String url;
		if(date_typeStr.equals("1")){
			url = NitConfig.queryOrderDayListUrl;
		}else{
			url = NitConfig.queryOrderMonListUrl;
		}
		
		new Thread(){
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
				if(mAdapter!=null){
					mAdapter.notifyDataSetChanged();
				}
                mSwipeRefreshLayout.setRefreshing(false);  
                break;  
            case LOADMORE:  
                mAdapter.notifyDataSetChanged();    //数据集变化后,通知adapter 
                int position = msg.arg1;  
                mListView.setSelection(position);    //设置选中项  
                loadMoreView.setVisibility(View.GONE);  
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
						List<OrderDetailData> orderList = new ArrayList<OrderDetailData>();
						//获取的list
						orderList = order.getOrderList();
						getMoerNum = orderList.size();

						if(pageNum == 1){
							lsOrder.clear();
						}
						lsOrder.addAll(orderList);
						Log.e("查询数据：", lsOrder.size()+""+"条");
						mAdapter = new OrderListAdapte(context, lsOrder,posProvider);
						mListView.setAdapter(mAdapter);
						//关闭上拉或下拉View，刷新Adapter
						if(pageNum > 1){
							if(loadMore){
								Message msg1 = new Message();  
	                            msg1.what = LOADMORE;  
	                            msg1.arg1 = visibleLastIndex - visibleItemCountNum + 1;  
	                            mHandler.sendMessage(msg1);  
							}else{
								mHandler.sendEmptyMessageDelayed(REFRESH, 2000);  
							}
                            
						}else{
							if(!loadMore){
								mHandler.sendEmptyMessageDelayed(REFRESH, 2000);  
							}
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
				
				
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				mSwipeRefreshLayout.setRefreshing(false);  
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
	    TextView tvDay = (TextView) view.findViewById(R.id.order_list_topPup_day); 
	    tvMonth.setOnClickListener(this); 
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
			in.setClass(OrderListActivity.this, SummaryActivity.class);
			startActivity(in);
			break;
		
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		OrderDetailData order = lsOrder.get(position);
		Intent in = new Intent();
		in.setClass(context, OrderDetailsActivity.class);
		in.putExtra("order", order);
		startActivity(in);
	}
}
