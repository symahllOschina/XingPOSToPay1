package com.wanding.xingpos.httputils;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.wanding.xingpos.util.FileStoreUtil;
import com.wanding.xingpos.util.NitConfig;

public class WebViewSynCookie {
	private static String url=NitConfig.basePath;
	private static String fileName = "reqsessionid";
    /** 
     * 同步一下cookie 
     */  
    public static void synCookies(Context context) {  
    	String JSESSIONID = null;
		String sessionName ="JSESSIONID";
		
		
		try {
			if(FileStoreUtil.isFileExist(context, fileName))
			{
				Log.e("WebView中文件存在", "文件存在");
				JSESSIONID = FileStoreUtil.readStringFile(context, fileName);
				
				if(JSESSIONID!=null && !JSESSIONID.equals(""))
				{
					Log.e("文件存在且不为空获取的sessionId为：", JSESSIONID);
				}else
				{
					Log.e("WebView中文件存在但为空", "文件存在但为空");
				}
			}else
			{
				Log.e("WebView中文件不存在","文件不存在");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        CookieSyncManager.createInstance(context);  
        CookieManager cookieManager = CookieManager.getInstance();  
        cookieManager.setAcceptCookie(true);  
//        cookieManager.removeSessionCookie();//移除  
        cookieManager.setCookie(url, sessionName+"="+JSESSIONID);//cookies是在HttpClient中获得的cookie  
        CookieSyncManager.getInstance().sync();  
    }  
}
