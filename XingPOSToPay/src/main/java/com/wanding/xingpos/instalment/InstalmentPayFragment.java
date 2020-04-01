package com.wanding.xingpos.instalment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.R;
import com.wanding.xingpos.activity.ByStagesActivity;
import com.wanding.xingpos.base.BaseFragment;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.EditTextUtils;
import com.wanding.xingpos.util.NitConfig;

import java.util.Arrays;

@SuppressLint("ValidFragment")
public class InstalmentPayFragment extends BaseFragment implements View.OnClickListener{

    private int mCurIndex = -1;
    private static final String FRAGMENT_INDEX = "1";
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;
    private boolean onResume=true;//onResume()方法初始化不执行

    private EditText etSumMoney;
    private ImageButton imagEliminate;
    private TextView tvOne,tvTwo,tvThree,tvFour,tvFive,tvSix,tvSeven,tvEight,tvNine,tvZero,tvSpot;
    private TextView tvOk;
    private StringBuilder pending = new StringBuilder();

    private UserLoginResData posPublicData;

    @SuppressLint("ValidFragment")
    public InstalmentPayFragment(UserLoginResData posPublicData) {
        super();
        this.posPublicData = posPublicData;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.instalment_pay_fragment, null, false);

        initView(view);
        initListener();

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
        //请求数据
    }

    private void initView(View view){
        etSumMoney = (EditText) view.findViewById(R.id.content_layout_etSumMoney);
        //强制隐藏Android输入法窗体
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //EditText始终不弹出软件键盘
        etSumMoney.setInputType(InputType.TYPE_NULL);
        EditTextUtils.setPricePoint(etSumMoney);
        imm.hideSoftInputFromWindow(etSumMoney.getWindowToken(),0);
        tvOne = (TextView) view.findViewById(R.id.content_layout_tvOne);
        tvTwo = (TextView) view.findViewById(R.id.content_layout_tvTwo);
        tvThree = (TextView) view.findViewById(R.id.content_layout_tvThree);
        tvFour = (TextView) view.findViewById(R.id.content_layout_tvFour);
        tvFive = (TextView) view.findViewById(R.id.content_layout_tvFive);
        tvSix = (TextView) view.findViewById(R.id.content_layout_tvSix);
        tvSeven = (TextView) view.findViewById(R.id.content_layout_tvSeven);
        tvEight = (TextView) view.findViewById(R.id.content_layout_tvEight);
        tvNine = (TextView) view.findViewById(R.id.content_layout_tvNine);
        tvZero = (TextView) view.findViewById(R.id.content_layout_tvZero);
        tvSpot = (TextView) view.findViewById(R.id.content_layout_tvSpot);
        imagEliminate = (ImageButton) view.findViewById(R.id.content_layout_imagEliminate);
        tvOk = (TextView) view.findViewById(R.id.card_pay_tvOk);
    }

    private void initListener(){
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvFive.setOnClickListener(this);
        tvSix.setOnClickListener(this);
        tvSeven.setOnClickListener(this);
        tvEight.setOnClickListener(this);
        tvNine.setOnClickListener(this);
        tvZero.setOnClickListener(this);
        tvSpot.setOnClickListener(this);
        imagEliminate.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }

    private boolean judje1() {
        String a = "+-*/.";
        int[] b = new int[a.length()];
        int max;
        for (int i = 0; i < a.length(); i++) {
            String c = "" + a.charAt(i);
            b[i] = pending.lastIndexOf(c);
        }
        Arrays.sort(b);
        if (b[a.length() - 1] == -1) {
            max = 0;
        } else {
            max = b[a.length() - 1];
        }
        if (pending.indexOf(".", max) == -1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        Intent in = null;
        String etTextStr,total_feeStr;
        int last = 0;
        if(pending.length()!=0)
        {
            last = pending.codePointAt(pending.length()-1);

        }

        switch (v.getId()) {
            case R.id.content_layout_tvOne:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("1");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvTwo:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("2");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvThree:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("3");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvFour:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("4");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvFive:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("5");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSix:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("6");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSeven:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("7");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvEight:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("8");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvNine:
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("9");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvZero:
                pending = pending.append("0");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                //输入内容头为0的情况下，只能输入小数点
                if (pending.toString().startsWith("0") && pending.toString().trim().length() > 1) {
                    if (!".".equals(pending.toString().substring(1, 2))) {
                        pending = pending.deleteCharAt(pending.length()-1);
                        return;
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSpot:
                if(pending.length()>0){
                    if (judje1()) {
                        pending = pending.append(".");
                        if (pending.toString().contains(".")) {
                            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                                //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                                pending = pending.deleteCharAt(pending.length()-1);
                            }
                        }
                        etSumMoney.setText("￥"+pending);
                    }
                }

                break;
            case R.id.content_layout_imagEliminate:
                //删除
                if (pending.length() != 0) {
                    pending = pending.delete(pending.length() - 1, pending.length());
                    etSumMoney.setText("￥"+pending);
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                    if(pending.length()<=0){
                        etSumMoney.setText("￥0.00");
                    }
                }
                //清空
//			pending = pending.delete(0, pending.length());
//			tvSumMoney.setText(pending);
                break;
            case R.id.card_pay_tvOk://确认
                try {
                    etTextStr = pending.toString();
                    if(!".".equals(etTextStr)){
                        Log.e("输入框文本text值：", etTextStr);
                        total_feeStr = DecimalUtil.scaleNumber(etTextStr);
                        double dou_total_feeStr = Double.valueOf(total_feeStr);
                        if(DecimalUtil.isRange(dou_total_feeStr,600,50000)){
                            String total_moneyStr = DecimalUtil.elementToBranch(total_feeStr);
                            Log.e("转换分字符串：",total_moneyStr);
                            int int_total_moneyStr = Integer.valueOf(total_moneyStr);
                            Log.e("最终提交金额：",int_total_moneyStr+"");
                            in = new Intent();
                            in.putExtra("txnAmt", int_total_moneyStr);
                            if("true".equals(NitConfig.isTest)){
                                in.putExtra("merchantId",posPublicData.getMercId_pos());
                            }else{
                                in.putExtra("merchantId","85731017011L660");
                            }
                            in.setClassName(getContext(), "com.newland.starpos.installmentsdk.MainActivity");
                            startActivity(in);
                        }else{
                            Toast.makeText(getContext(), "分期金额最低600，最高50000", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Log.e("输入金额有误！", "false");
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;

        }
    }
}
