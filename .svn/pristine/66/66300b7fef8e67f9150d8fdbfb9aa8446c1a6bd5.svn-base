package com.wanding.xingpos;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.igexin.sdk.PushManager;
import com.wanding.xingpos.application.BaseApplication;
import com.wanding.xingpos.baidu.tts.util.AutoCheck;
import com.wanding.xingpos.baidu.tts.util.InitConfig;
import com.wanding.xingpos.baidu.tts.util.MainHandlerConstant;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.baidu.tts.util.NonBlockSyntherizer;
import com.wanding.xingpos.baidu.tts.util.OfflineResource;
import com.wanding.xingpos.baidu.tts.util.UiMessageListener;
import com.wanding.xingpos.bean.UpdateInfo;
import com.wanding.xingpos.fragment.MainFunctionFragment;
import com.wanding.xingpos.fragment.MainPayFragment;
import com.wanding.xingpos.getui.DemoIntentService;
import com.wanding.xingpos.getui.GetuiPushService;
import com.wanding.xingpos.httputils.HttpURLConUtil;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;
import com.wanding.xingpos.view.ControlScrollViewPager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Administrator
 */
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements MainHandlerConstant,OnPageChangeListener,OnClickListener{

	private Context context;
	public static MainActivity activity;
	//(屏蔽HOME键1)需要自己定义标志
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; 
	
	private LinearLayout exitLayout;//退出
	private TextView tvPay,tvFunction;//收银，功能
	private ControlScrollViewPager mViewPager;
	
	/**viewPager适配器*/
	private ViewPagerAdapter mAdapter;
	/**Fragment界面*/
	private MainPayFragment payFragment;//收银
	private MainFunctionFragment functionFragment;//功能
	
	
	// ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     * 
     */
    protected String appId = "11072721";

    protected String appKey = "eZGGWmPXBYCbTBrcxZWkGX7B";

    protected String secretKey = "a336b0c83f57cc5a878489f372ecfe9a";
    
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;
    
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_MALE;
    
    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    public static MySyntherizer synthesizer;
    private static final String TAG = "MainActivity";
    protected Handler mainHandler;


	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	public static String posProvider;

	UpdateInfo info;

	// DemoPushService.class 自定义服务名称, 核心服务
	private Class userPushService = GetuiPushService.class;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		context = MainActivity.this;
		activity = MainActivity.this;
		BaseApplication.getInstance().addActivity(this);
        //(屏蔽HOME键2)关键代码
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.main_activity);
        mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handle(msg);
            }

        };
        initData();
        // 初始化TTS引擎
     	initialTts(); 
        initView();
        initActivity();

        try{
			PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
		}catch (Exception e){
        	e.printStackTrace();
		}



    }

	@Override
	protected void onStart() {
		super.onStart();
		try{
			PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/*
	 * 从服务器获取xml解析并进行版本号比对
	 */
	private void CheckVersionTask(){


		new Thread(){
			@Override
			public void run() {
				try {
					String versionName = "";
					try {
						versionName = Utils.getVersionName(context);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//获取服务器保存版本信息的路径
					String path = "";
					if(posProvider.equals(NEW_LAND)){
						path = NitConfig.getNEWLANDUrl;
						Log.e("新大陆版本更新地址：","-----------");
					}else if(posProvider.equals(FUYOU_SF)){
						path = NitConfig.getFUYOUUrl;
						Log.e("富友版本更新地址：","-----------");
					}
					//解析xml文件封装成对象
					info =  HttpURLConUtil.getUpdateInfo(path);
					Log.i(TAG,"版本号为："+info.getVersion());
					String xmlVersionName = info.getVersion();
					if(xmlVersionName.equals(versionName)){
						Log.i(TAG,"版本号相同无需升级");

					}else{
						Log.i(TAG,"版本号不同 ,提示用户升级 ");
						Message msg = new Message();
						msg.what = 1;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};

		}.start();

	}


    protected void handle(Message msg) {
        int what = msg.what;
        switch (what) {
//            case PRINT:
//                print(msg);
//                break;
//            case UI_CHANGE_INPUT_TEXT_SELECTION:
//                if (msg.arg1 <= mInput.getText().length()) {
//                    mInput.setSelection(0, msg.arg1);
//                }
//                break;
//            case UI_CHANGE_SYNTHES_TEXT_SELECTION:
//                SpannableString colorfulText = new SpannableString(mInput.getText().toString());
//                if (msg.arg1 <= colorfulText.toString().length()) {
//                    colorfulText.setSpan(new ForegroundColorSpan(Color.GRAY), 0, msg.arg1, Spannable
//                            .SPAN_EXCLUSIVE_EXCLUSIVE);
//                    mInput.setText(colorfulText);
//                }
//                break;
//            default:
//                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //对话框提示用户升级程序
                    showUpdateDialog();
                    break;
            }
        }
    };

    private void initData(){
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this, "posInit");
		if(sharedPreferencesUtil.contain("posProvider")){
			posProvider = (String) sharedPreferencesUtil.getSharedPreference("posProvider", "");
			if(Utils.isEmpty(posProvider)){
				//取默认值为新大陆
				posProvider = NEW_LAND;
			}
		}else{
			//取默认值为新大陆
			posProvider = NEW_LAND;
		}

		/** 检测版本 */
		CheckVersionTask();


	}
	
	/**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        try {
			LoggerProxy.printable(true); // 日志打印在logcat中
			// 设置初始化参数
			// 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
			SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

			Map<String, String> params = getParams();


			// appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
			InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);

			// 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
			// 上线时请删除AutoCheck的调用
			AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
			    @Override
			    public void handleMessage(Message msg) {
			        if (msg.what == 100) {
			            AutoCheck autoCheck = (AutoCheck) msg.obj;
			            synchronized (autoCheck) {
			                String message = autoCheck.obtainDebugMessage();
//                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
			                 Log.e("AutoCheckMessage", message);
			            }
			        }
			    }

			});
			synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
    
    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;

    }
    
    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
//            toPrint("【error】:copy files from assets failed." + e.getMessage());
              // 可以用下面一行替代，在logcat中查看代码
              Log.e("【error】:", e.getMessage());
        }
        return offlineResource;
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	synthesizer.release();
        Log.i(TAG, "释放资源成功");
    }
    
    private void initView(){
    	exitLayout = (LinearLayout) findViewById(R.id.main_top_exit_layout);
    	mViewPager=(ControlScrollViewPager) findViewById(R.id.main_viewPager);
    	//注册Viewpager滑动监听事件
    	mViewPager.setOnPageChangeListener(this);
    	//Tab
    	tvPay=(TextView) findViewById(R.id.main_top_pay_text);
    	tvFunction=(TextView) findViewById(R.id.main_top_function_text);
    	exitLayout.setOnClickListener(this);
    	tvPay.setOnClickListener(this);
    	tvFunction.setOnClickListener(this);
    }
    
    /**初始化界面配置*/
    private void initActivity(){
    	//初始化fragment
    	payFragment=new MainPayFragment(synthesizer);
    	functionFragment=new MainFunctionFragment(synthesizer);
    	//初始化Adapter
    	mAdapter=new ViewPagerAdapter(getSupportFragmentManager());
    	//预加载界面数(ViewPager预加载默认数是1个，既设置0也没效果，他会默认把相邻界面数据预加载)
		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setAdapter(mAdapter);
		//初始化默认加载界面
		mViewPager.setCurrentItem(0);
		tvPay.setTextSize(22);
    	
    }

    
    /**
     * 定义viewPager左右滑动的适配器
     */
    public class ViewPagerAdapter extends FragmentPagerAdapter{
		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		
		@Override
		public Fragment getItem(int position) {
			if(position==0){
				return payFragment;
			}else {
				return functionFragment;
			}
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
    	
    }

    /**ViewPager滑动改变Tab按钮状态*/
    @Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if(arg0==0){
			//先初始化所有Tab
			resetImg();
			tvPay.setTextSize(22);
		}else{
			//先初始化所有Tab
			resetImg();
			tvFunction.setTextSize(22);
		}
	}

    /**  Tab选中监听事件  */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_top_exit_layout://退出应用
			//弹出提示对话窗口
			showColseAPPDialog();
			break;
		case R.id.main_top_pay_text://收银
			//先初始化所有Tab
			resetImg();
			tvPay.setTextSize(22);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.main_top_function_text://功能
			//先初始化所有Tab
			resetImg();
			tvFunction.setTextSize(22);
			mViewPager.setCurrentItem(1);
			break;
		}
	}
	
	/**
	 * 初始化所有tab
	 */
	private void resetImg(){
		tvPay.setTextSize(16);
		tvFunction.setTextSize(16);
	}
	
	
	
	/**  退出应用提示窗口 */
	private void showColseAPPDialog(){
		View view = LayoutInflater.from(this).inflate(R.layout.close_dialog_activity, null);
		TextView btok = (TextView) view.findViewById(R.id.close_dialog_tvOk);
		TextView btCancel = (TextView) view.findViewById(R.id.close_dialog_tvCancel);
		final Dialog myDialog = new Dialog(this,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关闭应用
				finish();
				myDialog.dismiss();
				
			}
		});
		btCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

	/**
	 * 弹出版本升级提示框
	 */
	private void showUpdateDialog(){
		View view = LayoutInflater.from(this).inflate(R.layout.update_hint_dialog, null);
		//版本号：
		TextView tvVersion=(TextView) view.findViewById(R.id.update_hint_tvVersion);
		tvVersion.setText("v"+info.getVersion());
		//描述信息

		//操作按钮
		final Button btUpdate = (Button) view.findViewById(R.id.update_hint_btUpdate);
		final Dialog mDialog = new Dialog(this,R.style.dialog);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		mDialog.setContentView(view);
		btUpdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();

			}
		});
		//点击屏幕和物理返回键dialog不消失
		mDialog.setCancelable(false);
		mDialog.show();
	}

	//拦截/屏蔽返回键、MENU键实现代码
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
	        showColseAPPDialog();
	    } else 
	    if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
	        //监控/拦截菜单键
	         return true;
	    }else 
	    if (keyCode == KeyEvent. KEYCODE_HOME) { 
	    	 //(屏蔽HOME键3)
	   
	    	 return true;     
	    }   
	return super.onKeyDown(keyCode, event);
	} 
  
}
