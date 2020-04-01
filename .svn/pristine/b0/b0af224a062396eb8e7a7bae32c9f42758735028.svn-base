package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.fragment.CouponsRecodeFragment;
import com.wanding.xingpos.fragment.NumCardRecodeFragment;
import com.wanding.xingpos.util.Utils;
import com.wanding.xingpos.view.ClearEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_card_verifica_recode_list)
public class CardVerificaRecodeActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.search_header_titleLayout)
    private LinearLayout titleLayout;
    @ViewInject(R.id.search_header_tvTitle)
    private TextView tvTitle;

    /**
     * 搜索框
     */
    @ViewInject(R.id.search_header_etSearch)
    private ClearEditText etSearch;
    @ViewInject(R.id.search_header_tvSearch)
    private TextView tvSearch;
    @ViewInject(R.id.card_verifica_tab_numCardLayout)
    RelativeLayout numCardLayout;
    @ViewInject(R.id.card_verifica_tab_numCardText)
    TextView numCardText;
    @ViewInject(R.id.card_verifica_tab_numCardView)
    View numCardView;
    @ViewInject(R.id.card_verifica_tab_couponsLayout)
    RelativeLayout couponsLayout;
    @ViewInject(R.id.card_verifica_tab_couponsText)
    TextView couponsText;
    @ViewInject(R.id.card_verifica_tab_couponsView)
    View couponsView;


    /**
     * 区分界面入口标志
     */
    private String sign = "";
    private UserLoginResData loginInitData;

    String etSearchStr = "";

    int tabIndex = 0;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    NumCardRecodeFragment numCardRecodeFragment;
    CouponsRecodeFragment couponsRecodeFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        sign = intent.getStringExtra("sign");
        initFragments();


    }

    /**
     * 初始化界面控件
     */
    private void initView(){

        tvTitle.setText("核销记录");

    }

    private void initListener(){
        titleLayout.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        numCardLayout.setOnClickListener(this);
        couponsLayout.setOnClickListener(this);
    }

    public UserLoginResData getLoginInitData(){
        return loginInitData;
    }

    public String getCode(){
        return etSearchStr;
    }

    public int getTabIndex(){
        return tabIndex;
    }

    private void initFragments() {
        numCardRecodeFragment = new NumCardRecodeFragment();
        couponsRecodeFragment = new CouponsRecodeFragment();
        fragmentList.add(numCardRecodeFragment);
        fragmentList.add(couponsRecodeFragment);
        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        //初始化加载项
        int tabIndex = getIntent().getIntExtra("tabIndex", 0);
        changeHomeTab(tabIndex);
        numCardText.setTextColor(getResources().getColor(R.color.green_30d60a));
        numCardView.setBackgroundColor(getResources().getColor(R.color.green_30d60a));
    }
    private void addFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().add(R.id.card_verifica_recode_framelayout, fragment).commit();
        this.getSupportFragmentManager().executePendingTransactions();
    }

    private void hideFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        this.getSupportFragmentManager().executePendingTransactions();
    }

    private void showFragment(Fragment fragment) {
        this.getSupportFragmentManager().beginTransaction().show(fragment).commit();
        this.getSupportFragmentManager().executePendingTransactions();
    }

    public void changeHomeTab(int index) {
//        setTitle(titleList.get(index));
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == index) {
                showFragment(fragmentList.get(i));
            } else {
                hideFragment(fragmentList.get(i));
            }
        }
    }
    /**
     * 初始化所有tab
     */
    private void resetImg(){
        numCardText.setTextColor(getResources().getColor(R.color.grey_666666));
        numCardView.setBackgroundColor(getResources().getColor(R.color.white_ffffff));
        couponsText.setTextColor(getResources().getColor(R.color.grey_666666));
        couponsView.setBackgroundColor(getResources().getColor(R.color.white_ffffff));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.search_header_titleLayout:
                finish();
                break;
            case R.id.search_header_tvSearch:
                if(Utils.isFastClick()){
                    return;
                }
                etSearchStr = etSearch.getText().toString().trim();
                if(tabIndex == 0){
                    numCardRecodeFragment.setParameter(etSearchStr);
                }
                if(tabIndex == 1){
                    couponsRecodeFragment.setParameter(etSearchStr);
                }
                break;
            case R.id.card_verifica_tab_numCardLayout:
                tabIndex = 0;
                //先初始化所有Tab
                resetImg();
                numCardText.setTextColor(getResources().getColor(R.color.green_30d60a));
                numCardView.setBackgroundColor(getResources().getColor(R.color.green_30d60a));
                changeHomeTab(tabIndex);
                numCardRecodeFragment.setParameter(etSearchStr);
                break;
            case R.id.card_verifica_tab_couponsLayout:
                tabIndex = 1;
                //先初始化所有Tab
                resetImg();
                couponsText.setTextColor(getResources().getColor(R.color.green_30d60a));
                couponsView.setBackgroundColor(getResources().getColor(R.color.green_30d60a));
                changeHomeTab(tabIndex);
                couponsRecodeFragment.setParameter(etSearchStr);
                break;
            default:
                break;
        }
    }







}
