package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 查询预授权返回实体
 */
public class AuthRecodeListResData implements Serializable {

    private int pageNumber;//: 2,
    private int pageSize;//: 10,
    private int totalCount;//: 21
    private List<WdPreAuthHistoryVO> orderList;

    public AuthRecodeListResData() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<WdPreAuthHistoryVO> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<WdPreAuthHistoryVO> orderList) {
        this.orderList = orderList;
    }
}
