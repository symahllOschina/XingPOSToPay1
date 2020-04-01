package com.wanding.xingpos.instalment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newland.starpos.installmentsdk.NldInstallMent;
import com.wanding.xingpos.R;
import com.wanding.xingpos.activity.ShiftRecordActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date.picker.CustomDatePicker;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.summary.util.SettlementDateUtil;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.view.ControlScrollViewPager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 分期付款主界面
 * 第三方请求地址：http://sandbox.starpos.com.cn/installment
 */
public class InstalmentScreenActivity extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{


    private ImageView imgBack;
    private TextView tvTitle;

    private TextView tvStartDateTime,tvEndDateTime;
    private TextView tvOk;

    private TextView tvRePay,tvSettle,tvRefund;//还款中，已结清，退款
    private ControlScrollViewPager mViewPager;

    /**viewPager适配器*/
    private ViewPagerAdapter mAdapter;
    /**Fragment界面*/
    private InstalmentScreenRePayFragment rePayFragment;
    private InstalmentScreenRePayFragment settleFragment;
    private InstalmentScreenRePayFragment refundFragment;

    private UserLoginResData posPublicData;
    private CustomDatePicker datePicker;
    private String pickerStartDateTime,pickerEndDateTime,pickerSeleteDateTime;//日期选择控件的选择范围，起始日期和结束日期,以及选择的日期时间
    private String startDateTimeStr,endDateTimeStr,dateTimeStr,dateStr,timeStr;//

    public static String startDateStr,endDateStr;//请求参数开始时间和结束时间
    /**
     * contractsState	否	N1	0还款中，1结清，2退款中
     */
    public static String contractsState = "0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instalment_screen_list);



        initView();
        initListener();
        initData();
        initActivity();

