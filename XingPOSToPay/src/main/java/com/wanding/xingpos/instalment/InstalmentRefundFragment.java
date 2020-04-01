package com.wanding.xingpos.instalment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pull.refresh.view.XListView;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseFragment;
import com.wanding.xingpos.bean.InstalmentQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.Utils;

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
 * 查询订单
 */
@SuppressLint("ValidFragment")
public class InstalmentRefundFragment extends BaseFragment implements View.OnClickListener{
    private Dialog hintDialog;// 加载数据时对话框

    private int mCurIndex = -1;
    private static final String FRAGMENT_INDEX = "1";
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;
    private boolean onResume=true;//onResume()方法初始化不执行

    private EditText etOrderId;
    private TextView tvQuery;
    private TextView tvRefundHint;//退款说明


    private UserLoginResData posPublicData;

    /**
     * 2018071710024077133  这个订单号 刚刚分期的
     *
     * 2018071710194736925  这个也是刚刚分期的 第二个等明天改,可以看手续费
     *
     *
     */

    @SuppressLint("ValidFragment")
    public InstalmentRefundFragment(UserLoginResData posPublicData) {
        super();
        this.posPublicData = posPublicData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instalment_refund_fragment,null,false);
        initView(view);
        initListener();
        initData();

        isPrepared = true;
        lazyLoad();

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup)view.getParent();
        if(parent != null) {
            parent.removeView(view);
        }
        onResume=false;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onResume){
            //加载数据

        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible||mHasLoadedOnce) {
            return;
        }

    }

    /**
     * 初始化控件
     */
    private void initView(View view){
        etOrderId = view.findViewById(R.id.instalment_refund_etOrderId);
        tvQuery = view.findViewById(R.id.instalment_refund_tvQuery);
        tvRefundHint = view.findViewById(R.id.instalment_refund_tvRefundHint);
    }

    private void initListener(){
        tvQuery.setOnClickListener(this);
        tvRefundHint.setOnClickListener(this);
    }

    private void initData(){

    }

    /**
     *  查询订单
     */
    private void queryOrder(final String url,final String content){
        hintDialog= CustomDialog.CreateDialog(getContext(), "    查询中...");
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
                    Log.e("分期订单查询返回状态吗", code+"");
                    if(code == 200){
                        InputStream is = connection.getInputStream();
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("分期订单查询返回JSON值", jsonStr);
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
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
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


            String txnData = RequestUtil.queryReqTxnData(posPublicData,orderIdStr,inPublic);
            JSONObject txnObj = new JSONObject();
            txnObj.put("txnData", txnData);
            String signData=txnData+RequestUtil.getMd5key();
            txnObj.put("signValue", MD5.MD5Encode(signData));
            String content = txnObj.toString();
            Log.e("最终提交的参数：",content);
            //请求数据
            String url = NitConfig.instalmentServiceUrl;
//            String url = "http://sandbox.starpos.com.cn/installment";
            queryOrder(url,content);

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
            Gson gjson  =  GsonUtils.getGson();
            InstalmentQueryResData queryResData = gjson.fromJson(txnData, InstalmentQueryResData.class);
            String respCodeStr = queryResData.getRespCode();
            String respMsgStr = queryResData.getRespMsg();
            if(Utils.isNotEmpty(respCodeStr)){
                if("000000".equals(respCodeStr)){
                    Intent in = new Intent();
                    in.setClass(getContext(),InstalmentOrderRefundActivity.class);
                    in.putExtra("order",queryResData);
                    startActivity(in);
                }else{
                    Toast.makeText(getContext(),respMsgStr,Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getContext(),"查询出错！",Toast.LENGTH_LONG).show();
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
        Intent in = null;
        switch (v.getId()){
            case R.id.instalment_refund_tvQuery://查询
                String etOrderIdStr = etOrderId.getText().toString().trim();
                if(Utils.isEmpty(etOrderIdStr)){
                    Toast.makeText(getContext(),"请输入订单号查询！",Toast.LENGTH_LONG).show();
                    return;
                }
                //先获取封装的参数
                getReqParameter(etOrderIdStr);
                break;
            case R.id.instalment_refund_tvRefundHint://退款说明
                in = new Intent();
                in.setClass(getContext(),InstalmentRefundHintActivity.class);
                startActivity(in);
                break;
        }
    }


}
