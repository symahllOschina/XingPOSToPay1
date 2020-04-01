package com.wanding.xingpos.instalment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.InstalmentQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  退款查询订单详情界面（可申请退款）
 */
public class InstalmentOrderRefundActivity extends BaseActivity implements View.OnClickListener
{
    private Dialog hintDialog;// 加载数据时对话框

    private ImageView imgBack;
    private TextView tvTitle;

    private TextView
            tvOrderNum,tvTxnTime,tvAccName,tvTxnAmt,tvTxnterms,tvSumMoney,tvSumAmount,
            tvSumTerms,tvRemainAmount,tvRefundMoney,
            tvCancelInterest;
    private Button btApplyRefund;//申请退款

    private InstalmentQueryResData order;
    private UserLoginResData posPublicData;
    private boolean startService = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instalment_refund_activity);

        initView();
        initListener();
        initData();
    }

    /**  初始化控件 */
    private void initView(){
        imgBack = findViewById(R.id.title_imageBack);
        tvTitle = findViewById(R.id.title_tvTitle);
        tvTitle.setText("订单详情");

        tvOrderNum = findViewById(R.id.instalment_refund_tvOrderNum);
        tvTxnTime = findViewById(R.id.instalment_refund_tvTxnTime);
        tvAccName = findViewById(R.id.instalment_refund_tvAccName);
        tvTxnAmt = findViewById(R.id.instalment_refund_tvTxnAmt);
        tvTxnterms = findViewById(R.id.instalment_refund_tvTxnterms);
        tvSumMoney = findViewById(R.id.instalment_refund_tvSumMoney);
        tvSumAmount = findViewById(R.id.instalment_refund_tvSumAmount);
        tvSumTerms = findViewById(R.id.instalment_refund_tvSumTerms);
        tvRemainAmount = findViewById(R.id.instalment_refund_tvRemainAmount);
        tvRefundMoney = findViewById(R.id.instalment_refund_tvRefundMoney);
        tvCancelInterest = findViewById(R.id.instalment_refund_tvCancelInterest);


        btApplyRefund = findViewById(R.id.instalment_refund_btApplyRefund);
    }

    /** 事件监听注册 */
    private void initListener(){
        imgBack.setOnClickListener(this);
        btApplyRefund.setOnClickListener(this);
    }

    /** 数据初始化 */
    private void initData(){
        try {
            posPublicData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", this));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent in = getIntent();
        order = (InstalmentQueryResData) in.getSerializableExtra("order");
        updateView();
    }

    /**
     * 界面赋值
     */
    private void updateView(){
        //订单编号
        String orderIdStr = order.getOrderId();
        String orderId = "";
        if(Utils.isNotEmpty(orderIdStr)){
            orderId = orderIdStr;
        }
        tvOrderNum.setText(orderId);
        //订单交易时间
        String orderTimeStr = order.getTxnTime();
        String orderPayTime = "";
        if(Utils.isNotEmpty(orderTimeStr)){
            orderPayTime = DateTimeUtil.timeStrToFormatDateStr(orderTimeStr, "yyyy.MM.dd");
        }
        tvTxnTime.setText(orderPayTime);
        //持卡人姓名
        String accNameStr = order.getAccName();
        String accName = "";
        if(Utils.isNotEmpty(accNameStr)){
            accName = accNameStr;
        }
        tvAccName.setText(accName);
        //原交易金额
        String orderTotalStr = order.getTxnAmt();
        String orderTotal = "0.00";
        if(Utils.isNotEmpty(orderTotalStr)){
            //分转元
            orderTotal = DecimalUtil.branchToElement(orderTotalStr);
        }
        tvTxnAmt.setText(orderTotal);
        //分期期数
        String txntermsStr = order.getTxnterms();
        String txnterms = "";
        if(Utils.isNotEmpty(txntermsStr)){
            txnterms = txntermsStr;
        }
        tvTxnterms.setText(txnterms);
        //应还款金额
        String totleAmountStr = order.getTotleAmount();
        String totleAmount = "0.00";
        if(Utils.isNotEmpty(totleAmountStr)){
            //分转元
            totleAmount = DecimalUtil.branchToElement(totleAmountStr);
        }
        tvSumMoney.setText(orderTotal);
        //已还款金额
        String sumAmountStr = order.getSumAmount();
        String sumAmount = "0.00";
        if(Utils.isNotEmpty(sumAmountStr)){
            //分转元
            sumAmount = DecimalUtil.branchToElement(sumAmountStr);
        }
        tvSumAmount.setText(sumAmount);
        //已还期数
        String sumTermsStr = order.getSumTerms();
        String sumTerms = "";
        if(Utils.isNotEmpty(sumTermsStr)){
            sumTerms = sumTermsStr;
        }
        tvSumTerms.setText(sumTerms);
        //未还款金额
        String remainAmountStr = order.getRemainAmount();
        String remainAmount = "0.00";
        if(Utils.isNotEmpty(remainAmountStr)){
            remainAmount = DecimalUtil.branchToElement(remainAmountStr);
        }
        tvRemainAmount.setText(remainAmount);
        //商户退款金额
        String cancelAmountStr = order.getCancelAmount();
        String cancelAmount = "0.00";
        if(Utils.isNotEmpty(cancelAmountStr)){
            if("-1".equals(cancelAmountStr)){
                //等于原交易金额
                cancelAmount = DecimalUtil.branchToElement(orderTotalStr);
            }else{
                cancelAmount = DecimalUtil.branchToElement(cancelAmountStr);
            }
        }
        tvRefundMoney.setText(cancelAmount);
        //商户退款利息
        String cancelInterestStr = order.getCancelInterest();
        String cancelInterest = "0.00";
        if(Utils.isNotEmpty(cancelInterestStr)){
            if("-1".equals(cancelInterestStr)){
                cancelInterest = "0.00";
            }else{
                cancelInterest = DecimalUtil.branchToElement(cancelInterestStr);
            }

        }
        tvCancelInterest.setText(cancelInterest);
        //申请退款
        //状态：//contractsState:0,1未结清 2,3,4已结清 5已结清（退款操作）
        String orderTypeStr = order.getContractsState();
        //退款状态：0退款失败，1退款成功，2人工审核，无值或为null时表示没有退款行为"state": null,
        String stateStr = order.getState();
        setApplyRefundBtn(orderTypeStr,stateStr);
    }

    /**
     * 申请退款按钮状态
     * contractsState的为1时可以申请退款，提交申请之后，contractsState的值不变化，state的值会发生改变
     * contractsState 为5表示退款已完成  如果当天退款是全额退  返回的可退金额为-1，实际是全额，隔日退款会有手续费
     * state的值会随着提交申请退款后而发生变化，如果退款失败contractsState的值不变，可以继续退款
     */
    private void setApplyRefundBtn(String orderTypeStr,String stateStr){
        String status = "申请退款";
        boolean isOnClick = true;
        if(Utils.isNotEmpty(orderTypeStr)){
            if("0".equals(orderTypeStr) || "1".equals(orderTypeStr)){
                if(Utils.isNotEmpty(stateStr)){
                    if("1".equals(stateStr)){
                        isOnClick = false;
                        status = "退款成功";
                    }else if("2".equals(stateStr)){
                        isOnClick = false;
                        status = "人工审核";
                    }else{
                        status = "申请退款";
                    }
                }else{
                    status = "申请退款";
                }
            }else if("2".equals(orderTypeStr) || "3".equals(orderTypeStr) || "4".equals(orderTypeStr)){
                isOnClick = false;
                status = "已结清";
            }else if("5".equals(orderTypeStr)){
                isOnClick = false;
                status = "退款已结清";
            }
        }
        btApplyRefund.setText(status);
        if(isOnClick){
            btApplyRefund.setBackgroundColor(getResources().getColor(R.color.green_65db4e));
            btApplyRefund.setTextColor(getResources().getColor(R.color.white_FFFFFF));
            btApplyRefund.setEnabled(true);
        }else{
            btApplyRefund.setBackgroundColor(getResources().getColor(R.color.gray_e5e5e5));
            btApplyRefund.setTextColor(getResources().getColor(R.color.white_FFFFFF));
            btApplyRefund.setEnabled(false);
        }
    }

    /**
     * 申请退款
     *
     */
    private void applyRerund(final String url,final String content){
        hintDialog= CustomDialog.CreateDialog(getContext(), "    申请中...");
        hintDialog.show();
        hintDialog.setCancelable(false);
        new Thread(){
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Fiddler");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Charset", "UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(content.getBytes());
                    os.close();
                    int  code = connection.getResponseCode();
                    Log.e("分期申请退款返回状态吗", code+"");
                    if(code == 200){
                        InputStream is = connection.getInputStream();
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("分期申请退款返回JSON值", jsonStr);
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=jsonStr;
                        mHandler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=404;
                        mHandler.sendMessage(msg);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("IOException", "IOException");
                }

            };
        }.start();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String jsonStr = (String) msg.obj;
                    //使用私钥解密返回的响应内容
                    getResponseStr(jsonStr);
                    startService = true;
                    if(hintDialog!=null){
                        hintDialog.dismiss();
                    }
                    break;
                case 201:

                    Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
                    break;
                case 202:

                    Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
                    break;
                case 404:
                    if(hintDialog!=null){
                        hintDialog.dismiss();
                    }
                    startService = true;
                    Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();

                    break;
            }
        };
    };

    /**
     * 获取封装参数转换为请求参数
     */
    private void getReqParameter(String orderIdStr){

        // 从文件中得到公钥字符串
        try {
            InputStream inPublic;
            if("true".equals(NitConfig.isTest)){
                inPublic = getContext().getResources().getAssets().open("reqsdk_rsa_public_key.pem");
            }else{
                inPublic = getContext().getResources().getAssets().open("req_rsa_public_key.pem");
            }

            String txnData = RequestUtil.refundReqTxnData(posPublicData,orderIdStr,inPublic);
            JSONObject txnObj = new JSONObject();
            txnObj.put("txnData", txnData);
            String signData=txnData+RequestUtil.getMd5key();
            txnObj.put("signValue", MD5.MD5Encode(signData));
            String content = txnObj.toString();
            Log.e("最终提交的参数：",content);
            //请求数据
            String url = NitConfig.instalmentServiceUrl;
//            String url = "http://sandbox.starpos.com.cn/installment";
            applyRerund(url,content);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解密响应内容
     */
    private void getResponseStr(String jsonStr){

        try {
            JSONObject job = new JSONObject(jsonStr);
            String respTxnData = job.getString("txnData");
            // 从文件中得到私钥字符串
            InputStream inPublic;
            if("true".equals(NitConfig.isTest)){
                inPublic = getContext().getResources().getAssets().open("respsdk_pkcs8_rsa_private_key.pem");
            }else{
                inPublic = getContext().getResources().getAssets().open("resp_pkcs8_rsa_private_key.pem");
            }

            String txnData = RequestUtil.getResponseJsonStr(respTxnData,inPublic);
            Log.e("解密后的txnData字符串",txnData);
            JSONObject dataJob = new JSONObject(txnData);
            String respCodeStr = dataJob.getString("respCode");
            String respMsgStr = dataJob.getString("respMsg");
            if(Utils.isNotEmpty(respCodeStr)){
                if("000000".equals(respCodeStr)){
                    //状态：//contractsState:0,1未结清 2,3,4已结清 5已结清（退款操作）
                    String orderTypeStr = order.getContractsState();
                    //退款状态：0退款失败，1退款成功，2人工审核，无值或为null时表示没有退款行为"state": null,
                    String stateStr = dataJob.getString("state");
                    setApplyRefundBtn(orderTypeStr,stateStr);
                }else{
                    Toast.makeText(getContext(),respMsgStr,Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(),"申请失败！",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
            case R.id.instalment_refund_btApplyRefund://申请退款
                if(startService){
                    startService = false;
                    String etOrderIdStr = order.getOrderId();
                    //先获取封装的参数
                    getReqParameter(etOrderIdStr);
                }

                break;
        }
    }
}
