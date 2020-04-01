package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 * Time: 2019/11/19
 * Author:Administrator
 * Description: 付费购卡记录详情
 */
public class BuyCardRecodeDetail implements Serializable {

    /**
     * 订单号
     */
    private String orderId;// "84201910241142337250211442069497",
    /**
     * 金额（单位元）
     */
    private String totalFee;// "0.01",
    /**
     * ": null,
     */
    private String payWay;
    /**
     * "2019-11-18 10:25:38.0",
     */
    private String payTime;
    /**
     * "付费购卡",
     */
    private String memCode;
    /**
     * 购卡状态："0" 0 购卡成功，1 失败
     */
    private String displayStatus;
    /**
     * 创建时间："2019-11-18 10:25:38.0"
     */
    private String createTime;//

    /**
     * 领卡二维码
     */
    private String codeUrl;





    public BuyCardRecodeDetail() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getMemCode() {
        return memCode;
    }

    public void setMemCode(String memCode) {
        this.memCode = memCode;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }


}
