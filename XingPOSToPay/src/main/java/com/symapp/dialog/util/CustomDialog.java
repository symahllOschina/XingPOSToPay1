package com.symapp.dialog.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.R;

public class CustomDialog {
public static Dialog CreateDialog(Context context,String msg){
		
		//1：先创建子布局选择器对象
		LayoutInflater inflater=LayoutInflater.from(context);
		
		//获取dialog布局
		View dialogView=inflater.inflate(R.layout.custom_dialog, null);
		
		//获取dialog布局总布局LinearLayout
		LinearLayout layout=(LinearLayout) dialogView.findViewById(R.id.custom_dialog_dialog);
		
		//获取布局上的控件对象
		ImageView dialog_imag=(ImageView) dialogView.findViewById(R.id.custom_dialog_imag);
		
		TextView dialog_msgtv=(TextView) dialogView.findViewById(R.id.custom_dialog_msg);
		
		//给当前对话框加入动画
		Animation anim=AnimationUtils.loadAnimation(context, R.anim.pro_diaog_anim);
		
		//将动画效果跟ImageView图片结合
		dialog_imag.startAnimation(anim);
		
		//将用户提示给TextView赋值
		dialog_msgtv.setText(msg);
		
		//给Dialog添加自定义的样式
		Dialog dialog = new Dialog(context, R.style.pro_dialog);
		
		//将dialog添加到当前布局上去,并设置当前的布局填充屏幕
		dialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 
				LinearLayout.LayoutParams.FILL_PARENT)
		);
		
		//最后返回dalog;
		return dialog;
	}
}
