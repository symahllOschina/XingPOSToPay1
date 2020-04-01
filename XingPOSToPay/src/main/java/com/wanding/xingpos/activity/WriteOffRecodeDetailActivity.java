package com.wanding.xingpos.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WriteOffRecodeDetailResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.Utils;

/**
 * 核销记录详情界面
 */
public class WriteOffRecodeDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgBack;
    private TextView tvTitle;

    /**
     * 商户名称，卡劵名称，核销代码，使用状态，使用时间，POS设备号
     */
    private TextView tvMerName,tvWriteOffName,tvWriteOffCode,
                     tvWriteOffState,tvCreateTime,tvDeviceNum;

    private TextView tvOk,tvPrint;

    /**
     * 签到商户信息
     * 核销详情
     */
    UserLoginResData loginInitData;
    WriteOffRecodeDetailResData writeOffRecode;

    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private static final String NEW_LAND = "newland";
    private static final String FUYOU_SF= "fuyousf";
    private String posProvider;

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
     * 富友(打印机)AIDL服务
     */
    private PrintInterface printService = null;
    private PrintReceiver printReceiver;
    private ServiceConnection printServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            printService = PrintInterface.Stub.asInterface(arg1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }

    };

    class PrintReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
//			etBack.setText("reason："+result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_off_recode_details_activity);
        posProvider = MainActivity.posProvider;
        if(posProvider.equals(NEW_LAND)){
            //绑定打印机服务
            bindServiceConnection();
        }else if(posProvider.equals(FUYOU_SF)){
            initPrintService();
        }
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        writeOffRecode = (WriteOffRecodeDetailResData) intent.getSerializableExtra("writeOffRecode");

        initView();
        initListener();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(posProvider.equals(NEW_LAND)){
            unbindService(serviceConnection);
            aidlPrinter=null;
            aidlScanner = null;
        }else if(posProvider.equals(FUYOU_SF)){
            if(null != printReceiver){
                unregisterReceiver(printReceiver);
            }
            unbindService(printServiceConnection);
        }
        Log.e(TAG, "释放资源成功");
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

    private void initPrintService(){
        printRegisterReceiver();
        Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
        printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
        printIntent.setPackage("com.fuyousf.android.fuious");
        bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void printRegisterReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.fuyousf.android.fuious.service.print");
        printReceiver = new PrintReceiver();
        registerReceiver(printReceiver, intentFilter);
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
        }

    }

    private void initView(){
        imgBack = (ImageView) findViewById(R.id.title_imageBack);
        tvTitle = (TextView) findViewById(R.id.title_tvTitle);

        tvTitle.setText("核销单详情");

        tvMerName = (TextView) findViewById(R.id.write_off_recode_details_tvMerName);
        tvWriteOffName = (TextView) findViewById(R.id.write_off_recode_details_tvWriteOffName);
        tvWriteOffCode = (TextView) findViewById(R.id.write_off_recode_details_tvWriteOffCode);
        tvWriteOffState = (TextView) findViewById(R.id.write_off_recode_details_tvWriteOffState);
        tvCreateTime = (TextView) findViewById(R.id.write_off_recode_details_tvWriteOffCreateTime);
        tvDeviceNum = (TextView) findViewById(R.id.write_off_recode_details_tvDeviceNum);

        tvOk = (TextView) findViewById(R.id.write_off_recode_details_tvOk);
        tvPrint = (TextView) findViewById(R.id.write_off_recode_details_tvPrint);

        updateView();
    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
    }

    private void updateView(){
        try{
            tvMerName.setText(loginInitData.getMername_pos());
            tvWriteOffName.setText(writeOffRecode.getTitle());
            tvWriteOffCode.setText(writeOffRecode.getCode());
            tvWriteOffState.setText("使用成功");
            tvCreateTime.setText(DateTimeUtil.stampToDate(writeOffRecode.getUse_time()));
            tvDeviceNum.setText(loginInitData.getTrmNo_pos());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
            case R.id.write_off_recode_details_tvOk:
                finish();
                break;
            case R.id.write_off_recode_details_tvPrint:
                if(Utils.isFastClick()){
                    return;
                }
                FuyouPrintUtil.writeOffDetailPrintText(activity,printService,writeOffRecode,loginInitData);
                break;
                default:
                    break;
        }
    }
}
