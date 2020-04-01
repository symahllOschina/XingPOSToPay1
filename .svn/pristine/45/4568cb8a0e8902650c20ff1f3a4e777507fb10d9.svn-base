package com.wanding.xingpos.util;


/**
 * 服务地址管理类
 *
 * 
 */
public class NitConfig {
	
	/**  打包前必看：
	 * 1，替换正式域名前缀，
	 * 2,支付通道为星POS服务通道时，保存订单前判断是否为测试环境isTest = "test"
	 * 3，升级版本号
	 * 4，WelcomeActivity入口Activity中posProvider默认值必须区分开富友或新大陆
	 * 5,新大陆POS机交易设置界面隐藏银联二维码设置
	 */
	public static final String isTest = "true";//测试为 isTest = "test"正式为isTest = "true"

	//获取最新版本号（以下地址为生产环境地址，该地址不涉及测试地址）
	public static String getNEWLANDUrl = "https://download.wandingkeji.cn/download/downloadVersion/newland/xml";
	public static String getFUYOUUrl = "https://download.wandingkeji.cn/download/downloadVersion/fuiou/xml";

	/**
	 * 测试服务前缀
	 * {"data":{"isQueryCoupons":false,"merchant_no":"1000863","terminal_id":"11424","accessToken":"18ajl2liorh3u9iheqveby8xvj5hoxtw","mid":97,"eid":2074,"ename":"金华款台（测试勿动）"},"message":"查询成功","status":200}
	 * */						  // test.weupay.com/pay/api/qmp/100/1/barcodepay
	//支付链接前缀									 
	public static final String basePath1 =  			"https://devpay.wandingkeji.cn";
//	public static final String authBasePath =  			"http://192.168.2.17:8081";
	//交易明细查询（历史）
	public static final String queryHistoryPath1 = 		"https://devdownload.wandingkeji.cn";
	//会员系统业务(充值，消费，卡劵核销)
	public static final String memberBasePath1 =        "https://mp.wandingkeji.cn";//


	/** 正式服务器 */
	//支付链接前缀
	public static final String basePath =  						 "https://pay.wandingkeji.cn";
	//交易明细查询（历史）
	public static final String queryHistoryPath =     		 	 "https://download.wandingkeji.cn";
	//会员系统业务(充值，消费，卡劵核销)
	public static final String memberBasePath =          		 "https://mp.wandingkeji.cn";




	/**
	 * 分期请求地址
	 */
	//测试地址
	public static final String instalmentServiceUrl1 = "http://sandbox.starpos.com.cn/installment";
	//生产地址
	public static final String instalmentServiceUrl = "http://bystages-server.starpos.com.cn:8485/installment";
	
	/**
	 * 微信，支付宝（条码），刷卡支付请求
	 */																				 
	public static final String barcodepayUrl = basePath +"/pay/api/qmp/100/1/barcodepay";
	
	/**
	 * 微信，支付宝（条码），刷卡退款请求
	 */																				 
	public static final String refundUrl = basePath +"/pay/api/qmp/100/1/refund";
	
	/**
	 * 微信，支付宝（条码），刷卡支付查询请求   mer/queryOrderDetail
	 */
	public static final String queryUrl = basePath +"/pay/api/qmp/100/1/query";
	
	
	/**
	 * 正式服务器图片前缀 
	 */
	public static final String imgUrl="";
	public static final String doLoginUrl = "";
	
	/**
	 * 本地服务器图片前缀 
	 */
	public static final String imgUrls="";
	
