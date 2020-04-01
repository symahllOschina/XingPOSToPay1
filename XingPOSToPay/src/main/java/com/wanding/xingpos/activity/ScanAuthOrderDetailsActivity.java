package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.AuthRecodeListReqData;
import com.wanding.xingpos.bean.AuthRecodeListResData;
import com.wanding.xingpos.bean.AuthRefreshOrderStateReqData;
import com.wanding.xingpos.bean.AuthResultResponse;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WdPreAuthHistoryVO;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.QueryParamsReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**  预授权订单详情界面 */
@ContentView(R.layout.activity_auth_order_details)
public class ScanAuthOrderDetailsActivity extends BaseActivity implements OnClickListener {


	@ViewInject(R.id.menu_title_imageView)
	ImageView imgBack;
	@ViewInject(R.id.menu_title_layout)
	LinearLayout titleLayout;
	@ViewInject(R.id.menu_title_tvTitle)
	TextView tvTitle;
	@ViewInject(R.id.menu_title_imgTitleImg)
	ImageView imgTitleImg;
	@ViewInject(R.id.menu_title_tvOption)
	TextView tvOption;

	@ViewInject(R.id.auth_order_details_imgAuthState)
	ImageView imgAuthState;
	@ViewInject(R.id.auth_order_details_tvAuthState)
	TextView tvAuthState;

	@ViewInject(R.id.auth_order_details_tvAuthOrderMoney)
	TextView tvAuthOrderMoney;

	@ViewInject(R.id.auth_order_details_layoutAuthConSumMoney)
	RelativeLayout layoutAuthConSumMoney;
	@ViewInject(R.id.auth_order_details_tvAuthConSumMoney)
	TextView tvAuthConSumMoney;
	@ViewInject(R.id.auth_order_details_viewAuthConSumMoney)
	View viewAuthConSumMoney;
    @ViewInject(R.id.auth_order_details_layoutAuthRefundAmount)
    RelativeLayout layoutAuthRefundAmount;
    @ViewInject(R.id.auth_order_details_tvAuthRefundAmount)
    TextView tvAuthRefundAmount;
    @ViewInject(R.id.auth_order_details_viewAuthRefundAmount)
    View viewAuthRefundAmount;

	@ViewInject(R.id.auth_order_details_tvAuthPayType)
	TextView tvAuthPayType;
	@ViewInject(R.id.auth_order_details_tvPayStatus)
	TextView tvPayStatus;
	@ViewInject(R.id.auth_order_details_tvAuthPayCreateTime)
	TextView tvAuthPayCreateTime;
	@ViewInject(R.id.auth_order_details_tvAuthOrderId)
	TextView tvAuthOrderId;
	@ViewInject(R.id.auth_order_details_tvChannelOrderNo)
	TextView tvChannelOrderNo;
	@ViewInject(R.id.auth_order_details_layoutOldAuthOrderId)
	RelativeLayout layoutOldAuthOrderId;
	@ViewInject(R.id.auth_order_details_tvOldAuthOrderId)
	TextView tvOldAuthOrderId;



	

	@ViewInject(R.id.auth_order_details_btPrint)
	Button btPrint;
	@ViewInject(R.id.auth_order_details_btAuthCancel)
	Button btAuthCancel;
	@ViewInject(R.id.auth_order_details_btAuthConfrim)
	Button btAuthConfrim;
	@ViewInject(R.id.auth_order_details_btAuthConfirmCancel)
	Button btAuthConfirmCancel;


	/**
	 * 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联
	 */
	private String printNum = "printNumNo";
	/**
	 * 打印字体大小 isDefault:true默认大小，false即为大字体
	 */
	private boolean isDefault = true;

	/**
	 * cameraType为true表示打开后置摄像头，fasle为前置摄像头
	 */
	private boolean cameType = true;

	/**
	 * 打印小票第index联
	 */
	private int index = 1;

