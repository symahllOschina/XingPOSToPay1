package com.wanding.xingpos.bean;

/**
 * Time: 2019/12/16
 * Author:Administrator
 * Description: 预授权支付中轮询订单状态请求参数
 */
public class AuthRefreshOrderStateReqData {

    /**
     * 商户号
     **/
    private String merchant_no;

    /**
     * 终端号
     **/
    private String terminal_id;

    /**
     * 终端流水号
     **/
    private String terminal_trace;

    /**
     * 终端交易时间
     **/
    private String terminal_time;

    /**
     * 订单号，查询凭据，利楚订单号、微信订单号、支付宝订单号任意一个
     **/
    private String out_trade_no;

    public AuthRefreshOrderStateReqData() {
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

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
}
