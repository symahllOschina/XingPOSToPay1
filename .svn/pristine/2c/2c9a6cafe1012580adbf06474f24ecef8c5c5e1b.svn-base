package com.wanding.xingpos.application;

import com.newland.newpaysdk.NldPaySDK;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.activity.ScanPayActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/** 应用配置  */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new ArrayList<Activity>();
    //为了实现每次使用该类时不创建新的对象而创建的静态对象
    private static BaseApplication instance;


    //实例化
    public synchronized static BaseApplication getInstance(){
        if(instance==null){
            instance=new BaseApplication();
        }
        return instance;
    }

    // 保存打开的Actviity到集合中
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    private static GeTuiHandler handler;
    public static MainActivity activity;

	@Override
    public void onCreate() {
        super.onCreate();
        //注册xutils
        x.Ext.init(this);
        //星POS初始化初始化sdk
//        NldPaySDK.getInstance().init(SECRET_KEY);
//        其中SECRET_KEY为进件审核通过过的商户密钥
//        NldPaySDK.getInstance().setDebugModule(true);
//        NldPaySDK.getInstance().init("O0uFar3uBtuEgYwFazl14ww7vVYw0Q5lWtwyTd12WypwGLdBs0CvNzBHP50fNJem");
        if (handler == null) {
            handler = new GeTuiHandler();
        }
	}



    /**
     * 在每个activity被创建时加上: BaseApplication.getInstance().addActivity(this);
     *
     * 当你想关闭时，调用BaseApplication的exit方法关闭整个程序:
     * BaseApplication.getInstance().exit();
     */
    //关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity:mList) {
                if (activity != null){
                    activity.finish();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            System.exit(0);
//            synthesizer.release();
//
//        }
    }

    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }
    /**
     * 主要处理个推消息
     */
    public static class GeTuiHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (activity != null) {
                        String noticeMsg = (String) msg.obj;
                        Intent intent = new Intent();
                        intent.setClass(activity,ScanPayActivity.class);
                        activity.startActivity(intent);
                        Log.e("透传打印测试","跳转界面");
                    }
                    break;

            }
        }
    }
}