	//签到
	public static final String loginUrl = basePath +"/pay/api/qmp/200/1/indexLogin";
	/**
	 * 查询别名
	 */
	public static final String queryAliasStatusUrl = basePath+"/pay/api/app/200/1/queryClientId";
	//POS接口退款和查询时获取orderId
	public static final String getPosPayOrderId =  basePath +  "/pay/api/qmp/200/1/queryChannelId";
	//保存测试数据
	public static final String insertChannelIdTestUrl = basePath + "/pay/api/qmp/200/1/insertChannelIdTest";
	//结算(交接班退出)
	public static final String summaryOrderUrl = basePath +        "/pay/api/qmp/200/1/handOver";
	//结算(交接班数据查询)
	public static final String settlementOrderUrl = basePath +        "/pay/api/qmp/200/1/getWorkOverRecord";
	//结算记录，交接班记录(查询)
	public static final String settlementRecordUrl = basePath +        "/pay/api/qmp/200/1/getWorkOverRecordInterval";
	//结算记录，交接班记录详情(查询)
	public static final String settlementRecordDetailUrl = basePath +        "/pay/api/qmp/200/1/getWorkOverRecordHistory";
	//交易明细：(查当天)入参：当前页数：pageNum 一页数量：numPerPage mid，eid，date_type（"1"=当日交易）
	public static final String queryOrderDayListUrl = basePath +   "/admin/api/qmp/200/1/queryOrder";
	//交易明细：(查当月)入参：当前页数：pageNum 一页数量：numPerPage mid，eid，date_type（"2"=本月交易不含今天）
	public static final String queryOrderMonListUrl = queryHistoryPath+"/download/api/qmp/200/1/queryOrderByMonth";

	/**
	 * 微信，支付宝（条码），刷卡支付查询请求   mer/queryOrderDetail
	 */
	public static final String queryOrderStatusUrl = basePath +"/pay/payment/query";
	/**
	 * 当天汇总查询
	 * 入参：eid，mid，startTime，endTime
	 */
	public static final String querySummaryUrl = basePath +   "/pay/api/qmp/200/1/queryOrderSum";

	/**
	 * 历史汇总查询
	 * 入参：eid，mid，startTime，endTime
	 */																			    	
	public static final String queryHistorySummaryUrl = queryHistoryPath +   "/download/api/qmp/200/1/queryOrderSumHistory";

	/**
	 *  刷卡扫码预授权
	 *  http://192.168.2.63:8090
	 */
	public static final String scanAuthUrl = basePath + "/pay/payment/deposit/barcodepay";

	/**
	 *  预授权完成
	 *  http://192.168.2.63:8090
	 */
	public static final String authConfirmUrl = basePath + "/pay/payment/deposit/consume";

	/**
	 *  预授权撤销
	 *  http://192.168.2.63:8090
	 */
	public static final String authCancelUrl = basePath + "/pay/payment/deposit/reverse";

	/**
	 *  预授权完成撤销
	 *  http://192.168.2.63:8090
	 */
	public static final String authConfirmCancelUrl = basePath + "/pay/payment/deposit/refund";

	/**
	 *  预授权交易记录
	 *  http://192.168.2.63:8090
	 */
	public static final String authRecodeListUrl = basePath + "/pay/payment/deposit/queryOrder";

	/**
	 *  预授权支付中轮询
	 *  http://192.168.2.63:8090
	 */
	public static final String refreshOrderStateUrl = basePath + "/pay/payment/deposit/query";
	

	/**
	 *  核销劵码查询
	 *  参数：mid = 商户id,code = 核销码;
	 */
	public static final String writeOffQueryCodeUrl = basePath + "/admin/api/qmp/200/queryCode";
	/**
	 *  核销
	 *  参数：mid = 商户id,code = 核销码,couponId;
	 */
	public static final String writeOffConsumeCodeUrl = basePath + "/admin/api/qmp/200/consumeCode";

	/**
	 *  核销记录
	 *  参数：mid = 商户id,pagNum = 页数,code = 核销代码
	 */
	public static final String writeOffRecodeUrl = basePath + "/admin/api/qmp/200/queryConsumeList";
	/**
	 * 查询劵列表：/api/qmp/200/queryCouponList
	 * mid,pageNum
	 */
	public static final String queryCouponListUrl = basePath + "/admin/api/qmp/200/queryCouponList";
	/**
	 *	批量制劵：/api/qmp/200/qRCode
	 * total，cardId，mid
	 */
	public static final String qRCodeUrl = basePath + "/admin/api/qmp/200/qRCode";

