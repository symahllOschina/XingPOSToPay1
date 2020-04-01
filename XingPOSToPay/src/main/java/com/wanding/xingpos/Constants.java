package com.wanding.xingpos;

import com.wanding.xingpos.util.Utils;

/**
 *  常量帮助类
 */
public class Constants {



    /**
     * 表示pos机厂商（提供者），默认情况下为新大陆newland
     * 当调用新大陆SDK签到提示找不到界面时posProvider的值发生变化，改为 posProvider = "fuyousf"
     */
    public static final String NEW_LAND = "newland";
    public static final String FUYOU_SF = "fuyousf";

    /**
     * 支付类型选择
     * 现金 = "000";
     * 微信 = "010"，
     * 支付宝 = "020"，
     * 银联二维码 = "060"，
     * 刷卡 = "040"，
     * 翼支付 = "050"
     * 会员 = "800";
     */
    public static final String PAYTYPE_010WX = "010";
    public static final String PAYTYPE_020ALI = "020";
    public static final String PAYTYPE_040BANK = "040";
    public static final String PAYTYPE_050BEST = "050";
    public static final String PAYTYPE_060UNIONPAY = "060";
    public static final String PAYTYPE_000CASH = "000";
    public static final String PAYTYPE_800MEMBER = "800";

    /**
     * 支付类型选择
     * 微信 = "010"，
     * 支付宝 = "020"，
     * 银联二维码 = "060"，
     * 刷卡 = "040"，
     * 翼支付 = "050"
     */
    public static final String PAY_TYPE_010WX = "010";
    public static final String PAY_TYPE_020ALI = "020";
    public static final String PAY_TYPE_040BANK = "040";
    public static final String PAY_TYPE_050BEST = "050";
    public static final String PAY_TYPE_060UNIONPAY = "060";
    public static final String PAY_TYPE_000CASH = "000";
    public static final String PAY_TYPE_800MEMBER = "800";
    /**
     * API公共参数值
     */
    public static final String PAY_TYPE_WX = "WX";
    public static final String PAY_TYPE_ALI = "ALI";
    public static final String PAY_TYPE_BEST = "BEST";
    /**
     *DEBIT= 借记卡
     */
    public static final String PAY_TYPE_DEBIT = "DEBIT";
    /**
     *CREDIT=贷记卡
     */
    public static final String PAY_TYPE_CREDIT = "CREDIT";
    public static final String PAY_TYPE_UNIONPAY = "UNIONPAY";
    public static final String PAY_TYPE_BANK = "BANK";

    /**
     * 预授权交易状态
     **/
    public static final String AUTH_ACTION = "1";
    public static final String AUTH_CANCEL_ACTION = "2";
    public static final String AUTH_CONFIRM_ACTION = "3";
    public static final String AUTH_CONFIRM_CANCEL_ACTION = "4";


    /**
     * 快速买单
     */
    public static final String KSMD001 = "KSMD001";

    /**
     * 台卡消费
     */
    public static final String TK001 = "TK001";

    /**
     * 线上付费购卡
     */
    public static final String XSFFGK001 = "XSFF001";

    /**
     * 线下付费购卡
     */
    public static final String XXFFGK001 = "XXFF001";

    /**
     * 线上充值
     */
    public static final String XSCZ001 = "XSCZ001";

    /**
     * 在线预定通知
     */
    public static final String TG001 = "TG001";


