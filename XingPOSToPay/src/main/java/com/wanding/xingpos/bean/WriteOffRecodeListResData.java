package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.util.List;

/**
 *  核销记录
 */
public class WriteOffRecodeListResData implements Serializable {

    private int total;
    private List<WriteOffRecodeDetailResData> CouponList;//


    public WriteOffRecodeListResData() {

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<WriteOffRecodeDetailResData> getCouponList() {
        return CouponList;
    }

    public void setCouponList(List<WriteOffRecodeDetailResData> CouponList) {
        this.CouponList = CouponList;
    }


}
