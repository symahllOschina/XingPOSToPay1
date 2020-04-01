package com.wanding.xingpos.httputils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Message;
import android.util.Log;

public class HttpJsonReqUtil {

	public static final int CONNECTTIME = 60*1000*1;
	public static final int READTIME = 60*1000*1;

	/**
	 * 使用JSON格式向服务端发送数据
	 * @throws MalformedURLException 
	 */
	public static String doPos(String path,String content ) throws Exception{
		String jsonResult = "";
        HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();  
        //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();  
        connection.setConnectTimeout(CONNECTTIME);
        connection.setReadTimeout(CONNECTTIME);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);  
        connection.setRequestProperty("User-Agent", "Fiddler");  
        connection.setRequestProperty("Content-Type", "application/json");  
        connection.setRequestProperty("Charset", "UTF-8");  
        OutputStream os = connection.getOutputStream();  
        os.write(content.getBytes());  
        os.close();  
        int  code = connection.getResponseCode();
        Log.e("返回状态吗：", code+"");
        if(code == 200){
        	//请求成功之后的操作
     	   InputStream is = connection.getInputStream();
     	   String jsonStr = HttpJsonReqUtil.readString(is); 
     	   Log.e("返回JSON值：", jsonStr);
     	   //返回JSON值:{"Person":{"username":"zhangsan","age":"12"}}
     	  return jsonResult;
        }
		
		return jsonResult;
	}

	
	public static byte[] readBytes(InputStream is){
		try {
			byte[] buffer = new byte[1024];  
			int len = -1 ;  
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			while((len = is.read(buffer)) != -1){  
				baos.write(buffer, 0, len);  
			}  
			baos.close();  
			return baos.toByteArray();  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
			return null ;  
	}  
	
	public static String readString(InputStream is){  
		return new String(readBytes(is));  
	} 
}
