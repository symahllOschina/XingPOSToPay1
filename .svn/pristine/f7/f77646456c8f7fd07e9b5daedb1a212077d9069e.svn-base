package com.wanding.xingpos.activity;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.StaffListAdapter;
import com.wanding.xingpos.adapter.StaffListAdapter.OnItemDeleteListener;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.StaffData;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.Utils;

/**
 *  员工列表界面
 */
public class StaffListActivity extends BaseActivity implements OnClickListener,OnItemDeleteListener{
	
	private ImageView imgBack;
	private TextView tvTitle,tvOption;
	
	
	private ListView mListView;
	private StaffListAdapter mAdapter;
	private List<StaffData> lsStaff;
	
	private boolean OnResume = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.staff_list_activity);
		
		initView();
		initListener();
		initData();
		OnResume = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(OnResume){
			initData();
		}
	}
	
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvTitle.setText("员工管理");
		tvOption = (TextView) findViewById(R.id.title_tvFunction);
		tvOption.setVisibility(View.VISIBLE);
		tvOption.setText("添加");
		
		mListView = (ListView) findViewById(R.id.staff_list_listView);
	}
	
	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOption.setOnClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void initData(){
		try {
			String staffStr = MySerialize.getObject("staff", StaffListActivity.this);
			if(Utils.isNotEmpty(staffStr)){
				lsStaff = (List<StaffData>) MySerialize.deSerialization(staffStr);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(lsStaff!=null){
			if(lsStaff.size()>0){
				String isDelete = "1";
				mAdapter = new StaffListAdapter(StaffListActivity.this, lsStaff,isDelete);
				mListView.setAdapter(mAdapter);
				mAdapter.setOnItemDeleteListener(this);
			}else{
				Log.e("TAG", "lsStaff集合长度为0");
			}
		}else{
			Log.e("TAG", "lsStaff集合为null");
		}
       
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.title_imageBack://返回
			finish();
			break;
		case R.id.title_tvFunction://添加
			OnResume = true;
			
			in = new Intent();
			in.setClass(StaffListActivity.this, AddStaffActivity.class);
			startActivity(in);
			break;
		}
	}

	@Override
	public void onDelete(int position) {
		StaffData staff = lsStaff.get(position);
		lsStaff.remove(position);
		//保存
        try {
            String listStr = MySerialize.serialize(lsStaff);
            MySerialize.saveObject("staff",StaffListActivity.this,listStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initData();
        mAdapter.notifyDataSetChanged();
	}

}
