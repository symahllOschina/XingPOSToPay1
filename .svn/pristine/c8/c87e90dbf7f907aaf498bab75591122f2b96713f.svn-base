package com.wanding.xingpos.activity;

import java.io.IOException;
import java.util.Arrays;
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
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.RandomStringGenerator;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;

/** 
 * 预授权Activity
 */
public class AuthActivity extends BaseActivity implements OnClickListener{
	
	private Context context = AuthActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	private EditText etSumMoney;
	private ImageButton imagEliminate;
	private TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight,tvNine,tvZero,tvSpot;
	private TextView tvOk;
	private StringBuilder pending = new StringBuilder();
	
	private String authType = "";
	
	public static final int REQUEST_CODE = 1;
	private Dialog hintDialog;// 加载数据时对话框
	
	private UserLoginResData posPublicData;
	
	private String pos_order_noStr;
	/** POS支付成功返回的订单号，金额  */
	private String orderid_scan = "";
	private String transamount = "";//
	/** 是否为测试环境，默认为测试环境 */
    private boolean testState = true;
    

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private static final String TAG = "CardPayActivity";
//    protected Handler mainHandler;
	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider = MainActivity.posProvider;

//	private boolean isSettlement = false;//是否调用结算SDK,是为true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		synthesizer = MainActivity.synthesizer;
		initData();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//清空StringBuilder，EditText恢复初始值
		 //清空EditText
		if(posProvider.equals(NEW_LAND)){
			if(authType.equals("1")){
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
					etSumMoney.setText("￥0.00");
				}
				//取出保存的默认支付金额
				//defMoneyNum ：交易设置值存储应用本地的文件名称
				SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "defMoneyNum");
				//取出保存的默认值
				String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
				if(defMoney.equals("")||defMoney.equals("0")){
					etSumMoney.setHint("￥0.00");
				}else{
					etSumMoney.setText("￥"+defMoney);
					pending.append(defMoney);
				}
			}
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		Intent in = getIntent();
		authType = in.getStringExtra("authType");
		Log.e("操作区分码：",authType);

		Log.e("posProvider的值",posProvider);
		if(posProvider.equals(NEW_LAND)){
			Log.e("支付通道：", "星POS");
			if(authType.equals("1")){
				setContentView(R.layout.card_pay_activity);
				initView();
				//取出保存的默认支付金额
				//defMoneyNum ：交易设置值存储应用本地的文件名称
				SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "defMoneyNum");
				//取出保存的默认值
				String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
				if(defMoney.equals("")||defMoney.equals("0")){
					etSumMoney.setHint("￥0.00");
				}else{
					etSumMoney.setText("￥"+defMoney);
					pending.append(defMoney);
				}

			}else{
				String total_feeStr = "";
				XingPosServicePay(authType,total_feeStr);
			}

		}else if(posProvider.equals(FUYOU_SF)){
			Log.e("支付通道：", "富友POS");
			FuyouPosServicePay(authType);
		}
	}
	
	/**  初始化控件 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		etSumMoney = (EditText) findViewById(R.id.content_layout_etSumMoney);
		//强制隐藏Android输入法窗体 
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//EditText始终不弹出软件键盘 
		etSumMoney.setInputType(InputType.TYPE_NULL); 
		EditTextUtils.setPricePoint(etSumMoney);
        imm.hideSoftInputFromWindow(etSumMoney.getWindowToken(),0); 
		tvOne = (TextView) findViewById(R.id.content_layout_tvOne);
		tvTwo = (TextView) findViewById(R.id.content_layout_tvTwo);
		tvThree = (TextView) findViewById(R.id.content_layout_tvThree);
		tvFour = (TextView) findViewById(R.id.content_layout_tvFour);
		tvFive = (TextView) findViewById(R.id.content_layout_tvFive);
		tvSix = (TextView) findViewById(R.id.content_layout_tvSix);
		tvSeven = (TextView) findViewById(R.id.content_layout_tvSeven);
		tvEight = (TextView) findViewById(R.id.content_layout_tvEight);
		tvNine = (TextView) findViewById(R.id.content_layout_tvNine);
		tvZero = (TextView) findViewById(R.id.content_layout_tvZero);
		tvSpot = (TextView) findViewById(R.id.content_layout_tvSpot);
		imagEliminate = (ImageButton) findViewById(R.id.content_layout_imagEliminate);
		tvOk = (TextView) findViewById(R.id.card_pay_tvOk);
		
		
		
		tvTitle.setText("预授权");
		imgBack.setOnClickListener(this);
		tvOne.setOnClickListener(this);
		tvTwo.setOnClickListener(this);
		tvThree.setOnClickListener(this);
		tvFour.setOnClickListener(this);
		tvFive.setOnClickListener(this);
		tvSix.setOnClickListener(this);
		tvSeven.setOnClickListener(this);
		tvEight.setOnClickListener(this);
		tvNine.setOnClickListener(this);
		tvZero.setOnClickListener(this);
		tvSpot.setOnClickListener(this);
		imagEliminate.setOnClickListener(this);
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
    	String payTypeStr = "";
		if(authType.equals("1")){
			payTypeStr = "银行卡收款";
		}else if(authType.equals("2")){
			payTypeStr = "微信收款";
		}else if(authType.equals("3")){
			payTypeStr = "支付宝收款";
		}else if(authType.equals("4")){
			payTypeStr = "银联收款";
		}
    	
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
				//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
				break;
			}
		};
	};
	
	/**
	 *  星POS 内置接口支付
	 *  payToatl:支付金额
	 */
	private void XingPosServicePay(String authType,String total_fee){
		String deviceNum = posPublicData.getTrmNo_pos();
	    pos_order_noStr = RandomStringGenerator.getNlRandomNum(deviceNum);
	    Log.e("生成的订单号：", pos_order_noStr);
		NewPosServiceUtil.authReq(AuthActivity.this, authType, total_fee,pos_order_noStr);
		
	}

	/**
	 * 富友POS 内置支付
	 */
	private void FuyouPosServicePay(String authType){
		FuyouPosServiceUtil.authReq(AuthActivity.this, authType);
	}

	/**
	 * 调结算SDK（清空终端流水）

	private void emptyPosOrder(){
		isSettlement = true;
		FuyouPosServiceUtil.settleReq(AuthActivity.this);

	}
	 */
	
	/** 金额：transamountStr ，交易类型  */
	private void intentToActivity(String transamountStr){
		Intent in = new Intent();
		in.setClass(context, PaySuccessActivity.class);
		in.putExtra("totalKey", transamountStr);
		in.putExtra("payTypeKey", authType);
		startActivity(in);
	}
	
	private void intentActivity(){
		Intent in = new Intent();
		in.setClass(context, PayErrorActivity.class);
		in.putExtra("optionTypeStr", "010");
		startActivity(in);
	};
	

	
	private boolean judje1() {
        String a = "+-*/.";
        int[] b = new int[a.length()];
        int max;
        for (int i = 0; i < a.length(); i++) {
            String c = "" + a.charAt(i);
            b[i] = pending.lastIndexOf(c);
        }
        Arrays.sort(b);
        if (b[a.length() - 1] == -1) {
            max = 0;
        } else {
            max = b[a.length() - 1];
        }
        if (pending.indexOf(".", max) == -1) {
            return true;
        } else {
            return false;
        }
    }
	 
	 private String getTextNum(StringBuilder sbu){
		 String str = "";
		 //判断是否整数
		 
		 return str;
	 } 
	
	@Override
	public void onClick(View v) {
		Intent in = null;
		String etTextStr,total_feeStr;
		int last = 0;
        if(pending.length()!=0)
        {
            last = pending.codePointAt(pending.length()-1);

        }
		
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.content_layout_tvOne:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("1");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvTwo:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("2");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvThree:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("3");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvFour:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("4");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvFive:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("5");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSix:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("6");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSeven:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("7");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvEight:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("8");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvNine:
			if(pending.toString().length()>0){
				if(pending.toString().equals("0")){
					//清空pending
					pending.delete( 0, pending.length() );
				}
			}
			pending = pending.append("9");
			if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
			etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvZero:
			if(pending.toString().equals("0.0")){
				return;
			}
        	pending = pending.append("0");
        	if (pending.toString().contains(".")) {
	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	            	pending = pending.deleteCharAt(pending.length()-1);  
	            }
	        }
        	//输入内容头为0的情况下，只能输入小数点
        	if (pending.toString().startsWith("0") && pending.toString().trim().length() > 1) {
                if (!pending.toString().substring(1, 2).equals(".")) {
                	pending = pending.deleteCharAt(pending.length()-1); 
                    return;
                }
            }
        	etSumMoney.setText("￥"+pending);
			break;
		case R.id.content_layout_tvSpot:
			if(pending.length()>0){
				if (judje1()) {
	                pending = pending.append(".");
	                if (pending.toString().contains(".")) {
	    	            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
	    	            	//如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
	    	            	pending = pending.deleteCharAt(pending.length()-1);  
	    	            }
	    	        }
	                etSumMoney.setText("￥"+pending);
	            }
			}
			
			break;
		case R.id.content_layout_imagEliminate:
			//删除
			if (pending.length() != 0) {
                pending = pending.delete(pending.length() - 1, pending.length());
                etSumMoney.setText("￥"+pending);
                if(pending.toString().equals("0")){
                	//清空pending
                	pending.delete( 0, pending.length() );
                }
                if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
            }
			//清空
