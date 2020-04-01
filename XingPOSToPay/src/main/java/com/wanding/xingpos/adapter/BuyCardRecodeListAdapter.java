package com.wanding.xingpos.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.BuyCardRecodeDetail;
import com.wanding.xingpos.bean.PosMemConsumeRecodeDetail;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.TextStyleUtil;
import com.wanding.xingpos.util.Utils;

import java.util.List;

public class BuyCardRecodeListAdapter extends BaseAdapter {

    private Context context;
    private List<BuyCardRecodeDetail> list;
    private LayoutInflater inflater;

    public BuyCardRecodeListAdapter(Context context, List<BuyCardRecodeDetail> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        RelativeLayout layout;
        TextView tvOrderId;//订单号
        TextView tvCreateTime;//订单创建时间
        TextView tvTotal;//交易金额
        TextView tvState;//交易状态
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.buy_card_recode_list_item, null);
            holder.layout=(RelativeLayout) convertView.findViewById(R.id.buy_card_recode_list_item_cententLayout);
            holder.tvOrderId=(TextView) convertView.findViewById(R.id.buy_card_recode_list_item_tvOrderId);
            holder.tvCreateTime=(TextView) convertView.findViewById(R.id.buy_card_recode_list_item_tvCreateTime);
            holder.tvTotal=(TextView) convertView.findViewById(R.id.buy_card_recode_list_item_tvTotal);
            holder.tvState=(TextView) convertView.findViewById(R.id.buy_card_recode_list_item_tvState);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        if(position%2!=0){
            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_F8F8F8));
        }else{
            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_ffffff));
        }
        holder.tvOrderId.setText(list.get(position).getOrderId());
        String orderIdText = holder.tvOrderId.getText().toString();
        if(Utils.isNotEmpty(orderIdText)&&orderIdText.length()>=32){
            SpannableStringBuilder style1 = TextStyleUtil.changeStyle(orderIdText, 24, orderIdText.length());
            holder.tvOrderId.setText(style1);
        }
        holder.tvCreateTime.setText(list.get(position).getCreateTime());
        //支付金额
        holder.tvTotal.setText("￥"+DecimalUtil.StringToPrice(list.get(position).getTotalFee()));
        //支付状态
        String displayStateStr = list.get(position).getDisplayStatus();
        String displayState = "未知";
        int color = context.getResources().getColor(R.color.gray_999);
        if(Utils.isNotEmpty(displayStateStr)) {
            if (displayStateStr.equals("0")) {

                displayState = "支付成功";
                color = context.getResources().getColor(R.color.green_006400);

            } else if (displayStateStr.equals("1")) {

                displayState = "支付失败";
                color = context.getResources().getColor(R.color.red_d05450);

            }
        }
        holder.tvState.setText(displayState);
        holder.tvState.setTextColor(color);
        return convertView;
    }
}