	/**
	 * 消费有礼领劵
	 * member-mall/android/getConsumActivityAfterPay
	 */
	public static final String getCardStockUrl = memberBasePath+"/member-mall/android/getConsumActivityAfterPay";

	/**
	 * 订单详情查询消费有礼
	 * member-mall/android/queryPosConsumOrder
	 */
	public static final String queryPosConsumOrderUrl = memberBasePath+"/member-mall/android/queryPosConsumOrder";
	
	
	/**
     * 会员充值(预下单)
     */
	public static final String memberTopupPreOrderUrl = memberBasePath+"/member-mall/android/preOrder";

	/**
     * 会员充值
     */
	public static final String memberTopUpUrl = memberBasePath+"/member-mall/android/memberDeposit";

	/**
	 * 会员充值(更新订单状态)
	 */
	public static final String memberTopupUpdateOrderUrl = memberBasePath+"/member-mall/android/updateOrder";

	/**
     * 会员充值支付中查询订单状态
     */
	public static final String queryDepositStatusUrl = memberBasePath+"/member-mall/android/queryDepositStatus";

	/**
     * 会员充值记录
     */
	public static final String queryDepositOrderUrl = memberBasePath+"/member-mall/android/queryDepositOrder";

	/**
     * 会员充值记录详情
     */
	public static final String queryDepositOrderDetailUrl = memberBasePath+"/member-mall/android/queryDepositOrderDetail";


	/**
	 * 会员消费
	 */
	public static final String micropayUrl = memberBasePath+"/member-mall/member/pos/micropay";

	/**
	 * 会员消费（银行卡支付预下单）
	 */
	public static final String preOrderUrl = memberBasePath+"/member-mall/member/pos/preOrder";

	/**
	 * 会员消费（银行卡支付更新订单状态）
	 */
	public static final String updateOrderUrl = memberBasePath+"/member-mall/member/pos/updateOrder";

	/**
	 * 会员消费（查询会员消费订单记录）
	 */
	public static final String queryConsumeOrderUrl = memberBasePath+"/member-mall/member/pos/queryConsumeOrder";

	/**
	 * 会员消费（查询会员消费订单记录）
	 */
	public static final String queryConsumeOrderDetailUrl = memberBasePath+"/member-mall/member/pos/queryConsumeOrderDetail";

	/**
	 *  会员卡劵核销(查询自己的订单号)
	 *  /admin/api/qmp/2000/queryCode
	 */
	public static final String queryCodeUrl = memberBasePath+"/member-console/console/android/queryCodepos";

	/**
	 *  会员卡劵核销(核销卡劵)
	 *  /admin/api/qmp/2000/consumeCode
	 */
	public static final String consumeCodeUrl = memberBasePath+"/member-console/console/android/consumeCodepos";


	/**
	 *  查询核销次卡接口
	 *  /admin/api/qmp/2000/queryAdroidConsumeFreList
	 */
	public static final String numCardRecodeUrl = memberBasePath+"/member-console/console/android/queryAdroidConsumeFreList";

	/**
	 *  查询核销优惠券接口
	 *  /admin/api/qmp/2000/queryAdroidConsumeList
	 */
	public static final String couponsRecodeUrl = memberBasePath+"/member-console/console/android/queryAdroidConsumeList";

	/**
	 *  付费购卡类型列表
	 */
	public static final String queryMemCardListUrl = memberBasePath+"/member-mall/mini/card/queryMemCardList";

	/**
	 *  购卡记录列表
	 */
	public static final String buyCardRecodeListUrl = memberBasePath+"/member-mall/member/pos/queryBuyCardOrder";
	/**
	 *  购卡记录详情
	 */
	public static final String buyCardRecodeDetailUrl = memberBasePath+"/member-mall/member/pos/queryBuyCardOrderDetail";

}
