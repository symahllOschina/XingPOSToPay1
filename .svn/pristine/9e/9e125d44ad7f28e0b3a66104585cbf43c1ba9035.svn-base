package com.wanding.xingpos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;


/**
 * 文件读写公共类（用于在本地保存，读取数据）
 */
public class FileStoreUtil {
	
	/**
	 * 判断文件是否存在（根据文件名称判断）
	 * context : 上下文
	 * fileName : 文件名称
	 */
	public static boolean isFileExist(Context context,String fileName)throws Exception{
		boolean state = false;
		File file = new File(context.getFilesDir(), fileName);
		if(file.exists())
		{
			state = true;
		}
		return state;
	}

	/**   
	 * 保存文件 
	 * context : 上下文
	 * fileName : 文件名称
	 * str : 要保存的字符串
	 */
	public static void saveStringFile(Context context,String fileName,String str) throws Exception{
		
		//Context.MODE_PRIVATE权限，只有自身程序才能访问，而且写入的内容会覆盖文本内原有内
		FileOutputStream outStream=context.openFileOutput(fileName, Context.MODE_PRIVATE);
		
		outStream.write(str.getBytes()); 
			
		outStream.close();
		
	}
	
	/**
	 * 读取文件
	 * context : 上下文
	 * fileName : 文件名称
	 */
	public static String readStringFile(Context context,String fileName)throws Exception{
		String str = "";
		File file = new File(context.getFilesDir(), fileName);
		if(file.exists())
		{
			//打开文件输入流  
	        FileInputStream inStream = context.openFileInput(fileName);  
	        //定义1M的缓冲区  
	        byte[] buffer = new byte[1024];  
	        //定义字符串变量  
	        StringBuilder sb = new StringBuilder("");  
	        int len = 0;  
	        //读取文件内容，当文件内容长度大于0时，  
	        while ((len = inStream.read(buffer)) > 0) {  
	            //把字条串连接到尾部  
	            sb.append(new String(buffer, 0, len));  
	        }  
	        //关闭输入流  
	        inStream.close();  
	        //返回字符串  
	        str = sb.toString(); 
		}
		return str;
	} 
}
