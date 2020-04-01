package com.wanding.xingpos.instalment;

import android.util.Log;

import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.AppointRsaUtils;
import com.wanding.xingpos.util.DateFormatUtils;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.RandomStringGenerator;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

/**
 * 分期请求参数封装类
 */
public class RequestUtil {

    public static final String MD5KEY1 = "DLA0M8sP1kWpLHnpkqNOWMkba0RY5h0leMrkxiPwInzx651zE45xVaBXBufl8WWu";//测试
    public static final String MD5KEY2 = "MV1EadiWvJpXchOW4rFYOx8M5csHRl0J41VCA6F6qHTUZOYERI68BePDhoISb1ru";//生产

    public static String getMd5key(){
        if("true".equals(NitConfig.isTest)){
            return MD5KEY2;
        }else{
           return MD5KEY1;
        }
    }


    /**
     *
     *  查询参数（单个订单号查询）
     * orderIdStr:订单号
     * return : 返回的是经过公钥加密的txnData参数值
     *
     * 测试参数：orderid   2018050714291762349    orgNo  11658
     */
    public static String queryReqTxnData(UserLoginResData posPublicData,String orderIdStr, InputStream inPublic) throws Exception{
        //流水号
        String reqJnl = RandomStringGenerator.getSerialNum(15);//生成全数字随机数
        //日期时间
        String reqTime = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
        JSONObject userJSON = new JSONObject();
        if("true".equals(NitConfig.isTest)){
            userJSON.put("orgNo",posPublicData.getOrgNo_str_pos());
        }else{
            userJSON.put("orgNo","11658");
        }
        userJSON.put("reqJnl",reqJnl);
        userJSON.put("reqTime",reqTime);
        userJSON.put("reqType","6060006");//6060006:单个订单查询  6060007：申请退款
        userJSON.put("orderId",orderIdStr);
        String dataJson = String.valueOf(userJSON);
        Log.e("参数JSON:",dataJson);
        RSAPublicKey pubKey = AppointRsaUtils.loadPublicKey(inPublic);
        String txnData = AppointRsaUtils.encryptByPublicKey(dataJson, pubKey);

        return txnData;

    }

    /**
     *
     *  查询参数（订单列表查询）
     * orderIdStr:订单号
     * return : 返回的是经过公钥加密的txnData参数值
     *
     * 测试参数：orderid   2018050714291762349    orgNo  11658
     */
    public static String queryListReqTxnData(UserLoginResData posPublicData,String contractsState, String pageNum,String pageSize,String beginTime,String endTime,InputStream inPublic) throws Exception{
        //流水号
        String reqJnl = RandomStringGenerator.getSerialNum(15);//生成全数字随机数
        //日期时间
        String reqTime = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
        JSONObject userJSON = new JSONObject();
        if("true".equals(NitConfig.isTest)){
            userJSON.put("orgNo",posPublicData.getOrgNo_str_pos());
            userJSON.put("merchantId",posPublicData.getMercId_pos());//merchantId	 否	N15	新大陆处商户代码 85731017011L660
        }else{
            userJSON.put("orgNo","11658");
            userJSON.put("merchantId","85731017011L660");//merchantId	 否	N15	新大陆处商户代码 85731017011L660
        }
        userJSON.put("reqJnl",reqJnl);
        userJSON.put("reqTime",reqTime);
        userJSON.put("reqType","6060005");//6060005:订单列表查询
        userJSON.put("orderId","");
        userJSON.put("merName","");//merName	否	A1..100	商户名称
        userJSON.put("contractsState",contractsState);//contractsState	否	N1	0还款中，1结清，2退款中
        userJSON.put("pageNum",pageNum);//pageNum	否	N1..9999	当前页
        userJSON.put("pageSize",pageSize);//pageSize	否	N1..2	显示条数
        userJSON.put("beginTime",beginTime);//beginTime	是	yyyyMMddHHmmss	开始时间(时间戳)
        userJSON.put("endTime",endTime);//endTime	是	yyyyMMddHHmmss	结束时间(时间戳)
        userJSON.put("accName","");//accName	否	A1..32	持卡人姓名
        String dataJson = String.valueOf(userJSON);
        Log.e("参数JSON:",dataJson);
        RSAPublicKey pubKey = AppointRsaUtils.loadPublicKey(inPublic);
        String txnData = AppointRsaUtils.encryptByPublicKey(dataJson, pubKey);

        return txnData;

    }

