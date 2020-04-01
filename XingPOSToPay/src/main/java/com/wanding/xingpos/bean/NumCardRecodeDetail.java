package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.util.List;

/**
 *  核销记录
 */
public class NumCardRecodeDetail implements Serializable {

    /**
     * 计次卡
     */
    private int surplus_total;//: 3,
    private String code;//: "00435251745557",
    private long creat_time;//: 1563448888000,
    private long use_time;//: 1563448888000,
    private String openid;//: "oMe3y0CZ6N54GC7sjWvNylVJs5KA",
    private String card_name;//: "会员计次卡",
    private String name;//: "yearn.",
    private String reserve;//: "53301936545242",
    private int use_total;//: 1,
    private int id;//: 105,
    private String card_id;//: "7120190717142642247767498026",
    private String status;//: "2"





    public NumCardRecodeDetail() {

    }


    public int getSurplus_total() {
        return surplus_total;
    }

    public void setSurplus_total(int surplus_total) {
        this.surplus_total = surplus_total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(long creat_time) {
        this.creat_time = creat_time;
    }

    public long getUse_time() {
        return use_time;
    }

    public void setUse_time(long use_time) {
        this.use_time = use_time;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public int getUse_total() {
        return use_total;
    }

    public void setUse_total(int use_total) {
        this.use_total = use_total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
