package com.wanding.xingpos.payutil;

import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.RandomStringGenerator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 调用新大陆POS内置服务接口，支付，查询业务类
 */
public class NewPosServiceUtil {

	public static final int PAY_REQUEST_CODE = 1;
	
	//交易操作请求为0200 	返回为0210，查询操作请求为0300，返回为0310， 固定值，只区分请求和应答．
	private String msg_tp = "0200";	
	private String 	pay_tp = "1";//	""-不限 0-银行卡 1-扫码 11-微信支付 12-支付宝支付
	private String 	proc_tp = "00";	//交易类型：00表示消费类型，一般只传00
	//详见交易处理码:000000 	消费 ,200000 消费撤销（仅限当日）,660000 	扫码支付,680000 扫码撤销,900000 	结算
	private String 	proc_cd = "660000"; 
	private String systraceno = "";//非必填 消费撤销时，传入做撤销
	private String amt = ""; 	//消费、预授权、扫码支付时必填示例：100.01,扫码退货如该字段传入值为空，则默认全额退款。
	private String order_no = "";//订单号
	private String batchbillno = "";//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
	private String appid = "";//应用包名
	private String time_stamp = "";//交易成功返回交易时间
	private String print_info = "";//要打印备注信息
	private String reason = "";
	private String txndetail = "";//交易详情
	private String cardtype = "";//交易成功返回卡类型00:借记卡 01:贷记卡 02:准贷记卡 03:预付卡 04:其他
	
	/**
	 * 支付
	 * payType:支付类型，默认不限，0银行卡，1扫码，11微信，12支付宝，13银联
	 * 
	 */
	public static void payReq(Activity activity,String payType,String total_fee,String order_noStr,UserLoginResData posPublicData){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
		  //String payType = "";//银行卡，微信，支付宝，银联二维码分别顺序对应：040,010,020,030
		    if(payType.equals("040")){
		    	bundle.putString("pay_tp",  "0");//银行卡
		    	bundle.putString("proc_cd",  "000000");
		    }else if(payType.equals("010")){
		    	bundle.putString("pay_tp",  "11");//微信
		    	bundle.putString("proc_cd",  "660000");
		    }else if(payType.equals("020")){
		    	bundle.putString("proc_cd",  "660000");
		    	bundle.putString("pay_tp",  "12");//支付宝
		    }else if(payType.equals("030")){
		    	bundle.putString("pay_tp",  "13");//银联
		    	bundle.putString("proc_cd",  "660000");
		    } 
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    //设备号
		    bundle.putString("order_no",  order_noStr);
		    Log.e("生成的订单号：", order_noStr);
		    bundle.putString("batchbillno", "");//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
		    //TODO:
			Log.e("Exception：", "异常");
		}
	}
	
	/**
	 * 扫码退款
	 * total_fee: 退款金额
	 */
	public static void refundReq(Activity activity,String etOrderIdTextStr,String total_fee,UserLoginResData posPublicData){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
		    bundle.putString("pay_tp",  "");
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("proc_cd",  "680000");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    bundle.putString("order_no",  etOrderIdTextStr);
		    bundle.putString("batchbillno", "");
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
		    //TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 刷卡退款
	 * etOrderIdTextStr: 凭证号，不是订单号
	 */
	public static void cardRefundReq(Activity activity,String etOrderIdTextStr){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0200");
			bundle.putString("pay_tp",  "");
			bundle.putString("proc_tp",  "00");
			
			bundle.putString("proc_cd",  "200000");
			bundle.putString("systraceno",  etOrderIdTextStr);
			bundle.putString("amt",  "");
			//订单号：退款不传
			bundle.putString("order_no",  "");
			//流水号：凭证号（只有退款请求时输入）
			bundle.putString("batchbillno", "");
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 扫码查询
	 */
	public static void scanQueryReq(Activity activity,String etOrderIdTextStr,UserLoginResData posPublicData){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0300");
			bundle.putString("pay_tp",  "1");
			//订单号：
			bundle.putString("order_no",  etOrderIdTextStr);
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	/**
	 * 扫码查询
	 */
	public static void cardQueryReq(Activity activity,String etOrderIdTextStr,UserLoginResData posPublicData){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp",  "0300");
			bundle.putString("pay_tp",  "0");
			//订单号：
			bundle.putString("order_no",  etOrderIdTextStr);
			bundle.putString("appid",     "com.wanding.xingpos");
			bundle.putString("reason",     "");
			bundle.putString("txndetail",     "");
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
			//TODO:
			Log.e("Exception：", "异常");
		}
	}
	
	
	/**
	 * 预授权300000,预授权完成-330000, 预授权撤销-400000 ,预授权完成撤销-440000
	 * 
	 */
	public static void authReq(Activity activity,String type,String total_fee,String order_noStr){
		try {
		    ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
		    Intent intent = new Intent();
		    intent.setComponent(component);
		    Bundle bundle = new Bundle();
		    bundle.putString("msg_tp",  "0200");
	    	bundle.putString("pay_tp",  "1");
		    if(type.equals("1")){
		    	bundle.putString("proc_cd",  "300000");
		    }else if(type.equals("2")){
				bundle.putString("proc_cd",  "400000");
		    }else if(type.equals("3")){
				bundle.putString("proc_cd",  "330000");
		    }else if(type.equals("4")){
		    	bundle.putString("proc_cd",  "440000");
		    } 
		    bundle.putString("proc_tp",  "00");
		    bundle.putString("systraceno",  "");
		    bundle.putString("amt",  total_fee);
		    //设备号
		    bundle.putString("order_no",  order_noStr);
		    Log.e("生成的订单号：", order_noStr);
		    bundle.putString("batchbillno", "");//流水号：batchbillno=批次号+凭证号（只有退款请求时输入）
		    bundle.putString("appid",     "com.wanding.xingpos");
		    bundle.putString("reason",     "");
		    bundle.putString("txndetail",     "");
		    intent.putExtras(bundle);
		    activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
		    //TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
		    //TODO:
			Log.e("Exception:","异常");

		}
	}

	/**
	 * 结算
	 *
	 */
	public static void settleReq(Activity activity){
		try {
			ComponentName component = new ComponentName("com.newland.caishen", "com.newland.caishen.ui.activity.MainActivity");
			Intent intent = new Intent();
			intent.setComponent(component);
			Bundle bundle = new Bundle();
			bundle.putString("msg_tp", "0200");
			bundle.putString("proc_tp", "00");
			bundle.putString("proc_cd", "900000");
			bundle.putString("appid", "com.wanding.xingpos");//
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, PAY_REQUEST_CODE);
		} catch(ActivityNotFoundException e) {
			e.printStackTrace();
			//TODO:
			Log.e("NotFoundException：", "找不到界面");
		} catch(Exception e) {
			e.printStackTrace();
			//TODO:
			Log.e("Exception:","异常");

		}
	}
}
