package com.wanding.xingpos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 会员（卡券）管理界面
 */
@ContentView(R.layout.activity_member_topup_cardcode)
public class MemberTopUpCardCodeActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.member_topup_cardcode_etCardCode)
    EditText etCardCode;

    @ViewInject(R.id.member_topup_cardcode_imagScan)
    ImageView imgScan;
    @ViewInject(R.id.member_topup_cardcode_btOk)
    Button btOk;


    /**
     * 区分不同的操作入口（会员充值/会员消费）
     */
    private String sign = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("会员充值");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.VISIBLE);
        tvOption.setText("充值记录");

        Intent intent = getIntent();
        sign = intent.getStringExtra("sign");

        if(sign.equals("2")){
            tvTitle.setText("会员消费");
            tvOption.setText("消费记录");
        }

        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        imgScan.setOnClickListener(this);
        btOk.setOnClickListener(this);
        tvOption.setOnClickListener(this);
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

                            etCardCode.setText(auth_no);

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

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.member_topup_cardcode_imagScan:
                if(Utils.isFastClick()){
                    return;
                }
                etCardCode.setText("");
                FuyouPosServiceUtil.scanReq(activity);
                break;
            case R.id.member_topup_cardcode_btOk:
                if(Utils.isFastClick()){
                    return;
                }
                String cardCodeStr = etCardCode.getText().toString().trim();
                if(Utils.isNotEmpty(cardCodeStr)){
                    if(sign.equals("1")){
                        intent.setClass(activity,MemberTopUpActivity.class);
                    }else if(sign.equals("2")){
                        intent.setClass(activity,MemberPayActivity.class);
                    }
                    intent.putExtra("cardCode",cardCodeStr);
                    startActivity(intent);
                }else{
                    ToastUtil.showText(activity,"请输入会员卡号！",1);
                }
                break;
            case R.id.menu_title_tvOption:
                if(Utils.isFastClick()){
                    return;
                }
                if(sign.equals("1")){
                    intent.setClass(activity,MemberTopUpRecodeListActivity.class);
                }else if(sign.equals("2")){
                    intent.setClass(activity,MemberPayRecodeListActivity.class);
                }
                startActivity(intent);
                break;
                default:
                    break;
        }
    }
}
