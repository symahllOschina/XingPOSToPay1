package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.pull.refresh.view.XListView;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.BuyCardTypeAdapter;
import com.wanding.xingpos.adapter.PayTypeGridViewAdapter;
import com.wanding.xingpos.bean.DepositOrder;
import com.wanding.xingpos.bean.MemberCardDetail;
import com.wanding.xingpos.bean.PayWayBean;
import com.wanding.xingpos.bean.PosDepositRecodeReqData;
import com.wanding.xingpos.bean.PosMemConsumePreOrderReqData;
import com.wanding.xingpos.bean.PosMemConsumePreOrderRespData;
import com.wanding.xingpos.bean.PosMemConsumeReqData;
import com.wanding.xingpos.bean.PosMemConsumeRespData;
import com.wanding.xingpos.bean.PosMemConsumeUpdateOrderReqData;
import com.wanding.xingpos.bean.PosMemConsumeUpdateOrderRespData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.RandomStringGenerator;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 付费购卡
 */
@ContentView(R.layout.activity_buy_card)
public class BuyCardActivity extends BaseActivity implements View.OnClickListener{

    private Dialog hintDialog;// 加载数据时对话框


    @ViewInject(R.id.menu_title_imageView)
    ImageView imgBack;
    @ViewInject(R.id.menu_title_layout)
    LinearLayout titleLayout;
    @ViewInject(R.id.menu_title_tvTitle)
    TextView tvTitle;
    @ViewInject(R.id.menu_title_imgTitleImg)
    ImageView imgTitleImg;
    @ViewInject(R.id.menu_title_tvOption)
    TextView tvOption;

    @ViewInject(R.id.buy_card_imgCardBg)
    ImageView imgCardBg;
    @ViewInject(R.id.buy_card_tvName)
    TextView tvName;
    @ViewInject(R.id.buy_card_tvDepict)
    TextView tvDepict;
    @ViewInject(R.id.buy_card_tvOPrice)
    TextView tvOPrice;
    @ViewInject(R.id.buy_card_tvPrice)
    TextView tvPrice;

    @ViewInject(R.id.pay_type_mGridView)
    GridView mGridView;
    private List<PayWayBean> list = new ArrayList<>();
    private BaseAdapter mAdapter;

    private UserLoginResData loginInitData;
    private MemberCardDetail memberCard;

    /**
     * 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联
     */
    private String printNum = "printNumNo";
    /**
     * 打印字体大小 isDefault:true默认大小，false即为大字体
     */
    private boolean isDefault = true;
    /**
     * cameraType为true表示打开后置摄像头，fasle为前置摄像头
     */
    private boolean cameType = true;

    /**
     * 会员卡号
     */
    private String cardCode = "";
    /**
     * 支付金额
     */
    private String totalFee;
    /**
     * 支付方式
     */
    private String payType;

    /**
     * 打印小票第index联
     */
    private int index = 1;
    /**
     *  轮询查询订单状态标示
     */
    private int queryIndex = 1;

    /**
     * 会员消费成功返回
     */
    PosMemConsumeRespData consumeRespData = null;

    /**
     * 各支付通道不同的流水号
     */
    private String pos_order_noStr;

    /**
     * 会员消费预下单返回实体
     */
    private PosMemConsumePreOrderRespData consumePreOrderRespData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("线上购卡");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        Intent intent = getIntent();
        memberCard = (MemberCardDetail) intent.getSerializableExtra("memberCard");
        try {
            loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        initData();
        initListener();

        initView();




    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("onResume()方法", "已近执行");




        //取出设置的打印值
        SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(activity, "printing");
        //取出保存的默认值
        printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
        isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
        Log.e("取出保存的打印值", printNum);
        //取出保存的摄像头参数值
        SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(activity, "scancamera");
        cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
        if(cameType){
            Log.e("当前摄像头打开的是：", "后置");
        }else{
            Log.e("当前摄像头打开的是：", "前置");
        }

    }

    private void initData(){
        PayWayBean bankBean = new PayWayBean();
        bankBean.setImg(R.drawable.bank_icon);
        bankBean.setText("刷卡");
        PayWayBean wxBean = new PayWayBean();
        wxBean.setImg(R.drawable.wx_pay_icon);
        wxBean.setText("微信");
        PayWayBean aliBean = new PayWayBean();
        aliBean.setImg(R.drawable.ali_pay_icon);
        aliBean.setText("支付宝");
        PayWayBean unionBean = new PayWayBean();
        unionBean.setImg(R.drawable.unionpay_icon);
        unionBean.setText("银联二维码");
        PayWayBean cashBean = new PayWayBean();
        cashBean.setImg(R.drawable.cash_icon);
        cashBean.setText("现金");
        list.add(bankBean);
        list.add(wxBean);
        list.add(aliBean);
        list.add(unionBean);
        list.add(cashBean);

        mAdapter = new PayTypeGridViewAdapter(activity,list);
        mGridView.setAdapter(mAdapter);
    }


