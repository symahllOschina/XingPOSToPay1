package com.wanding.xingpos.instalment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.wanding.xingpos.util.BitmapUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.PhotoModule;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  查询订单详情界面
 */
public class InstalmentOrderDetailActivity extends BaseActivity implements View.OnClickListener
{
    private Dialog hintDialog;// 加载数据时对话框
    private ImageView imgBack;
    private TextView tvTitle;

    private TextView
            tvOrderNum,tvTxnTime,tvAccName,tvTxnAmt,tvTxnterms,tvSumMoney,tvSumAmount,
            tvSumTerms,tvRemainAmount,tvRefundMoney,
            tvCancelInterest,tvVocher,tvStatus;

    private Button btUpload;//上传
    private PhotoModule mPhotoModule;
    private final int CROP_SMALL_PICTURE = 0x1003;   //这里的CROP_SMALL_PICTURE是自己任意定义的

    private InstalmentQueryResData order;
    private UserLoginResData posPublicData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instalment_details_activity);


        initView();
        initListener();
        initData();


    }

    /**  初始化控件 */
    private void initView(){
        imgBack = findViewById(R.id.title_imageBack);
        tvTitle = findViewById(R.id.title_tvTitle);
        tvTitle.setText("分期明细");

        tvOrderNum = findViewById(R.id.instalment_details_tvOrderNum);
        tvTxnTime = findViewById(R.id.instalment_details_tvTxnTime);
        tvAccName = findViewById(R.id.instalment_details_tvAccName);
        tvTxnAmt = findViewById(R.id.instalment_details_tvTxnAmt);
        tvTxnterms = findViewById(R.id.instalment_details_tvTxnterms);
        tvSumMoney = findViewById(R.id.instalment_details_tvSumMoney);
        tvSumAmount = findViewById(R.id.instalment_details_tvSumAmount);
        tvSumTerms = findViewById(R.id.instalment_details_tvSumTerms);
        tvRemainAmount = findViewById(R.id.instalment_details_tvRemainAmount);
        tvRefundMoney = findViewById(R.id.instalment_details_tvRefundMoney);
        tvCancelInterest = findViewById(R.id.instalment_details_tvCancelInterest);
        btUpload = findViewById(R.id.instalment_details_btUpload);
        tvVocher = findViewById(R.id.instalment_details_tvVocher);
        tvStatus = findViewById(R.id.instalment_details_tvStatus);
    }

    /** 事件监听注册 */
    private void initListener(){
        imgBack.setOnClickListener(this);
        btUpload.setOnClickListener(this);
    }

    /** 数据初始化 */
    private void initData(){
        mPhotoModule = new PhotoModule(this);

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
        //退款金额
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
        //退款利息
        String cancelInterestStr = order.getCancelInterest();
        String cancelInterest = "0.00";
        if(Utils.isNotEmpty(cancelInterestStr)){
            if(cancelInterestStr.equals("-1")){
                cancelInterest = "0.00";
            }else{
                cancelInterest = DecimalUtil.branchToElement(cancelInterestStr);
            }
        }
        tvCancelInterest.setText(cancelInterest);
        //vocher是否上传退款凭证 1已上传，其余未上传"vocher": null,
        String vocherStr = order.getVocher();
        setButtonStyles(vocherStr);
        //状态：//contractsState:0,1未结清 2,3,4已结清 5已结清（退款操作）
        String orderTypeStr = order.getContractsState();
        //退款状态：0退款失败，1退款成功，2人工审核，无值或为null时表示没有退款行为"state": null,
        String stateStr = order.getState();
        String status = "";
        if(Utils.isNotEmpty(orderTypeStr)){
            if("0".equals(orderTypeStr) || "1".equals(orderTypeStr)){
                if(Utils.isNotEmpty(stateStr)){
                    if("1".equals(stateStr)){
                        status = "退款成功";
                    }else if("2".equals(stateStr)){
                        status = "人工审核";
                    }else{
                        status = "还款中";
                    }
                }else{
                    status = "还款中";
                }
            }else if("2".equals(orderTypeStr) || "3".equals(orderTypeStr) || "4".equals(orderTypeStr)){
                status = "已结清";
            }else if("5".equals(orderTypeStr)){
                status = "退款已结清";
            }
        }
        tvStatus.setText(status);
    }

    /**
     * 更改界面上的上传按钮状态
     */
    private void setButtonStyles(String vocherStr){

        String vocher = "未上传";
        btUpload.setBackgroundColor(getResources().getColor(R.color.green_65db4e));
        btUpload.setTextColor(getResources().getColor(R.color.white_FFFFFF));
        btUpload.setEnabled(true);
        if(Utils.isNotEmpty(vocherStr)){
            if("1".equals(vocherStr)){
                vocher = "已上传";
                btUpload.setBackgroundColor(getResources().getColor(R.color.gray_e5e5e5));
                btUpload.setTextColor(getResources().getColor(R.color.white_FFFFFF));
                btUpload.setEnabled(false);
            }
        }
        tvVocher.setText(vocher);

    }

    /**
     * 开始上传
     */
    private void upload(final String url,final String content){
        hintDialog= CustomDialog.CreateDialog(getContext(), "    正在上传...");
        hintDialog.show();
        hintDialog.setCancelable(false);
        new Thread(){
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();
                    connection.setConnectTimeout(60000);
                    connection.setReadTimeout(60000);
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Fiddler");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Charset", "UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(content.getBytes());
                    os.close();
                    int  code = connection.getResponseCode();
                    Log.e("凭证上传返回状态吗", code+"");
                    if(code == 200){
                        InputStream is = connection.getInputStream();
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("凭证上传返回JSON值", jsonStr);
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
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String jsonStr = (String) msg.obj;
                    //使用私钥解密返回的响应内容
                    getResponseStr(jsonStr);
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
                    Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();

                    break;
            }
        };
    };

    /**
     * 获取上传的txnData参数值
     */
    private void getReqParameter(Bitmap bt){
        //订单号
        String orderIdStr = order.getOrderId();
        //十六进制的图片
        String bitmapStr = BitmapUtil.BitmapToString(bt);

        // 从文件中得到公钥字符串
        try {
            InputStream inPublic;
            if("true".equals(NitConfig.isTest)){
                inPublic = getContext().getResources().getAssets().open("reqsdk_rsa_public_key.pem");
            }else{
                inPublic = getContext().getResources().getAssets().open("req_rsa_public_key.pem");
            }


            String txnData = RequestUtil.upLoadReqTxnData(posPublicData,orderIdStr,bitmapStr,inPublic);
            JSONObject txnObj = new JSONObject();
            txnObj.put("txnData", txnData);
            String signData=txnData+RequestUtil.getMd5key();
            txnObj.put("signValue", MD5.MD5Encode(signData));
            String content = txnObj.toString();
            Log.e("最终提交的参数：",content);
            //请求数据
            String url = NitConfig.instalmentServiceUrl;
//            String url = "http://sandbox.starpos.com.cn/installment";
            upload(url,content);

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
                    //更改按钮状态
                    String vocherStr = "1";
                    setButtonStyles(vocherStr);

                }else{
                    Toast.makeText(getContext(),respMsgStr,Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(),"上传失败！",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        Uri tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 裁剪之后的图片数据
     *
     * @param
     *
     * @param data
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bm = extras.getParcelable("data");
            Log.e("Bitmap的值",bm+"");
            //先获取封装的参数
            getReqParameter(bm);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoModule.REQUEST_CODE_CROP_PHOTO:
//				Bitmap photo = data.getParcelableExtra("data");
//				iv_setting_photo.setImageBitmap(photo);
//				mPhotoModule.savePhoto(photo);
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case PhotoModule.REQUEST_CODE_CAMERA:
                    Intent i = new Intent("com.android.camera.action.CROP");
                    i.setDataAndType(Uri.fromFile(mPhotoModule.getTempPhoto()), "image/*");
                    i.putExtra("crop", "true");
                    i.putExtra("aspectX", 1);
                    i.putExtra("aspectY", 1);
                    i.putExtra("outputX", 150);
                    i.putExtra("outputY", 150);
                    i.putExtra("return-data", true);
                    startActivityForResult(i, CROP_SMALL_PICTURE);
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
            case R.id.instalment_details_btUpload://上传
                mPhotoModule.takePhoto(PhotoModule.REQUEST_CODE_CAMERA);
                break;
        }
    }
}
