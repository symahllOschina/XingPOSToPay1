package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author baijiao
 * @date  2019 09 11
 * 统一返回结果集，根据不同的接口， 给其对应的参数赋值。
 * 若有必须返回的参数字段其值为空，则赋值为空串（"" ）.
 * 最后经toMap（）方法处理，去掉为null的字段 做加密返回
 */
public class AuthResultResponse implements Serializable {





	/**
	 *接口调用结果  响应码：“01”成功 ，02”失败
	 */
	private String return_code;
	/**
	 *  返回信息提示，如“预支付成功”，“预支付失败“等
	 */
	private String return_msg;

	/**
	 * 业务结果
	 */
	private String result_code;

	/**
	*返回信息提示，如“预支付成功”，“预支付失败“等
	 */
	private String result_msg;

	/**
	 * 授权订单号
	 */
	private String auth_no;



	/**
	 * 万鼎唯一订单号(商户请求流水单号)
	 */
	private String out_trade_no;

	/**
	 * 第三方通道订单流水号
	 */
	private String channel_trade_no;


	/**
	 * 单位为：分（人民币），精确到小数点后两位
	 */
	private String total_amount;
	/**
	 * 单位为：分（人民币），精确到小数点后两位
	 */
	private String consume_fee;
	/**
	 * 支付方式
	 */
	private String pay_type;

	/**
	 * 预授权交易成功时间，格式需要为"yyyy-MM-dd HH:mm:ss"
	 */
	private String end_time;

	/**
	 * 预授权交易状态
	 */
	private String status;
	/**
	 * 商户号
	 */
	private String merchant_no;
	/**
	 * 商户名称
	 */
	private String merchant_name;
	/**
	 * 终端
	 */
	private String terminal_trace;

	/**
	 * 终端号
	 */
	private String terminal_id;
	/**
	 * 终端交易时间
	 */
	private String terminal_time;


	/**
	 * 退款金额
	 */
	private String refund_fee;
	/**
	 * 退款单号
	 */
	private String out_refund_no;


	/**
	 * 请求完成时间，格式"yyyy-MM-dd HH:mm:ss"
	 */
	private String timestamp;

	/**
	 * 随机字符串，长度要求在32位以内。
	 */
	private String nonceStr;

	private String sign;

	/**
	 * 业务扩展参数，用于特定业务信息的传递，json格式。
	 */
	private String extraParam;

	private String trade_status;


	/**
	 * 签名检验串
	 */
	private String key_sign;



	public AuthResultResponse() {
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			Object obj;
			try {
				obj = field.get(this);
				if (obj != null) {
					map.put(field.getName(), obj);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return map;
	}


	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}

	public String getAuth_no() {
		return auth_no;
	}

	public void setAuth_no(String auth_no) {
		this.auth_no = auth_no;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getChannel_trade_no() {
		return channel_trade_no;
	}

	public void setChannel_trade_no(String channel_trade_no) {
		this.channel_trade_no = channel_trade_no;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

	public String getConsume_fee() {
		return consume_fee;
	}

	public void setConsume_fee(String consume_fee) {
		this.consume_fee = consume_fee;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public String getTerminal_trace() {
		return terminal_trace;
	}

	public void setTerminal_trace(String terminal_trace) {
		this.terminal_trace = terminal_trace;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getTerminal_time() {
		return terminal_time;
	}

	public void setTerminal_time(String terminal_time) {
		this.terminal_time = terminal_time;
	}

	public String getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}

	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getExtraParam() {
		return extraParam;
	}

	public void setExtraParam(String extraParam) {
		this.extraParam = extraParam;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}

	public String getKey_sign() {
		return key_sign;
	}

	public void setKey_sign(String key_sign) {
		this.key_sign = key_sign;
	}


}
