package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class HeiTaoPayController {
	private static final Logger logger = LoggerFactory.getLogger(HeiTaoPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	private static Map<String, Float> goodsAmount = new HashMap<String, Float>();
	static {
		goodsAmount.put("com.sfsy.mwqy.30.y", 30F);
		goodsAmount.put("com.sfsy.mwqy.98.z", 98F);
		goodsAmount.put("com.sfsy.mwqy.5000", 5000F);
		goodsAmount.put("com.sfsy.mwqy.2000", 2000F);
		goodsAmount.put("com.sfsy.mwqy.1000", 1000F);
		goodsAmount.put("com.sfsy.mwqy.500", 500F);
		goodsAmount.put("com.sfsy.mwqy.200", 200F);
		goodsAmount.put("com.sfsy.mwqy.100", 100F);
		goodsAmount.put("com.sfsy.mwqy.50", 50F);
		goodsAmount.put("com.sfsy.mwqy.30", 30F);
		goodsAmount.put("com.sfsy.mwqy.10", 10F);
	}

	@RequestMapping(value = "heiTaobill", method = { RequestMethod.GET, RequestMethod.POST })
	public void heiTaobill(String sid, String uid, String orderno, String amount, String status, String extinfo, String nonce, String token, HttpServletResponse response) throws IOException {
		logger.info("sid=========================" + sid);
		logger.info("uid=========================" + uid);
		logger.info("orderno=========================" + orderno);
		logger.info("amount=========================" + amount);
		logger.info("status=========================" + status);
		logger.info("extinfo=========================" + extinfo);
		logger.info("nonce=========================" + nonce);
		logger.info("token=========================" + token);

		float amountFloat = Float.parseFloat(amount);
		if (goodsAmount.containsKey(extinfo) && goodsAmount.get(extinfo) != amountFloat) {
			response.getWriter().write("{\"errno\":\"1\",\"errmsg\":\"商品金额不符\"}");
			return;
		}
		String result = "{\"errno\":\"0\",\"errmsg\":\"success\"}";
		String secret = "6fdce9b2f2d3a4087ba24b0e0bdcd7f8";
		StringBuffer sb = new StringBuffer(sid);
		sb.append(uid);
		sb.append(orderno);
		sb.append(amount);
		sb.append(extinfo);
		sb.append(nonce);
		sb.append(secret);
		logger.info("=sb==" + sb.toString());
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("=sign1==" + sign1);
		if (sign1.equals(token)) {
			String[] args = extinfo.split(",");
			String cpOrderId = args[3];
			if (cpOrderId == null || cpOrderId.isEmpty()) {
				result = "{\"errno\":\"1\",\"errmsg\":\"CPOrderId is null\"}";
			} else {
				OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
				if (orderInfo == null) {
					result = "{\"errno\":\"1\",\"errmsg\":\"DB CpOrderId is null\"}";
				} else {
					switch (orderInfo.getStatus()) {
					case 3:
						break;
					case 2:
					case 1:
						orderInfo.setStatus(2);
						orderInfo.setDnyOrderId(orderno);
						orderInfo.setPriceUnit("CNY");
						orderInfo.setType(args.length > 4 ? args[4] : "");
						orderInfo.setRoleId(args[1]);
						orderServcie.update(orderInfo);
						orderInfo.setDate(new Timestamp(Long.parseLong(nonce)));
						orderInfo.setTotalFee(amount);
						if (doPostOrderRecharge(orderInfo)) {
							result = "{\"errno\":\"0\",\"errmsg\":\"success\"}";
						} else {
							result = "{\"errno\":\"1\",\"errmsg\":\"发钻失败\"}";
						}
						break;
					default:
						break;
					}
				}
			}
		} else {
			result = "{\"errno\":\"1\",\"errmsg\":\"验证失败\"}";
		}
		PrintWriter writer = response.getWriter();
		response.getWriter().write(result);
		writer.close();
	}

	public boolean doPostOrderRecharge(OrderInfo orderInfo) {
		logger.info("======doPostOrderRecharge================");
		int id = orderInfo.getServerId();
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=1004");
			args.append("&playerId=" + orderInfo.getRoleId());
			args.append("&shopId=" + orderInfo.getSubjectId());
			args.append("&order=" + orderInfo.getOrderId());
			args.append("&price=" + orderInfo.getTotalFee());
			args.append("&priceUnit=CN");
			args.append("&source=" + orderInfo.getType());
			args.append("&goodsCount=1");
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			logger.info("发送HTTP请求=========" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("拿到HTTP请求结果============" + result);
			org.json.JSONObject jsonObject;
			try {
				jsonObject = new org.json.JSONObject(result);
				if (jsonObject.has("errorId")) {
					String errorId = jsonObject.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRemak(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CNY", Double.parseDouble(orderInfo.getTotalFee()) * 65, "HT", "HT", "GAME101", "82C950944EA64DC580830543C4DF0B64");
						orderInfo.setStatus(3);
						orderServcie.update(orderInfo);
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

}
