package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 *  预授权记录查询请求实体
 */
public class AuthRecodeListReqData implements Serializable {

    /**
     * 请求页下标
     */
    private String pageSize;

    /**
     * 请求页数
     */
    private String pageNumber;

    /**
     * 终端号
     */
    private String terminal_id;

    /**
     *  查询订单号（授权号）
     */
    private String out_trade_no;

    /**
     * 查询起始时间
     */
    private String startTime;

    /**
     * 查询结束时间
     */
    private String endTime;


    public AuthRecodeListReqData() {
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTerminal_id() {
        return terminal_id;
    }

    public void setTerminal_id(String terminal_id) {
        this.terminal_id = terminal_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
