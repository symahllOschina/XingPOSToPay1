package com.wanding.xingpos.bean;


import com.wanding.xingpos.util.MD5;

import java.io.Serializable;

public class PosMemConsumeUpdateOrderReqData implements Serializable {

    private String pay_ver = "100";//版本号

    private String pay_type;//请求类型

    private String service_id = "010";//接口类型

    private String merchant_no;//商户号

    private String terminal_id;//终端号

    private String memberCode;//会员卡号

    private String out_trade_no;//唯一订单号

    private String terminal_trace;//交易流水号

    private String key_sign; //签名检验串

    private String order_body;

    public PosMemConsumeUpdateOrderReqData() {
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

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTerminal_trace() {
        return terminal_trace;
    }

    public void setTerminal_trace(String terminal_trace) {
        this.terminal_trace = terminal_trace;
    }

    public String getKey_sign() {
        return key_sign;
    }

    public void setKey_sign(String key_sign) {
        this.key_sign = key_sign;
    }

    public String getOrder_body() {
        return order_body;
    }

    public void setOrder_body(String order_body) {
        this.order_body = order_body;
    }

    public String getSignStr(String access_token) {
        final StringBuilder sb = new StringBuilder("");
        sb.append("pay_ver=").append(pay_ver).append("&");
        sb.append("pay_type=").append(pay_type).append("&");
        sb.append("service_id=").append(service_id).append("&");
        sb.append("memberCode='").append(memberCode).append("&");
        sb.append("merchant_no=").append(merchant_no).append("&");
        sb.append("terminal_id=").append(terminal_id).append("&");
        sb.append("memberCode=").append(memberCode).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        sb.append("terminal_trace=").append(terminal_trace).append("&");
        sb.append("access_token=").append(access_token);
        String keySign = MD5.MD5Encode(sb.toString());
        return keySign;
    }

}
