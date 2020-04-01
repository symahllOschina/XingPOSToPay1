package com.wanding.xingpos.activity;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.RandomStringGenerator;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;

/** 银行卡退款界面 */
public class CardRefundActivity extends BaseActivity implements OnClickListener{
	
	
	private ImageView imgBack;
	private TextView tvTitle;
	private EditText etOrderId;
	private TextView tvOk;
	
	private Dialog hintDialog;// 加载数据时对话框
	
	private Context context = CardRefundActivity.this;
	private UserLoginResData posPublicData;
	
	// 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private static final String TAG = "CardRefundActivity";


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
		setContentView(R.layout.card_refund_activity);
		initView();
		initData();
		synthesizer = MainActivity.synthesizer;
		posProvider = MainActivity.posProvider;
	}
	
	private void initData(){
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
		etOrderId = (EditText) findViewById(R.id.card_refund_etOrderId);
		tvOk = (TextView) findViewById(R.id.card_refund_tvOk);
		
		tvTitle.setText("消费撤销");
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}
	
	/**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String total) {
//        String text = mInput.getText().toString();
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(mInput.getText())) {
//            text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
//            mInput.setText(text);
//        }
//        String text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
    	//刷卡，微信，支付宝，银联，翼支付，分别顺序对应：040,010,020,030,050
    	String payTypeStr = "银行卡退款";
    	
        String text = payTypeStr+total+"元";
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        	Log.e("error code :", result+" method:" + method );
        }
    }
	
	private Handler mHandler = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				
				break;
			case 201:
				
				Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
				break;
			case 202:
				
				Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
				break;
			case 404:
				Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
				hintDialog.dismiss();
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
		case R.id.card_refund_tvOk:
			
			refundStepOne();
			
			break;
		}
	}
	
	/** 退款 */
	private void refundStepOne(){
		String orderTextStr = etOrderId.getText().toString().trim();
		if(Utils.isEmpty(orderTextStr)){
			Toast.makeText(context, "凭证号不能为空！", Toast.LENGTH_LONG).show();
			return;
		}
		if(posProvider.equals(NEW_LAND)){
			Log.e("支付通道：", "星POS");
			NewPosServiceUtil.cardRefundReq(CardRefundActivity.this, orderTextStr);
		}else if(posProvider.equals(FUYOU_SF)){
			Log.e("支付通道：", "富友POS");
			//设备号
			String deviceNum = posPublicData.getTrmNo_pos();
			String pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
			Log.e("富友退款流水号",pos_order_noStr);
			FuyouPosServiceUtil.cardRefundReq(CardRefundActivity.this, orderTextStr,pos_order_noStr);
		}

	}
	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail退款成功返回信息：", txndetail);
			try {
				JSONObject job = new JSONObject(txndetail);
				String transamount = job.getString("transamount");

				Gson gjson  =  GsonUtils.getGson();
				CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);
				/**
				 * 下面是调用帮助类将一个对象以序列化的方式保存
				 * 方便我们在其他界面调用，类似于Intent携带数据
				 */
				try {
					MySerialize.saveObject("cardPayOrder",getContext(),MySerialize.serialize(posResult));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
				Boolean cardPayValue = true;
				int cardOption = 11;
				sharedPreferencesUtil.put("cardPayYes", cardPayValue);
				sharedPreferencesUtil.put("cardPayType", "refund");
				sharedPreferencesUtil.put("cardOption", cardOption);

				speak(transamount);
//							finish();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			    	   Toast.makeText(context, "退款成功！", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 富友界面访问成功返回
	 */
	private void fuyouResult(Bundle bundle){
		String amountStr = bundle.getString("amount");//金额
		String traceNoStr = bundle.getString("traceNo");//凭证号
		String batchNoStr = bundle.getString("batchNo");//批次号
		String referenceNoStr = bundle.getString("referenceNo");//参考号
		String cardNoStr = bundle.getString("cardNo");//卡号
		String typeStr = bundle.getString("type");//卡类型
		String issueStr = bundle.getString("issue");//发卡行
		String dateStr = bundle.getString("date");//日期
		String timeStr = bundle.getString("time");//时间
		String orderNumberStr = bundle.getString("orderNumber");//订单流水号
		String merchantldStr = bundle.getString("merchantld");//商户号
		String terminalldStr = bundle.getString("terminalld");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名称
		String transactionTypeStr = bundle.getString("transactionType");//交易类型

		Log.e("返回的支付金额信息：", amountStr);
		Log.e("交易参考号：", referenceNoStr);
		Log.e("返回的卡类型：", typeStr);
		String totalStr = DecimalUtil.branchToElement(amountStr);
		//播放语音
		speak(totalStr);

		//获取系统年份
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR));
		String dateTimeStr = year + dateStr +  timeStr;
		dateStr = year + dateStr;
		Log.e("手动拼接日期时间：",dateTimeStr);
		Log.e("手动拼接日期：",dateStr);

		CardPaymentDate posResult =  new CardPaymentDate();
		posResult.setPriaccount(cardNoStr);
		posResult.setAcqno("");
		posResult.setIisno(issueStr);
		posResult.setSystraceno(traceNoStr);
		posResult.setTranslocaldate(dateStr);
		posResult.setTranslocaltime(timeStr);
		posResult.setTransamount(totalStr);//此处金额是分转元之后的金额

		/**
		 * 下面是调用帮助类将一个对象以序列化的方式保存
		 * 方便我们在其他界面调用，类似于Intent携带数据
		 */
		try {
			MySerialize.saveObject("cardPayOrder",getContext(),MySerialize.serialize(posResult));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
		Boolean cardPayValue = true;
		int cardOption = 11;
		sharedPreferencesUtil.put("cardPayYes", cardPayValue);
		sharedPreferencesUtil.put("cardPayType", "refund");
		sharedPreferencesUtil.put("cardOption", cardOption);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();  
		
		if (requestCode == 1&&bundle != null) {
			switch (resultCode) {
			 // 退款成功
			  case Activity.RESULT_OK:
				  if(posProvider.equals(NEW_LAND)){
					  newlandResult(bundle);
				  }else if(posProvider.equals(FUYOU_SF)){
					  fuyouResult(bundle);
				  }
			    break;
			// 支付取消
			 case Activity.RESULT_CANCELED:
				 String reason = bundle.getString("reason");
			     if (reason != null) {
			    	 // TODO:
			     }
//			     Toast.makeText(context, "退款失败！", Toast.LENGTH_LONG).show();
			     break;
			
			}
		}
	}
}
