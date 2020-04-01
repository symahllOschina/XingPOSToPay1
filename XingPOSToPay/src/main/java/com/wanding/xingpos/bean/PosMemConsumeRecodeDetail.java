package com.wanding.xingpos.bean;


import java.io.Serializable;
/**
 * 会员消费记录实体
 */
public class PosMemConsumeRecodeDetail implements Serializable {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 金额
     */
    private String totalFee;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 会员卡号
     */
    private String memCode;

    /**
     * 订单显示状态
     */
    private String displayStatus;

    private String createTime;

    /**
     * 支付方式
     */
    private String payWay;

    private String imgUrl;

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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }
}
