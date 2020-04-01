package com.wanding.xingpos.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.MySerialize;

import java.io.IOException;
import java.util.Arrays;

/** 
 * 分期Activity
 */
public class ByStagesActivity extends BaseActivity implements OnClickListener{
	
	private Context context = ByStagesActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	private EditText etSumMoney;
	private ImageButton imagEliminate;
	private TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight,tvNine,tvZero,tvSpot;
	private TextView tvOk;
	private StringBuilder pending = new StringBuilder();
	
	private String authType = "";
	

	
	private UserLoginResData posPublicData;
	

    

    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;
    private static final String TAG = "CardPayActivity";

  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_pay_activity);
		initView();
		initData();
		synthesizer = MainActivity.synthesizer;

	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
		
		
		
		tvTitle.setText("分期");
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

				//清空EditText
				pending.delete( 0, pending.length() );
				if(pending.length()<=0){
                	etSumMoney.setText("￥0.00");
                }
				break;
			}
		};
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
				if(!etTextStr.equals(".")){
					Log.e("输入框文本text值：", etTextStr);
					total_feeStr = DecimalUtil.scaleNumber(etTextStr);
					double dou_total_feeStr = Double.valueOf(total_feeStr);
					if(DecimalUtil.isRange(dou_total_feeStr,600,50000)){
						String total_moneyStr = DecimalUtil.elementToBranch(total_feeStr);
						Log.e("转换分字符串：",total_moneyStr);
						int int_total_moneyStr = Integer.valueOf(total_moneyStr);
						Log.e("最终提交金额：",int_total_moneyStr+"");
						in = new Intent();
						in.putExtra("txnAmt", int_total_moneyStr);
						in.putExtra("merchantId","85731017011L660");
						in.setClassName(ByStagesActivity.this, "com.newland.starpos.installmentsdk.MainActivity");
						startActivity(in);
					}else{
						Toast.makeText(getContext(), "分期金额最低600，最高50000", Toast.LENGTH_LONG).show();
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
	
	
	

}
