package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.BatchSecurListResData;
import com.wanding.xingpos.bean.SecurDetailData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量制劵
 */
public class BatchSecurActivity extends BaseActivity implements View.OnClickListener {

    private Dialog hintDialog;// 加载数据时对话框
    private ImageView imgBack;
    private TextView tvTitle;

    private TextView tvSecurType;
    private EditText etPrintNum;

    private TextView tvOk;
    private TextView tvHint;

    private int REQUEST_CODE = 1;

    private UserLoginResData loginInitData;
    SecurDetailData securDetailData = null;
    /**
     * listIndex:list大于listIndex条时，以listIndex为下标分为两个list集合
     * list:服务端返回的全部数据
     * partList：打印数据大于listIndex条，取出的前listIndex条内容
     * afterList：打印数据大于listIndex条，取出的后面内容
     */
    private int listIndex = 10;
    List<String> list = null;
    List<String> frontList = new ArrayList<String>();
    List<String> afterList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.batch_secur_activtiy);

        try {
            loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initView();
    }

    /**
     * 初始化界面控件
     */
    private void initView(){

        imgBack = (ImageView) findViewById(R.id.title_imageBack);
        tvTitle = (TextView) findViewById(R.id.title_tvTitle);
        tvSecurType = (TextView) findViewById(R.id.batch_secur_tvSecurType);
        etPrintNum = (EditText) findViewById(R.id.batch_secur_etPrintNum);
        etPrintNum.setSelection(etPrintNum.getText().toString().length());
        tvOk = (TextView) findViewById(R.id.batch_secur_tvOk);

        tvHint = (TextView) findViewById(R.id.batch_secur_tvHint);

        tvTitle.setText("批量制劵");
        imgBack.setOnClickListener(this);
        tvSecurType.setOnClickListener(this);
        tvOk.setOnClickListener(this);

    }

    /**
     * 获取打印劵内容
     */
    private void getCouponPrintList(final int num){
        hintDialog=CustomDialog.CreateDialog(getContext(), "    加载中...");
        hintDialog.show();
        hintDialog.setCanceledOnTouchOutside(false);
        final String url = NitConfig.qRCodeUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("total",num+"");
                    userJSON.put("cardId",securDetailData.getWxcard_id());
                    userJSON.put("mid",loginInitData.getMid());
                    String content = String.valueOf(userJSON);
                    Log.e("发起请求参数：", content);
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
                    Log.e("返回状态吗：", code+"");
                    if(code == 200){

                        InputStream is = connection.getInputStream();
                        //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("返回JSON值：", jsonStr);
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=jsonStr;
                        mHandler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=404;
                        mHandler.sendMessage(msg);
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
                }   catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
                }

            };
        }.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String couponPrintListJsonStr=(String) msg.obj;
                    couponPrintListJsonStr(couponPrintListJsonStr);
                    if(hintDialog!=null&&hintDialog.isShowing()){
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
                    Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
            }
        };
    };

    private void couponPrintListJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                JSONObject dataJob = new JSONObject(dataJson);
                String urlListStr = dataJob.getString("urlList");
                list = com.alibaba.fastjson.JSONObject.parseObject(urlListStr,List.class);
