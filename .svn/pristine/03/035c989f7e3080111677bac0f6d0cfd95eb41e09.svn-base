package com.wanding.xingpos.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.bean.NumCardRecodeDetail;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 核销记录详情界面(计次卡)
 */
@ContentView(R.layout.activity_num_card_recode_details)
public class NumCardRecodeDetailActivity extends BaseActivity implements View.OnClickListener {

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
     * 商户名称，卡劵名称，核销代码，使用状态，使用时间，POS设备号
     */
    @ViewInject(R.id.num_card_recode_details_tvMerName)
    TextView tvMerName;
    @ViewInject(R.id.num_card_recode_details_tvName)
    TextView tvName;
    @ViewInject(R.id.num_card_recode_details_tvCode)
    TextView tvCode;
    @ViewInject(R.id.num_card_recode_details_tvState)
    TextView tvState;
    @ViewInject(R.id.num_card_recode_details_tvCreateTime)
    TextView tvCreateTime;
    @ViewInject(R.id.num_card_recode_details_tvDeviceNum)
    TextView tvDeviceNum;

    @ViewInject(R.id.num_card_recode_details_tvOk)
    TextView tvOk;
    @ViewInject(R.id.num_card_recode_details_tvPrint)
    TextView tvPrint;

    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
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
     * 签到商户信息
     * 核销详情
     */
    UserLoginResData loginInitData;
    NumCardRecodeDetail order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posProvider = MainActivity.posProvider;
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("核销单详情");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");
        try{
            if(posProvider.equals(Constants.NEW_LAND)){
                //绑定打印机服务
                bindServiceConnection();
            }else if(posProvider.equals(Constants.FUYOU_SF)){

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        order = (NumCardRecodeDetail) intent.getSerializableExtra("order");

        initListener();

        updateView();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(posProvider.equals(Constants.NEW_LAND)){
            unbindService(serviceConnection);
            aidlPrinter=null;
            aidlScanner = null;
        }else if(posProvider.equals(Constants.FUYOU_SF)){

        }
        Log.e(TAG, "释放资源成功");
    }


    private void initListener(){
        imgBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
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
        }

    }

    private void updateView(){
        try{
            tvMerName.setText(loginInitData.getMername_pos());
            tvName.setText(order.getCard_name());
            tvCode.setText(order.getCode());
            tvState.setText("使用成功");
            tvCreateTime.setText(DateTimeUtil.stampToDate(order.getUse_time()));
            tvDeviceNum.setText(loginInitData.getTrmNo_pos());
        }catch (Exception e){
            e.printStackTrace();
        }

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
            case R.id.num_card_recode_details_tvOk:
                finish();
                break;
            case R.id.num_card_recode_details_tvPrint:
                if(Utils.isFastClick()){
                    return;
                }
                try {
                    if(Constants.NEW_LAND.equals(posProvider)){

                        getPrinter();
                        NewlandPrintUtil.numCardDetailPrintText(activity,aidlPrinter,order,loginInitData);
                    }else if(Constants.FUYOU_SF.equals(posProvider)){
                        String printTextStr = FuyouPrintUtil.numCardDetailPrintText(order,loginInitData);
                        FuyouPosServiceUtil.printTextReq(activity,printTextStr);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.showText(activity,"打印出错！",1);
                }


                break;
                default:
                    break;
        }
    }
}
