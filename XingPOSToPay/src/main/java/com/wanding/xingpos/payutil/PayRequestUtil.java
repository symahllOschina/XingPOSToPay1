package com.wanding.xingpos.payutil;

import android.util.Log;

import com.wanding.xingpos.bean.AuthBaseRequest;
import com.wanding.xingpos.bean.AuthConfirmReqDate;
import com.wanding.xingpos.bean.BuyCardRecodeReqData;
import com.wanding.xingpos.bean.CardVerificaRecodeReqData;
import com.wanding.xingpos.bean.PosDepositRecodeReqData;
import com.wanding.xingpos.bean.PosDepositReqData;
import com.wanding.xingpos.bean.PosMemConsumePreOrderReqData;
import com.wanding.xingpos.bean.PosMemConsumePreOrderRespData;
import com.wanding.xingpos.bean.PosMemConsumeRecodeReqData;
import com.wanding.xingpos.bean.PosMemConsumeReqData;
import com.wanding.xingpos.bean.PosMemConsumeUpdateOrderReqData;
import com.wanding.xingpos.bean.PosPayQueryReqData;
import com.wanding.xingpos.bean.PosRefundReqData;
import com.wanding.xingpos.bean.PosScanpayReqData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.DateFormatUtils;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.RandomStringGenerator;

import java.util.Date;
import java.util.Map;


/**
 * 二维码支付,退款,查询交易请求参数公共类
 */
public class PayRequestUtil {