//                for (int i = 0;i<list.size();i++){
//                    Log.e(TAG,list.get(i));
//                }
                //打印劵
                printCouponText(list);
            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开始打印
     */
    private void printCouponText(List<String> list){
        String printTextStr = "";
        if(list.size()>listIndex){
            //先取出10条打印，打印完继续打印剩余的内容
            frontList.clear();
            afterList.clear();
            for(int i = 0;i<listIndex;i++){
                String textStr = list.get(i);
                Log.e(TAG,textStr);
                frontList.add(textStr);
            }
            for (int j = listIndex;j<list.size();j++){
                String textStr = list.get(j);
                Log.e(TAG,textStr);
                afterList.add(textStr);
            }
            printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,frontList,loginInitData,securDetailData);
        }else{
            printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,list,loginInitData,securDetailData);
        }
        Intent intent=new Intent();
        intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
        intent.putExtra("data", printTextStr);
        intent.putExtra("isPrintTicket", "true");
        startActivityForResult(intent, FuyouPrintUtil.PRINT_REQUEST_CODE);
    }

    /**  重打印提示窗口 */
    private void showPrintTwoDialog(final int printNum){
        View view = LayoutInflater.from(activity).inflate(R.layout.option_hint_dialog, null);
        TextView hintNumMsg = (TextView) view.findViewById(R.id.option_hint_dialog_hintNumMsg);
        hintNumMsg.setVisibility(View.VISIBLE);
        hintNumMsg.setText("当前打印数量"+" "+printNum+""+"份");
        TextView tvHintMsg = (TextView) view.findViewById(R.id.option_hint_dialog_hintMsg);
        tvHintMsg.setText("是否确认打印？");
        TextView btCancel = (TextView) view.findViewById(R.id.option_hint_tvCancel);
        TextView btok = (TextView) view.findViewById(R.id.option_hint_tvOk);
        final Dialog myDialog = new Dialog(activity,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                myDialog.dismiss();

            }
        });
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getCouponPrintList(printNum);
                myDialog.dismiss();

            }
        });
        myDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 选择劵类型返回
         */
        if(requestCode == REQUEST_CODE){
            if(resultCode == CouponTypeListActivity.RESULT_CODE){
                Bundle bundle = data.getExtras();
                securDetailData = (SecurDetailData) bundle.getSerializable("securDetailData");
                if(securDetailData!=null){
                    tvSecurType.setText(securDetailData.getTitle());
                }
            }
        }
        /**
         * 打印返回
         */
        if(requestCode == FuyouPrintUtil.PRINT_REQUEST_CODE){
            Log.e(TAG,resultCode+"");
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    String reason = "";
                    String traceNo = "";
                    String batchNo = "";
                    String ordernumber = "";
                    if (data != null) {
                        Bundle b = data.getExtras();
                        if (b != null) {
                            reason = (String) b.get("reason");
                            traceNo = (String)b.getString("traceNo");
                            batchNo = (String)b.getString("batchNo");
                            ordernumber = (String)b.getString("ordernumber");
                        }
                    }
                    if (reason != null) {
                        Log.e("reason", reason);
                        if(FuyouPrintUtil.ERROR_NONE == Integer.valueOf(reason)){
                            //打印正常
                            if(afterList!=null){
                                if(afterList.size()>0){
                                    String printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,afterList,loginInitData,securDetailData);
                                    Intent intent=new Intent();
                                    intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
                                    intent.putExtra("data", printTextStr);
                                    intent.putExtra("isPrintTicket", "true");
                                    startActivityForResult(intent, FuyouPrintUtil.PRINT_REQUEST_CODE);
                                    afterList.clear();
                                }
                            }
                        }else if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
                            //缺纸，不能打印
                            ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
                        }else {
                            ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
                        }

                    }
                    Log.w("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

                    tvHint.setText("失败："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);

                    break;
                case Activity.RESULT_OK:
                    //Activity.RESULT_OK:经过测试发现该分支只返回支付相关业务


                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
            case R.id.batch_secur_tvSecurType:
                intent.setClass(activity,CouponTypeListActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                intent.putExtra("securDetailData",securDetailData);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.batch_secur_tvOk:
                if(Utils.isFastClick()){
                    return;
                }
                String etPrintNumStr = etPrintNum.getText().toString().trim();
                int printNum = 0;
                if(securDetailData == null){
                    ToastUtil.showText(activity,"请选择劵类型！",1);
                    return;
                }
                if(Utils.isEmpty(etPrintNumStr)){
                    ToastUtil.showText(activity,"请输入打印数量！",1);
                    return;
                }
                printNum = Integer.valueOf(etPrintNumStr);
                if(printNum == 0){
                    ToastUtil.showText(activity,"打印数量最小为1 ！",1);
                    return;
                }
                if(printNum>20){
                    ToastUtil.showText(activity,"打印数量不能超过20条！",1);
                    return;
                }
                showPrintTwoDialog(printNum);

                break;
                default:
                    break;
        }
    }
}
