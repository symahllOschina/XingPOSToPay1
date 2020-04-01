package com.wanding.xingpos.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.DepositOrder;
import com.wanding.xingpos.bean.MemberCardDetail;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.TextStyleUtil;
import com.wanding.xingpos.util.Utils;

import java.util.List;

public class BuyCardTypeAdapter extends BaseAdapter {

    private Context context;
    private List<MemberCardDetail> list;
    private LayoutInflater inflater;

    public BuyCardTypeAdapter(Context context, List<MemberCardDetail> list) {
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
        LinearLayout layout;
        TextView tvName;//名称
        TextView tvContent;//内容
        TextView tvOPrice;//原价
        TextView tvPrice;//金额
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.buy_card_type_item, null);
            holder.layout=(LinearLayout) convertView.findViewById(R.id.buy_card_type_item_cententLayout);
            holder.tvName=(TextView) convertView.findViewById(R.id.buy_card_tvCardTypeName);
            holder.tvContent=(TextView) convertView.findViewById(R.id.buy_card_tvCardTypeContent);
            holder.tvOPrice=(TextView) convertView.findViewById(R.id.buy_card_tvCardTypeOPrice);
            holder.tvPrice=(TextView) convertView.findViewById(R.id.buy_card_tvCardTypePrice);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
//        if(position%2!=0){
//            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_F8F8F8));
//        }else{
//            holder.layout.setBackgroundColor(ContextCompat.getColor(context,R.color.white_ffffff));
//        }
        //会员卡名称
        String name = list.get(position).getProduct_name();
        holder.tvName.setText(name);
        //会员卡简介
        String depict = list.get(position).getDepict();
        holder.tvContent.setText(depict);
        //会员卡原价
        holder.tvOPrice.setText("￥"+DecimalUtil.StringToPrice(list.get(position).getO_price()));
        holder.tvOPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //会员卡现价
        holder.tvPrice.setText("￥"+DecimalUtil.StringToPrice(list.get(position).getN_price()));

        return convertView;
    }
}
