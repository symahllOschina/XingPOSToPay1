package com.wanding.xingpos.activity;

import android.app.Activity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.pull.refresh.view.XListView;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.AuthOrderListAdapter;
import com.wanding.xingpos.bean.AuthRecodeListReqData;
import com.wanding.xingpos.bean.AuthRecodeListResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WdPreAuthHistoryVO;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.QueryParamsReqUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;
import com.wanding.xingpos.view.ClearEditText;


import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ContentView(R.layout.auth_recode_list)
public class ScanAuthRecodeListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {


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
    @ViewInject(R.id.menu_title_imgOption)
    ImageView imgOption;


    @ViewInject(R.id.auth_recode_list_xListView)
    private XListView xListView;


    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
    //总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private int refreshCount = 1;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作

    private UserLoginResData userLoginResData;


    String etSearchStr = "";
    private List<WdPreAuthHistoryVO> list = new ArrayList<WdPreAuthHistoryVO>();
    private BaseAdapter mAdapter;


    /**
     * cameraType为true表示打开后置摄像头，fasle为前置摄像头
     */
    private boolean cameType = true;

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
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("预授权交易记录");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");
        imgOption.setVisibility(View.VISIBLE);

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

        initView();
        initListener();
        mAdapter = new AuthOrderListAdapter(activity,list);
        xListView.setAdapter(mAdapter);

        getRecodeList(pageNum,pageNumCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //取出保存的摄像头参数值
        SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(activity, "scancamera");
        cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
        if(cameType){
            Log.e("当前摄像头打开的是：", "后置");
        }else{
            Log.e("当前摄像头打开的是：", "前置");
        }
        refreshCount = 1;
        loadMore = "0";
        pageNum = 1;
        etSearchStr = "";

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(posProvider.equals(NEW_LAND)){
            unbindService(serviceConnection);
            aidlPrinter=null;
            aidlScanner = null;
        }else if(posProvider.equals(FUYOU_SF)){

        }
        Log.e(TAG, "释放资源成功");
    }

    private void initView(){



        xListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        xListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉

    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        imgOption.setOnClickListener(this);
        //注册刷新和加载更多接口
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
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
                        ToastUtil.showText(activity,"扫码失败！",1);
                    }

                }

                @Override
                public void onFinish() throws RemoteException {
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showText(activity,"扫码失败！",1);
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
                        ToastUtil.showText(activity,"扫码失败！",1);
                    }

                }

                @Override
                public void onFinish() throws RemoteException {
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
//                    ToastUtil.showText(activity,"扫码失败！",1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            ToastUtil.showText(activity,"扫码失败！",1);
        }
    }



    private void getRecodeList(final int pageNum,final int pageNumCount){

        if(refreshCount == 1){
            showWaitDialog();
        }

        final String startTimeStr = "";
        final String endTimeStr = "";
        //参数实体
        final AuthRecodeListReqData reqData = QueryParamsReqUtil.queryAuthRecodeListReq(pageNum,pageNumCount,userLoginResData,etSearchStr,startTimeStr,endTimeStr);

        final String url = NitConfig.authRecodeListUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(reqData);
                    Log.e("发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("返回字符串结果：", jsonStr);
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
                case NetworkUtils.MSG_WHAT_ONEHUNDRED:
                    String auth_no = scanCodeStr;

                    etSearchStr = scanCodeStr;
                    refreshCount = 1;
                    loadMore = "0";
                    pageNum = 1;
                    getRecodeList(pageNum,pageNumCount);
                    break;
                case REFRESH:
                    mAdapter.notifyDataSetChanged();
                    //更新完毕
                    onLoad();
                    break;
                case LOADMORE:
                    xListView.setPullLoadEnable(true);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成
                    onLoad();
                    break;
                case NOLOADMORE:
                    xListView.setPullLoadEnable(false);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    if(list.size() == 1){

                        WdPreAuthHistoryVO order = list.get(0);
                        Intent intent = new Intent();
                        intent.setClass(activity,ScanAuthOrderDetailsActivity.class);
                        intent.putExtra("userLoginResData",userLoginResData);
                        intent.putExtra("order",order);
                        startActivity(intent);
                    }
                    break;
                case NetworkUtils.MSG_WHAT_ONE:
                    String recodeListJsonStr = (String) msg.obj;
                    recodeListJsonStr(recodeListJsonStr);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                default:
                    break;
            }
        };
    };

    private void recodeListJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");

                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<AuthRecodeListResData>() {}.getType();
                AuthRecodeListResData recodeResData = gjson.fromJson(dataJson, type);
                //获取总条数
                orderListTotalCount = recodeResData.getTotalCount();
                Log.e("总条数：", orderListTotalCount+"");
                List<WdPreAuthHistoryVO> recodeList = new ArrayList<WdPreAuthHistoryVO>();
                //获取的list
                recodeList = recodeResData.getOrderList();
                getMoerNum = recodeList.size();
                if(pageNum == 1){
                    list.clear();
                    mAdapter.notifyDataSetInvalidated();
                }
                list.addAll(recodeList);
                Log.e("查询数据：", list.size()+""+"条");
                //关闭上拉或下拉View，刷新Adapter
                if("0".equals(loadMore)){
                    if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                        Message msg1 = new Message();
                        msg1.what = LOADMORE;
                        handler.sendEmptyMessageDelayed(LOADMORE, 0);
                    }else{
                        Message msg1 = new Message();
                        msg1.what = NOLOADMORE;
                        handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                    }
                }else if("1".equals(loadMore)){
                    if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                        Message msg1 = new Message();
                        msg1.what = LOADMORE;
                        handler.sendEmptyMessageDelayed(LOADMORE, 0);
                    }else{
                        Message msg1 = new Message();
                        msg1.what = NOLOADMORE;
                        handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                    }

                }else if("2".equals(loadMore)){
                    Message msg1 = new Message();
                    msg1.what = LOADMORE;
                    handler.sendEmptyMessageDelayed(LOADMORE, 0);
                }


            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                            etSearchStr = scanCodeStr;
                            refreshCount = 1;
                            loadMore = "0";
                            pageNum = 1;
                            getRecodeList(pageNum,pageNumCount);

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

                        //打印正常


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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.menu_title_imgOption:
                if(Utils.isFastClick()){
                    return;
                }
                //开始扫码
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
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(Utils.isFastClick()){
            return;
        }
        try{
            if(list.size()>=1){
                WdPreAuthHistoryVO order = list.get(position - 1);
                Intent intent = new Intent();
                intent.setClass(activity,ScanAuthOrderDetailsActivity.class);
                intent.putExtra("userLoginResData",userLoginResData);
                intent.putExtra("order",order);
                startActivity(intent);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getRecodeList(pageNum,pageNumCount);
        etSearchStr = "";
    }

    @Override
    public void onLoadMore() {
        refreshCount++;
        loadMore = "2";
        etSearchStr = "";
        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getRecodeList(pageNum,pageNumCount);
        }else{
            //没有数据执行两秒关闭view
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    handler.sendMessage(msg);
                }
            }, 0);

        }
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(new Date().toLocaleString());
    }
}