	private static String pay_ver = "100";


    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private static final String NEW_LAND = "newland";
    private static final String FUYOU_SF= "fuyousf";
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“010”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * auth_no	授权码(二维码号)
	 * total_fee	金额，单位分
	 * order_body	订单描述
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosScanpayReqData payReq(String pay_type,String auth_no,String total_fee,UserLoginResData loginInitData,String posProvider){
		
		PosScanpayReqData posBean = new PosScanpayReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

        String terminal_traceStr = "";
    	if(posProvider.equals(NEW_LAND)){
            //terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
            terminal_traceStr = RandomStringGenerator.getAWRandomNum();
        }else if(posProvider.equals(FUYOU_SF)){
            terminal_traceStr = RandomStringGenerator.getAFRandomNum();
        }
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String auth_noStr = auth_no;
		String total_feeStr = total_fee;
		String order_bodyStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setAuth_no(auth_noStr);
		posBean.setTotal_fee(total_feeStr);
		posBean.setOrder_body(order_bodyStr);
		
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));


		
		return posBean;
	}
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“030”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端退款流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * refund_fee	退款金额，单位分
	 * out_trade_no	订单号，查询凭据，万鼎订单号、微信订单号、支付宝订单号任意一个
	 * operator_id	操作员号
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosRefundReqData refundReq(String refund_fee, String out_trade_no,UserLoginResData loginInitData,String posProvider){
		
	
		
		PosRefundReqData posBean = new PosRefundReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = "";
		String service_idStr = "030";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

		String terminal_traceStr = "";
		if(posProvider.equals(NEW_LAND)){
			//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
			terminal_traceStr = RandomStringGenerator.getAWRandomNum();
		}else if(posProvider.equals(FUYOU_SF)){
			terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		}
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String refund_feeStr = refund_fee;
		String out_trade_noStr = out_trade_no;
		//operator_id 操作员号	否	String	2
		String operator_idStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setRefund_fee(refund_feeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setOperator_id(operator_idStr);
		
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
		
		
		
		return posBean;
	}
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“020”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端退款流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * refund_fee	退款金额，单位分
	 * out_trade_no	订单号，查询凭据，万鼎订单号、微信订单号、支付宝订单号任意一个
	 * operator_id	操作员号
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosPayQueryReqData queryReq(String pay_type,String out_trade_no,UserLoginResData loginInitData,String posProvider){
		
		PosPayQueryReqData posBean = new PosPayQueryReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "020";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

		String terminal_traceStr = "";
		if(posProvider.equals(NEW_LAND)){
			//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
			terminal_traceStr = RandomStringGenerator.getAWRandomNum();
		}else if(posProvider.equals(FUYOU_SF)){
			terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		}
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String out_trade_noStr = out_trade_no;
		//pay_trace	当前支付终端流水号，与pay_time同时传递	否	String	6
		String pay_traceStr = "";
		//pay_time	当前支付终端交易时间，yyyyMMddHHmmss，全局统一时间格式	否	String	14
		String pay_timeStr = "";
		//operator_id 操作员号	否	String	2
		String operator_idStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setPay_trace(pay_traceStr);
		posBean.setPay_time(pay_timeStr);
		posBean.setOperator_id(operator_idStr);
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("u3n38fwe6u7ic86dps4v21oqr8s0l53p"));
		return posBean;
	}

	/**
	 * 充值实体参数值
	 */
	public static PosDepositReqData depositReq(String pay_type,String auth_no,String total_fee,String memberCode,
											   UserLoginResData loginInitData){

		PosDepositReqData posBean = new PosDepositReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";


		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String auth_noStr = auth_no;
		String total_feeStr = total_fee;
		String memberCodeStr = memberCode;
		String order_bodyStr = "";

		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setAuth_no(auth_noStr);
		posBean.setTotal_fee(total_feeStr);
		posBean.setMemberCode(memberCodeStr);
		posBean.setOrder_body(order_bodyStr);

		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("dygtmyyxbomgvs6750aueo5kq1fjlzpg"));



		return posBean;
	}


	/**
	 * 会员充值轮询充值状态接口参数
	 */
	public static PosPayQueryReqData depositStateQueryReq(String pay_type,String out_trade_no,UserLoginResData loginInitData){

		PosPayQueryReqData posBean = new PosPayQueryReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "020";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";

		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String out_trade_noStr = out_trade_no;
		//pay_trace	当前支付终端流水号，与pay_time同时传递	否	String	6
		String pay_traceStr = "";
		//pay_time	当前支付终端交易时间，yyyyMMddHHmmss，全局统一时间格式	否	String	14
		String pay_timeStr = "";
		//operator_id 操作员号	否	String	2
		String operator_idStr = "";

		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setPay_trace(pay_traceStr);
		posBean.setPay_time(pay_timeStr);
		posBean.setOperator_id(operator_idStr);
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("dygtmyyxbomgvs6750aueo5kq1fjlzpg"));
		return posBean;
	}

	/**
	 * 充值记录查询
	 */
	public static PosDepositRecodeReqData depositRecodeQueryReq(int page,String memCode,String startTime,String endTime,UserLoginResData loginInitData, String posProvider){

		PosDepositRecodeReqData posBean = new PosDepositRecodeReqData();
		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";
		posBean.setMerchantNo(merchant_noStr);
		posBean.setTerminalId(terminal_idStr);
		posBean.setMemCode(memCode);
		posBean.setStartTime(startTime);
		posBean.setEndTime(endTime);
		posBean.setPageNum(page+"");
		return posBean;
	}


	/**
	 * 会员消费实体参数值
	 */
	public static PosMemConsumeReqData consumeReq(String pay_type, String auth_no, String total_fee, String memberCode,
												  UserLoginResData loginInitData,int cardId){

		PosMemConsumeReqData posBean = new PosMemConsumeReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";

		//终端流水号
		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		//终端交易时间
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String auth_noStr = auth_no;
		String total_feeStr = total_fee;
		String memberCodeStr = memberCode;
		String order_bodyStr = String.valueOf(cardId);

		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setAuth_no(auth_noStr);
		posBean.setTotal_fee(total_feeStr);
		posBean.setMemberCode(memberCodeStr);
		posBean.setOrder_body(order_bodyStr);

		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("dygtmyyxbomgvs6750aueo5kq1fjlzpg"));



		return posBean;
	}


	/**
	 * 会员消费预下单实体参数值
	 */
	public static PosMemConsumePreOrderReqData consumePreOrderReq(String pay_type, String total_fee, String memberCode,
														  UserLoginResData loginInitData,int cardId){

		PosMemConsumePreOrderReqData posBean = new PosMemConsumePreOrderReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";

		//终端流水号
		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		//终端交易时间
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String total_feeStr = total_fee;
		String memberCodeStr = memberCode;
		String order_bodyStr = String.valueOf(cardId);

		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setTotal_fee(total_feeStr);
		posBean.setMemberCode(memberCodeStr);
		posBean.setOrder_body(order_bodyStr);

		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("dygtmyyxbomgvs6750aueo5kq1fjlzpg"));



		return posBean;
	}

	/**
	 * 会员充值和消费成功更新订单实体参数值
	 */
	public static PosMemConsumeUpdateOrderReqData consumeUpdateOrderReq(String pos_order_noStr,PosMemConsumePreOrderRespData respData,
																		UserLoginResData loginInitData,int cardId){

		PosMemConsumeUpdateOrderReqData posBean = new PosMemConsumeUpdateOrderReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = respData.getPay_type();
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";

		String memberCodeStr = respData.getMemberCode();
		String out_trade_noStr = respData.getOut_trade_no();
		//终端流水号
		String terminal_traceStr = pos_order_noStr;
		//卡ID
		String order_bodyStr = String.valueOf(cardId);


		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setMemberCode(memberCodeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setOrder_body(order_bodyStr);


		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("dygtmyyxbomgvs6750aueo5kq1fjlzpg"));



		return posBean;
	}

	/**
	 * 会员消费记录查询
	 */
	public static PosMemConsumeRecodeReqData memPayRecodeQueryReq(int page, String memCode, String startTime, String endTime, UserLoginResData loginInitData, String posProvider){

		PosMemConsumeRecodeReqData posBean = new PosMemConsumeRecodeReqData();
		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000853";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "11407";
		posBean.setMerchantNo(merchant_noStr);
		posBean.setTerminalId(terminal_idStr);
		posBean.setMemCode(memCode);
		posBean.setStartDate(startTime);
		posBean.setEndDate(endTime);
		posBean.setPageNum(page);
		return posBean;
	}

	/**
	 * 核销记录查询
	 */
	public static CardVerificaRecodeReqData cardVerificaRecodeReq(int page, int numPerPage,String code, UserLoginResData loginInitData){

		CardVerificaRecodeReqData reqBean = new CardVerificaRecodeReqData();
//		String midStr = loginInitData.getMid();
		String terminal_idStr = loginInitData.getTerminal_id();
		reqBean.setPageNum(page+"");
		reqBean.setNumPerPage(numPerPage+"");
//		reqBean.setMid(midStr);
		reqBean.setTerminal_id(terminal_idStr);
		reqBean.setCode(code);
		return reqBean;
	}

	/**
	 * 付费购卡记录查询
	 */
	public static BuyCardRecodeReqData buyCardRecodeQueryReq(int page, String memCode, UserLoginResData loginInitData){

		BuyCardRecodeReqData posBean = new BuyCardRecodeReqData();
		posBean.setMerchantNo(loginInitData.getMerchant_no());
		posBean.setTerminalId(loginInitData.getTerminal_id());
		posBean.setMemCode(memCode);
		posBean.setPageNum(page);
		return posBean;
	}

	/**
	 * 支付中状态时轮询获取订单状态
	 */
	public static PosPayQueryReqData paymentStateQueryReq(String pay_type, PosScanpayResData resData, UserLoginResData userBean){

		PosPayQueryReqData posBean = new PosPayQueryReqData();

		posBean.setPay_ver(pay_ver);
		posBean.setPay_type(pay_type);
		posBean.setService_id("020");
		posBean.setMerchant_no(userBean.getMerchant_no());
		posBean.setTerminal_id(userBean.getTerminal_id());

		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		posBean.setTerminal_trace(terminal_traceStr);

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setOut_trade_no(resData.getOut_trade_no());
		posBean.setPay_trace("");
		posBean.setPay_time("");
		posBean.setOperator_id("");
		posBean.setKey_sign(posBean.getSignStr(userBean.getAccess_token()));
		return posBean;
	}


	/**
	 *  预授权
	 */
	public static AuthBaseRequest authReq( UserLoginResData userLoginResData, String pay_type, String total_fee, String auth_no){
		AuthBaseRequest request = new AuthBaseRequest();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "011";

		//merchant_no	商户号
		String merchant_noStr = userLoginResData.getMerchant_no();
		//terminal_id	终端号
		String terminal_idStr = userLoginResData.getTerminal_id();


		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String auth_noStr = auth_no;
		String operator_idStr = null;
		String total_feeStr = total_fee;
		request.setPay_ver(pay_verStr);
		request.setPay_type(pay_typeStr);
		request.setService_id(service_idStr);
		request.setMerchant_no(merchant_noStr);
		request.setTerminal_id(terminal_idStr);

		request.setTerminal_time(terminal_timeStr);
		//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
		request.setTerminal_trace(RandomStringGenerator.getAFRandomNum());
		request.setAuth_code(auth_noStr);
		request.setOrder_body(null);
		request.setOperator_id(operator_idStr);
		request.setTotal_fee(total_feeStr);


		//参数加签
		Log.e("参数:",FastJsonUtil.toJSONString(request));
		Map<String, Object> map = request.toMap();
		Log.e("toMap参数:",map.toString());
		String mapStr = FacePayUtils.getSign(map,userLoginResData.getAccess_token());
		request.setKey_sign(mapStr);
		return request;
	}


	/**
	 *  预授权撤销
	 */
	public static AuthConfirmReqDate authCancelReq(UserLoginResData userLoginResData, String auth_no){
		AuthConfirmReqDate request = new AuthConfirmReqDate();
		String pay_verStr = pay_ver;
		String service_idStr = "012";
		String merchant_nameStr = userLoginResData.getMername_pos();
		//merchant_no	商户号
		String merchant_noStr = userLoginResData.getMerchant_no();
		//terminal_id	终端号
		String terminal_idStr = userLoginResData.getTerminal_id();
		//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());

		request.setPay_ver(pay_verStr);
		request.setService_id(service_idStr);
		request.setMerchant_name(merchant_nameStr);
		request.setMerchant_no(merchant_noStr);
		request.setTerminal_id(terminal_idStr);
		request.setTerminal_trace(terminal_traceStr);
		request.setTerminal_time(terminal_timeStr);



		request.setOut_trade_no(auth_no);

		//参数加签
		Log.e("参数:",FastJsonUtil.toJSONString(request));
		Map<String, Object> map = request.toMap();
		Log.e("toMap参数:",map.toString());
		String mapStr = FacePayUtils.getSign(map,userLoginResData.getAccess_token());
		request.setKey_sign(mapStr);
		return request;
	}

	/**
	 *  预授权完成
	 */
	public static AuthConfirmReqDate authConfirmReq(UserLoginResData userLoginResData, String auth_no, String total_fee){
		AuthConfirmReqDate request = new AuthConfirmReqDate();
		String pay_verStr = pay_ver;
		String service_idStr = "013";
		String merchant_nameStr = userLoginResData.getMername_pos();
		//merchant_no	商户号
		String merchant_noStr = userLoginResData.getMerchant_no();
		//terminal_id	终端号
		String terminal_idStr = userLoginResData.getTerminal_id();
		//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String total_feeStr = total_fee;

		request.setPay_ver(pay_verStr);
		request.setService_id(service_idStr);
		request.setMerchant_name(merchant_nameStr);
		request.setMerchant_no(merchant_noStr);
		request.setTerminal_id(terminal_idStr);
		request.setTerminal_trace(terminal_traceStr);
		request.setTerminal_time(terminal_timeStr);

		request.setConsume_amount(total_feeStr);


		request.setOut_trade_no(auth_no);

		//参数加签
		Log.e("参数:",FastJsonUtil.toJSONString(request));
		Map<String, Object> map = request.toMap();
		Log.e("toMap参数:",map.toString());
		String mapStr = FacePayUtils.getSign(map,userLoginResData.getAccess_token());
		request.setKey_sign(mapStr);
		return request;
	}

	/**
	 *  预授权完成撤销
	 */
	public static AuthConfirmReqDate authConfirmCancelReq(UserLoginResData userLoginResData, String auth_no, String total_fee){
		AuthConfirmReqDate request = new AuthConfirmReqDate();
		String pay_verStr = pay_ver;
		String service_idStr = "013";
		String merchant_nameStr = userLoginResData.getMername_pos();
		//merchant_no	商户号
		String merchant_noStr = userLoginResData.getMerchant_no();
		//terminal_id	终端号
		String terminal_idStr = userLoginResData.getTerminal_id();
		//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
		String terminal_traceStr = RandomStringGenerator.getAFRandomNum();

		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String total_feeStr = total_fee;

		request.setPay_ver(pay_verStr);
		request.setService_id(service_idStr);
		request.setMerchant_name(merchant_nameStr);
		request.setMerchant_no(merchant_noStr);
		request.setTerminal_id(terminal_idStr);
		request.setTerminal_trace(terminal_traceStr);
		request.setTerminal_time(terminal_timeStr);

		request.setRefund_fee(total_feeStr);


		request.setOut_trade_no(auth_no);

		//参数加签
		Log.e("参数:",FastJsonUtil.toJSONString(request));
		Map<String, Object> map = request.toMap();
		Log.e("toMap参数:",map.toString());
		String mapStr = FacePayUtils.getSign(map,userLoginResData.getAccess_token());
		request.setKey_sign(mapStr);
		return request;
	}


}
