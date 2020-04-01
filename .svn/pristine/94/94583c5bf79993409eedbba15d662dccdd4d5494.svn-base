package com.wanding.xingpos.activity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newland.newpaysdk.model.NldQueryResult;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.PosPayQueryReqData;
import com.wanding.xingpos.bean.PosPayQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;
import com.zijunlin.Zxing.Demo.CaptureActivity;

/** 扫码查单界面 */
public class ScanQueryActivity extends BaseActivity implements OnClickListener{
	public static final int REQUEST_CODE = 1;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private EditText etOrderId;
	private ImageView imagScan;
	private TextView tvOk;
	
	private Dialog hintDialog;// 加载数据时对话框
	private String scanCodeStr;//扫描返回结果
	
	private Context context = ScanQueryActivity.this;
	private UserLoginResData posPublicData;
	
	private static final String TAG = "ScanPayActivity";
	AidlDeviceService aidlDeviceService = null;
    public AidlScanner aidlScanner=null;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
	private boolean cameType = true;

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
		setContentView(R.layout.scan_query_activity);
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}
		initView();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

        aidlScanner = null;
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
                    scanCodeStr = arg0[0];
                    //如果扫描的二维码为空则不执行支付请求
    				if(scanCodeStr!=null&&!scanCodeStr.equals("")){
    					Log.e("扫描返回扫描结果：", scanCodeStr);
    					etOrderId.setText(scanCodeStr);
    			    	
    				}else{
    					
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
    private void backscan(){
		try {
			Log.i(TAG, "-------------scan-----------");
			aidlScanner = AidlScanner.Stub.asInterface(aidlDeviceService.getScanner());
			aidlScanner.startScan(ScanConstant.ScanType.BACK, 10, new AidlScannerListener.Stub() {
				@Override
				public void onScanResult(String[] arg0) throws RemoteException {
					Log.w(TAG,"onScanResult-----"+arg0[0]);
					scanCodeStr = arg0[0];
					if(scanCodeStr!=null&&!scanCodeStr.equals("")){
						//auth_no	授权码（及扫描二维码值）
						String auth_no = scanCodeStr;
						Log.e("后置扫码值：", auth_no);


						Message msg = new Message();
						msg.obj = auth_no;
						msg.what = 100;
						mHandler.sendMessage(msg);



					}else{

						Log.e("后置扫码值：", "为空");
					}
				}

				@Override
				public void onFinish() throws RemoteException {

				}

				@Override
				public void onError(int i, String s) throws RemoteException {

				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void initData(){
		//取出保存的摄像头参数值
		SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(context, "scancamera");
		cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
		if(cameType){
			Log.e("当前摄像头打开的是：", "后置");
		}else{
			Log.e("当前摄像头打开的是：", "前置");
		}
		//加载数据
		try {
			posPublicData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
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
		etOrderId = (EditText) findViewById(R.id.scan_query_etOrderId);
		imagScan = (ImageView) findViewById(R.id.scan_query_imagScan);
		tvOk = (TextView) findViewById(R.id.scan_query_tvOk);
		
		tvTitle.setText("扫码查单");
		imgBack.setOnClickListener(this);
		imagScan.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}
	
	/** 获取自己生成的订单号   */
	private void getRefundOrderId(final String etOrderIdTextStr){
		final String url = NitConfig.getPosPayOrderId;
		new Thread(){
			@Override
			public void run() {
				
				try {
					JSONObject userJSON = new JSONObject();  
					userJSON.put("refundCode",etOrderIdTextStr);
					if(posProvider.equals(NEW_LAND)){
						userJSON.put("SDKType","NEWLAND_SDK");
					}else if(posProvider.equals(FUYOU_SF)){
						userJSON.put("SDKType","FUIOU_SDK");
					}
					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
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
                   Log.e("返回状态吗：", code+"");
                   if(code == 200){
                	   
                	   InputStream is = connection.getInputStream();  
                	   //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型  
                	   String jsonStr = HttpJsonReqUtil.readString(is); 
                	   Log.e("返回JSON值：", jsonStr);
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
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				}   catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg=new Message();
				    msg.what=404;
				    mHandler.sendMessage(msg);
				}  
				
			};
		}.start();
	}
	
	/** 扫描结果发起支付查询请求  */
	private void payRequestMethood(final String url,final PosPayQueryReqData posBean){
		hintDialog=CustomDialog.CreateDialog(getContext(), "    查询中...");
		hintDialog.show();
		hintDialog.setCanceledOnTouchOutside(false);
		new Thread(){
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
					userJSON.put("out_trade_no",posBean.getOut_trade_no());   
					userJSON.put("pay_trace",posBean.getPay_trace());   
					userJSON.put("pay_time",posBean.getPay_time());   
					userJSON.put("operator_id",posBean.getOperator_id());   
					userJSON.put("key_sign",posBean.getKey_sign());   

					String content = String.valueOf(userJSON);
					Log.e("发起请求参数：", content);
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

	
	/**
	 *  星POS 内置接口查询
	 */
	private void XingPosServiceQuery(String etOrderIdTextStr){
		
		getRefundOrderId(etOrderIdTextStr);
		
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				String auth_no = (String) msg.obj;
				etOrderId.setText(auth_no);
				break;
			case 1:
				String jsonStr=(String) msg.obj;
				//然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}  
         	   	Gson gjson  =  GsonUtils.getGson();
        		PosPayQueryResData posResult = gjson.fromJson(jsonStr, PosPayQueryResData.class);
        		//return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        		String return_codeStr = posResult.getReturn_code();
        		//return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        		String return_msgStr = posResult.getReturn_msg();
        		//result_code	业务结果：“01”支付成功 ，02”支付失败 ，“03”支付中,"04"含退款，
        		String result_codeStr = posResult.getResult_code();
        		if(return_codeStr.equals("01")){
        			if(result_codeStr.equals("01")){
        				
        				intentToActivity(posResult);
        				
//        				Toast.makeText(getContext(), "查询成功!", Toast.LENGTH_LONG).show();
        			}else if(result_codeStr.equals("02")){
        				intentToActivity(posResult);
//        				Toast.makeText(getContext(), "支付失败!", Toast.LENGTH_LONG).show();
        			}else if(result_codeStr.equals("03")){
        				intentToActivity(posResult);
//        				Toast.makeText(getContext(), "支付中!", Toast.LENGTH_LONG).show();
        			}else if(result_codeStr.equals("04")){
        				intentToActivity(posResult);
//        				Toast.makeText(getContext(), "全部退款", Toast.LENGTH_LONG).show();
        			}else if(result_codeStr.equals("05")){
        				intentToActivity(posResult);
//        				Toast.makeText(getContext(), "部分退款", Toast.LENGTH_LONG).show();
        			}else{
        				intentActivity();
        				Toast.makeText(getContext(), "查询失败!", Toast.LENGTH_LONG).show();
        			}
        		}else{
        			
        			intentActivity();
        			Toast.makeText(getContext(), return_msgStr+"!", Toast.LENGTH_LONG).show();
        		}
        		hintDialog.dismiss();
        		etOrderId.setText("");
				break;
			case 2:
				String orderIdJSONStr=(String) msg.obj;
				//{"isSuccess":true,"errorCode":null,"errorMessage":null,"data":"{}"}
				try {
					JSONObject job = new JSONObject(orderIdJSONStr);
					boolean isSuccess = job.getBoolean("isSuccess");
					if(isSuccess){
						String dataStr = job.getString("data");
						JSONObject dataJob = new JSONObject(dataStr);
						String refundfCode = dataJob.getString("refundfCode");
						String new_channelOrderIdStr = dataJob.getString("channelOrderId");//支付时自定义商户号
						String way = dataJob.getString("way");
						//判断支付通道
						if(way.equals("SDK")){
							if(posProvider.equals(NEW_LAND)){
								//调用星POS内置接口
								Log.e("查询：", "星pos二维码");
								NewPosServiceUtil.scanQueryReq(ScanQueryActivity.this,new_channelOrderIdStr,posPublicData);
							}else if(posProvider.equals(FUYOU_SF)){
								Log.e("查询：", "富友POS二维码");
								//支付方式
								String payTypeStr = dataJob.getString("payWay");
								//凭证号
								String oldTraceStr = dataJob.getString("refundfCode");
								String orderIdStr = dataJob.getString("orderId");
								String channelOrderIdStr = dataJob.getString("channelOrderId");//支付时自定义商户号
								FuyouPosServiceUtil.scanQueryOrderReq(ScanQueryActivity.this,payTypeStr,oldTraceStr,channelOrderIdStr);
							}
						}else{
							Log.e("查询：", "默认格式二维码");
							String pay_type = "";
							PosPayQueryReqData posBean = PayRequestUtil.queryReq(pay_type,refundfCode,posPublicData,posProvider);
							//付款二维码内容(发起查询请求)
							String url = NitConfig.queryUrl;
							payRequestMethood(url,posBean);
						}

					}else{
						String errorMessage = job.getString("errorMessage");
						Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					intentActivity();
				}
				etOrderId.setText("");
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				if(hintDialog!=null&&hintDialog.isShowing()){
					hintDialog.dismiss();
				}
        		etOrderId.setText("");
				break;
			}
		};
	};
	
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.scan_query_imagScan:
			etOrderId.setText("");
			if(posProvider.equals(NEW_LAND)){
				if(cameType){
					Log.e("扫码调用：", "后置摄像头");
//    			in = new Intent();
//    			in.setClass(this, CaptureActivity.class);
//    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//    			startActivityForResult(in, REQUEST_CODE);
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
				startActivityForResult(intent, 11);
			}

			
			break;
		case R.id.scan_query_tvOk:
			scanQueryStepOne();
			break;
		}
	}


	
	
	/** 查询（判断）  */
	private void scanQueryStepOne(){
		String etOrderIdTextStr = etOrderId.getText().toString().trim();
		if(Utils.isEmpty(etOrderIdTextStr)){
			Toast.makeText(context, "查询订单号不能为空！", Toast.LENGTH_LONG).show();
			return;
		}
		getRefundOrderId(etOrderIdTextStr);

	}
	
	/** 跳转目标Activity */
	private void intentToActivity(PosPayQueryResData queryResData){
		Intent in = new Intent();
		in.setClass(context, QueryOrderDetailsActivity.class);
		in.putExtra("queryResData", queryResData);
		startActivity(in);
	}
	
	/** 跳转目标Activity */
	private void intentActivity(){
		Intent in = new Intent();
		in.setClass(context, PayErrorActivity.class);
		in.putExtra("optionTypeStr", "030");
		startActivity(in);
	};
	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0310")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail支付返回信息：", txndetail);
			Toast.makeText(context, "查询订单成功！", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){

		Toast.makeText(ScanQueryActivity.this,"查询订单成功！",Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();  
		if(requestCode == REQUEST_CODE){
			if(resultCode == CaptureActivity.RESULT_CODE){
				scanCodeStr=bundle.getString("ScanCode");
				Log.e("扫描返回扫描结果：", scanCodeStr);
				etOrderId.setText(scanCodeStr);
			}
		}
		if (requestCode == 11&&bundle != null) {
			switch (resultCode) {
				// 退款成功
				case Activity.RESULT_OK:
					if(posProvider.equals(NEW_LAND)){

					}else if(posProvider.equals(FUYOU_SF)){
						scanCodeStr = bundle.getString("return_txt");//扫码返回数据
						Log.e("扫描返回扫描结果：", scanCodeStr);
						etOrderId.setText(scanCodeStr);
					}

					break;
				// 支付取消
				case Activity.RESULT_CANCELED:
					if(posProvider.equals(NEW_LAND)){
						String reason = bundle.getString("reason");
						if (reason != null) {
							// TODO:
						}
						intentActivity();
//						Toast.makeText(context, "退款失败！", Toast.LENGTH_LONG).show();
					}else if(posProvider.equals(FUYOU_SF)){

					}

					break;

			}
		}
		
		if (requestCode == 1&&bundle != null) {
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
					 if (reason != null) {
						 // TODO:
					 }
					 Toast.makeText(context, "查询订单失败！", Toast.LENGTH_LONG).show();
					 intentActivity();
				 }else if(posProvider.equals(FUYOU_SF)){

				 }
			     break;
			
			}
		}
	}
}
