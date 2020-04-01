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
public class AuthConfirmReqDate implements Serializable {


	private String pay_ver;//版本号

	private String service_id ;//接口类型

	private String merchant_name;//商户名称

	private String merchant_no;//商户号



	private String terminal_id;//终端号

	private String terminal_trace;//终端流水号

	private String terminal_time;//终端交易时间


	private String consume_amount; //押金消费金额

	/**
	 * 预授权订单号参数
	 */
	private String out_trade_no;//订单号，查询凭据，利楚订单号、微信订单号、支付宝订单号任意一个



	/**
	 * 预授权完成撤销退款金额参数
	 */
	private String refund_fee;

	private String channel_trade_no;//通道订单号，微信订单号、支付宝订单号等

	private String attach;

	private String key_sign; //签名检验串

	/**
	 * 设备类型：machineType
	 */
	private String machineType;


	public AuthConfirmReqDate() {
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

	public String getPay_ver() {
		return pay_ver;
	}

	public void setPay_ver(String pay_ver) {
		this.pay_ver = pay_ver;
	}

	public String getService_id() {
		return service_id;
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
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

	public String getChannel_trade_no() {
		return channel_trade_no;
	}

	public void setChannel_trade_no(String channel_trade_no) {
		this.channel_trade_no = channel_trade_no;
	}

	public String getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
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
