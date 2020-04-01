package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.DepositOrder;
import com.wanding.xingpos.bean.PosMemConsumeRecodeDetail;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;

/**
 * 会员充值记录详情界面
 */
@ContentView(R.layout.activity_member_pay_recode_details)
public class MemberPayRecodeDetailActivity extends BaseActivity implements View.OnClickListener {


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

    /**
     * 会员卡号，订单号，金额，支付时间，支付方式
     */
    @ViewInject(R.id.mempay_pay_recode_details_tvMemCode)
    TextView tvMemCode;
    @ViewInject(R.id.mempay_pay_recode_details_tvOrderId)
    TextView tvOrderId;
    @ViewInject(R.id.mempay_pay_recode_details_tvTotal)
    TextView tvTotal;
    @ViewInject(R.id.mempay_pay_recode_details_tvPayTime)
    TextView tvPayTime;
    @ViewInject(R.id.mempay_pay_recode_details_tvCreateTime)
    TextView tvCreateTime;
    @ViewInject(R.id.mempay_pay_recode_details_tvPayWay)
    TextView tvPayWay;
    @ViewInject(R.id.mempay_pay_recode_details_tvOrderState)
    TextView tvOrderState;

    @ViewInject(R.id.mempay_pay_recode_details_btOk)
    Button tvOk;
    @ViewInject(R.id.mempay_pay_recode_details_btPrint)
    Button tvPrint;


    private Dialog hintDialog;// 加载数据时对话框

    private UserLoginResData loginInitData;
    private PosMemConsumeRecodeDetail order;

    /**
     * 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联
     */
    private String printNum = "printNumNo";
    /**
     * 打印字体大小 isDefault:true默认大小，false即为大字体
     */
    private boolean isDefault = true;

    /**
     * 打印小票第index联
     */
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("订单详情");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);

        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        order = (PosMemConsumeRecodeDetail) intent.getSerializableExtra("order");

        initListener();

        updateView();

        getRecodeDetails();

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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
    }

    /**
     * 更新界面
     */
    private void updateView(){
        tvMemCode.setText("");
        tvOrderId.setText("");
        tvTotal.setText("");
        tvCreateTime.setText("");
        tvPayTime.setText("");
        tvPayWay.setText("");
        tvOrderState.setText("");
        if(order!=null){
            tvMemCode.setText(order.getMemCode());
            tvOrderId.setText(order.getOrderId());
            tvTotal.setText(DecimalUtil.StringToPrice(order.getTotalFee()));
            tvCreateTime.setText(order.getCreateTime());
            String payTimeStr = order.getPayTime();
            String payTime = "";
            if(Utils.isNotEmpty(payTimeStr)){
                payTime = payTimeStr;
            }
            tvPayTime.setText(payTime);
            String payTypeStr = order.getPayWay();
            String payType = "未知";
            if(Utils.isNotEmpty(payTypeStr)){
                if(payTypeStr.equals("WX")){
                    payType = "微信";
                }else if(payTypeStr.equals("ALI")){
                    payType = "支付宝";
                }else if(payTypeStr.equals("BEST")){
                    payType = "翼支付";
                }else if(payTypeStr.equals("DEBIT")){
                    //DEBIT= 借记卡       CREDIT=贷记卡
                    payType = "银行卡(借记卡)";
                }else if(payTypeStr.equals("CREDIT")){
                    //DEBIT= 借记卡       CREDIT=贷记卡
                    payType = "银行卡(贷记卡)";
                }else if(payTypeStr.equals("UNIONPAY")){
                    //UNIONPAY = 银联二维码
                    payType = "银联二维码";
                }else if(payTypeStr.equals("BANK")){
                    //BANK = 银行卡
                    payType = "银行卡";
                }else if(payTypeStr.equals("CASH")){
                    //CASH = 现金
                    payType = "现金";
                }else if(payTypeStr.equals("MEMBER")){
                    //CASH = 现金
                    payType = "会员";
                }else{
                    payType = "未知";
                }
            }
            tvPayWay.setText(payType);
            String displayStateStr = order.getDisplayStatus();
            String displayState = "未知";
            if(Utils.isNotEmpty(displayStateStr)) {
                if (displayStateStr.equals("0")) {

                    displayState = "初始创建";

                } else if (displayStateStr.equals("1")) {

                    displayState = "支付成功";

                } else if (displayStateStr.equals("2")) {

                    displayState = "支付失败";

                } else if (displayStateStr.equals("3")) {

                    displayState = "支付未知";

                }
            }
            tvOrderState.setText(displayState);
        }
    }


    /**
     * 获取充值记录详情
     */
    private void getRecodeDetails(){

        hintDialog = CustomDialog.CreateDialog(activity, "    加载中...");
        hintDialog.show();
        hintDialog.setCanceledOnTouchOutside(false);

        final String url = NitConfig.queryConsumeOrderDetailUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject job = new JSONObject();
                    job.put("orderId",order.getOrderId());
                    String content = job.toString();
                    Log.e("记录详情查询发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("记录详情查询返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_ONE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }

            };
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
            switch (msg.what) {
                case NetworkUtils.MSG_WHAT_ONE:
                    String recodeDetailStr = (String) msg.obj;
                    recodeDetailStr(recodeDetailStr);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        };
    };

    private void recodeDetailStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String code = job.getString("code");
            String msg = job.getString("msg");
            if("000000".equals(code)){
                String subCode = job.getString("subCode");
                String subMsg = job.getString("subMsg");
                if("000000".equals(subCode)){
                    String dataJson = job.getString("data");
                    JSONObject dataJob = new JSONObject(dataJson);
                    String imgUrl = dataJob.getString("imgUrl");
                    order.setImgUrl(imgUrl);

                }else{
                    if(Utils.isNotEmpty(subMsg)){
                        Toast.makeText(activity, subMsg, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                    }
                }

            }else{
                if(Utils.isNotEmpty(msg)){
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
            String printTextStr = FuyouPrintUtil.memPayRecodeDetailPrintText(activity, order,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
            FuyouPosServiceUtil.printTextReq(activity,printTextStr);

        }else if(printNum.equals("printNumTwo")){
            //打印两次
            String printTextStr = FuyouPrintUtil.memPayRecodeDetailPrintText(activity, order,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
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

                String printTextStr = FuyouPrintUtil.memPayRecodeDetailPrintText(activity, order,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
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
                            }

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.mempay_pay_recode_details_btOk:
                finish();
                break;
            case R.id.mempay_pay_recode_details_btPrint:
                if(Utils.isFastClick()){
                    return;
                }
                startPrint();
                break;
                default:
                    break;
        }
    }
}
