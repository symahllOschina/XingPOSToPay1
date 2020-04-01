package com.wanding.xingpos.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pull.refresh.view.XListView;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.view.ClearEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 会员查询界面
 */
@ContentView(R.layout.activity_member_query)
public class MemberQueryActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.member_query_xListView)
    private XListView xListView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("会员查询");
        etSearch.setHint("请输入会员卡号");

        initListener();

    }

    private void initListener(){
        titleLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_header_titleLayout:
                finish();
                break;
                default:
                    break;

        }
    }
}
