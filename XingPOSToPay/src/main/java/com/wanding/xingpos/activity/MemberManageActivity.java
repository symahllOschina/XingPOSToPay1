package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.util.ToastUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 会员（卡券）管理界面
 */
@ContentView(R.layout.activity_member_manage)
public class MemberManageActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.member_manage_queryLayout)
    RelativeLayout memberQueryLayout;
    @ViewInject(R.id.member_manage_topUpLayout)
    RelativeLayout topUpLayout;
    @ViewInject(R.id.member_manage_payLayout)
    RelativeLayout payLayout;
    @ViewInject(R.id.member_manage_writeOffLayout)
    RelativeLayout writeOffLayout;
    @ViewInject(R.id.member_manage_buyCardLayout)
    RelativeLayout buyCardLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.back_icon));
        tvTitle.setText("会员管理");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);
        tvOption.setText("");

        initListener();

    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        memberQueryLayout.setOnClickListener(this);
        topUpLayout.setOnClickListener(this);
        payLayout.setOnClickListener(this);
        writeOffLayout.setOnClickListener(this);
        buyCardLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String sign = "";
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.member_manage_queryLayout:
//                intent.setClass(activity,MemberQueryActivity.class);
//                startActivity(intent);
                ToastUtil.showText(activity,"暂未开通！",1);
                break;
            case R.id.member_manage_topUpLayout:
                sign = "1";
                intent.setClass(activity,MemberTopUpCardCodeActivity.class);
                intent.putExtra("sign",sign);
                startActivity(intent);
                break;
            case R.id.member_manage_payLayout:
                sign = "2";
                intent.setClass(activity,MemberTopUpCardCodeActivity.class);
                intent.putExtra("sign",sign);
                startActivity(intent);
//                ToastUtil.showText(activity,"暂未开通！",1);
                break;
            case R.id.member_manage_writeOffLayout:
                sign = "2";
                intent.setClass(activity,WriteOffActivity.class);
                intent.putExtra("sign",sign);
                startActivity(intent);
                break;
            case R.id.member_manage_buyCardLayout:
                intent.setClass(activity,BuyCardTypeActivity.class);
                startActivity(intent);
                break;
                default:
                    break;
        }
    }
}
