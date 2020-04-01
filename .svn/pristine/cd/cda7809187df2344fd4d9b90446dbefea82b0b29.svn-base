package com.wanding.xingpos.activity;
import java.io.IOException;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.auth.bean.PreLicensingResp;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.PosRefundResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.WriteOffResData;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.SharedPreferencesUtil;
import com.wanding.xingpos.util.Utils;

/** 重打印Activity */
public class AgainPrintActivity extends BaseActivity implements OnClickListener{
	
	private Context context = AgainPrintActivity.this;
	
	private ImageView imgBack;
	private TextView tvTitle;
	private RelativeLayout cardPayLayout,scanPayLayout,writeOffLayout;//上一笔银行卡交易，上一笔二维码交易,上一笔核劵交易
	
	
	private CardPaymentDate carPayResData;//银行卡消费保存数据
	private UserLoginResData posPublicData;
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
    private String TAG = "lyc";
    
    private SharedPreferencesUtil sharedPreferencesUtil;
    private SharedPreferencesUtil sharedPreferencesUtil1;
	/** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
	private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private static final String NEW_LAND = "newland";
	private static final String FUYOU_SF= "fuyousf";
	private String posProvider;

	/**
	 * 新大陆(打印机，扫码摄像头)AIDL服务
	 */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
        }
    };

	/**
	 * 富友(打印机)AIDL服务
	 */
	private PrintInterface printService = null;
	private PrintReceiver printReceiver;
	private ServiceConnection printServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			printService = PrintInterface.Stub.asInterface(arg1);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}

	};

	class PrintReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getStringExtra("result");
