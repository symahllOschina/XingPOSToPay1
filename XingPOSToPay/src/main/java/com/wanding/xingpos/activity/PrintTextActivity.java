package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.gson.Gson;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.PosPrintTemplate;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;

import java.io.IOException;

/**
 * 打印机工作界面
 */
@ContentView(R.layout.activity_print_text)
public class PrintTextActivity extends BaseActivity {


    private UserLoginResData loginInitData;

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

    String dataSourceStr = "未知";
    PosPrintTemplate order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        try {
            loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //取出设置的打印值
        SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(activity, "printing");
        //取出保存的默认值
        printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
        isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
        Log.e("取出保存的打印值", printNum);

        Intent intent = getIntent();
        String dataStr = intent.getStringExtra("data");
        Log.e(TAG,dataStr);
        if(Utils.isNotEmpty(dataStr)){
            parsingJson(dataStr);
        }

    }

    private void parsingJson(String dataStr){
        try {
            JSONObject job = new JSONObject(dataStr);
            String msg = job.getString("msg");
            String templateId = job.getString("templateId");

            if(Utils.isNotEmpty(templateId)){
                if(templateId.equals(Constants.KSMD001)){
                    dataSourceStr = "会员消费";
                }else if(templateId.equals(Constants.TK001)){
                    dataSourceStr = "会员消费";
                }else if(templateId.equals(Constants.XSFFGK001)){
                    dataSourceStr = "线上付费购卡";
                }else if(templateId.equals(Constants.XXFFGK001)){
                    dataSourceStr = "线下付费购卡";
                }else if(templateId.equals(Constants.XSCZ001)){
                    dataSourceStr = "线上充值";
                }else if(templateId.equals(Constants.TG001)){
                    dataSourceStr = "在线预订";
                }
            }
            if(Utils.isNotEmpty(msg)){
                Gson gjson  =  GsonUtils.getGson();
                order = gjson.fromJson(msg, PosPrintTemplate.class);
                startPrintActivity();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showText(activity,"打印数据错误！",1);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showText(activity,"打印数据错误！",1);
            finish();
        }
    }


    private void startPrintActivity(){
        String printTextStr = FuyouPrintUtil.pussPrintText(dataSourceStr,order,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
        FuyouPosServiceUtil.printTextReq(activity,printTextStr);
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
                String printTextStr = FuyouPrintUtil.pussPrintText(dataSourceStr,order,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
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
                finish();
            }

        }


    }
}
