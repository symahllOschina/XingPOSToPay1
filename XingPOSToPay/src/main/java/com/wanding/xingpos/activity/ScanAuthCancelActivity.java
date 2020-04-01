package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.bean.AuthConfirmReqDate;
import com.wanding.xingpos.bean.AuthResultResponse;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WdPreAuthHistoryVO;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
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
 *  预授权撤销
 **/
@ContentView(R.layout.scan_auth_cancel_activity)
public class ScanAuthCancelActivity extends BaseActivity implements View.OnClickListener{
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

    @ViewInject(R.id.auth_cancel_tvOrderIdTitle)
    TextView tvAuthCodeTitle;
    @ViewInject(R.id.auth_cancel_imgScan)
    ImageView imgScan;
    @ViewInject(R.id.auth_cancel_etOrderId)
    EditText etAuthCode;
    @ViewInject(R.id.auth_cancel_etCancelMoney)
    EditText etAuthMoney;
    @ViewInject(R.id.auth_cancel_ok)
    Button tvSubmit;

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
     * 打印小票第index联
     */
    private int index = 1;

    /**
     * 支付方式
     */
    private String payType;

    private UserLoginResData userLoginResData;

    AuthResultResponse authResultResponse;

    /**
     * 预授权订单对象
     */
    private WdPreAuthHistoryVO order;
    /**
     * 预授权业务操作
     * authAction:1 :预授权，2：预授权撤销，3：预授权完成，4：预授权完成撤销
     */
    private String authAction = "";

    String authCodeStr;
    String authMoneyStr;
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;

    AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;

