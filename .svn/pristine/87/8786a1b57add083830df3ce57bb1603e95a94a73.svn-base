package com.wanding.xingpos.bean;


import com.wanding.xingpos.util.MD5;

import java.io.Serializable;

public class PosMemConsumeUpdateOrderRespData implements Serializable {

    private String return_code;//响应码

    private String return_msg;//返回信息提示


    private String result_code;//业务结果


    private String pay_type;//请求类型

    private String out_trade_no;//唯一订单号

    private String terminal_time;//终端交易时间

    private String total_fee;//金额

    private String memberCode;//会员卡号

    private String receiveTicketUrl;

    private String key_sign;//签名检验串

    public String getSignStr(String access_token) {
        final StringBuilder sb = new StringBuilder("");
        sb.append("return_code=").append(return_code).append("&");
        sb.append("return_msg=").append(return_msg).append("&");
        sb.append("result_code=").append(result_code).append("&");
        sb.append("pay_type=").append(pay_type).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        sb.append("terminal_time='").append(terminal_time).append("&");
        sb.append("total_fee=").append(total_fee).append("&");
        sb.append("receiveTicketUrl=").append(receiveTicketUrl).append("&");
        sb.append("access_token=").append(access_token);
        String keySign = MD5.MD5Encode(sb.toString());
        return keySign;
    }

    public PosMemConsumeUpdateOrderRespData() {
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

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTerminal_time() {
        return terminal_time;
    }

    public void setTerminal_time(String terminal_time) {
        this.terminal_time = terminal_time;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getReceiveTicketUrl() {
        return receiveTicketUrl;
    }

    public void setReceiveTicketUrl(String receiveTicketUrl) {
        this.receiveTicketUrl = receiveTicketUrl;
    }

    public String getKey_sign() {
        return key_sign;
    }

    public void setKey_sign(String key_sign) {
        this.key_sign = key_sign;
    }
}