    /**
     *
     *  申请退款参数
     * orderIdStr:订单号
     * return : 返回的是经过公钥加密的txnData参数值
     *
     * 测试参数：orderid   2018050714291762349    orgNo  11658
     */
    public static String refundReqTxnData(UserLoginResData posPublicData,String orderIdStr,InputStream inPublic) throws Exception{
        //流水号
        String reqJnl = RandomStringGenerator.getSerialNum(15);//生成全数字随机数
        //日期时间
        String reqTime = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
        JSONObject userJSON = new JSONObject();
        if("true".equals(NitConfig.isTest)){
            userJSON.put("orgNo",posPublicData.getOrgNo_str_pos());
            userJSON.put("merchantId",posPublicData.getMercId_pos());//merchantId	 否	N15	新大陆处商户代码 85731017011L660
        }else{
            userJSON.put("orgNo","11658");
            userJSON.put("merchantId","85731017011L660");//merchantId	 否	N15	新大陆处商户代码 85731017011L660
        }
        userJSON.put("reqJnl",reqJnl);
        userJSON.put("reqTime",reqTime);
        userJSON.put("reqType","6060007");//6060006:单个订单查询  6060007：申请退款
        userJSON.put("reqFlg","1");//商户信息上送标志1：小票商户号，2：门店号
        userJSON.put("orderId",orderIdStr);
        String dataJson = String.valueOf(userJSON);
        Log.e("参数JSON:",dataJson);
        RSAPublicKey pubKey = AppointRsaUtils.loadPublicKey(inPublic);
        String txnData = AppointRsaUtils.encryptByPublicKey(dataJson, pubKey);

        return txnData;
    }

    /**
     *
     *  上传参数
     * orderIdStr:订单号
     * return : 返回的是经过公钥加密的txnData参数值
     *
     * 测试参数：orderid   2018050714291762349    orgNo  11658
     */
    public static String upLoadReqTxnData(UserLoginResData posPublicData,String orderIdStr,String bitmapStr,InputStream inPublic) throws Exception{
        //流水号
        String reqJnl = RandomStringGenerator.getSerialNum(15);//生成全数字随机数
        //日期时间
        String reqTime = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
        JSONObject userJSON = new JSONObject();
        if("true".equals(NitConfig.isTest)){
            userJSON.put("orgNo",posPublicData.getOrgNo_str_pos());
        }else{
            userJSON.put("orgNo","11658");
        }
        userJSON.put("reqJnl",reqJnl);
        userJSON.put("reqTime",reqTime);
        userJSON.put("reqType","6060008");//6060008：退款凭证上传
        userJSON.put("orderId",orderIdStr);
        userJSON.put("imageValue",bitmapStr);//图片转换的二进制
        String dataJson = String.valueOf(userJSON);
        Log.e("参数JSON:",dataJson);
        RSAPublicKey pubKey = AppointRsaUtils.loadPublicKey(inPublic);
        String txnData = AppointRsaUtils.encryptByPublicKey(dataJson, pubKey);

        return txnData;
    }

    /**
     *  响应内容解密
     */
    public static String getResponseJsonStr(String respTxnData,InputStream inPublic)throws Exception{


        RSAPrivateKey priKey = AppointRsaUtils.loadPrivateKey(inPublic);
        String jsonStr = AppointRsaUtils.decryptByPrivateKey(respTxnData, priKey);

        return jsonStr;
    }


}