    /**
     * 新大陆(打印机，扫码摄像头)AIDL服务
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
        }
    };

    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private static final String NEW_LAND = "newland";
    private static final String FUYOU_SF= "fuyousf";
    private String posProvider;
    private String scanCodeStr;//扫描返回结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posProvider = MainActivity.posProvider;
        if(posProvider.equals(NEW_LAND)){
            //绑定打印机服务
            bindServiceConnection();
        }else if(posProvider.equals(FUYOU_SF)){

        }

        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("扫码预授权");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        synthesizer = MainActivity.synthesizer;
        Intent intent = getIntent();
        order = (WdPreAuthHistoryVO) intent.getSerializableExtra("order");
        authAction = intent.getStringExtra("authAction");
        try {
            userLoginResData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
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

        if(order!=null){
            String orderId = order.getMchntOrderNo();
            if(Utils.isNotEmpty(orderId)){
                etAuthCode.setText(orderId);
                etAuthCode.setEnabled(false);
                etAuthCode.setSelection(etAuthCode.getText().toString().length());
                imgScan.setVisibility(View.INVISIBLE);
            }

        }
        if(Constants.AUTH_CANCEL_ACTION.equals(authAction)){
            tvTitle.setText("预授权撤销");
            etAuthMoney.setText("全额撤销");
            etAuthMoney.setEnabled(false);
        }else if(Constants.AUTH_CONFIRM_ACTION.equals(authAction)){
            tvTitle.setText("预授权完成");
        }else if(Constants.AUTH_CONFIRM_CANCEL_ACTION.equals(authAction)){
            tvTitle.setText("预授权完成撤销");
        }else{
            tvTitle.setText("扫码预授权");
        }

        EditTextUtils.setPricePoint(etAuthMoney);
        initListener();


    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(posProvider.equals(NEW_LAND)){
            unbindService(serviceConnection);
            aidlPrinter=null;
            aidlScanner=null;
        }else if(posProvider.equals(FUYOU_SF)){

        }
        Log.e(TAG, "释放资源成功");
    }

    /**
     *  所有事件注册监听
     */
    private void initListener(){
        imgBack.setOnClickListener(this);
        imgScan.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    /**
     * 绑定服务
     */
    public void bindServiceConnection() {
        bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
        Log.e("绑定服务", "绑定服务1");
    }

    /**
     * 初始化打印设备
     */
    public void getPrinter() {
        Log.i(TAG, "获取打印机设备实例...");
        try {
            aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
//            showMsgOnTextView("初始化打印机实例");
        } catch (RemoteException e) {
            e.printStackTrace();
            ToastUtil.showText(activity,"打印机调起失败！",1);
        }

    }

    /**
     * 初始化扫码设备
     */
    public void initScanner() {
        try {
            if (aidlScanner == null){
                aidlScanner = AidlScanner.Stub.asInterface(aidlDeviceService.getScanner());
            }
//            showMsgOnTextView("初始化打扫码实例");
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    /**
     * 前置扫码
     */
    public void frontscan(){
        try {
            Log.i(TAG, "-------------scan-----------");
            aidlScanner= AidlScanner
                    .Stub.asInterface(aidlDeviceService.getScanner());
            int time=10;//超时时间
            aidlScanner.startScan(ScanConstant.ScanType.FRONT, time, new AidlScannerListener.Stub() {

                @Override
                public void onScanResult(String[] arg0) throws RemoteException {
//                    showMsgOnTextView("onScanResult-----"+arg0[0]);
                    Log.w(TAG,"onScanResult-----"+arg0[0]);
                    scanCodeStr = arg0[0];
                    //如果扫描的二维码为空则不执行支付请求
                    if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
                        String auth_no = scanCodeStr;
                        Log.e("前置扫码值：", auth_no);
                        int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
                        String text = auth_no;
                        sendMessage(msg,text);

                    }else{
                        Log.e("后置扫码值：", "为空");
                        ToastUtil.showText(activity,"扫码取消或失败！",1);
                    }

                }

                @Override
                public void onFinish() throws RemoteException {

                    Log.e(TAG, "onFinish()...");
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
//                    ToastUtil.showText(activity,"扫码出现错误！",1);
                    Log.e(TAG, "onError()...");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showText(activity,"扫码失败！",1);
            Log.e(TAG, "catch_Exception()...");
        }
    }

    /**
     * 后置扫码
     */
    public void backscan(){
        try {
            Log.i(TAG, "-------------scan-----------");
            aidlScanner= AidlScanner
                    .Stub.asInterface(aidlDeviceService.getScanner());
            aidlScanner.startScan(ScanConstant.ScanType.BACK, 10, new AidlScannerListener.Stub() {

                @Override
                public void onScanResult(String[] arg0) throws RemoteException {
//					showMsgOnTextView("onScanResult-----"+arg0[0]);
                    Log.w(TAG,"onScanResult-----"+arg0[0]);

                    scanCodeStr = arg0[0];
                    if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
                        //auth_no	授权码（及扫描二维码值）
                        String auth_no = scanCodeStr;
                        Log.e("后置扫码值：", auth_no);

                        int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
                        String text = auth_no;
                        sendMessage(msg,text);

                    }else{

                        Log.e("后置扫码值：", "为空");
                        ToastUtil.showText(activity,"扫码取消或失败！",1);
                    }

                }

                @Override
                public void onFinish() throws RemoteException {
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
//                    ToastUtil.showText(activity,"扫码出现错误！",1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showText(activity,"扫码失败！",1);
        }
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String text) {

        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    /**
     * 暂停播放
     */
    private void stop() {

        int result = synthesizer.stop();
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
            Log.e("error code :", result+" method:" + method );
        }
    }

    /**
     * 预授权完成
     */
    private void authConfirm(){
        showWaitDialog();
        //支付时金额以分为单位
        String totalFeeStr = DecimalUtil.elementToBranch(authMoneyStr);
        final AuthConfirmReqDate request = PayRequestUtil.authConfirmReq(userLoginResData,authCodeStr,totalFeeStr);
        final String url = NitConfig.authConfirmUrl;
        Log.e(TAG,"请求地址："+url);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(request);
                    Log.e("请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回结果：", jsonStr);
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
            }
        }.start();
    }

    /**
     * 预授权撤销
     */
    private void authCancel() {
        showWaitDialog();
        final String url = NitConfig.authCancelUrl;
        final AuthConfirmReqDate request = PayRequestUtil.authCancelReq(userLoginResData,authCodeStr);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(request);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_TWO;
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
            }
        }.start();

    }

    /**
     * 预授权完成撤销
     */
    private void authConfirmCancel() {
        showWaitDialog();
        final String url = NitConfig.authConfirmCancelUrl;
        //支付时金额以分为单位
        String totalFeeStr = DecimalUtil.elementToBranch(authMoneyStr);
        final AuthConfirmReqDate request = PayRequestUtil.authConfirmCancelReq(userLoginResData,authCodeStr,totalFeeStr);
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(request);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_THREE;
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
            }
        }.start();

    }



    public void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }




    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONEHUNDRED:
                    String code_msg = (String) msg.obj;
                    etAuthCode.setText("");
                    etAuthCode.setText(code_msg);
                    etAuthCode.setSelection(etAuthCode.getText().toString().length());
                    break;
                case NetworkUtils.MSG_WHAT_ONE:
                    String authConfirmJson = (String) msg.obj;
                    authConfirmJson(authConfirmJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.MSG_WHAT_TWO:
                    String authCancelJson = (String) msg.obj;
                    authCancelJson(authCancelJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.MSG_WHAT_THREE:
                    String authConfirmCancelJson=(String) msg.obj;
                    authConfirmJson(authConfirmCancelJson);
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);

                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);

                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);

                    hideWaitDialog();
                    break;
                default:
                    finish();
                    break;
            }
        }
    };

    private void authConfirmJson(String jsonStr){

        try{
            Gson gjson  =  GsonUtils.getGson();
            authResultResponse = gjson.fromJson(jsonStr, AuthResultResponse.class);
            //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
            String return_codeStr = authResultResponse.getReturn_code();
            //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
            String return_msgStr = authResultResponse.getReturn_msg();
            //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
            String result_codeStr = authResultResponse.getResult_code();
            String result_msgStr = authResultResponse.getResult_msg();
            if("01".equals(return_codeStr)) {
                if("01".equals(result_codeStr)){
                    //语音提示
                    startSpeakMsg();
                    //打印小票
                    startPrint();
                    ToastUtil.showText(activity, "交易成功！", 1);

                }else{
                    Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(activity, return_msgStr, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            speak("交易结果返回错误");
            ToastUtil.showText(activity,"交易结果返回错误！",1);

        }
        finish();

    }

    private void authCancelJson(String jsonStr){
        try{
            Gson gjson  =  GsonUtils.getGson();
            authResultResponse = gjson.fromJson(jsonStr, AuthResultResponse.class);
            //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
            String return_codeStr = authResultResponse.getReturn_code();
            //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
            String return_msgStr = authResultResponse.getReturn_msg();
            //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
            String result_codeStr = authResultResponse.getResult_code();
            String result_msgStr = authResultResponse.getResult_msg();
            if("01".equals(return_codeStr)) {
                if("01".equals(result_codeStr)){
                    if(Utils.isEmpty(authResultResponse.getTotal_amount())){
                        if(Utils.isNotEmpty(order.getOrderAmt())){
                            String totalFee = DecimalUtil.StringToPrice(order.getOrderAmt());
                            String totalAmount = DecimalUtil.elementToBranch(totalFee);
                            authResultResponse.setTotal_amount(totalAmount);
                        }else{
                            authResultResponse.setTotal_amount("");
                        }

                    }
                    //语音提示
                    startSpeakMsg();
                    //打印小票
                    startPrint();
                    ToastUtil.showText(activity, "交易成功！", 1);

                }else{
                    Toast.makeText(activity, result_msgStr, Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(activity, return_msgStr, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            speak("交易结果返回错误");
            ToastUtil.showText(activity,"交易结果返回错误！",1);

        }
        finish();
    }
    private void authConfirmCancelJson(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startSpeakMsg(){
        try{
            if(Utils.isNotEmpty(authAction))
            {
                if("1".equals(authAction))
                {



                }else if("2".equals(authAction))
                {
                    String totalFee = authResultResponse.getTotal_amount();
                    if(Utils.isNotEmpty(totalFee)){
                        speak("交易成功，预授权撤销"+DecimalUtil.branchToElement(totalFee)+"元");
                    }else{
                        speak("交易成功");
                    }



                }else if("3".equals(authAction))
                {
                    String totalFee = authResultResponse.getConsume_fee();
                    if(Utils.isNotEmpty(totalFee)){
                        speak("交易成功，预授权完成"+DecimalUtil.branchToElement(totalFee)+"元");
                    }else{
                        speak("交易成功");
                    }

                }else if("4".equals(authAction))
                {
                    String totalFee = authResultResponse.getRefund_fee();
                    if(Utils.isNotEmpty(totalFee)){
                        speak("交易成功，预授权完成撤销"+DecimalUtil.branchToElement(totalFee)+"元");
                    }else{
                        speak("交易成功");
                    }

                }
            }
        }catch (Exception e){
            speak("交易金额返回错误！");
        }

    }

    /**
     * 打印带优惠券的小票
     */
    private void startPrint(){
        /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
        index = 1;
        if(Constants.NEW_LAND.equals(posProvider)){
            //初始化打印机
            getPrinter();
            if(printNum.equals("printNumNo")){

            }else if(printNum.equals("printNumOne")){
                //打印
                NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);

            }else if(printNum.equals("printNumTwo")){
                //打印
                NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(NewlandPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //弹出对话框提示打印下一联

                showPrintTwoDialog();

            }
        }else if(Constants.FUYOU_SF.equals(posProvider)){
            if(printNum.equals("printNumNo")){

                //不执行打印
//            intentToActivity(totalStr);
                finish();

            }else if(printNum.equals("printNumOne")){

                //打印一次
                String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                FuyouPosServiceUtil.printTextReq(activity,printTextStr);

            }else if(printNum.equals("printNumTwo")){
                //打印两次
                String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                FuyouPosServiceUtil.printTextReq(activity,printTextStr);
            }
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

                //打印第二联
                index = 2;
                if(posProvider.equals(NEW_LAND)){
                    NewlandPrintUtil.authPrintText(activity, aidlPrinter, userLoginResData, authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                }else if(posProvider.equals(FUYOU_SF)){
                    String printTextStr = FuyouPrintUtil.authPrintText(userLoginResData,authResultResponse,authAction,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                    FuyouPosServiceUtil.printTextReq(activity,printTextStr);
                }


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

                            etAuthCode.setText(auth_no);
                            etAuthCode.setSelection(etAuthCode.getText().toString().length());

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


    /** 事件监听处理 */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_title_imageView://返回
                finish();
                break;
            case R.id.auth_cancel_imgScan:
                if(Utils.isFastClick()){
                    return;
                }
                etAuthCode.setText("");
                if(posProvider.equals(Constants.NEW_LAND)){
                    initScanner();
                    if(cameType){
                        Log.e("扫码调用：", "后置摄像头");
                        backscan();
                    }else{
                        Log.e("扫码调用：", "前置摄像头");
                        frontscan();
                    }
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    //开始扫码
                    FuyouPosServiceUtil.scanReq(activity);
                }
                break;
            case R.id.auth_cancel_ok://确定，提交
                if(Utils.isFastClick()){
                    return;
                }
                authCodeStr = etAuthCode.getText().toString().trim();
                authMoneyStr = etAuthMoney.getText().toString().trim();
                if(Utils.isEmpty(authCodeStr)){
                    ToastUtil.showText(activity,"请输入原交易订单号！",1);
                    return;
                }

                if(Utils.isNotEmpty(authAction))
                {
                    if(Constants.AUTH_ACTION.equals(authAction))
                    {

                    }else if(Constants.AUTH_CANCEL_ACTION.equals(authAction))
                    {
                        authCancel();

                    }else if(Constants.AUTH_CONFIRM_ACTION.equals(authAction))
                    {
                        if(Utils.isEmpty(authMoneyStr)){
                            ToastUtil.showText(activity,"请输入金额！",1);
                            return;
                        }
                        authConfirm();

                    }else if(Constants.AUTH_CONFIRM_CANCEL_ACTION.equals(authAction))

                    {
                        if(Utils.isEmpty(authMoneyStr)){
                            ToastUtil.showText(activity,"请输入金额！",1);
                            return;
                        }
                        authConfirmCancel();

                    }
                }
                break;
                default:
                    break;
        }
    }
}