	private UserLoginResData userLoginResData;
	private WdPreAuthHistoryVO order;//订单对象
    private List<WdPreAuthHistoryVO> list = new ArrayList<WdPreAuthHistoryVO>();
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
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){

		}

		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("交易详情");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("刷新");



		Intent intent = getIntent();
		userLoginResData = (UserLoginResData) intent.getSerializableExtra("userLoginResData");
		order = (WdPreAuthHistoryVO) intent.getSerializableExtra("order");


		initListener();





		
	}

	@Override
	protected void onResume() {
		super.onResume();
		//取出设置的打印值
		SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(activity, "printing");
		//取出保存的默认值
		printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
		Log.e("取出保存的打印值", printNum);
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(activity, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}
		getRecodeList(1,1);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
		if(posProvider.equals(NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
		}else if(posProvider.equals(FUYOU_SF)){

		}
		Log.e(TAG, "释放资源成功");
    }


	
	/** 
	 * 初始化界面控件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		tvOption.setOnClickListener(this);
		btPrint.setOnClickListener(this);

		btAuthCancel.setOnClickListener(this);
		btAuthConfrim.setOnClickListener(this);
		btAuthConfirmCancel.setOnClickListener(this);
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


	/** 界面数据初始化 */
    private void updateViewData(){
		btAuthCancel.setVisibility(View.GONE);
		btAuthConfrim.setVisibility(View.GONE);
		btAuthConfirmCancel.setVisibility(View.GONE);
    	//预授权状态1:预授权，2：预授权撤销，3：预授权完成,4：预授权完成撤销
		String payAuthStatusStr = order.getPayAuthStatus();
		if(Utils.isNotEmpty(payAuthStatusStr))
		{
			if("1".equals(payAuthStatusStr))
			{
				imgAuthState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.anto_ysq_icon));
				tvAuthState.setText("预授权");
				//显示预授权撤销按钮和预授权完成按钮
//				btAuthCancel.setVisibility(View.VISIBLE);
//				btAuthConfrim.setVisibility(View.VISIBLE);
			}else if("2".equals(payAuthStatusStr))
			{
				imgAuthState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.anto_ysqcx_icon));
				//显示预授权撤销按钮
				tvAuthState.setText("预授权撤销");
			}else if("3".equals(payAuthStatusStr))
			{
				imgAuthState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.anto_ysqwc_icon));
				tvAuthState.setText("预授权完成");
				//显示预授权消费金额
				layoutAuthConSumMoney.setVisibility(View.VISIBLE);
				viewAuthConSumMoney.setVisibility(View.VISIBLE);
				//显示预授权完成撤销按钮