        //分期SDK初始化
        NldInstallMent.init(this,true);
        //设置界面顶部图片，不设置默认为星pos主题图片
//        NldInstallMent.setmResTitleImg(R.mipmap.fenqi_banner_huanyingshiyongxinpos);
        NldInstallMent.setmResTitleImg(R.drawable.instalment_title_img);


    }

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



        //起始日期为一个月前
        pickerStartDateTime = DateTimeUtil.getAMonthDateStr(-6, "yyyy-MM-dd HH:mm");
        //初始化日期时间（即系统默认时间）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        dateTimeStr = sdf.format(new Date());

        pickerEndDateTime = dateTimeStr;
        pickerSeleteDateTime = dateTimeStr;
        dateStr = dateTimeStr.split(" ")[0];
        timeStr = dateTimeStr.split(" ")[1];
        //初始化显示的开始时间，结束时间
        startDateTimeStr = pickerStartDateTime.split(" ")[0];
        endDateTimeStr = dateStr;

        tvStartDateTime.setText(startDateTimeStr);
        tvEndDateTime.setText(endDateTimeStr);

        startDateStr = QueryDateUtil.getStartTime(startDateTimeStr);
        endDateStr = QueryDateUtil.getEndTime(endDateTimeStr);

    }


    private void initView(){
        imgBack = findViewById(R.id.title_imageBack);
        tvTitle = findViewById(R.id.title_tvTitle);
        tvTitle.setText("筛选");

        tvStartDateTime = (TextView) findViewById(R.id.select_datetime_tvStartTime);
        tvEndDateTime = (TextView) findViewById(R.id.select_datetime_tvEndTime);
        tvOk = (TextView) findViewById(R.id.select_datetime_tvOk);

        tvRePay = findViewById(R.id.instalment_screen_tvRePay);
        tvSettle = findViewById(R.id.instalment_screen_tvSettle);
        tvRefund = findViewById(R.id.instalment_screen_tvRefund);

        mViewPager = findViewById(R.id.instalment_screen_viewPager);
    }

    private void initListener(){
        //注册Viewpager滑动监听事件
        mViewPager.setOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        //Tab
        imgBack.setOnClickListener(this);
        tvStartDateTime.setOnClickListener(this);
        tvEndDateTime.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        tvRePay.setOnClickListener(this);
        tvSettle.setOnClickListener(this);
        tvRefund.setOnClickListener(this);
    }

    /**初始化界面配置*/
    private void initActivity(){
        //初始化fragment
        rePayFragment = new InstalmentScreenRePayFragment(posPublicData);
        settleFragment = new InstalmentScreenRePayFragment(posPublicData);
        refundFragment = new InstalmentScreenRePayFragment(posPublicData);
        //初始化Adapter
        mAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        //预加载界面数(ViewPager预加载默认数是1个，既设置0也没效果，他会默认把相邻界面数据预加载)
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        //初始化默认加载界面
        mViewPager.setCurrentItem(0);
        tvRePay.setTextSize(22);
        contractsState = "0";

    }

    /**
     * 定义viewPager左右滑动的适配器
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return rePayFragment;
            }else if(position == 1){
                return settleFragment;
            }else{
                return refundFragment;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

    }

    /**ViewPager滑动改变Tab按钮状态*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }


    @Override
    public void onPageSelected(int position) {
        if(position==0){
            //先初始化所有Tab
            resetImg();
            contractsState = "0";
            tvRePay.setTextSize(22);
        }else if(position==1){
            //先初始化所有Tab
            resetImg();
            contractsState = "1";
            tvSettle.setTextSize(22);
        }else{
            //先初始化所有Tab
            resetImg();
            contractsState = "2";
            tvRefund.setTextSize(22);
        }
    }

    /**
     *  显示日期控件
     */
    private void setQueryDateText(final TextView tvText){
        datePicker = new CustomDatePicker(this, "请选择日期", new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                pickerSeleteDateTime = time.split(" ")[0];
                tvText.setText(pickerSeleteDateTime);
            }
        }, pickerStartDateTime, pickerEndDateTime);
        datePicker.showSpecificTime(1); //不显示时和分为false
        datePicker.setIsLoop(false);
        datePicker.setDayIsLoop(true);
        datePicker.setMonIsLoop(true);

        datePicker.show(pickerEndDateTime);
    }

    private void setQueryRecordParams(){
        String startTimeStr = tvStartDateTime.getText().toString();
        String endTimeStr = tvEndDateTime.getText().toString();
        Log.e("起始时间：", startTimeStr);
        Log.e("结束时间：", endTimeStr);
        startDateStr = QueryDateUtil.getStartTime(startTimeStr);
        endDateStr = QueryDateUtil.getEndTime(endTimeStr);
        if(Long.parseLong(startDateStr)<=Long.parseLong(endDateStr)){

            if("0".equals(contractsState)){
                rePayFragment.lazyLoad();
            }else if("1".equals(contractsState)){
                settleFragment.lazyLoad();
            }else{
                refundFragment.lazyLoad();
            }
        }else{
            Toast.makeText(InstalmentScreenActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_imageBack:
                finish();
                break;
            case R.id.select_datetime_tvStartTime://开始时间
                setQueryDateText(tvStartDateTime);
                break;
            case R.id.select_datetime_tvEndTime://结束时间
                setQueryDateText(tvEndDateTime);
                break;
            case R.id.select_datetime_tvOk://确定
                setQueryRecordParams();
                break;
            case R.id.instalment_screen_tvRePay:
                //先初始化所有Tab
                resetImg();
                contractsState = "0";
                tvRePay.setTextSize(22);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.instalment_screen_tvSettle:
                //先初始化所有Tab
                resetImg();
                contractsState = "1";
                mViewPager.setCurrentItem(1);
                tvSettle.setTextSize(22);
                break;
            case R.id.instalment_screen_tvRefund:
                //先初始化所有Tab
                resetImg();
                contractsState = "2";
                mViewPager.setCurrentItem(2);
                tvRefund.setTextSize(22);
                break;
        }
    }

    /**
     * 初始化所有tab
     */
    private void resetImg(){

        tvRePay.setTextSize(16);
        tvSettle.setTextSize(16);
        tvRefund.setTextSize(16);

    }

}