//			etBack.setText("reason："+result);
		}
	}

	private int printType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.again_print_activity);
		posProvider = MainActivity.posProvider;
		if(posProvider.equals(NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(FUYOU_SF)){
			initPrintService();
		}
		initView();
		initData();
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

		if(posProvider.equals(NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
		}else if(posProvider.equals(FUYOU_SF)){
			if(null != printReceiver){
				unregisterReceiver(printReceiver);
			}
			unbindService(printServiceConnection);
		}
		Log.e(TAG, "释放资源成功");
    }
	
	/**
     * 绑定服务
     */
    public void bindServiceConnection() {
        bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
        Log.e("绑定服务", "绑定服务1");
    }
	private void initPrintService(){
		printRegisterReceiver();
		Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
		printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
		printIntent.setPackage("com.fuyousf.android.fuious");
		bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void printRegisterReceiver(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.fuyousf.android.fuious.service.print");
		printReceiver = new PrintReceiver();
		registerReceiver(printReceiver, intentFilter);
	}

    
    /**
     * 初始化打印设备
     */
    public void getPrinter() {
        Log.i(TAG, "获取打印机设备实例...");
        try {
            aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
//            showMsgOnTextView("初始化打印机实例");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
	
	/** 初始化数据 */
	private void initData(){
		try {
			posPublicData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", getContext()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 初始化界面控件
	 */
	private void initView(){
		imgBack = (ImageView) findViewById(R.id.title_imageBack);
		tvTitle = (TextView) findViewById(R.id.title_tvTitle);
		cardPayLayout = (RelativeLayout) findViewById(R.id.again_print_layoutCardPay);
		scanPayLayout= (RelativeLayout) findViewById(R.id.again_print_layoutScanPay);
		writeOffLayout= (RelativeLayout) findViewById(R.id.again_print_layoutWriteOffRecord);

		cardPayLayout.setOnClickListener(this);
		scanPayLayout.setOnClickListener(this);
		writeOffLayout.setOnClickListener(this);

		tvTitle.setText("重打印");
		imgBack.setOnClickListener(this);
	}
	
	/**
	 * 银行卡交易记录打印
	 * int cardOption的值分别有11.1.2.3.4.分别对应，银行卡消费，预授权，预授权撤销，预授权完成，预授权完成撤销
	 */
	private void startPrintingCardPayRecord(){
		try{
			//判断本地是否有交易记录
			sharedPreferencesUtil = new SharedPreferencesUtil(context, "cardPay");
			sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "printing");
			//取出保存的默认值
			printNum = (String) sharedPreferencesUtil1.getSharedPreference("printNumKey", printNum);
			isDefault = (boolean) sharedPreferencesUtil1.getSharedPreference("isDefaultKey", isDefault);
			if(sharedPreferencesUtil.contain("cardPayYes")){
				boolean cardPayValue = ((Boolean)sharedPreferencesUtil.getSharedPreference("cardPayYes", false));
				String cardPayTypeValue = ((String)sharedPreferencesUtil.getSharedPreference("cardPayType", ""));
				int cardOption = ((int)sharedPreferencesUtil.getSharedPreference("cardOption", 0));
				if(cardPayValue){
					if(cardPayTypeValue!=null&&!cardPayTypeValue.equals("")){
						if(cardPayTypeValue.equals("pay")){
							try {
								carPayResData = (CardPaymentDate) MySerialize.deSerialization(MySerialize.getObject("cardPayOrder", getContext()));
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							printType = 1;
							if(posProvider.equals(NEW_LAND)){
								//初始化打印机
								getPrinter();
								/** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
								int index = 1;
								if(printNum.equals("printNumNo")){

								}else if(printNum.equals("printNumOne")){
									//打印
									NewlandPrintUtil.cardPaySuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,cardOption,isDefault,index);

								}else if(printNum.equals("printNumTwo")){
									//打印
									NewlandPrintUtil.cardPaySuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,cardOption,isDefault,index);
									try {
										Thread.sleep(NewlandPrintUtil.time);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//弹出对话框提示打印下一联

									showPrintTwoDialog(cardOption);

								}

							}else if(posProvider.equals(FUYOU_SF)){
								int index = 1;
								if(printNum.equals("printNumNo")){

								}else if(printNum.equals("printNumOne")){
									//打印
									FuyouPrintUtil.cardPaySuccessPrintText(getContext(),printService,carPayResData,posPublicData,cardOption,isDefault,index);

								}else if(printNum.equals("printNumTwo")){
									//打印
									FuyouPrintUtil.cardPaySuccessPrintText(getContext(),printService,carPayResData,posPublicData,cardOption,isDefault,index);
									try {
										Thread.sleep(FuyouPrintUtil.time);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//弹出对话框提示打印下一联

									showPrintTwoDialog(cardOption);

								}



							}

						}else if(cardPayTypeValue.equals("refund")){
							try {
								carPayResData = (CardPaymentDate) MySerialize.deSerialization(MySerialize.getObject("cardPayOrder", getContext()));
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							printType = 2;
							if(posProvider.equals(NEW_LAND)){
								//初始化打印机
								getPrinter();
								int index = 1;
								if(printNum.equals("printNumNo")){

								}else if(printNum.equals("printNumOne")){
									//打印
									NewlandPrintUtil.cardRefundSuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,isDefault,index);

								}else if(printNum.equals("printNumTwo")){
									//打印
									NewlandPrintUtil.cardRefundSuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,isDefault,index);
									try {
										Thread.sleep(NewlandPrintUtil.time);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//弹出对话框提示打印下一联
									showPrintTwoDialog(cardOption);

								}
							}else if(posProvider.equals(FUYOU_SF)){
								int index = 1;
								if(printNum.equals("printNumNo")){

								}else if(printNum.equals("printNumOne")){
									//打印
									FuyouPrintUtil.cardRefundSuccessPrintText(getContext(),printService,carPayResData,posPublicData,isDefault,index);

								}else if(printNum.equals("printNumTwo")){
									//打印
									FuyouPrintUtil.cardRefundSuccessPrintText(getContext(),printService,carPayResData,posPublicData,isDefault,index);
									try {
										Thread.sleep(FuyouPrintUtil.time);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									//弹出对话框提示打印下一联
									showPrintTwoDialog(cardOption);

								}

							}

						}
					}else{
						Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
					}

				}else{
					Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
			}
		}catch (Exception e){
			e.printStackTrace();
			Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
		}

	}
	
	/**  扫码交易记录打印 */
	private void startPrintingScanPayRecord(){
		try{
			PosScanpayResData payResData = null;
			PreLicensingResp authPayResData = null;
			PosRefundResData refundResData = null;
			ScanPaymentDate paymentData = null;
			ScanPaymentDate refundmentData = null;
			sharedPreferencesUtil = new SharedPreferencesUtil(context, "scanPay");
			sharedPreferencesUtil1 = new SharedPreferencesUtil(context, "printing");
			printNum = (String) sharedPreferencesUtil1.getSharedPreference("printNumKey", printNum);
			isDefault = (boolean) sharedPreferencesUtil1.getSharedPreference("isDefaultKey", isDefault);
			//判断本地是否有交易记录
			if(sharedPreferencesUtil.contain("scanPayYes")){
				boolean scanPayValue = ((Boolean)sharedPreferencesUtil.getSharedPreference("scanPayYes", false));
				boolean payServiceType = ((Boolean)sharedPreferencesUtil.getSharedPreference("payServiceType", false));
				String scanPayTypeValue = ((String)sharedPreferencesUtil.getSharedPreference("scanPayType", ""));
				//是否有交易 true : 有
				if(scanPayValue){
					//交易通道：true:默认通道，false:第三方
					if(payServiceType){
						if(scanPayTypeValue!=null&&!scanPayTypeValue.equals("")){
							//判断交易类型是支付还是退款pay:扫码支付，refund:扫码退款，auth:扫码预授权
							if(scanPayTypeValue.equals("pay")){
								try {
									//扫码支付成功返回对象保存在本地
									payResData=(PosScanpayResData) MySerialize.deSerialization(MySerialize.getObject("scanPayOrder", getContext()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								printType = 3;
								int index = 1;
								if(posProvider.equals(NEW_LAND)){

									//初始化打印机
									getPrinter();
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);
										try {
											Thread.sleep(NewlandPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}


								}else if(posProvider.equals(FUYOU_SF)){
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										FuyouPrintUtil.paySuccessAgainPrintText(getContext(), printService, payResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										FuyouPrintUtil.paySuccessAgainPrintText(getContext(), printService, payResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);
										try {
											Thread.sleep(FuyouPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}

								}

							}else if(scanPayTypeValue.equals("refund")){
								try {
									//退款成功保存在本地的数据
									refundResData=(PosRefundResData) MySerialize.deSerialization(MySerialize.getObject("refundOrder", getContext()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								printType = 4;
								int index = 1;
								if(posProvider.equals(NEW_LAND)){
									//初始化打印机
									getPrinter();
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);
										try {
											Thread.sleep(NewlandPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}


								}else if(posProvider.equals(FUYOU_SF)){
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										FuyouPrintUtil.refundSuccessPrintText(getContext(),printService,refundResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										FuyouPrintUtil.refundSuccessPrintText(getContext(),printService,refundResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);
										try {
											Thread.sleep(FuyouPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}
								}

							}else if(scanPayTypeValue.equals("auth")){
								Toast.makeText(context,"dddddddd",Toast.LENGTH_LONG).show();

								try {
									//退款成功保存在本地的数据
									authPayResData = (PreLicensingResp) MySerialize.deSerialization(MySerialize.getObject("scanAuth", getContext()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								printType = 7;
								int index = 1;
								if(posProvider.equals(NEW_LAND)){
									//初始化打印机
									getPrinter();
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印



									}else if(printNum.equals("printNumTwo")){
										//打印


									}


								}else if(posProvider.equals(FUYOU_SF)){
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
//									FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
										String printTextStr = FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
										Intent intent=new Intent();
										intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
										intent.putExtra("data", printTextStr);
										intent.putExtra("isPrintTicket", "true");
										startActivityForResult(intent, 99);

									}else if(printNum.equals("printNumTwo")){
										//打印
//									FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
										String printTextStr = FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
										Intent intent=new Intent();
										intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
										intent.putExtra("data", printTextStr);
										intent.putExtra("isPrintTicket", "true");
										startActivityForResult(intent, 99);
										try {
											Thread.sleep(FuyouPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}
								}

							}else{
								Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
						}
					}else{
						//最后一笔交易记录是走新大陆通道交易记录
						if(scanPayTypeValue!=null&&!scanPayTypeValue.equals("")){
							//判断交易类型是支付还是退款
							if(scanPayTypeValue.equals("pay")){
								try {
									//扫码支付成功返回对象保存在本地
									paymentData=(ScanPaymentDate) MySerialize.deSerialization(MySerialize.getObject("scanPayOrder", getContext()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								printType = 5;
								int index = 1;
								if(posProvider.equals(NEW_LAND)){
									//初始化打印机
									getPrinter();

									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, paymentData,posPublicData,"pay",isDefault,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, paymentData,posPublicData,"pay",isDefault,index);
										try {
											Thread.sleep(NewlandPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}
								}else if(posProvider.equals(FUYOU_SF)){
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, paymentData,posPublicData,"pay",isDefault,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, paymentData,posPublicData,"pay",isDefault,index);
										try {
											Thread.sleep(FuyouPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}
								}

							}else if(scanPayTypeValue.equals("refund")){
								try {
									//退款成功保存在本地的数据
									refundmentData=(ScanPaymentDate) MySerialize.deSerialization(MySerialize.getObject("refundOrder", getContext()));
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								printType = 6;
								int index = 1;
								if(posProvider.equals(NEW_LAND)){
									//初始化打印机
									getPrinter();
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, refundmentData,posPublicData,"refund",isDefault,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, refundmentData,posPublicData,"refund",isDefault,index);
										try {
											Thread.sleep(NewlandPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}
								}else if(posProvider.equals(FUYOU_SF)){
									if(printNum.equals("printNumNo")){

									}else if(printNum.equals("printNumOne")){
										//打印
										FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, refundmentData,posPublicData,"refund",isDefault,index);

									}else if(printNum.equals("printNumTwo")){
										//打印
										FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, refundmentData,posPublicData,"refund",isDefault,index);
										try {
											Thread.sleep(FuyouPrintUtil.time);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										//弹出对话框提示打印下一联

										showPrintTwoDialog(payResData,refundResData,paymentData,refundmentData,authPayResData);

									}

								}

							}else{
								Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
							}
						}else{
							Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
						}
					}


				}else{
					Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
			}
		}catch (Exception e){
			e.printStackTrace();
			Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 核销记录打印
	 */
	private void startPrintingWriteOffRecord(){
		try{
			sharedPreferencesUtil = new SharedPreferencesUtil(context, "writeOff");
			//判断本地是否有交易记录
			if(sharedPreferencesUtil.contain("scanPayYes")){
				boolean scanPayValue = ((Boolean)sharedPreferencesUtil.getSharedPreference("scanPayYes", false));
				if(scanPayValue){
					/**
					 * isMakeUp:是否为补打，""正常打印，"C"重打印
					 */
					String isMakeUp = "C";
					try {
						//扫码支付成功返回对象保存在本地
						WriteOffResData writeOffResData =(WriteOffResData) MySerialize.deSerialization(MySerialize.getObject("writeOffOrder", getContext()));
						FuyouPrintUtil.writeOffPrintText(activity,printService,writeOffResData,posPublicData,isMakeUp);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}else{
					Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
			}
		}catch (Exception e){
			e.printStackTrace();
			Toast.makeText(context, "没有交易记录！", Toast.LENGTH_LONG).show();
		}



	}


	/**  重打印提示窗口 */
	private void showPrintTwoDialog(final String printType){
		View view = LayoutInflater.from(context).inflate(R.layout.option_hint_dialog, null);
		TextView btCancel = (TextView) view.findViewById(R.id.option_hint_tvCancel);
		TextView btok = (TextView) view.findViewById(R.id.option_hint_tvOk);
		final Dialog myDialog = new Dialog(context,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				myDialog.dismiss();
				
			}
		});
		btok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//判断打印类型
				if("cardPay".equals(printType)){
					startPrintingCardPayRecord();
				}else if("scanPay".equals(printType)){
					startPrintingScanPayRecord();
				}else if("writeOff".equals(printType)){
					startPrintingWriteOffRecord();
				}
				myDialog.dismiss();
				
			}
		});
		myDialog.show();
	}

	/**  银行卡业务打印下一联提示窗口 */
	private void showPrintTwoDialog(final int cardOption){
		View view = LayoutInflater.from(context).inflate(R.layout.printtwo_dialog_activity, null);
		Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(context,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = 2;
				if(posProvider.equals(NEW_LAND)){

					if(printType == 1){
						NewlandPrintUtil.cardPaySuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,cardOption,isDefault,index);
					}else if(printType == 2){
						NewlandPrintUtil.cardRefundSuccessPrintText(getContext(), aidlPrinter, carPayResData,posPublicData,isDefault,index);
					}

				}else if(posProvider.equals(FUYOU_SF)){
					if(printType == 1){
						FuyouPrintUtil.cardPaySuccessPrintText(getContext(),printService,carPayResData,posPublicData,cardOption,isDefault,index);
					}else if(printType == 2){
						FuyouPrintUtil.cardRefundSuccessPrintText(getContext(),printService,carPayResData,posPublicData,isDefault,index);
					}
				}
				myDialog.dismiss();

			}
		});
		myDialog.show();
		myDialog.setCanceledOnTouchOutside(false);
	}

	/**  下一联提示窗口 */
	private void showPrintTwoDialog(final PosScanpayResData payResData,final PosRefundResData refundResData,
									final ScanPaymentDate paymentData,final ScanPaymentDate refundmentData,final PreLicensingResp authPayResData){
		View view = LayoutInflater.from(context).inflate(R.layout.printtwo_dialog_activity, null);
		Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(context,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = 2;
				if(posProvider.equals(NEW_LAND)){

					if(printType == 3){
						//打印
						NewlandPrintUtil.paySuccessPrintText(getContext(), aidlPrinter, payResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);
					}else if(printType == 4){
						//打印
						NewlandPrintUtil.refundSuccessPrintText(getContext(), aidlPrinter, refundResData,posPublicData,isDefault,NewlandPrintUtil.rePrintRemarks,index);
					}else if(printType == 5){
						//打印
						NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, paymentData,posPublicData,"pay",isDefault,index);
					}else if(printType == 6){
						//打印
						NewlandPrintUtil.posPayAndRefundAgainPrintText(getContext(), aidlPrinter, refundmentData,posPublicData,"refund",isDefault,index);
					}

				}else if(posProvider.equals(FUYOU_SF)){
					if(printType == 3){
						FuyouPrintUtil.paySuccessAgainPrintText(getContext(), printService, payResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);
					}else if(printType == 4){
						//打印
						FuyouPrintUtil.refundSuccessPrintText(getContext(),printService,refundResData,posPublicData,isDefault,FuyouPrintUtil.rePrintRemarks,index);
					}else if(printType == 5){
						FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, paymentData,posPublicData,"pay",isDefault,index);
					}else if(printType == 6){
						FuyouPrintUtil.posPayAndRefundAgainPrintText(getContext(), printService, refundmentData,posPublicData,"refund",isDefault,index);
					}else if(printType == 7){
//						FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
						String printTextStr = FuyouPrintUtil.authPaySuccessPrintText(getContext(),printService,authPayResData,posPublicData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
						Intent intent=new Intent();
						intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
						intent.putExtra("data", printTextStr);
						intent.putExtra("isPrintTicket", "true");
						startActivityForResult(intent, 99);
					}

				}
				myDialog.dismiss();

			}
		});
		myDialog.show();
		myDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		String printType = "";
		switch (v.getId()) {
		case R.id.title_imageBack:
			finish();
			break;
		case R.id.again_print_layoutCardPay://上一笔银行卡交易
			if(Utils.isFastClick()){
				return;
			}
			printType = "cardPay";
			showPrintTwoDialog(printType);
			break;
		case R.id.again_print_layoutScanPay://上一笔二维码交易
			if(Utils.isFastClick()){
				return;
			}
			printType = "scanPay";
			showPrintTwoDialog(printType);
			break;
		case R.id.again_print_layoutWriteOffRecord://上一笔核劵交易
			if(Utils.isFastClick()){
				return;
			}
			printType = "writeOff";
			showPrintTwoDialog(printType);
			break;
			
		}
	}
}
