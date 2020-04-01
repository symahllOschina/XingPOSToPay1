package com.wanding.face.bean;

import java.io.Serializable;

/**
 * Time: 2019/12/16
 * Author:Administrator
 * Description: 预授权支付中轮询订单状态返回
 */
public class AuthRefreshOrderStateResData implements Serializable {

    /**
     *
     **/
    private String return_code;

    /**
     *
     **/
    private String return_msg;
    /**
     *
     **/
    private String result_code;

    /**
     *
     **/
    private String result_msg;

    /**
     * 订单号
     **/
    private String out_trade_no;

    /**
     * 第三方渠道单号
     **/
    private String channel_trade_no;


    /**
     *
     **/
    private String merchant_name;


    /**
     * 终端号
     **/
    private String terminal_id;

    /**
     * 预授权状态
     **/
    private String payAuthStatus;

    /**
     *
     **/
    private String pay_type;

    /**
     * 金额（单位元）
     **/
    private String total_amount;

    /**
     * 支付时间
     **/
    private String end_time;

    /**
     * 商品描述
     **/
    private String goodsdes;








    public AuthRefreshOrderStateResData() {
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

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getPayAuthStatus() {
        return payAuthStatus;
    }

    public void setPayAuthStatus(String payAuthStatus) {
        this.payAuthStatus = payAuthStatus;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getGoodsdes() {
        return goodsdes;
    }

    public void setGoodsdes(String goodsdes) {
        this.goodsdes = goodsdes;
    }
}
