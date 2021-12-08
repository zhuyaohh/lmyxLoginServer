package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class MW_KJIOSPayController {
	private static final Logger logger = LoggerFactory.getLogger(MW_KJIOSPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	private static Map<String, Float> goodsAmount = new HashMap<String, Float>();
	static {
		goodsAmount.put("com.xzqy.gemstone30", 30F);
		goodsAmount.put("com.xzqy.gemstone98", 98F);
		goodsAmount.put("com.xzqy.gemstone648", 648F);
		goodsAmount.put("com.xzqy.gemstone328", 328F);
		goodsAmount.put("com.xzqy.gemstone128", 128F);
		goodsAmount.put("com.xzqy.gemstone68", 68F);
		goodsAmount.put("com.xzqy.zuanshi30", 30F);
		goodsAmount.put("com.xzqy.gemstone6", 6F);
	}

	@RequestMapping(value = "kjIOSPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getKJIOSPay(String amount, int appid, String charid, String cporderid, String extinfo, int gold, String orderid, String serverid, int time, int uid, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.info(String.format("KJIOSPayController:amount:{%s},appid:{%s},charid:{%s},cporderid:{%s},extinfo:{%s},gold:{%s},orderid:{%s},serverid:{%s},time:{%s},uid:{%s},sign:{%s},", amount, appid, charid, cporderid, extinfo, gold, orderid, serverid, time, uid, sign));
		String result = "-1";

		float amountFloat = Float.parseFloat(amount);
		if (goodsAmount.containsKey(extinfo) && goodsAmount.get(extinfo) != amountFloat) {
			result = "商品信息验证失败";
			response.getWriter().write(result);
			return null;
		}

		StringBuffer sb = new StringBuffer("");
		sb.append("amount=" + URLDecoder.decode("" + amount, "utf-8") + "&");
		sb.append("appid=" + URLDecoder.decode("" + appid, "utf-8") + "&");
		sb.append("charid=" + URLDecoder.decode("" + charid, "utf-8") + "&");
		sb.append("cporderid=" + URLDecoder.decode("" + cporderid, "utf-8") + "&");
		sb.append("extinfo=" + URLDecoder.decode("" + extinfo, "utf-8") + "&");
		sb.append("gold=" + URLDecoder.decode("" + gold, "utf-8") + "&");
		sb.append("orderid=" + URLDecoder.decode("" + orderid, "utf-8") + "&");
		sb.append("serverid=" + URLDecoder.decode("" + serverid, "utf-8") + "&");
		sb.append("time=" + URLDecoder.decode("" + time, "utf-8") + "&");
		sb.append("uid=" + URLDecoder.decode("" + uid, "utf-8"));
		sb.append("448f135ea7d1cf85243df9ad8c76e32c");
		String signZ = sb.toString().toLowerCase();
		logger.info("==============signZ==================" + signZ);
		String newSign = MD5.GetMD5Code(signZ);
		logger.info("==============newSign==================" + newSign);
		if (newSign.toString().equals(sign)) {
			OrderInfo orderInfo = orderServcie.getOrderByOrderId(orderid);
			if (orderInfo == null) {
				orderInfo = new OrderInfo();
				orderInfo.setUserId("" + uid);
				orderInfo.setActuralFee(amount);
				orderInfo.setOrderId(orderid);
				orderInfo.setCpOrderId(cporderid);
				orderInfo.setDnyOrderId(orderid);
				orderInfo.setType("KJ_IOS");
				orderInfo.setSubjectId(extinfo);
				orderInfo.setServerId((Integer.parseInt(serverid)));
				orderInfo.setAccountId(0);
				orderInfo.setTotalFee(amount);
				orderInfo.setRoleId(charid);
				Timestamp ts = new Timestamp(time * 1000L);
				orderInfo.setDate(ts);
				orderServcie.create(orderInfo);
			}
			if (doPostOrderRecharge(orderInfo)) {
				result = "SUCCESS";
			} else {
				result = "游戏服务器发钻失败";
			}
			response.getWriter().write(result);
		} else {
			response.getWriter().write("签名验证失败");
		}
		return null;
	}

	public boolean doPostOrderRecharge(OrderInfo orderInfo) {
		logger.info("======doPostIOSRecharge================");
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
			args.append("&priceUnit=US");
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
						orderInfo.setStatus(3);
						orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
						orderServcie.update(orderInfo);
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getUserId(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CN", Double.parseDouble(orderInfo.getTotalFee()) * 10, "" + orderInfo.getServerId(), "KJ", orderInfo.getSubjectId(), "FE114607A47549658B12E77C01D264C8");
						return true;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
