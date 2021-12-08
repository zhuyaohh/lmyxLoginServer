package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
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
public class ChuangKuPayColler {
	private static final Logger logger = LoggerFactory.getLogger(ChuangKuPayColler.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	String privateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQCFfHqidxuD2WQ5yDwjYWuzjIip9lgi90XaQdlplKTBy/LAcRk/PLU0oLoWR2SwpGZ8OttdrzSoX9n8qJZDaZ2nkZmRqNlsp75Sv16fITpbgpD21qg1FHrMCC7E2peGgFGHFHJ4Xi6MZkgvaL5tolb+lgPMum8jxeN9pG5sJwiYZ7O3GrxnZYp2IdCbYRO8bdzxTifPz+1C/asraiZELB0fz60HTnFkplCsCLsgPtaZfvrJPyLKVMd9Wwb9SZVykKiC1laH0EhqTywsnfoVWhmoRVcb97qgxZtq1uhw7Ob/rP1dT8qK/ui8MNS5nQ2e/CzDyf0yA6B8SYoW3hkSHraPAgMBAAECggEBAINuXRE6VU0s7xI1pYDwmyreZHF0rLNA5PZRf9AFG3P1a78vnLw2+fQnjRICmZGTTZVkHjnQGPjv56JuHAWkKCalpZ9VsT7aqvqukmv8O4l+tV1VXall0MK0C9inoio8jgmCiHMbyJlU3B5R863e+ekRrAwYRPXkEmnTTSYQCNaFRanIx4YhhpAGaX8WZfq6VPL9zs4qiCJqzy+fSh/zKJst/br+4pTDwXrCJhPpJz4xmlmBOFFDhjcgTo0bKEzey2wc9o3WYsgtc0W/XxsKUG+VQf+pQC6TTZxe8GHUuIeB3P98aHD1mAY9d1nVyFSUXfcVCtY0c3MMI+gjm2Dp6QECgYEA/2GROccUR9HhqJgm0E1GbdM+aUx6im/aWK0D0ZqTMq4KgCu1DR5ZWGLTo2idug4bbAWCHDAstqJGJ5YUfP4VQ24BunXv3LUBPjabHLKsdikwinLOzVE8tTVZK9vpwhMS2EOPDbpGvzb88dgFfdqwGYrXJnC+inIM3SspnbpJImcCgYEAhc9KeRvu8tiA7Ydy/buUCxbgs3vu6QabCD+gsLTHqfWFWJ7t42c4QHblXPIoVdWfAEeYpYl6ji3kau8y2jdjv66p8WcS8geh3QAHq3OnDhjs3dfltMkkPa48n3GCPwcoUGLXHP4MnRBSLIxazmB2RtjFINnGSRvSUzYyRyXLQZkCgYEA29PDa6WnGm9avIJEHtf1wQGuXnubm+DOZbiAagORo3VWyp9U2LcAF2nyXV3nL0mHZ0OSjOsXAhrLIrY4aqNX8FQYHsX4M9aG/GOKv3g5pfistPBAfVHpXN5QJMXHAOzkMs5srFdlIKGXuCHIEF6XaVUvAALasVedJSBLUV8SJw8CgYEAgT+cGOVxZiLYThdCbHHuatO7UcMJHw7hWxNSnLzSC4n7NoG1GK2QOTxfCetiKJ7CCZwehu8HcSRE+UF5JizQbctBncrJEv1qTLUFs4dvun869IHhAEI1flq5de1HOKgqOZNvYzLFbkTgiuU7pRKpzH7/0l5oWB/GhdfYsr5/2SkCgYEA+OHFa27+VzYSTdfbiXBXYowuYmYDhuMGpToyW6qmsfz3W+Uqe3/0hgGbw39SejTGaBvHTY0Cn2Ekqr90xOkEF7oWIox7D8hVR7Y2vc08ZJEU3B4xzCrSOKzbmjOuCV9WMQnip59/xTI1Gx5ALL5xC8jd83hP8JnXhAdKjopyNL8=";

	@RequestMapping(value = "chuangKuPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getChuangkuPay(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String secretKey = "c35adc61e7164bad96c612f33cacdf7e";

		String resultReturn = "{\"resultCode\": -1}";
		BufferedReader br = request.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		logger.info("welcome to chuangKuPay=========================" + body);
		try {
			// {"gameId":"1501","uid":"go#106512452861510577629","productId":"4","create_time":1537856114000,"orderId":"kf18092500003367","sign":"1f4d5289d3935239ae647cbde04c5b68","attach":"
			// 1:100107","prices":"19800","productName":"2040鉆石","channelId":"151","subAppID":"1"}
			JSONObject jsonObject = new JSONObject(body);
			String orderId = "";
			if (jsonObject.has("orderId")) {
				orderId = jsonObject.get("orderId").toString();
			}
			String uid = "";

			if (jsonObject.has("uid")) {
				uid = jsonObject.get("uid").toString();
			}
			String prices = "";
			if (jsonObject.has("prices")) {
				prices = jsonObject.get("prices").toString();
			}
			String productId = "";
			if (jsonObject.has("productId")) {
				productId = jsonObject.get("productId").toString();
			}

			String productName = "";
			if (jsonObject.has("productName")) {
				productName = jsonObject.get("productName").toString();
			}
			String channelId = "";
			if (jsonObject.has("channelId")) {
				channelId = jsonObject.get("channelId").toString();
			}
			String gameId = "";
			if (jsonObject.has("gameId")) {
				gameId = jsonObject.get("gameId").toString();
			}

			String errMsg = "";
			if (jsonObject.has("errMsg")) {
				errMsg = jsonObject.get("errMsg").toString();
			}

			String create_time = "";
			if (jsonObject.has("create_time")) {
				create_time = jsonObject.get("create_time").toString();
			}

			String attach = "";
			if (jsonObject.has("attach")) {
				attach = jsonObject.get("attach").toString();
			}

			String sign = "";
			if (jsonObject.has("sign")) {
				sign = jsonObject.get("sign").toString();
			}
			logger.info("========" + orderId + "=======");
			logger.info("========" + uid + "=======");
			logger.info("========" + prices + "=======");
			logger.info("========" + productId + "=======");
			logger.info("========" + channelId + "=======");
			logger.info("========" + gameId + "=======");
			logger.info("========" + errMsg + "=======");
			logger.info("========" + create_time + "=======");
			logger.info("========" + attach + "=======");
			logger.info("========" + sign + "=======");
			StringBuffer sb = new StringBuffer("");
			if (uid != null && !uid.equals("")) {
				sb.append(uid);
			}
			if (channelId != null && !channelId.equals("")) {
				sb.append(channelId);
			}
			if (gameId != null && !gameId.equals("")) {
				sb.append(gameId);
			}
			if (orderId != null && !orderId.equals("")) {
				sb.append(orderId);
			}
			if (productId != null && !productId.equals("")) {
				sb.append(productId);
			}
			if (secretKey != null && !secretKey.equals("")) {
				sb.append(secretKey);
			}
			String sign2 = MD5.GetMD5Code(sb.toString());
			String[] str1 = attach.split("\\:");
			logger.info("========" + sign2 + "=======");
			if (sign.equals(sign2)) {

				OrderInfo orderInfo = new OrderInfo();
				orderInfo.setRemak(productName);
				orderInfo.setType(channelId);
				orderInfo.setRoleId(str1[1]);
				orderInfo.setOrderId(orderId);
				orderInfo.setServerId(Integer.parseInt(str1[0]));
				orderInfo.setSubjectId(productId);
				orderInfo.setActuralFee(prices);
				orderInfo.setDate(new Timestamp(Long.parseLong(create_time)));
				orderInfo.setTotalFee(prices);
				orderInfo.setStatus(1);
				if (orderServcie.createStatus(orderInfo)) {
					if (doPostOrderRecharge(orderInfo)) {
						resultReturn = "{\"resultCode\": 1}";
					}
				}
				PrintWriter writer = response.getWriter();
				response.getWriter().write(resultReturn);
				writer.close();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			response.getWriter().write("JSON 解析失败");
		}

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
			args.append("&priceUnit=TW");
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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRemak(),
								orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "US",
								Double.parseDouble(orderInfo.getTotalFee()) * 68, "CK", "JCY", "GAME101",
								"D7A03786003A4AB2AC9571B69EEE0B5D");
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
