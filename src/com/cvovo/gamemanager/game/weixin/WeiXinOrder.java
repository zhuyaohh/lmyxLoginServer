package com.cvovo.gamemanager.game.weixin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.cvovo.gamemanager.game.weixin.until.PayCommonUtil;
import com.cvovo.gamemanager.game.weixin.until.XMLUtil;

/**
 * Created by Administrator on 2016-08-01.
 */
public class WeiXinOrder {
	public String resultCode;
	public String outTradeNo;
	public String totalFee;
	public String timeEnd;
	public String sign;
	public String transactionId;
	public Map<String, String> resultMap;

	public WeiXinOrder(HttpServletRequest req) {
		try {
			InputStream in = req.getInputStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.close();
			in.close();
			String msgxml = new String(out.toByteArray(), "utf-8");// xml数据
			System.out.println("xml: " + msgxml);
			resultMap = XMLUtil.doXMLParse(msgxml);
			for (String key : resultMap.keySet()) {
				System.out.println(key + " : " + resultMap.get(key));
			}
			this.resultCode = resultMap.get("result_code");
			this.outTradeNo = resultMap.get("out_trade_no");
			this.totalFee = resultMap.get("total_fee");
			this.transactionId = resultMap.get("transaction_id");
			this.sign = resultMap.get("sign");
			this.timeEnd = resultMap.get("time_end");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static WeiXinOrder getWeiXinOrder(HttpServletRequest req) {
		WeiXinOrder order = new WeiXinOrder(req);
		if (!order.resultCode.equals("SUCCESS")) {
			System.out.println("result_code is not SUCCESS");
			return null;
		}
		String newSign = PayCommonUtil.createSign("UTF-8", order.getSortedMap(order.resultMap));
		if (newSign.equals(order.sign)) {
			return order;
		}
		return null;
	}

	// 水浒传
	public static WeiXinOrder getWeiXinOrderSHZ(HttpServletRequest req) {
		WeiXinOrder order = new WeiXinOrder(req);
		if (!order.resultCode.equals("SUCCESS")) {
			System.out.println("result_code is not SUCCESS");
			return null;
		}
		String newSign = PayCommonUtil.createSign("UTF-8", order.getSortedMap(order.resultMap));
		if (newSign.equals(order.sign)) {
			return order;
		}
		return null;
	}

	public SortedMap<Object, Object> getSortedMap(Map<String, String> map) {
		SortedMap<Object, Object> sortedMap = new TreeMap<>();
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
