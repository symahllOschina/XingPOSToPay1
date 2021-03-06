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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pull.refresh.view.XListView;
import com.symapp.dialog.util.CustomDialog;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.DepositRecodeListAdapter;
import com.wanding.xingpos.bean.DepositOrder;
import com.wanding.xingpos.bean.PosDepositRecodeReqData;
import com.wanding.xingpos.bean.PosDepositRecodeResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.httputils.HttpUtil;
import com.wanding.xingpos.httputils.NetworkUtils;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.util.FastJsonUtil;
import com.wanding.xingpos.util.GsonUtils;
import com.wanding.xingpos.util.MySerialize;
import com.wanding.xingpos.util.NitConfig;
import com.wanding.xingpos.util.ToastUtil;
import com.wanding.xingpos.util.Utils;
import com.wanding.xingpos.view.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 核销记录
 */
@ContentView(R.layout.member_topup_recode_list)
public class MemberTopUpRecodeListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {

    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private static final String NEW_LAND = "newland";
    private static final String FUYOU_SF= "fuyousf";
    private String posProvider;
    public static final int RESULT_CODE = 1;
    private Dialog hintDialog;// 加载数据时对话框

    @ViewInject(R.id.search_header_titleLayout)
    private LinearLayout titleLayout;
    @ViewInject(R.id.search_header_tvTitle)
    private TextView tvTitle;

    /**
     * 搜索框
     */
    @ViewInject(R.id.search_header_etSearch)
    private ClearEditText etSearch;
    @ViewInject(R.id.search_header_tvSearch)
    private TextView tvSearch;
    @ViewInject(R.id.member_topup_recode_xListView)
    private XListView xListView;


    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 10;//默认一页加载xx条数据（死值不变）
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
    /**
     * 会员卡号
     */
    String etSearchStr = "";
    private List<DepositOrder> list = new ArrayList<DepositOrder>();
    private BaseAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posProvider = MainActivity.posProvider;

        try {
            loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
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

        initView();
        initListener();
        mAdapter = new DepositRecodeListAdapter(activity,list);
        xListView.setAdapter(mAdapter);



        getRecodeList(pageNum,pageNumCount);
    }

    /**
     * 初始化界面控件
     */
    private void initView(){

        tvTitle.setText("充值记录");
        etSearch.setHint("请输入会员卡号");


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
    private void getRecodeList(final int pageNum,final int pageCount){
        final String startTimeStr = "";
        final String endTimeStr = "";
        //参数实体
        final PosDepositRecodeReqData posBean = PayRequestUtil.depositRecodeQueryReq(pageNum,etSearchStr,startTimeStr,endTimeStr,loginInitData,posProvider);


        if(refreshCount == 1){
            hintDialog=CustomDialog.CreateDialog(activity, "    加载中...");
            hintDialog.show();
            hintDialog.setCanceledOnTouchOutside(false);
        }
        final String url = NitConfig.queryDepositOrderUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    String content = FastJsonUtil.toJSONString(posBean);
                    Log.e("充值记录查询发起请求参数：", content);
                    String content_type = HttpUtil.CONTENT_TYPE_JSON;
                    String jsonStr = HttpUtil.doPos(url,content,content_type);
                    Log.e("充值记录查询返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_ONE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }

            };
        }.start();
    }

    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
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
                case NetworkUtils.MSG_WHAT_ONE:
                    String depositRecodeStr = (String) msg.obj;
                    depositRecodeStr(depositRecodeStr);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    if(hintDialog!=null&&hintDialog.isShowing()){
                        hintDialog.dismiss();
                    }
                    loadMore = "0";
                    break;
                default:
                    break;
            }
        };
    };

    private void depositRecodeStr(String jsonStr){
        //{"code":"000000","msg":"SUCCESS","timestamp":"1563331736045","subCode":"100000","subMsg":"查询异常"}

        try {
            JSONObject job = new JSONObject(jsonStr);
            String code = job.getString("code");
            String msg = job.getString("msg");
            if("000000".equals(code)){
                String subCode = job.getString("subCode");
                String subMsg = job.getString("subMsg");
                if("000000".equals(subCode)){
                    String dataJson = job.getString("data");
                    Gson gjson  =  GsonUtils.getGson();
                    java.lang.reflect.Type type = new TypeToken<PosDepositRecodeResData>() {}.getType();
                    PosDepositRecodeResData recodeResData = gjson.fromJson(dataJson, type);
                    //获取总条数
                    orderListTotalCount = recodeResData.getTotal();
                    Log.e("总条数：", orderListTotalCount+"");
                    List<DepositOrder> recodeList = new ArrayList<DepositOrder>();
                    //获取的list
                    recodeList = recodeResData.getDepositOrder();
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
                            handler.sendEmptyMessageDelayed(LOADMORE, 0);
                        }else{
                            Message msg1 = new Message();
                            msg1.what = NOLOADMORE;
                            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                        }
                    }else if("1".equals(loadMore)){
                        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
                            Message msg1 = new Message();
                            msg1.what = LOADMORE;
                            handler.sendEmptyMessageDelayed(LOADMORE, 0);
                        }else{
                            Message msg1 = new Message();
                            msg1.what = NOLOADMORE;
                            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                        }

                    }else if("2".equals(loadMore)){
                        Message msg1 = new Message();
                        msg1.what = LOADMORE;
                        handler.sendEmptyMessageDelayed(LOADMORE, 0);
                    }
                }else{
                    if(Utils.isNotEmpty(subMsg)){
                        Toast.makeText(activity, subMsg, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                    }
                    Message msg1 = new Message();
                    msg1.what = NOLOADMORE;
                    handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                }

            }else{
                if(Utils.isNotEmpty(msg)){
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
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
                getRecodeList(pageNum,pageNumCount);

                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(Utils.isFastClick()){
            return;
        }
        DepositOrder order = list.get(position - 1);
        Intent intent = new Intent();
        intent.setClass(activity,DepositRecodeDetailActivity.class);
        intent.putExtra("userLoginData",loginInitData);
        intent.putExtra("order",order);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getRecodeList(pageNum,pageNumCount);

    }

    @Override
    public void onLoadMore() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "2";
        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getRecodeList(pageNum,pageNumCount);
        }else{
            //没有数据执行两秒关闭view
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    handler.sendMessage(msg);
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
