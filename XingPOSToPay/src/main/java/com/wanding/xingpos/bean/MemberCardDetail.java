package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 * 会员卡信息
 */
public class MemberCardDetail implements Serializable {


    private int id;//: 11,
    private int mid;//: 2933,
    private int sid;//: null,
    private int eid;//null,
    private String card_id;//: "pyS0n1tOAosuM1YDRKRmscAsyyzM",
    private String product_name;//: "商品",
    private String small_url;//: "http://weixin.weupay.com/image/agent/0/2933/20190726c14xbqed9n.jpg",
    private String pic_url;//: null,
    /**
     * 会员卡现价
     * //: 0.02,
     */
    private String n_price;
    /**
     * 会员卡会员价
     * //: 1.00,
     */
    private String v_price;
    /**
     * 会员卡原价
     * //: 15.00,
     */
    private String o_price;
    private int stock;//: 200,
    private String status;//: "Y",
    private String sale_count;//: null,
    private String base_count;//: null,
    private String depict;//: "付费购买",
    private String rule;//: null,
    private String sort;//: null,
    private String isBuy;//: "Y",
    private int payRuleId;//: 7,
    private long creat_time;//: null,
    private long update_time;//: null,
    private int group_storeId;//: null,
    private String buy_read;//: null,
    private int card_level_id;//: null,
    private String card_level_name;//: null

    public MemberCardDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSmall_url() {
        return small_url;
    }

    public void setSmall_url(String small_url) {
        this.small_url = small_url;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getN_price() {
        return n_price;
    }

    public void setN_price(String n_price) {
        this.n_price = n_price;
    }

    public String getV_price() {
        return v_price;
    }

    public void setV_price(String v_price) {
        this.v_price = v_price;
    }

    public String getO_price() {
        return o_price;
    }

    public void setO_price(String o_price) {
        this.o_price = o_price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSale_count() {
        return sale_count;
    }

    public void setSale_count(String sale_count) {
        this.sale_count = sale_count;
    }

    public String getBase_count() {
        return base_count;
    }

    public void setBase_count(String base_count) {
        this.base_count = base_count;
    }

    public String getDepict() {
        return depict;
    }

    public void setDepict(String depict) {
        this.depict = depict;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(String isBuy) {
        this.isBuy = isBuy;
    }

    public int getPayRuleId() {
        return payRuleId;
    }

    public void setPayRuleId(int payRuleId) {
        this.payRuleId = payRuleId;
    }

    public long getCreat_time() {
        return creat_time;
    }

    public void setCreat_time(long creat_time) {
        this.creat_time = creat_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public int getGroup_storeId() {
        return group_storeId;
    }

    public void setGroup_storeId(int group_storeId) {
        this.group_storeId = group_storeId;
    }

    public String getBuy_read() {
        return buy_read;
    }

    public void setBuy_read(String buy_read) {
        this.buy_read = buy_read;
    }

    public int getCard_level_id() {
        return card_level_id;
    }

    public void setCard_level_id(int card_level_id) {
        this.card_level_id = card_level_id;
    }

    public String getCard_level_name() {
        return card_level_name;
    }

    public void setCard_level_name(String card_level_name) {
        this.card_level_name = card_level_name;
    }
}
