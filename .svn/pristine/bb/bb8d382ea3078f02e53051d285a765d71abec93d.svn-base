package com.wanding.xingpos.instalment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pull.refresh.view.XListView;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.base.BaseFragment;
import com.wanding.xingpos.bean.InstalmentQueryListResData;
import com.wanding.xingpos.bean.InstalmentQueryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 查询订单
 */
@SuppressLint("ValidFragment")
public class InstalmentScreenRePayFragment extends BaseFragment implements View.OnClickListener,AdapterView.OnItemClickListener,XListView.IXListViewListener{

    private Dialog hintDialog;// 加载数据时对话框
    private int mCurIndex = -1;
    private static final String FRAGMENT_INDEX = "1";
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;
    private boolean onResume=true;//onResume()方法初始化不执行


    private XListView mListView;



    private UserLoginResData posPublicData;

    /**
     * contractsState	否	N1	0还款中，1结清，2退款中
     */
    public static String contractsState;



    private List<InstalmentQueryResData> lsOrder = new ArrayList<InstalmentQueryResData>();
    private InstalmentListAdapte mAdapter;

    private int pageNum = 1;
    private int pageSize = 20;
    //交易总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，

    private boolean isService = true;//服务是否正在运行

    @SuppressLint("ValidFragment")
    public InstalmentScreenRePayFragment(UserLoginResData posPublicData) {
        super();
        this.posPublicData = posPublicData;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instalment_screen_fragment,null,false);
        initView(view);
        initListener();
        initData();

        isPrepared = true;
        lazyLoad();

        //因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup)view.getParent();
        if(parent != null) {
            parent.removeView(view);
        }
        onResume=false;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(onResume){
            //加载数据

        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible||mHasLoadedOnce) {
            return;
        }