//				btAuthConfirmCancel.setVisibility(View.VISIBLE);
			}else if("4".equals(payAuthStatusStr))
			{
				imgAuthState.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.anto_ysqwccx_icon));
				tvAuthState.setText("预授权完成撤销");
				//显示预授权消费金额
				layoutAuthConSumMoney.setVisibility(View.VISIBLE);
				viewAuthConSumMoney.setVisibility(View.VISIBLE);
				//显示预授权退款金额
				layoutAuthRefundAmount.setVisibility(View.VISIBLE);
				viewAuthRefundAmount.setVisibility(View.VISIBLE);
				//显示原授权单号
				layoutOldAuthOrderId.setVisibility(View.VISIBLE);
			}
		}else{

		}
		//押金金额（预授权金额）
		String orderAmtStr = order.getOrderAmt();
		String orderAmt = "";
		if(Utils.isNotEmpty(orderAmtStr)){
			orderAmt = DecimalUtil.StringToPrice(orderAmtStr);
		}
		tvAuthOrderMoney.setText(String.format(getResources().getString(R.string.order_list_item_orderPayTotal),orderAmt));
		//消费金额（预授权完成金额）
		String consumeFeeStr = order.getConsumeFee();
		String consumeFee = "";
		if(Utils.isNotEmpty(consumeFeeStr)){
			consumeFee = DecimalUtil.StringToPrice(consumeFeeStr);
		}
		tvAuthConSumMoney.setText(String.format(getResources().getString(R.string.order_list_item_orderPayTotal),consumeFee));
		//退款金额（预授权完成撤销金额）
		String refundFeeStr = order.getRefundFee();
		String refundFee = "";
		if(Utils.isNotEmpty(refundFeeStr)){
			refundFee = DecimalUtil.StringToPrice(refundFeeStr);
		}
		tvAuthRefundAmount.setText(String.format(getResources().getString(R.string.order_list_item_orderPayTotal),refundFee));
		//支付方式
		String preWayStr = order.getPreWay();
		String preWay = "未知";
		if(Utils.isNotEmpty(preWayStr)){
			preWay = preWayStr;
		}
		tvAuthPayType.setText(Constants.getPayWay("0",preWay,false));
		//日期时间
		String payTime ="";
		if(Utils.isNotEmpty(payAuthStatusStr)){
			if("1".equals(payAuthStatusStr))
			{
				Long payTimeStr = order.getPreTime();
				if(payTimeStr !=null){
					payTime = DateTimeUtil.stampToFormatDate(payTimeStr, "yyyy-MM-dd HH:mm:ss");
				}
			}else if("2".equals(payAuthStatusStr))
			{
				Long payTimeStr = order.getTxnEndTs();
				if(payTimeStr !=null){
					payTime = DateTimeUtil.stampToFormatDate(payTimeStr, "yyyy-MM-dd HH:mm:ss");
				}
			}else if("3".equals(payAuthStatusStr))
			{
				Long payTimeStr = order.getTxnEndTs();
				if(payTimeStr !=null){
					payTime = DateTimeUtil.stampToFormatDate(payTimeStr, "yyyy-MM-dd HH:mm:ss");
				}


			}else if("4".equals(payAuthStatusStr))
			{
				Long payTimeStr = order.getTxnEndTs();
				if(payTimeStr !=null){
					payTime = DateTimeUtil.stampToFormatDate(payTimeStr, "yyyy-MM-dd HH:mm:ss");
				}

			}
		}
		tvAuthPayCreateTime.setText(payTime);
		//授权单号
		String orderIdStr = order.getMchntOrderNo();
		String orderId = "";
		if(Utils.isNotEmpty(orderIdStr)){
			orderId = orderIdStr;
		}
		tvAuthOrderId.setText(orderId);
		//渠道单号
		String channelOrderNoStr = order.getChannelOrderNo();
		String channelOrderNo = "";
		if(Utils.isNotEmpty(channelOrderNoStr)){
			channelOrderNo = channelOrderNoStr;
		}
		tvChannelOrderNo.setText(channelOrderNo);
		//原授权单号
		String oldOrderIdStr = order.getOrgOrderNo();
		String oldOrderId = "";
		if(Utils.isNotEmpty(oldOrderIdStr)){
			oldOrderId = oldOrderIdStr;
		}
		tvOldAuthOrderId.setText(oldOrderId);
		//支付状态"0/1/2/5分别代表初始、成功、失败",支付中
		Integer status_int = order.getStatus();
		String status = "未知";
		if(status_int != null){
			if(status_int == 0){
				status = "初始订单";
			}else if(status_int == 1){
				status = "支付成功";
			}else if(status_int == 2){
				status = "支付失败";
			}else if(status_int == 5){
				status = "支付中";
				tvOption.setVisibility(View.VISIBLE);
				tvOption.setText("刷新");
			}
		}
		tvPayStatus.setText(status);


    }

    /**
	 * 获取交易详情
	 */
    private void getRecodeList(final int pageNum,final int pageNumCount){

        showWaitDialog();

        final String etSearchStr = order.getMchntOrderNo();
        final String startTimeStr = "";
        final String endTimeStr = "";
        //参数实体
        final AuthRecodeListReqData reqData = QueryParamsReqUtil.queryAuthRecodeListReq(pageNum,pageNumCount,userLoginResData,etSearchStr,startTimeStr,endTimeStr);

        final String url = NitConfig.authRecodeListUrl;
        Log.e("请求地址：", url);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_ONE;
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

            };
        }.start();
    }

    /**
	 * 刷新订单状态
	 */
    private void updateOrderState(){

        showWaitDialog();
        //参数实体
		String orderId = order.getMchntOrderNo();
        final AuthRefreshOrderStateReqData reqData = QueryParamsReqUtil.refreshOrderStateReq(userLoginResData,orderId);

        final String url = NitConfig.refreshOrderStateUrl;
        Log.e("请求地址：", url);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_TWO;
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

            };
        }.start();
    }

	private void sendMessage(int what,String text){
		Message msg = new Message();
		msg.what = what;
		msg.obj = text;
		handler.sendMessage(msg);
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			String errorJsonText = "";
			switch (msg.what) {
				case NetworkUtils.MSG_WHAT_ONE:
					String orderDetailJsonStr = (String) msg.obj;
					orderDetailJsonStr(orderDetailJsonStr);
					hideWaitDialog();
					break;
				case NetworkUtils.MSG_WHAT_TWO:
					String refreshOrderStateStr = (String) msg.obj;
					refreshOrderStateStr(refreshOrderStateStr);
					hideWaitDialog();
					break;
				case NetworkUtils.REQUEST_JSON_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					break;
				case NetworkUtils.REQUEST_IO_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					break;
				case NetworkUtils.REQUEST_CODE:
					errorJsonText = (String) msg.obj;
					ToastUtil.showText(activity,errorJsonText,1);
					hideWaitDialog();
					break;
				default:
					break;
			}
		};
	};

	private void orderDetailJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<AuthRecodeListResData>() {}.getType();
                AuthRecodeListResData recodeResData = gjson.fromJson(dataJson, type);
                List<WdPreAuthHistoryVO> recodeList = new ArrayList<WdPreAuthHistoryVO>();
                //获取的list
                recodeList = recodeResData.getOrderList();
                list.clear();
                list.addAll(recodeList);
                if(list!=null&&list.size()>0){
                    order = list.get(0);
                    updateViewData();
                }
            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void refreshOrderStateStr(String jsonStr){
		try{
			Gson gjson  =  GsonUtils.getGson();
			com.wanding.face.bean.AuthRefreshOrderStateResData resData = gjson.fromJson(jsonStr, com.wanding.face.bean.AuthRefreshOrderStateResData.class);
			//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
			String return_codeStr = resData.getReturn_code();
			//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
			String return_msgStr = resData.getReturn_msg();
			//result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
			String result_codeStr = resData.getResult_code();
			String result_msgStr = resData.getResult_msg();
			if("01".equals(return_codeStr)) {
				if("01".equals(result_codeStr)){
					//0/1/2/5分别代表初始、成功、失败",支付中
					order.setStatus(1);
				}else if("03".equals(result_codeStr)){
					if(Utils.isNotEmpty(result_msgStr)){
						Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();

					}
					order.setStatus(5);
				}else{
					if(Utils.isNotEmpty(result_msgStr)){
						Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();

					}
					order.setStatus(2);
				}
			}else{
				if(Utils.isNotEmpty(return_msgStr)){
					Toast.makeText(activity, return_msgStr, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();

				}
				order.setStatus(2);
			}
		}catch (Exception e){
			e.printStackTrace();
			ToastUtil.showText(activity,"获取支付状态结果返回错误！",1);
			finish();
		}
		updateViewData();
	}


	private void startPrint(){
		/** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
		index = 1;
		if(Constants.NEW_LAND.equals(posProvider)){
			//初始化打印机
			getPrinter();
			if(printNum.equals("printNumNo")){

			}else if(printNum.equals("printNumOne")){

				NewlandPrintUtil.authRecodeDetailPrintText(activity,aidlPrinter,userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);

			}else if(printNum.equals("printNumTwo")){
				//打印
				NewlandPrintUtil.authRecodeDetailPrintText(activity,aidlPrinter,userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				try {
					Thread.sleep(NewlandPrintUtil.time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//弹出对话框提示打印下一联

				showPrintTwoDialog();

			}

		}else if(Constants.FUYOU_SF.equals(posProvider)){
			if(printNum.equals("printNumNo")){

				//不执行打印
//            intentToActivity(totalStr);
				finish();

			}else if(printNum.equals("printNumOne")){

				//打印一次
				String printTextStr = FuyouPrintUtil.authRecodeDetailPrintText(userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				FuyouPosServiceUtil.printTextReq(activity,printTextStr);

			}else if(printNum.equals("printNumTwo")){
				//打印两次
				String printTextStr = FuyouPrintUtil.authRecodeDetailPrintText(userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);
				FuyouPosServiceUtil.printTextReq(activity,printTextStr);
			}
		}


	}

	/**  打印下一联提示窗口 */
	private void showPrintTwoDialog(){
		View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
		Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(activity,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//打印第二联
				index = 2;
				if(Constants.NEW_LAND.equals(posProvider)){
					NewlandPrintUtil.authRecodeDetailPrintText(activity,aidlPrinter,userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);

				}else if(Constants.FUYOU_SF.equals(posProvider)){
					String printTextStr = FuyouPrintUtil.authRecodeDetailPrintText(userLoginResData,order,isDefault,FuyouPrintUtil.payPrintRemarks,index);
					FuyouPosServiceUtil.printTextReq(activity,printTextStr);
				}
				myDialog.dismiss();

			}
		});
		myDialog.show();
		myDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle = data.getExtras();
		/**
		 * 富友POS扫码返回
		 */
		if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
			if(bundle != null){
				Log.e(TAG,resultCode+"");
				String reason = "扫码取消";
				String traceNo = "";
				String batchNo = "";
				String ordernumber = "";
				String reason_str = (String) bundle.get("reason");
				String traceNo_str = (String)bundle.getString("traceNo");
				String batchNo_str = (String)bundle.getString("batchNo");
				String ordernumber_str = (String)bundle.getString("ordernumber");

				switch (resultCode) {
					case Activity.RESULT_OK:
						String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
						Log.e("获取扫描结果：", scanCodeStr);
						//如果扫描的二维码为空则不执行支付请求
						if(Utils.isNotEmpty(scanCodeStr)){
							//auth_no	授权码（及扫描二维码值）
							String auth_no = scanCodeStr;

						}else{

							ToastUtil.showText(activity,"扫描结果为空！",1);
						}


						break;
					// 扫码取消
					case Activity.RESULT_CANCELED:

						if (Utils.isNotEmpty(reason_str)) {
							Log.e("reason", reason_str);
							reason = reason_str;
						}
						ToastUtil.showText(activity,reason,1);
						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
						break;
					default:
						break;

				}
			}else{
				ToastUtil.showText(activity,"扫码失败！",1);
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
						if(printNum.equals("printNumTwo")){
							if(index < 2){
								//打印正常
								//弹出对话框提示打印下一联
								showPrintTwoDialog();
							}

						}else{
							//打印完成关闭界面
//							finish();
						}

						break;
					case Activity.RESULT_CANCELED:
						if (Utils.isNotEmpty(reason_str)) {
							reason = reason_str;
							Log.e("reason", reason);
							if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
								//缺纸，不能打印
								ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
							}else {
								ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
							}
						}else{
							ToastUtil.showText(activity,reason,1);
						}
						finish();

						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
						break;
					default:
						break;

				}
			}else{
				ToastUtil.showText(activity,"打印返回数据为空！",1);
			}

		}
		/**
		 * 支付返回
		 */
		if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
			if(bundle != null){
				Log.e(TAG,resultCode+"");
				String reason = "支付取消";
				String traceNo = "";
				String batchNo = "";
				String ordernumber = "";

				String reason_str = (String) bundle.get("reason");
				String traceNo_str = (String)bundle.getString("traceNo");
				String batchNo_str = (String)bundle.getString("batchNo");
				String ordernumber_str = (String)bundle.getString("ordernumber");
				switch (resultCode) {
					// 支付成功
					case Activity.RESULT_OK:


						break;
					// 支付取消
					case Activity.RESULT_CANCELED:
						if (Utils.isNotEmpty(reason_str)) {
							reason = reason_str;
							Log.e("reason", reason);
						}
						ToastUtil.showText(activity,reason,1);
						finish();

						Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

						break;
					default:
						break;
				}
			}else{
				ToastUtil.showText(activity,"支付返回数据为空！",1);
			}

		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		String authAction = "";
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.menu_title_tvOption:
			if(Utils.isFastClick()){
				return;
			}
			updateOrderState();
			break;
		case R.id.auth_order_details_btPrint:
			if(Utils.isFastClick()){
				return;
			}
			//支付状态"0/1/2/5分别代表初始、成功、失败",支付中
			Integer status = order.getStatus();
			if(status != 1){
				ToastUtil.showText(activity,"支付状态支付中或支付失败不支持补打小票！",1);
				return;
			}
			startPrint();
			break;
		case R.id.auth_order_details_btAuthCancel:
			if(Utils.isFastClick()){
				return;
			}
			authAction = "2";
			intent = new Intent();
			intent.setClass(activity,ScanAuthCancelActivity.class);
			intent.putExtra("order",order);
			intent.putExtra("authAction",authAction);
			startActivity(intent);
			break;
		case R.id.auth_order_details_btAuthConfrim:
			if(Utils.isFastClick()){
				return;
			}
			authAction = "3";
			intent = new Intent();
			intent.setClass(activity,ScanAuthCancelActivity.class);
			intent.putExtra("order",order);
			intent.putExtra("authAction",authAction);
			startActivity(intent);
			break;
		case R.id.auth_order_details_btAuthConfirmCancel:
			if(Utils.isFastClick()){
				return;
			}
			authAction = "4";
			intent = new Intent();
			intent.setClass(activity,ScanAuthCancelActivity.class);
			intent.putExtra("order",order);
			intent.putExtra("authAction",authAction);
			startActivity(intent);
			break;
			default:
				break;



		}
	}
}
