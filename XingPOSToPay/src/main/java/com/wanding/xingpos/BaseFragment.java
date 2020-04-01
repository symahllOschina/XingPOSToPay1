package com.wanding.xingpos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wanding.xingpos.application.BaseApplication;

import org.xutils.x;

public class BaseFragment extends Fragment{

    protected final String TAG = getClass().getSimpleName();
    protected BaseActivity activity;
    protected BaseApplication myApp;


    protected LayoutInflater inflater;
    protected View rootView;
    private boolean injected = false;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        myApp = (BaseApplication) activity.getApplication();
    }

    /**
     * 取消Fragment的预加载，根据当前Fragment的UI是否可见决定是否加载
     */



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        injected = true;
        rootView = x.view().inject(this,inflater,container);
        return rootView;
    }


}
