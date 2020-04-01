package com.wanding.xingpos.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.httputils.WebViewSynCookie;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.PhotoModule;

/**
 *  共同的WebView 
 */
public class TestWebView extends BaseActivity{


	private WebView mWebView;
	

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_webview_activity);

		initView();
	}




	

	
	/**   初始化View  */
	@SuppressLint("JavascriptInterface") 
	private void initView(){
		String url = "https://www.baidu.com/";
		mWebView = (WebView) findViewById(R.id.test_webview);
		//设置WebView属性，能够执行Javascript脚本  
		mWebView.getSettings().setJavaScriptEnabled(true);  
		//同步Http请求Cookie
		WebViewSynCookie.synCookies(this);
		//加载需要显示的网页  
		mWebView.loadUrl(url);
		//不使用缓存
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		Log.e("加载的Url是：", url);
		// 添加js交互接口类，并起别名 yiloneAppActivityJs  
		mWebView.addJavascriptInterface(this, "yiloneAppActivityJs"); 
		
		//设置Web视图  
		mWebView.setWebViewClient(new HelloWebViewClient()); 
//		LinearLayout.LayoutParams mWebViewLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);  
//		mWebView.setLayoutParams(mWebViewLP);  
//		mWebView.setInitialScale(25);  
//		WebSettings settings = mWebView.getSettings();  
//		//适应屏幕  
//		settings.setUseWideViewPort(true);  
//		settings.setSupportZoom(true);  
//		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
//		settings.setLoadWithOverviewMode(true);  
//		settings.setBuiltInZoomControls(true);  
//		settings.setJavaScriptEnabled(true); 
	}

	
	//Web视图  
    private class HelloWebViewClient extends WebViewClient {  
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
        	

        	view.loadUrl(url);

            return true;  
        }  
    }



}
