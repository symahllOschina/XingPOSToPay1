package com.wanding.xingpos.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pull.refresh.view.XListView;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.WriteOffRecodeListAdapter;
import com.wanding.xingpos.base.BaseActivity;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.bean.WriteOffRecodeDetailResData;
import com.wanding.xingpos.bean.WriteOffRecodeListResData;
import com.wanding.xingpos.httputils.HttpJsonReqUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.Utils;
import com.wanding.xingpos.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 核销记录
 */
public class WriteOffRecodeListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {

    public static final int RESULT_CODE = 1;
    private Dialog hintDialog;// 加载数据时对话框

    private LinearLayout titleLayout;
    private ImageView imgBack;
    private TextView tvTitle;

    /**
     * 搜索框
     */
    private ClearEditText etSearch;
    private TextView tvSearch;

    private XListView xListView;

    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
    //总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private int refreshCount = 1;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，

    private UserLoginResData loginInitData;

    String etSearchStr = "";
    private List<WriteOffRecodeDetailResData> list = new ArrayList<WriteOffRecodeDetailResData>();
    private BaseAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_off_recode_list);
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initView();
        initListener();
        mAdapter = new WriteOffRecodeListAdapter(activity,list);
        xListView.setAdapter(mAdapter);


        getWriteOffRecodeList(pageNum,pageNumCount);
    }

    /**
     * 初始化界面控件
     */
    private void initView(){
        titleLayout = findViewById(R.id.search_header_titleLayout);
        tvTitle = (TextView) findViewById(R.id.search_header_tvTitle);

        etSearch = findViewById(R.id.search_header_etSearch);
        tvSearch = findViewById(R.id.search_header_tvSearch);


        xListView = findViewById(R.id.writeOff_recode_xListView);

        tvTitle.setText("核销记录");

        /**
         * ListView初始化
         */
        xListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        xListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉

    }

    private void initListener(){
        titleLayout.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        //注册刷新和加载更多接口
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
    }

    /**
     * 查询核销记录
     */
    private void getWriteOffRecodeList(final int pageNum,final int pageCount){
        if(refreshCount == 1){
            hintDialog=CustomDialog.CreateDialog(getContext(), "    加载中...");
            hintDialog.show();
            hintDialog.setCanceledOnTouchOutside(false);
        }
        final String url = NitConfig.writeOffRecodeUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("pageNum",pageNum+"");
                    userJSON.put("mid",loginInitData.getMid());
                    userJSON.put("code",etSearchStr);
                    String content = String.valueOf(userJSON);
                    Log.e("发起请求参数：", content);
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
                    Log.e("返回状态吗：", code+"");
                    if(code == 200){

                        InputStream is = connection.getInputStream();
                        //下面的json就已经是{"Person":{"username":"zhangsan","age":"12"}}//这个形式了,只不过是String类型
                        String jsonStr = HttpJsonReqUtil.readString(is);
                        Log.e("返回JSON值：", jsonStr);
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=jsonStr;
                        mHandler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=404;
                        mHandler.sendMessage(msg);
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg=new Message();
                    msg.what=404;
                    mHandler.sendMessage(msg);
                }   catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                    xListView.setPullLoadEnable(true);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成
                    onLoad();
                    break;
                case NOLOADMORE:
                    xListView.setPullLoadEnable(false);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    break;
                case 1:
                    String batchSecurListJsonStr=(String) msg.obj;
                    batchSecurListJsonStr(batchSecurListJsonStr);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
                case 2:
                    String writeOffResStr=(String) msg.obj;

                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    break;
                case 201:
                    loadMore = "0";
                    Toast.makeText(getContext(), "网络连接断开，数据获取失败！", Toast.LENGTH_LONG).show();
                    break;
                case 202:
                    Toast.makeText(getContext(), "请检查网络是否连接！", Toast.LENGTH_LONG).show();
                    loadMore = "0";
                    break;
                case 404:
                    Toast.makeText(getContext(), "服务器异常...", Toast.LENGTH_LONG).show();
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
            }
        };
    };

    private void batchSecurListJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<WriteOffRecodeListResData>() {}.getType();
                WriteOffRecodeListResData writeOffRecodeListResData = gjson.fromJson(dataJson, type);
                //获取总条数
                orderListTotalCount = writeOffRecodeListResData.getTotal();
                Log.e("总条数：", orderListTotalCount+"");
                List<WriteOffRecodeDetailResData> recodeList = new ArrayList<WriteOffRecodeDetailResData>();
                //获取的list
                recodeList = writeOffRecodeListResData.getCouponList();
                getMoerNum = recodeList.size();
                if(pageNum == 1){
                    list.clear();
                }
                list.addAll(recodeList);
                Log.e("查询数据：", list.size()+""+"条");
                //关闭上拉或下拉View，刷新Adapter
                if("0".equals(loadMore)){
                    if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                        Message msg1 = new Message();
                        msg1.what = LOADMORE;
                        mHandler.sendEmptyMessageDelayed(LOADMORE, 0);
                    }else{
                        Message msg1 = new Message();
                        msg1.what = NOLOADMORE;
                        mHandler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                    }
                }else if("1".equals(loadMore)){
                    Message msg1 = new Message();
                    msg1.what = REFRESH;
                    mHandler.sendEmptyMessageDelayed(REFRESH, 0);
                }else if("2".equals(loadMore)){
                    Message msg1 = new Message();
                    msg1.what = LOADMORE;
                    mHandler.sendEmptyMessageDelayed(LOADMORE, 0);
                }

            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                mHandler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.search_header_titleLayout:
                finish();
                break;
            case R.id.search_header_tvSearch:
                etSearchStr = etSearch.getText().toString().trim();
                if(Utils.isFastClick()){
                    return;
                }
                refreshCount = 1;
                loadMore = "0";
                pageNum = 1;
                getWriteOffRecodeList(pageNum,pageNumCount);

                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WriteOffRecodeDetailResData writeOffRecode = list.get(position - 1);
        Intent intent = new Intent();
        intent.setClass(activity,WriteOffRecodeDetailActivity.class);
        intent.putExtra("userLoginData",loginInitData);
        intent.putExtra("writeOffRecode",writeOffRecode);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getWriteOffRecodeList(pageNum,pageNumCount);

    }

    @Override
    public void onLoadMore() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "2";
        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getWriteOffRecodeList(pageNum,pageNumCount);
        }else{
            //没有数据执行两秒关闭view
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    mHandler.sendMessage(msg);
                }
            }, 0);

        }
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(new Date().toLocaleString());
    }
}
