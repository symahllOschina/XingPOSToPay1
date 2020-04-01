package com.wanding.xingpos.instalment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.newland.starpos.installmentsdk.NldInstallMent;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.view.ControlScrollViewPager;

import java.io.IOException;

/**
 * 分期付款主界面
 * 第三方请求地址：http://sandbox.starpos.com.cn/installment
 */
public class InstalmentActivity extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{

    private TextView tvPay,tvRefund,tvQuery;//分期付款，退款，查询
    private ControlScrollViewPager mViewPager;

    /**viewPager适配器*/
    private ViewPagerAdapter mAdapter;
    /**Fragment界面*/
    private InstalmentPayFragment payFragment;
    private InstalmentRefundFragment refundFragment;
    private InstalmentQueryFragment queryFragment;

    private UserLoginResData posPublicData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instalment_activity);



        initView();
        initListener();
        initData();
        initActivity();

        //分期SDK初始化
        if("true".equals(NitConfig.isTest)){
            NldInstallMent.init(this);
        }else{
            NldInstallMent.init(this,true);
        }

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
    }

    private void initView(){
        tvPay = findViewById(R.id.instalment_title_tvPay);
        tvRefund = findViewById(R.id.instalment_title_tvRefund);
        tvQuery = findViewById(R.id.instalment_title_tvQuery);

        mViewPager = findViewById(R.id.instalment_viewPager);
    }

    private void initListener(){
        //注册Viewpager滑动监听事件
        mViewPager.setOnPageChangeListener(this);
        mViewPager.addOnPageChangeListener(this);
        //Tab
        tvPay.setOnClickListener(this);
        tvRefund.setOnClickListener(this);
        tvQuery.setOnClickListener(this);
    }

    /**初始化界面配置*/
    private void initActivity(){
        //初始化fragment
        payFragment = new InstalmentPayFragment(posPublicData);
        refundFragment = new InstalmentRefundFragment(posPublicData);
        queryFragment = new InstalmentQueryFragment(posPublicData);
        //初始化Adapter
        mAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        //预加载界面数(ViewPager预加载默认数是1个，既设置0也没效果，他会默认把相邻界面数据预加载)
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapter);
        //初始化默认加载界面
        mViewPager.setCurrentItem(0);
        tvPay.setTextSize(22);

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
                return payFragment;
            }else if(position == 1){
                return refundFragment;
            }else{
                return queryFragment;
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
            tvPay.setTextSize(22);
        }else if(position==1){
            //先初始化所有Tab
            resetImg();
            tvRefund.setTextSize(22);
        }else{
            //先初始化所有Tab
            resetImg();
            tvQuery.setTextSize(22);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.instalment_title_tvPay:
                //先初始化所有Tab
                resetImg();
                tvPay.setTextSize(22);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.instalment_title_tvRefund:
                //先初始化所有Tab
                resetImg();
                tvRefund.setTextSize(22);
                mViewPager.setCurrentItem(1);
                break;
            case R.id.instalment_title_tvQuery:
                //先初始化所有Tab
                resetImg();
                tvQuery.setTextSize(22);
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    /**
     * 初始化所有tab
     */
    private void resetImg(){
        tvPay.setTextSize(16);
        tvRefund.setTextSize(16);
        tvQuery.setTextSize(16);
    }

}
