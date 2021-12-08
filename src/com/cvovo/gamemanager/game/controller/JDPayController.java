package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.alipay.RSA;
import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class JDPayController {
	private static final Logger logger = LoggerFactory.getLogger(JDPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private AccountService accountService;

	String privateKey = "MIIEwAIBADANBgkqhkiG9w0BAQEFAASCBKowggSmAgEAAoIBAQCFfHqidxuD2WQ5yDwjYWuzjIip9lgi90XaQdlplKTBy/LAcRk/PLU0oLoWR2SwpGZ8OttdrzSoX9n8qJZDaZ2nkZmRqNlsp75Sv16fITpbgpD21qg1FHrMCC7E2peGgFGHFHJ4Xi6MZkgvaL5tolb+lgPMum8jxeN9pG5sJwiYZ7O3GrxnZYp2IdCbYRO8bdzxTifPz+1C/asraiZELB0fz60HTnFkplCsCLsgPtaZfvrJPyLKVMd9Wwb9SZVykKiC1laH0EhqTywsnfoVWhmoRVcb97qgxZtq1uhw7Ob/rP1dT8qK/ui8MNS5nQ2e/CzDyf0yA6B8SYoW3hkSHraPAgMBAAECggEBAINuXRE6VU0s7xI1pYDwmyreZHF0rLNA5PZRf9AFG3P1a78vnLw2+fQnjRICmZGTTZVkHjnQGPjv56JuHAWkKCalpZ9VsT7aqvqukmv8O4l+tV1VXall0MK0C9inoio8jgmCiHMbyJlU3B5R863e+ekRrAwYRPXkEmnTTSYQCNaFRanIx4YhhpAGaX8WZfq6VPL9zs4qiCJqzy+fSh/zKJst/br+4pTDwXrCJhPpJz4xmlmBOFFDhjcgTo0bKEzey2wc9o3WYsgtc0W/XxsKUG+VQf+pQC6TTZxe8GHUuIeB3P98aHD1mAY9d1nVyFSUXfcVCtY0c3MMI+gjm2Dp6QECgYEA/2GROccUR9HhqJgm0E1GbdM+aUx6im/aWK0D0ZqTMq4KgCu1DR5ZWGLTo2idug4bbAWCHDAstqJGJ5YUfP4VQ24BunXv3LUBPjabHLKsdikwinLOzVE8tTVZK9vpwhMS2EOPDbpGvzb88dgFfdqwGYrXJnC+inIM3SspnbpJImcCgYEAhc9KeRvu8tiA7Ydy/buUCxbgs3vu6QabCD+gsLTHqfWFWJ7t42c4QHblXPIoVdWfAEeYpYl6ji3kau8y2jdjv66p8WcS8geh3QAHq3OnDhjs3dfltMkkPa48n3GCPwcoUGLXHP4MnRBSLIxazmB2RtjFINnGSRvSUzYyRyXLQZkCgYEA29PDa6WnGm9avIJEHtf1wQGuXnubm+DOZbiAagORo3VWyp9U2LcAF2nyXV3nL0mHZ0OSjOsXAhrLIrY4aqNX8FQYHsX4M9aG/GOKv3g5pfistPBAfVHpXN5QJMXHAOzkMs5srFdlIKGXuCHIEF6XaVUvAALasVedJSBLUV8SJw8CgYEAgT+cGOVxZiLYThdCbHHuatO7UcMJHw7hWxNSnLzSC4n7NoG1GK2QOTxfCetiKJ7CCZwehu8HcSRE+UF5JizQbctBncrJEv1qTLUFs4dvun869IHhAEI1flq5de1HOKgqOZNvYzLFbkTgiuU7pRKpzH7/0l5oWB/GhdfYsr5/2SkCgYEA+OHFa27+VzYSTdfbiXBXYowuYmYDhuMGpToyW6qmsfz3W+Uqe3/0hgGbw39SejTGaBvHTY0Cn2Ekqr90xOkEF7oWIox7D8hVR7Y2vc08ZJEU3B4xzCrSOKzbmjOuCV9WMQnip59/xTI1Gx5ALL5xC8jd83hP8JnXhAdKjopyNL8=";

	@RequestMapping(value = "jdbill", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult billByJD(String token) {
		logger.info("billByJD=========================" + token);
		String sign = "";
		String[] str = token.split("&");
		Hashtable<String, String> table = new Hashtable<String, String>();
		for (int i = 0; i < str.length; i++) {
			String tempStr = str[i];
			int d = tempStr.indexOf("=");
			String key = tempStr.substring(0, d);
			String value = tempStr.substring(d + 1, tempStr.length());
			table.put(key, value);
		}
		String type = table.get("type");
		String serverid = table.get("serverid");
		String subjectId = table.get("subjectId");
		String acturalFee = table.get("acturalFee");
		String totalFee = table.get("acturalFee");
		String accountId = table.get("accountId");
		String billNo = PublicMethod.getUniqueId();
		switch (type) {
		case "JD_HW": {
			logger.info("===================进入======");
			TreeMap<String, String> map = new TreeMap<>();
			map.put("productName", table.get("productName"));
			map.put("productDesc", table.get("productDesc"));
			map.put("requestId", billNo);
			DecimalFormat decimalFormat = new DecimalFormat(".00");
			map.put("amount", "" + decimalFormat.format(Double.parseDouble(acturalFee) * 0.01D));
			map.put("applicationID", "100170611");
			map.put("merchantId", "890086000102039852");
			map.put("sdkChannel", "3");
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
			String noRSASign = sb.toString().substring(0, sb.toString().length() - 1);
			sign = RSA.sign(noRSASign, privateKey, "SHA256WithRSA", "UTF-8");
		}
			break;
		default:
			break;
		}
		OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, (int) Float.parseFloat(serverid), subjectId, type, accountId, billNo, String.valueOf(billNo));
		logger.info("=========对象存储=========");
		if (orderServcie.create(orderInfo)) {
			return RestResult.getBillWXSuc("1", String.valueOf(billNo), sign, sign, "");
		} else {
			return RestResult.getBillSuc("0", String.valueOf(billNo), "");
		}
	}

	@RequestMapping(value = "jdhuaweiBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getOrderNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedReader br = request.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		logger.info("btBillNo:body=========================" + body);
		JSONObject jsonObject = new JSONObject(body);
		int state = jsonObject.getInt("state");
		if (state != 1) {
			response.getWriter().write("FAIL");
			return;
		}
		JSONObject data = jsonObject.getJSONObject("data");
		String channelID = data.getString("channelID");
		String orderID = data.getString("orderID");
		String currency = data.getString("currency");
		String subjectId = data.getString("productID");
		String userID = data.getString("userID");// 用户ID
		String serverid = data.getString("serverID");
		String acturalFee = data.getString("money");
		String playerId = data.getString("extension");
		String gameId = data.getString("gameID");
		String serverID = data.getString("serverID");
		Account tempAccount = accountService.findByUin(userID);
		if (tempAccount == null) {
			response.getWriter().write("FAIL userId is null!!!");
			return;
		}
		String type = "BT";
		String appSecret = "b8fcaa147f0fcfcfdea4f6ec7c649a09";
		StringBuilder sb = new StringBuilder();
		sb.append("channelID=").append(channelID).append("&").append("currency=").append(currency).append("&").append("extension=").append(playerId).append("&");
		sb.append("gameID=").append(gameId).append("&").append("money=").append(acturalFee).append("&").append("orderID=").append(orderID).append("&");
		sb.append("productID=").append(subjectId).append("&").append("serverID=").append(serverID).append("&").append("userID=").append(userID).append("&").append(appSecret);
		String md5Code = MD5.GetMD5Code(sb.toString());

		if (!md5Code.equalsIgnoreCase(data.getString("sign"))) {
			response.getWriter().write("FAIL MD5 is ERROR!!!");
			return;
		}
		OrderInfo orderInfo = new OrderInfo(orderID, acturalFee, acturalFee, (int) Float.parseFloat(serverid), subjectId, type, userID, orderID, orderID);
		orderInfo.setPriceUnit(currency);
		orderInfo.setRoleId(playerId);
		if (!orderServcie.create(orderInfo)) {
			response.getWriter().write("FAIL");
			return;
		}
		ServerInfo serverInfo = serverService.getServerInfo(Integer.parseInt(serverid));
		if (serverInfo != null) {
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=5001");
			args.append("&playerId=" + playerId);
			if (subjectId != null && !subjectId.equals("")) {
				args.append("&shopId=" + subjectId);
			} else {
				args.append("&shopId=" + "0000");
			}
			args.append("&order=" + orderID);
			args.append("&price=" + acturalFee);
			args.append("&source=" + type);
			args.append("&priceUnit=" + currency);
			args.append("&goodsCount=0");
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			logger.info("发送HTTP请求========================" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("拿到HTTP请求结果========================");
			JSONObject resultData;
			try {
				resultData = new JSONObject(result);
				if (resultData.has("errorId")) {
					String errorId = resultData.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						logger.info("游戏服务器返回正确结果========================");
						orderInfo.setStatus(3);
						orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
						orderServcie.update(orderInfo);
						response.getWriter().write("SUCCESS");
						return;
					} else {
						String msg = resultData.get("msg").toString();
						logger.info("msg=======" + msg);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		response.getWriter().write("FAIL");
	}
}
