package com.wanding.xingpos.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.StaffData;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.Utils;

/**
 *  员工列表界面
 */
public class AddStaffActivity extends BaseActivity implements OnClickListener{
	
	private ImageView imgBack;
	private TextView tvTitle;
	
	private EditText etName;
	private TextView tvSaveStaff;//保存员工
	private List<StaffData> lsStaff;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_staff_activity);
		
		initView();
		initData();
		initListener();
	}
	
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		tvTitle.setText("员工添加");
		
		etName = (EditText) findViewById(R.id.add_staff_etName);
		etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
		EditTextUtils.setEditTextInputSpeChat(etName);
		tvSaveStaff = (TextView) findViewById(R.id.add_staff_tvSaveStaff);
		
		
		
	}
	
	private void initListener(){
		imgBack.setOnClickListener(this);
		tvSaveStaff.setOnClickListener(this);
	}
	
	private void initData(){
		try {
			String staffStr = MySerialize.getObject("staff", AddStaffActivity.this);
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
		
		if(lsStaff==null||lsStaff.size()<=0){
			lsStaff = new ArrayList<StaffData>();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_imageBack://返回
			finish();
			break;
		case R.id.add_staff_tvSaveStaff://保存员工
			String staffNameStr = etName.getText().toString().trim();
			if(Utils.isEmpty(staffNameStr)){
				Toast.makeText(AddStaffActivity.this, "名称不能为空！", Toast.LENGTH_LONG).show();
				return;
			}
			if(lsStaff.size()>0){
				for (int i = 0; i < lsStaff.size(); i++) {
					StaffData data = lsStaff.get(i);
					String name = data.getName();
					if(staffNameStr.equals(name)){
						Toast.makeText(AddStaffActivity.this, "该名称已存在！", Toast.LENGTH_LONG).show();
						return;
					}
				}
			}
			if(lsStaff.size()>=20){
				Toast.makeText(AddStaffActivity.this, "录入名称已达上限！！", Toast.LENGTH_LONG).show();
				return;
			}
			//保存名称
			StaffData staff = new StaffData();
			staff.setName(staffNameStr);
			lsStaff.add(staff);
			//保存
	        try {
	            String listStr = MySerialize.serialize(lsStaff);
	            MySerialize.saveObject("staff",AddStaffActivity.this,listStr);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        Toast.makeText(AddStaffActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
	        finish();
			break;
		}
	}

}
