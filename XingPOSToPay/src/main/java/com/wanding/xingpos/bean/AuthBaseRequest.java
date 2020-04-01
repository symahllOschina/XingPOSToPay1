package com.wanding.xingpos.bean;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Description 统一对外来请求参数接收
 * @author baijiao
 * 2019年9月12日
 */
public class AuthBaseRequest  implements Serializable {



	private String pay_ver;//版本号

	private String pay_type;//请求类型

	private String service_id ;//接口类型

	private String auth_code ;//扫码授权码

	private String face_code ;//刷脸授权码

	private String openid ;//

	private String merchant_no;//商户号

	private String terminal_id;//终端号

	private String terminal_trace;//终端流水号

	private String terminal_time;//终端交易时间

	private String refund_fee;//退款金额

	private String total_fee;//订单金额

	private String consume_amount; //押金消费金额

	private String out_trade_no;//订单号，查询凭据，利楚订单号、微信订单号、支付宝订单号任意一个

	private String operator_id;//操作员号

	private String order_body;//订单描述

	private String key_sign; //签名检验串

	/**
	 * 设备类型：machineType
	 */
	private String machineType;






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



	public String getPay_ver() {
		return pay_ver;
	}

	public void setPay_ver(String pay_ver) {
		this.pay_ver = pay_ver;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getFace_code() {
		return face_code;
	}

	public void setFace_code(String face_code) {
		this.face_code = face_code;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}


	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getTerminal_trace() {
		return terminal_trace;
	}

	public void setTerminal_trace(String terminal_trace) {
		this.terminal_trace = terminal_trace;
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

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getConsume_amount() {
		return consume_amount;
	}

	public void setConsume_amount(String consume_amount) {
		this.consume_amount = consume_amount;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getOrder_body() {
		return order_body;
	}

	public void setOrder_body(String order_body) {
		this.order_body = order_body;
	}

	public String getKey_sign() {
		return key_sign;
	}

	public void setKey_sign(String key_sign) {
		this.key_sign = key_sign;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
}