//			pending = pending.delete(0, pending.length());
//			tvSumMoney.setText(pending);
			break;
		case R.id.card_pay_tvOk://确认
			try {
				etTextStr = pending.toString();
				Log.e("输入框文本text值：", etTextStr);
				if(!etTextStr.equals(".")){
					
					Log.e("输入框文本text值：", pending.toString());
					total_feeStr = DecimalUtil.scaleNumber(etTextStr);
					if(DecimalUtil.isEqual(total_feeStr)==1){
				    	XingPosServicePay(authType,total_feeStr);
				    }else{
				    	Toast.makeText(getContext(), "请输出有效金额！", Toast.LENGTH_LONG).show();
				    }
					
				}else{
					Log.e("输入金额有误！", "false");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		
		}
	}

	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail支付返回信息：", txndetail);
			try {
				JSONObject job = new JSONObject(txndetail);
				transamount = job.getString("transamount");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Gson gjson  =  GsonUtils.getGson();
			CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);

			savaOrderPrintText(posResult);



			String transamountStr = transamount;
			Log.e("服务环境：", "生产环境");
			Log.e("返回的支付金额信息222：", transamountStr);
			//播放语音
//						   speak(transamountStr);
//						   intentToActivity(transamountStr);

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
		String authorizationCodeStr = bundle.getString("authorizationCode");//授权码
		String merchantldStr = bundle.getString("merchantld");//商户号
		String terminalldStr = bundle.getString("terminalld");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名称

		Log.e("返回的支付金额信息：", amountStr);
		String totalStr = DecimalUtil.branchToElement(amountStr);

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
		posResult.setAuthcode(authorizationCodeStr);//授权码
		posResult.setRefernumber(referenceNoStr);
		posResult.setTranslocaldate(dateStr);
		posResult.setTranslocaltime(timeStr);
		posResult.setTransamount(totalStr);//此处金额是分转元之后的金额

		savaOrderPrintText(posResult);

	}

	/**
	 * 保存打印信息(重打印数据)
	 */
	private void savaOrderPrintText(CardPaymentDate posResult){
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
		if(authType.equals("1")){


			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
			Boolean cardPayValue = true;
			int cardOption = 1;
			sharedPreferencesUtil.put("cardPayYes", cardPayValue);
			sharedPreferencesUtil.put("cardPayType", "pay");
			sharedPreferencesUtil.put("cardOption", cardOption);
			if(posProvider.equals(NEW_LAND)){
				//清空StringBuilder，EditText恢复初始值
				//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
					etSumMoney.setText("￥0.00");
				}
			}else{
				finish();
			}
		}else if(authType.equals("2")){
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
			Boolean cardPayValue = true;
			int cardOption = 2;
			sharedPreferencesUtil.put("cardPayYes", cardPayValue);
			sharedPreferencesUtil.put("cardPayType", "pay");
			sharedPreferencesUtil.put("cardOption", cardOption);
			finish();
		}else if(authType.equals("3")){
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
			Boolean cardPayValue = true;
			int cardOption = 3;
			sharedPreferencesUtil.put("cardPayYes", cardPayValue);
			sharedPreferencesUtil.put("cardPayType", "pay");
			sharedPreferencesUtil.put("cardOption", cardOption);
			finish();
		}else if(authType.equals("4")){
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext(), "cardPay");
			Boolean cardPayValue = true;
			int cardOption = 4;
			sharedPreferencesUtil.put("cardPayYes", cardPayValue);
			sharedPreferencesUtil.put("cardPayType", "pay");
			sharedPreferencesUtil.put("cardOption", cardOption);
			finish();
		}else{
			finish();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			Bundle bundle=data.getExtras(); 
			
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
					 String reason = bundle.getString("reason");
					 Log.e("失败返回值", reason);
					 if(posProvider.equals(NEW_LAND)){

					 }else if(posProvider.equals(FUYOU_SF)){
						 if (Utils.isNotEmpty(reason)) {
							 if(reason.equals("交易流水满,请结算")){
//								Toast.makeText(this, "交易流水已满，请结算啊啊啊啊啊", 0*100).show();
//								 //调结算SDK（清空终端流水）
//								 emptyPosOrder();
							 }
						 }
					 }
				    //清空StringBuilder，EditText恢复初始值
					//清空EditText
					 if(posProvider.equals(NEW_LAND)){
						 if(authType.equals("1")){
							 pending.delete( 0, pending.length() );
							 if(pending.length()<=0){
								 etSumMoney.setText("￥0.00");
							 }
						 }else if(authType.equals("2")||authType.equals("3")||authType.equals("4")){
							 finish();
						 }else{
							 finish();
						 }
					 }else if(posProvider.equals(FUYOU_SF)){
						 finish();
					 }

				     break;
				
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
