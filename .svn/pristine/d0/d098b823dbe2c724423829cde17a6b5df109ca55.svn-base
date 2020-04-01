package com.wanding.xingpos.base;

import java.util.LinkedList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.wanding.xingpos.httputils.HttpUtil;

public abstract class BaseFragment extends Fragment {

	/** Fragment当前状态是否可见 */
	protected boolean isVisible;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=getActivity();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}
	
	/**
	 * 可见
	 */
	protected void onVisible() {
		lazyLoad();		
	}
	/**
	 * 不可见
	 */
	protected void onInvisible() {
	}
	
	
	/** 
	 * 延迟加载
	 * 子类必须重写此方法
	 */
	protected abstract void lazyLoad();
	
	@Override
	public Context getContext() {
		return context;
	}
	



}