    private void initListener(){
        imgBack.setOnClickListener(this);
        mGridView.setOnItemClickListener(payTypeCheckedLinstener);

    }

    private void initView(){
        //初始化
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.placeholder_icon);
        if(memberCard!=null){
            Glide.with(this)
                    .load(memberCard.getSmall_url())
                    .apply(options)
                    .into(imgCardBg);
            tvName.setText(memberCard.getProduct_name());
            tvDepict.setText(memberCard.getDepict());
            tvOPrice.setText("￥"+DecimalUtil.StringToPrice(memberCard.getO_price()));
            tvOPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrice.setText("￥"+DecimalUtil.StringToPrice(memberCard.getN_price()));
        }
    }


    /**
     * 消费第一步
     */
    private void payMethodOne(){

        try {
            totalFee =  DecimalUtil.StringToPrice(memberCard.getN_price());
            if(payType.equals(Constants.PAYTYPE_000CASH)){
                //直接下单支付
                String auth_no = "";
                payMethodTwo(auth_no);
            }else{
                //金额是否合法
                int isCorrect = DecimalUtil.isEqual(totalFee);
                if(isCorrect != 1){
                    ToastUtil.showText(activity,"会员卡金额错误！",1);
                    return;
                }
                Log.e("金额值转换后：", totalFee);
                if(payType.equals(Constants.PAYTYPE_040BANK)){
                    //生成预下单
                    cardPayMerhodTwo();
                }else if(payType.equals(Constants.PAYTYPE_000CASH)){
                    //直接下单支付
                    String auth_no = "";
                    payMethodTwo(auth_no);
                }else{
                    //开始扫码
                    FuyouPosServiceUtil.scanReq(activity);
                }
            }







        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ToastUtil.showText(activity,"付款金额错误！",1);
        }
    }

    /**
     * 消费第二步
     * auth_no：扫码结果（付款码Code）
     */
    private void payMethodTwo(String auth_no){
        //元转分
        String total_fee = DecimalUtil.elementToBranch(totalFee);

        PosMemConsumeReqData posBean = PayRequestUtil.consumeReq(payType, auth_no, total_fee,cardCode,loginInitData,memberCard.getId());
        payMethodThree(posBean);

    }

    /**
     * 会员消费第三步
     */
    private void payMethodThree(final PosMemConsumeReqData posBean){

        hintDialog=CustomDialog.CreateDialog(activity, "    加载中...");
        hintDialog.show();
        hintDialog.setCanceledOnTouchOutside(false);

        //付款二维码内容(发起支付请求)
        final String url = NitConfig.micropayUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(posBean);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_ONE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }

    /**
     *  银行卡会员消费第二步（创建预下单）
     */
    private void cardPayMerhodTwo(){
        //元转分
        String total_fee = DecimalUtil.elementToBranch(totalFee);
        final PosMemConsumePreOrderReqData posBean = PayRequestUtil.consumePreOrderReq(payType, total_fee,cardCode,loginInitData,memberCard.getId());

        hintDialog=CustomDialog.CreateDialog(activity, "    加载中...");
        hintDialog.show();
        hintDialog.setCanceledOnTouchOutside(false);

        //付款二维码内容(发起支付请求)
        final String url = NitConfig.preOrderUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(posBean);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_THREE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();

    }

    /**
     * 银行卡充值第三步，调用SDK支付
     */
    private void cardPayMerhodThree(){

        String total_feeStr = FieldTypeUtil.makeFieldAmount(totalFee);
        Log.e("SDK提交带规则金额",total_feeStr);
        //设备号
        String deviceNum = loginInitData.getTrmNo_pos();
        pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
        Log.e("生成的订单号：", pos_order_noStr);
        //是否开启前置摄像头
        FuyouPosServiceUtil.payReq(activity, payType, total_feeStr,pos_order_noStr,cameType);
    }

    /**
     * 银行卡充值第四步，调用SDK支付成功，更新服务器订单状态
     */
    private void cardPayMerhodFour(){

        final PosMemConsumeUpdateOrderReqData posBean = PayRequestUtil.consumeUpdateOrderReq(pos_order_noStr,consumePreOrderRespData,loginInitData,memberCard.getId());

        hintDialog=CustomDialog.CreateDialog(activity, "    加载中...");
        hintDialog.show();
        hintDialog.setCanceledOnTouchOutside(false);

        final String url = NitConfig.updateOrderUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(posBean);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_FOUR;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(com.wanding.xingpos.httputils.NetworkUtils.REQUEST_CODE, com.wanding.xingpos.httputils.NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }


    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            switch (msg.what){
                case com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_ONE:
                    String scanPayJsonStr=(String) msg.obj;
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    scanPayJsonStr(scanPayJsonStr);
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_TWO:
                    String payStateQueryJson=(String) msg.obj;
//                    payStateQueryJson(payStateQueryJson);
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_THREE:
                    //创建预下单返回
                    String createOrderJson=(String) msg.obj;
                    createOrderJson(createOrderJson);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.MSG_WHAT_FOUR:
                    //更新订单返回
                    String updateOrderJson=(String) msg.obj;
                    updateOrderJson(updateOrderJson);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    finish();
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    finish();
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    finish();
                    break;
                case com.wanding.xingpos.httputils.NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void scanPayJsonStr(String json){
        //然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}
        Gson gjson  =  GsonUtils.getGson();
        consumeRespData = gjson.fromJson(json, PosMemConsumeRespData.class);
        //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        String return_codeStr = consumeRespData.getReturn_code();
        //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        String return_msgStr = consumeRespData.getReturn_msg();
        //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        String result_codeStr = consumeRespData.getResult_code();
        synchronized (ScanPayActivity.class){
            if(return_codeStr.equals("01")) {
                if(result_codeStr.equals("01")){
//                    ToastUtil.showText(activity,"支付成功!",1);
                    startPrint();

                }else if(result_codeStr.equals("03")){
                    ToastUtil.showText(activity,"支付中!",1);
                    finish();
                    //查询订单状态
//                    queryDepositStatus();
                }else{
                    ToastUtil.showText(activity,"支付失败!",1);
                    finish();
                }
            }else{
                ToastUtil.showText(activity,return_msgStr,1);
                finish();
            }
        }

    }

    /**
     * 预下单接口返回
     */
    private void createOrderJson(String json){
        //然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}
        Gson gjson  =  GsonUtils.getGson();
        consumePreOrderRespData = gjson.fromJson(json, PosMemConsumePreOrderRespData.class);
        //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        String return_codeStr = consumePreOrderRespData.getReturn_code();
        //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        String return_msgStr = consumePreOrderRespData.getReturn_msg();
        //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        String result_codeStr = consumePreOrderRespData.getResult_code();
        synchronized (ScanPayActivity.class){
            if(return_codeStr.equals("01")) {
                if(result_codeStr.equals("01")){

                    //创建预下单成功后调起银行卡支付SDK
                    cardPayMerhodThree();
                }else{
                    ToastUtil.showText(activity,"预下单失败!",1);
                    finish();
                }
            }else{
                ToastUtil.showText(activity,return_msgStr,1);
                finish();
            }
        }


    }

    /**
     * 更新订单接口返回
     */
    private void updateOrderJson(String json){
        //然后我们把json转换成JSONObject类型得到{"Person"://{"username":"zhangsan","age":"12"}}
        Gson gjson  =  GsonUtils.getGson();
        PosMemConsumeUpdateOrderRespData consumeUpdateOrderRespData = gjson.fromJson(json, PosMemConsumeUpdateOrderRespData.class);
        //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        String return_codeStr = consumeUpdateOrderRespData.getReturn_code();
        //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        String return_msgStr = consumeUpdateOrderRespData.getReturn_msg();
        //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        String result_codeStr = consumeUpdateOrderRespData.getResult_code();
        synchronized (ScanPayActivity.class){
            if(return_codeStr.equals("01")) {
                if(result_codeStr.equals("01")){
                    consumeRespData = new PosMemConsumeRespData();
                    consumeRespData.setPay_type(consumeUpdateOrderRespData.getPay_type());
                    consumeRespData.setOut_trade_no(consumeUpdateOrderRespData.getOut_trade_no());
                    consumeRespData.setEnd_time(consumeUpdateOrderRespData.getTerminal_time());
                    consumeRespData.setTotal_fee(consumeUpdateOrderRespData.getTotal_fee());
                    consumeRespData.setMemberCode(consumeUpdateOrderRespData.getMemberCode());
                    consumeRespData.setReceiveTicketUrl(consumeUpdateOrderRespData.getReceiveTicketUrl());
//                    ToastUtil.showText(activity,"更新订单成功!",1);
                    startPrint();
                }else{
                    ToastUtil.showText(activity,"更新订单失败!",1);
                    finish();
                }
            }else{
                ToastUtil.showText(activity,return_msgStr,1);
                finish();
            }
        }
    }

    /**
     * 打印带优惠券的小票
     */
    private void startPrint(){
        /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
        index = 1;
        if(printNum.equals("printNumNo")){

            //不执行打印
//            intentToActivity(totalStr);
            finish();

        }else if(printNum.equals("printNumOne")){

            //打印一次
            String printTextStr = FuyouPrintUtil.memConsumePrintText(activity, consumeRespData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
            FuyouPosServiceUtil.printTextReq(activity,printTextStr);

        }else if(printNum.equals("printNumTwo")){
            //打印两次
            String printTextStr = FuyouPrintUtil.memConsumePrintText(activity, consumeRespData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
            FuyouPosServiceUtil.printTextReq(activity,printTextStr);
        }
    }

    /**  打印下一联提示窗口 */
    private void showPrintTwoDialog(){
        View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
        Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
        final Dialog myDialog = new Dialog(activity,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                index = 2;

                String printTextStr = FuyouPrintUtil.memConsumePrintText(activity, consumeRespData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                FuyouPosServiceUtil.printTextReq(activity,printTextStr);


                myDialog.dismiss();

            }
        });
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        /**
         * 富友POS扫码返回
         */
        if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
            if(bundle != null){
                Log.e(TAG,resultCode+"");
                String reason = "扫码取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";
                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
                        Log.e("获取扫描结果：", scanCodeStr);
                        //如果扫描的二维码为空则不执行支付请求
                        if(Utils.isNotEmpty(scanCodeStr)){
                            //auth_no	授权码（及扫描二维码值）
                            String auth_no = scanCodeStr;

                            payMethodTwo(auth_no);

                        }else{

                            ToastUtil.showText(activity,"扫描结果为空！",1);
                        }


                        break;
                    // 扫码取消
                    case Activity.RESULT_CANCELED:

                        if (Utils.isNotEmpty(reason_str)) {
                            Log.e("reason", reason_str);
                            reason = reason_str;
                        }
                        ToastUtil.showText(activity,reason,1);
                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
                        break;
                    default:
                        break;

                }
            }else{
                ToastUtil.showText(activity,"扫码失败！",1);
            }

        }

        /**
         * 打印返回
         */
        if(requestCode == FuyouPosServiceUtil.PRINT_REQUEST_CODE){
            if(bundle!=null){
                Log.e(TAG,resultCode+"");
                String reason = "打印取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";

                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if(printNum.equals("printNumTwo")){
                            if(index < 2){
                                //打印正常
                                //弹出对话框提示打印下一联
                                showPrintTwoDialog();
                            }else{
                                //打印完成关闭界面
                                finish();
                            }

                        }else{
                            //打印完成关闭界面
                            finish();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        if (Utils.isNotEmpty(reason_str)) {
                            reason = reason_str;
                            Log.e("reason", reason);
                            if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
                                //缺纸，不能打印
                                ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
                            }else {
                                ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
                            }
                        }else{
                            ToastUtil.showText(activity,reason,1);
                        }
                        finish();

                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);
                        break;
                    default:
                        break;

                }
            }else{
                ToastUtil.showText(activity,"打印返回数据为空！",1);
            }

        }
        /**
         * 支付返回
         */
        if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
            if(bundle != null){
                Log.e(TAG,resultCode+"");
                String reason = "支付取消";
                String traceNo = "";
                String batchNo = "";
                String ordernumber = "";

                String reason_str = (String) bundle.get("reason");
                String traceNo_str = (String)bundle.getString("traceNo");
                String batchNo_str = (String)bundle.getString("batchNo");
                String ordernumber_str = (String)bundle.getString("ordernumber");
                switch (resultCode) {
                    // 支付成功
                    case Activity.RESULT_OK:
                        //更新订单
                        cardPayMerhodFour();

                        break;
                    // 支付取消
                    case Activity.RESULT_CANCELED:
                        if (Utils.isNotEmpty(reason_str)) {
                            reason = reason_str;
                            Log.e("reason", reason);
                        }
                        ToastUtil.showText(activity,reason,1);
                        finish();

                        Log.e("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

                        break;
                    default:
                        break;
                }
            }else{
                ToastUtil.showText(activity,"支付返回数据为空！",1);
            }

        }
    }

    /**
     * GridView的Item事件回调
     */
    private AdapterView.OnItemClickListener payTypeCheckedLinstener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(Utils.isFastClick()){
                return;
            }
            if(position == 0&&list.get(position).getText().equals("刷卡")){
                payType = Constants.PAYTYPE_040BANK;
                payMethodOne();
            }else if(position == 1&&list.get(position).getText().equals("微信")){
                payType = Constants.PAYTYPE_010WX;
                payMethodOne();
            }else if(position == 2&&list.get(position).getText().equals("支付宝")){
                payType = Constants.PAYTYPE_020ALI;
                payMethodOne();
            }else if(position == 3&&list.get(position).getText().equals("银联二维码")){
                payType = Constants.PAYTYPE_060UNIONPAY;
                payMethodOne();
            }else if(position == 4&&list.get(position).getText().equals("现金")){
                payType = Constants.PAYTYPE_000CASH;
                payMethodOne();
            }else{
                payType = "";
                ToastUtil.showText(activity,"请选择付款类型",1);
            }

        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String sign = "";
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
                default:
                    break;
        }
    }


}
