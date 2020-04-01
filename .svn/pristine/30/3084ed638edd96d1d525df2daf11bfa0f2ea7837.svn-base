package com.wanding.xingpos.bean;



import java.io.Serializable;
import java.util.Date;

/**
 * 预授权操作历史记录对象 wd_pre_auth_history
 * 
 * @author ruoyi
 * @date 2019-09-10
 */
public class WdPreAuthHistoryVO implements Serializable
{


    /** 支付方式, */
    //(name = "支付方式,")
    private String preWay;

    /** 支付类型 WX_JSAPI, WX_APP, WX_MICRO,WX_FACE */
    //(name = "支付类型 WX_JSAPI, WX_APP, WX_MICRO,WX_FACE")
    private String preType;

    /** 预授权状态类型： 1 预授权 2撤销 3押金消费 4押金退款 */
    //(name = "预授权状态类型： 1 预授权 2撤销 3押金消费 4押金退款")
    private String payAuthStatus;

    /** 0/1/2分别代表初始、成功、失败 */
    //(name = "0/1/2/5分别代表初始、成功、失败",支付中)
    private Integer status;

    /** 请求商户订单号 */
    //(name = "请求商户订单号")
    private String mchntOrderNo;

    /** 原始流水 */
    //(name = "原始流水")
    private String orgOrderNo;

    /** 渠道订单号 */
    //(name = "渠道订单号")
    private String channelOrderNo;

    /** 支付时间 */
    //(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private long preTime;


    /** 终端号(没有真实终端号统一填88888888) */
    //(name = "终端号(没有真实终端号统一填88888888)")
    private String termId;

    /** 终端IP地址 */
    //(name = "终端IP地址")
    private String termIp;

    /** 商品描述, 商品或支付单简要描述 */
    //(name = "商品描述, 商品或支付单简要描述")
    private String goodsdes;


    /** 订单总金额, 单位为元小数 */
    //(name = "订单金额, 单位为分")
    private String orderAmt;


    /** 凭证号 终端请求号码 */
    //(name = "凭证号")
    private String terminalTrace;

    /** 退款状态 0退款失败 1退款成功 2 退款中 */
    //(name = "退款状态 0退款失败 1退款成功 2 退款中")
    private String refundStatus;


    /** 优惠金额（分） */
    //(name = "优惠金额", readConverterExp = "分=")
    private String resCouponFee;

    /** 付款金额(分) 订单总金额，单位为分，只能为整数 */
    //(name = "付款金额(分) 订单总金额，单位为分，只能为整数")
    private String payAmount;

    /** 第三方押金退费订单号 */
    //(name = "第三方押金退费订单号")
    private String channelRefundId;

    /** 消费金额 订单总金额，单位为分，只能为整数 */
    //(name = "消费金额 订单总金额，单位为分，只能为整数")
    private String consumeFee;

    /** 退款金额，金额分 */
    //(name = "退款金额，金额分")
    private String refundFee;


    /** 创建时间 */
    //(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private long gmtCreate;

    /** 修改时间 */
    //(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private long gmtModified;

    /** 预留字段 */
    //(name = "预留字段")
    private String reserved1;

    /** 预留字段 */
    //(name = "预留字段")
    private String reserved2;

    /**
     * 交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss
     */
    //(name = "交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss", width = 30, dateFormat = "yyyy-MM-dd")
    private long txnBeginTs;

    /**
     * 交易结束时间, 订单生成时间，格式为yyyyMMddHHmmss
     */
    //(name = "交易结束时间, 订单生成时间，格式为yyyyMMddHHmmss", width = 30, dateFormat = "yyyy-MM-dd")
    private long txnEndTs;

    public WdPreAuthHistoryVO() {
    }

    public String getPreWay() {
        return preWay;
    }

    public void setPreWay(String preWay) {
        this.preWay = preWay;
    }

    public String getPreType() {
        return preType;
    }

    public void setPreType(String preType) {
        this.preType = preType;
    }

    public String getPayAuthStatus() {
        return payAuthStatus;
    }

    public void setPayAuthStatus(String payAuthStatus) {
        this.payAuthStatus = payAuthStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMchntOrderNo() {
        return mchntOrderNo;
    }

    public void setMchntOrderNo(String mchntOrderNo) {
        this.mchntOrderNo = mchntOrderNo;
    }

    public String getOrgOrderNo() {
        return orgOrderNo;
    }

    public void setOrgOrderNo(String orgOrderNo) {
        this.orgOrderNo = orgOrderNo;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public long getPreTime() {
        return preTime;
    }

    public void setPreTime(long preTime) {
        this.preTime = preTime;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTermIp() {
        return termIp;
    }

    public void setTermIp(String termIp) {
        this.termIp = termIp;
    }

    public String getGoodsdes() {
        return goodsdes;
    }

    public void setGoodsdes(String goodsdes) {
        this.goodsdes = goodsdes;
    }

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getTerminalTrace() {
        return terminalTrace;
    }

    public void setTerminalTrace(String terminalTrace) {
        this.terminalTrace = terminalTrace;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getResCouponFee() {
        return resCouponFee;
    }

    public void setResCouponFee(String resCouponFee) {
        this.resCouponFee = resCouponFee;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getChannelRefundId() {
        return channelRefundId;
    }

    public void setChannelRefundId(String channelRefundId) {
        this.channelRefundId = channelRefundId;
    }

    public String getConsumeFee() {
        return consumeFee;
    }

    public void setConsumeFee(String consumeFee) {
        this.consumeFee = consumeFee;
    }

    public String getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(String refundFee) {
        this.refundFee = refundFee;
    }

    public long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public long getTxnBeginTs() {
        return txnBeginTs;
    }

    public void setTxnBeginTs(long txnBeginTs) {
        this.txnBeginTs = txnBeginTs;
    }

    public long getTxnEndTs() {
        return txnEndTs;
    }

    public void setTxnEndTs(long txnEndTs) {
        this.txnEndTs = txnEndTs;
    }
}