        //先获取参数
        pageNum = 1;
        getReqParameter();

    }

    /**
     * 初始化控件
     */
    private void initView(View view){
        mListView = view.findViewById(R.id.instalment_screen_xListView);
    }

    private void initListener(){
        mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        mListView.setPullRefreshEnable(false);//是否可以下拉刷新,默认可以下拉
        mListView.setXListViewListener(this);//注册刷新和加载更多接口

        mListView.setOnItemClickListener(this);





    }

    private void initData(){
        mAdapter = new InstalmentListAdapte(getContext(),lsOrder);
        mListView.setAdapter(mAdapter);
    }

    /**
     *  查询订单
     */
    private void queryOrderList(final String url,final String content){
        if(pageNum == 1){
            hintDialog= CustomDialog.CreateDialog(getContext(), "    查询中...");
            hintDialog.show();
            hintDialog.setCancelable(false);
        }
        isService = false;
        new Thread(){
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    //HttpURLConnection connection = (HttpURLConnection) nURL.openConnection();
                    connection.setConnectTimeout(60000);
                    connection.setReadTimeout(60000);
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("User-Agent", "Fiddler");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Charset", "UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(content.getBytes());
                    os.close();
                    int  code = connection.getResponseCode();
                    Log.e("分期订单列表查询返回状态吗", code+"");
                    if(code == 200){
                        InputStream is = connection.getInputStream();
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("分期订单列表查询返回JSON值", jsonStr);
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=jsonStr;
                        mHandler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=404;
                        mHandler.sendMessage(msg);
                    }
                }catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("IOException", "IOException");
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
                }catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("Exception", "Exception");
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
                }

            };
        }.start();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    mAdapter.notifyDataSetChanged();
                    //更新完毕
                    onLoad();
                    break;
                case LOADMORE:
                    mListView.setPullLoadEnable(true);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成
                    onLoad();
                    break;
                case NOLOADMORE:
                    mListView.setPullLoadEnable(false);//是否可以上拉加载更多
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    break;
                case 1:
                    String list_jsonStr = (String) msg.obj;
                    //使用私钥解密返回的响应内容
                    getResponseStr(list_jsonStr);
                    if(hintDialog!=null){
                        hintDialog.dismiss();
                    }
                    isService = true;
                    loadMore = "0";
                    break;
                case 201:
                    loadMore = "0";
                    Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
                    break;
                case 202:
                    loadMore = "0";
                    Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
                    break;
                case 404:
                    if(hintDialog!=null){
                        hintDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
                    isService = true;
                    loadMore = "0";
                    break;
            }
        };
    };


    /**
     * 获取封装参数转换为请求参数
     */
    private void getReqParameter(){
        String contractsStateStr = InstalmentScreenActivity.contractsState;
        String pageNumStr = String.valueOf(pageNum);
        String pageSizeStr = String.valueOf(pageSize);
        String beginTime = InstalmentScreenActivity.startDateStr;
        String endTime = InstalmentScreenActivity.endDateStr;

        try {
            // 从文件中得到公钥字符串
            InputStream pubInput;
            if("true".equals(NitConfig.isTest)){
                pubInput = getContext().getResources().getAssets().open("reqsdk_rsa_public_key.pem");
            }else{
                pubInput = getContext().getResources().getAssets().open("req_rsa_public_key.pem");
            }

            String txnData = RequestUtil.queryListReqTxnData(posPublicData,contractsStateStr,pageNumStr,pageSizeStr,beginTime,endTime,pubInput);
            JSONObject txnObj = new JSONObject();
            txnObj.put("txnData", txnData);
            String signData=txnData+RequestUtil.getMd5key();
            txnObj.put("signValue", MD5.MD5Encode(signData));
            String content = txnObj.toString();
            Log.e("最终提交的参数：",content);

            if(isService){
                //请求数据
                String url = NitConfig.instalmentServiceUrl;
//            String url = "http://sandbox.starpos.com.cn/installment";
                queryOrderList(url,content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解密响应内容
     */
    private void getResponseStr(String jsonStr){

        try {
            // 从文件中得到私钥字符串
            InputStream priInput;
            if("true".equals(NitConfig.isTest)){
                priInput = getContext().getResources().getAssets().open("respsdk_pkcs8_rsa_private_key.pem");
            }else{
                priInput = getContext().getResources().getAssets().open("resp_pkcs8_rsa_private_key.pem");
            }

            JSONObject job = new JSONObject(jsonStr);
            String respTxnData = job.getString("txnData");
            String txnData = RequestUtil.getResponseJsonStr(respTxnData,priInput);
            Log.e("解密后的txnData字符串",txnData);

            getQueryOrderList(txnData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getQueryOrderList(String txnData){
        Gson gjson  =  GsonUtils.getGson();
        java.lang.reflect.Type type = new TypeToken<InstalmentQueryListResData>() {}.getType();
        InstalmentQueryListResData queryListResData = gjson.fromJson(txnData, type);
        String respCodeStr = queryListResData.getRespCode();
        String respMsgStr = queryListResData.getRespMsg();
        if(Utils.isNotEmpty(respCodeStr)){
            if("000000".equals(respCodeStr)){
                List<InstalmentQueryResData>  orderList = new ArrayList<InstalmentQueryResData>();
                //获取的list
                orderList = queryListResData.getREC();
                getMoerNum = orderList.size();
                Log.e("当前页查询条数：", getMoerNum+""+"条");
                if(pageNum == 1){//获取总条数
                    orderListTotalCount = Integer.parseInt(queryListResData.getTotal());
                    Log.e("总条数：", orderListTotalCount+"");
                    lsOrder.clear();
                }
                lsOrder.addAll(orderList);
                Log.e("查询数据：", lsOrder.size()+""+"条");
                //关闭上拉或下拉View，刷新Adapter
                if("0".equals(loadMore)){
                    mAdapter = new InstalmentListAdapte(getContext(),lsOrder);
                    mListView.setAdapter(mAdapter);
                    if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageSize){
                        Message msg = new Message();
                        msg.what = LOADMORE;
                        mHandler.sendEmptyMessageDelayed(LOADMORE, 0);
                    }else{
                        Message msg = new Message();
                        msg.what = NOLOADMORE;
                        mHandler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                    }
                }else if("1".equals(loadMore)){
                    Message msg = new Message();
                    msg.what = REFRESH;
                    mHandler.sendEmptyMessageDelayed(REFRESH, 2000);
                }else if("2".equals(loadMore)){
                    Message msg = new Message();
                    msg.what = LOADMORE;
                    mHandler.sendEmptyMessageDelayed(LOADMORE, 2000);
                }
            }else{
                lsOrder.clear();
                mAdapter = new InstalmentListAdapte(getContext(),lsOrder);
                mListView.setAdapter(mAdapter);
                Message msg = new Message();
                msg.what = NOLOADMORE;
                mHandler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                Toast.makeText(getContext(),respMsgStr,Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getContext(),"查询出错！",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.instalment_query_tvQuery:

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //这里因为添加了头部，所以数据Item的索引值发生变化，即对应item应为：position-1；
        InstalmentQueryResData order = lsOrder.get(position-1);
        Intent in = new Intent();
        in.setClass(getContext(),InstalmentOrderDetailActivity.class);
        in.putExtra("order",order);
        startActivity(in);
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(new Date().toLocaleString());
    }

    @Override
    public void onRefresh() {
        loadMore = "1";
        pageNum = 1;
        //先获取参数
        getReqParameter();
    }

    @Override
    public void onLoadMore() {
        loadMore = "2";
        if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageSize){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            //先获取参数
            getReqParameter();
        }else{
            //没有数据执行两秒关闭view
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    mHandler.sendMessage(msg);
                }
            }, 1000);

        }
    }
}
