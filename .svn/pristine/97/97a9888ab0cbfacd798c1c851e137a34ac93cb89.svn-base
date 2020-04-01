package com.wanding.xingpos.payutil;

import android.util.Log;


import com.wanding.xingpos.bean.AuthConfirmReqDate;
import com.wanding.xingpos.bean.AuthRecodeListReqData;
import com.wanding.xingpos.bean.AuthRefreshOrderStateReqData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WdPreAuthHistoryVO;
import com.wanding.xingpos.util.DateFormatUtils;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.RandomStringGenerator;

import java.util.Date;
import java.util.Map;

/**
 * 支付参数封装
 */
public class QueryParamsReqUtil {

    private static final String TAG = "PayParamsReqUtil";

    private static String pay_ver = "100";


    /**
     * 预授权记录查询
     */
    public static AuthRecodeListReqData queryAuthRecodeListReq(int pageNum, int pageNumCount, UserLoginResData userBean,
                                                               String orderId, String startTime, String endTime){
    AuthRecodeListReqData reqData = new AuthRecodeListReqData();


        reqData.setPageNumber(pageNum+"");
        reqData.setPageSize(pageNumCount+"");
        reqData.setTerminal_id(userBean.getTerminal_id());
        reqData.setOut_trade_no(orderId);
        reqData.setStartTime(startTime);
        reqData.setEndTime(endTime);
        return reqData;
    }


    /**
     *  预授权交易详情
     */
    public static AuthConfirmReqDate authOrderDetailReq(UserLoginResData userBean, WdPreAuthHistoryVO order){
        AuthConfirmReqDate request = new AuthConfirmReqDate();

        //terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
        String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

        String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());

        request.setPay_ver(pay_ver);
        request.setService_id("014");
        request.setMerchant_no(userBean.getMerchant_no());
        request.setTerminal_id(userBean.getTerminal_id());
        request.setTerminal_trace(terminal_traceStr);
        request.setTerminal_time(terminal_timeStr);

        request.setOut_trade_no(order.getMchntOrderNo());

        //参数加签
        Log.e("参数:",FastJsonUtil.toJSONString(request));
        Map<String, Object> map = request.toMap();
        Log.e("toMap参数:",map.toString());
        String mapStr = FacePayUtils.getSign(map,userBean.getAccess_token());
        request.setKey_sign(mapStr);
        return request;
    }

    /**
     * 轮询获取订单状态
     */
    public static AuthRefreshOrderStateReqData refreshOrderStateReq(UserLoginResData userBean, String orderId){
        AuthRefreshOrderStateReqData reqData = new AuthRefreshOrderStateReqData();



        reqData.setMerchant_no(userBean.getMerchant_no());
        reqData.setTerminal_id(userBean.getTerminal_id());
        String terminal_traceStr = RandomStringGenerator.getAFRandomNum();
        String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
        reqData.setTerminal_trace(terminal_traceStr);
        reqData.setTerminal_time(terminal_timeStr);
        reqData.setOut_trade_no(orderId);
        return reqData;
    }

}
