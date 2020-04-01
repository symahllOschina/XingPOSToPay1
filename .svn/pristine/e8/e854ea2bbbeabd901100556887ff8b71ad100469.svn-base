package com.wanding.xingpos.payutil;

import android.util.Log;


import com.wanding.xingpos.util.MD5;
import com.wanding.xingpos.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;


public class FacePayUtils {

	/**
	 * 刷脸参数加签（参数不排序）
	 */
	public static String generateSignature(final Map<String, String> data, String key, String signType)
			throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if ("sign".equals(k)) {
				continue;
			}
			if (data.get(k).trim().length() > 0){
				// 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
			}
		}
		sb.append("key=").append(key);
		Log.e("sign",sb.toString());
		if ("MD5".equals(signType)) {
			return MD5.MD5Encode(sb.toString()).toUpperCase();
		} else {
			throw new Exception(String.format("Invalid sign_type: %s", signType));
		}
	}

	public static boolean isSignatureValid(Map<String, String> data, String key, String signType) throws Exception {
		if (!data.containsKey("sign")) {
			return false;
		}
		String sign = data.get("sign");
		return generateSignature(data, key, signType).equals(sign);
	}


	/**
	 * 签名算法
	 *
	 * @param map
	 * @param key
	 * @return
	 * @author zhang.hui@pufubao.net
	 * @date 2016年11月11日 下午2:53:33
	 */
	public static String getSign(Map<String, Object> map, String key) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null && Utils.isNotEmpty(String.valueOf(entry.getValue()))) {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result = result.substring(0, result.length() - 1);
		result += key;
		result = MD5.MD5Encode(result).toUpperCase();
		return result;
	}
}