    /**
     * 获取支付类型
     * orderTypeStr:0正向 ,1退款
     * payWay：支付方式
     * isPrint：是否打印小票方式
     */
    public static String getPayWay(String orderTypeStr,String payWay,boolean isPrint){
        String payWayStr = "未知";
        if("0".equals(orderTypeStr)){
            if(Utils.isNotEmpty(payWay)){
                if(PAY_TYPE_WX.equals(payWay)||PAY_TYPE_010WX.equals(payWay)){
                    if(isPrint){
                        payWayStr = "微信支付/WEIXIN PAY";
                    }else
                    {
                        payWayStr = "微信";
                    }
                }else if(PAY_TYPE_ALI.equals(payWay)||PAY_TYPE_020ALI.equals(payWay)){
                    if(isPrint)
                    {
                        payWayStr = "支付宝支付/ALI PAY";
                    }else
                    {
                        payWayStr = "支付宝";
                    }
                }else if(PAY_TYPE_BEST.equals(payWay)||PAY_TYPE_050BEST.equals(payWay)){
                    if(isPrint)
                    {
                        payWayStr = "翼支付/BEST PAY";
                    }else
                    {
                        payWayStr = "翼支付";
                    }
                }else if(PAY_TYPE_DEBIT.equals(payWay)||PAY_TYPE_040BANK.equals(payWay)){
                    //DEBIT= 借记卡       CREDIT=贷记卡
                    if(isPrint)
                    {
                        payWayStr = "刷卡支付/BANK PAY";
                    }else
                    {
                        payWayStr = "银行卡(借记卡)";
                    }
                }else if(PAY_TYPE_CREDIT.equals(payWay)||PAY_TYPE_040BANK.equals(payWay)){
                    //DEBIT= 借记卡       CREDIT=贷记卡
                    if(isPrint)
                    {
                        payWayStr = "刷卡支付/BANK PAY";
                    }else
                    {
                        payWayStr = "银行卡(贷记卡)";
                    }
                }else if(PAY_TYPE_UNIONPAY.equals(payWay)||PAY_TYPE_060UNIONPAY.equals(payWay)){
                    //UNIONPAY = 银联二维码
                    if(isPrint)
                    {
                        payWayStr = "银联二维码支付/UNIONPAY PAY";
                    }else
                    {
                        payWayStr = "银联二维码";
                    }
                }else if(PAY_TYPE_BANK.equals(payWay)||PAY_TYPE_040BANK.equals(payWay)){
                    //BANK = 银行卡
                    if(isPrint)
                    {
                        payWayStr = "刷卡支付/BANK PAY";
                    }else
                    {
                        payWayStr = "银行卡";
                    }
                }
            }
        }else if("1".equals(orderTypeStr)){

            if(PAY_TYPE_WX.equals(payWay)||PAY_TYPE_010WX.equals(payWay)){
                return "微信退款";
            }else if(PAY_TYPE_ALI.equals(payWay)||PAY_TYPE_020ALI.equals(payWay)){
                return "支付宝退款";
            }else if(PAY_TYPE_BEST.equals(payWay)||PAY_TYPE_050BEST.equals(payWay)){
                return "翼支付退款";
            }else if(PAY_TYPE_UNIONPAY.equals(payWay)||PAY_TYPE_060UNIONPAY.equals(payWay)){
                return "银联二维码退款";
            }else if(PAY_TYPE_CREDIT.equals(payWay) || PAY_TYPE_DEBIT.equals(payWay)){
                //DEBIT= 借记卡       CREDIT=贷记卡
                return "消费撤销";
            }else if(PAY_TYPE_BANK.equals(payWay)||PAY_TYPE_040BANK.equals(payWay)){
                // BANK = 银行卡
                return "消费撤销";
            }
        }

        return payWayStr;
    }


    /**
     * 扫码预授权，预授权撤销，预授权完成
     * payType支付方式：010：微信，020：支付宝
     * auth_type支付类型：1:预授权，2：预授权撤销，3：预授权完成,4：预授权完成撤销
     *
     */

    public static String getAuthPayTypeStr(String auth_type){

        if("1".equals(auth_type)){
            return "预授权";
        }else if("2".equals(auth_type)){
            return "预授权撤销";
        }else if("3".equals(auth_type)){
            return "预授权完成";
        }else if("4".equals(auth_type)){
            return "预授权完成撤销";
        }

        return "交易未知";
    }
}
