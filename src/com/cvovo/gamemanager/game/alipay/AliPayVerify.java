package com.cvovo.gamemanager.game.alipay;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nsm on 2016/6/20.
 */
@WebServlet("/AliPayVerify")
public class AliPayVerify extends HttpServlet {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("===========AliPayVerify============");
		String ip = getIpAddress(req);
		Map<String, String> params = new HashMap<>();
		Map requestParams = req.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		for (String key : params.keySet()) {
			System.out.print(key + " : " + params.get(key));
		}
		String status = "";
		String resultMsg = "SUCCESS";
		String trade_status = new String(req.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
		if (!trade_status.equals("TRADE_FINISHED") && !trade_status.equals("TRADE_SUCCESS")) {
			sendToClient(resp, "success");
			return;
		}
		String orderId = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String payTime = new String(req.getParameter("gmt_payment").getBytes("ISO-8859-1"), "UTF-8");
		String buyerLoginId = new String(req.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");
		if (payTime == null) {
			payTime = sdf.format(new Date());
		}
		String userId = "";
		String productId = "";
		String channelNumber = "AliPay";
		String responsString = "success";
		String privateData = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String price = new String(req.getParameter("price").getBytes("ISO-8859-1"), "UTF-8");
		System.out.println("交易流水号trade_no: " + orderId);
		System.out.println("交易状态：" + trade_status);
		System.out.println("交易金额：" + price);
		String returnOrder = "AliPay" + orderId;
		System.out.println("alipay ip: " + ip);
		if (!ip.startsWith("110.75") || buyerLoginId.equals("13872009326")) {
			System.out.println("alipay buyerLoginId: " + buyerLoginId);
			resultMsg = "ip is error";
		} else {
			// ReturnOrder ro = DataHandler.getReturnOrderDao().findByOrder(returnOrder);
			// if (ro != null) {
			// resultMsg = "The order has benn done";
			// } else {
			// ShopOrder so = DataHandler.getShopOrderDao().findByOrder(privateData);
			// if (so == null) {
			// resultMsg = "the order not exist";
			// } else {
			// if (!AliPayNotify.verify(params)) {
			// resultMsg = "alipay check is error!";
			// responsString = "fail";
			// } else {
			// ShopGold sg = DataHandler.getShopGoldDao().findByProductId(so.getShopGold(), so.getChannel_unique());
			// if (sg == null) {
			// // 找该渠道通用计费点
			// sg = DataHandler.getShopGoldDao().findByCommom(so.getShopGold(), so.getChannelId());
			// }
			// if (!price.equals(String.valueOf(sg.getRmb()) + ".00")) {
			// resultMsg = "the shopGold price is error!";
			// } else {
			// so.setStatus(1);
			// DataHandler.getShopOrderDao().saveOrUpdate(so);
			// ReturnOrder rr = new ReturnOrder();
			// rr.setOrderId(returnOrder);
			// rr.setChannelId(channelNumber);
			// rr.setSystime(new Date());
			// DataHandler.getReturnOrderDao().saveOrUpdate(rr);
			// userId = String.valueOf(so.getUserId());
			// productId = so.getShopGold();
			// }
			// // }
			// }
			// }
			// }
			// }
			// System.out.println("resultMsg: " + resultMsg + " response: " + responsString);
			// sendToClient(resp, responsString);
			// if (resultMsg.equals("SUCCESS")) {
			// status = "0";
			// JSONObject jo = new JSONObject();
			// Map<String, String> map = new HashMap<String, String>();
			// map.put("order_id", orderId);
			// map.put("pay_time", payTime);
			// map.put("game_user_id", userId);
			// map.put("product_id", productId);
			// map.put("channel_number", channelNumber);
			// map.put("private_data", privateData);
			// map.put("status", status);
			// map.put("payWay", "支付宝");
			// jo.putAll(map);
			// System.out.println("alipay send to gameServer : " + jo.toString());
			// ProducerClient.send(jo.toString(), "anySDK_sx", "anySDK_sx");
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private void sendToClient(HttpServletResponse response, String content) {
		response.setContentType("text/plain;charset=utf-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.write(content);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;

	}
}
