package com.wanding.xingpos.instalment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.InstalmentQueryResData;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.date_util.DateTimeUtil;
import com.wanding.xingpos.util.DecimalUtil;
import com.wanding.xingpos.util.Utils;

import java.util.List;

public class InstalmentListAdapte extends BaseAdapter {

	private Context context;
	private List<InstalmentQueryResData> lsOrder;
	private LayoutInflater inflater;

	public InstalmentListAdapte(Context context, List<InstalmentQueryResData> lsOrder) {
		super();
		this.context = context;
		this.lsOrder = lsOrder;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return lsOrder.size();
	}

	@Override
	public Object getItem(int position) {
		return lsOrder.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class ViewHolder{
		TextView tvTime;
		TextView tvName;
		TextView tvMoney;
		TextView tvOrderId;//
		TextView tvStatus;//交易状态
	}

	@Override
	public View getView(int position, View subView, ViewGroup parent) {
		InstalmentQueryResData order = lsOrder.get(position);
		ViewHolder vh = null;
		if(subView == null){
			subView = inflater.inflate(R.layout.instalment_query_list_item, null);
			vh = new ViewHolder();
			vh.tvTime = (TextView) subView.findViewById(R.id.instalment_query_list_item_tvTime);
			vh.tvName = (TextView) subView.findViewById(R.id.instalment_query_list_item_tvName);
			vh.tvMoney = (TextView) subView.findViewById(R.id.instalment_query_list_item_tvMoney);
			vh.tvOrderId = (TextView) subView.findViewById(R.id.instalment_query_list_item_tvOrderId);
			vh.tvStatus = (TextView) subView.findViewById(R.id.instalment_query_list_item_tvStatus);

			subView.setTag(vh);
		}else{
			vh = (ViewHolder) subView.getTag();
		}
		//订单交易时间
		String orderTimeStr = order.getTxnTime();
		String orderPayTime = "";
		if(Utils.isNotEmpty(orderTimeStr)){
			orderPayTime = DateTimeUtil.timeStrToFormatDateStr(orderTimeStr, "yyyy.MM.dd");
		}
		vh.tvTime.setText(orderPayTime);
		//商户姓名
		String merNameStr = order.getMerName();
		String merName = "";
		if(Utils.isNotEmpty(merNameStr)){
			merName = merNameStr;
		}
		vh.tvName.setText(merName);
		//订单交易金额
		String orderTotalStr = order.getTxnAmt();
		String orderTotal = "";
		if(Utils.isNotEmpty(orderTotalStr)){
			//分转元
			orderTotal = DecimalUtil.branchToElement(orderTotalStr);
		}
		vh.tvMoney.setText(orderTotal);
		//订单号
		String orderIdStr = order.getOrderId();
		String orderId = "";
		if(Utils.isNotEmpty(orderIdStr)){
			orderId = orderIdStr;
		}
		vh.tvOrderId.setText(orderId);
		//交易状态
		String orderStatus = "未知状态";
		//contractsState:0,1未结清 2,3,4已结清 5已结清（退款操作）
		String orderTypeStr = order.getContractsState();
		//退款状态：0退款失败，1退款成功，2人工审核，无值或为null时表示没有退款行为"state": null,
		String stateStr = order.getState();
		if(Utils.isNotEmpty(orderTypeStr)){
			if("0".equals(orderTypeStr) || "1".equals(orderTypeStr)){
				if(Utils.isNotEmpty(stateStr)){
					if("1".equals(stateStr)){
						orderStatus = "退款成功";
					}else if("2".equals(stateStr)){
						orderStatus = "人工审核";
					}else{
						orderStatus = "还款中";
					}
				}else{
					orderStatus = "还款中";
				}
			}else if("2".equals(orderTypeStr) || "3".equals(orderTypeStr) || "4".equals(orderTypeStr)){
				orderStatus = "已结清";
			}else if("5".equals(orderTypeStr)){
				//vocher;//是否上传退款凭证 1已上传，其余未上传"vocher": null,
				String vocherStr = order.getVocher();
				if(Utils.isNotEmpty(vocherStr)){
					if("1".equals(vocherStr)){
						orderStatus = "退款已结清已传凭证";
					}else{
						orderStatus = "退款已结清未传凭证";
					}
				}else{
					orderStatus = "退款已结清未传凭证";
				}

			}
		}
		vh.tvStatus.setText(orderStatus);
		return subView;
	}

}
