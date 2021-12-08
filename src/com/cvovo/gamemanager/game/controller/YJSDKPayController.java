package com.cvovo.gamemanager.game.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Hashtable;

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
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class YJSDKPayController {
	private static final Logger logger = LoggerFactory.getLogger(YJSDKPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "yjsdkbill", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRegister(String token) {
		logger.info("token=========================" + token);
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

		if (type == null || type.equals("")) {
			type = "未知";
		}
		logger.info("===================进入======");
		String billNo = PublicMethod.getUniqueId();
		OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, (int) Float.parseFloat(serverid), subjectId, type, 0, billNo, String.valueOf(billNo));
		logger.info("=========对象存储=========");
		if (orderServcie.create(orderInfo)) {
			return RestResult.getBillSuc("1", String.valueOf(billNo));
		} else {
			return RestResult.getBillSuc("0", String.valueOf(billNo));
		}
	}

	@RequestMapping(value = "yjsdkbillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public String getOrderInfo(String app, String cbi, long ct, int fee, long pt, String sdk, String ssid, int st, String tcd, String uid, String ver, String sign) throws Exception {
		logger.info(String.format("app:{%s},cbi:{%s},ct:{%s},fee:{%s},pt:{%s},sdk:{%s},ssid:{%s},st:{%s},tcd:{%s},uid:{%s},ver:{%s},sign:{%s}", app, cbi, ct, fee, pt, sdk, ssid, st, tcd, uid, ver, sign));
		String passport = "PUY9S8NB0T2UOK75GLJDL2L9CVXOD9WW";
		JSONObject clientArgs = new JSONObject(cbi);
		String cpOrderId = clientArgs.getString("orderId");
		if (cpOrderId == null || cpOrderId.isEmpty() || st != 1) {
			return "getOrderInfo : FAIL cpOrderId is Null";
		}

		OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
		if (orderInfo == null) {
			return "FAIL cpOrderId is Null DB";
		}
		switch (orderInfo.getStatus()) {
		case 3:
			return "SUCCESS";
		case 1:
			orderInfo.setStatus(2);
			orderInfo.setDnyOrderId(tcd);
			orderInfo.setPriceUnit("CNY");
			orderServcie.update(orderInfo);
			break;
		default:
			break;
		}
		logger.info("==获取订单信息====================");
		StringBuffer tempSign = new StringBuffer("");
		tempSign.append("app=" + app + "&");
		tempSign.append("cbi=" + cbi + "&");
		tempSign.append("ct=" + ct + "&");
		tempSign.append("fee=" + fee + "&");
		tempSign.append("pt=" + pt + "&");
		tempSign.append("sdk=" + sdk + "&");
		tempSign.append("ssid=" + ssid + "&");
		tempSign.append("st=" + st + "&");
		tempSign.append("tcd=" + tcd + "&");
		tempSign.append("uid=" + uid + "&");
		tempSign.append("ver=" + ver);
		tempSign.append(passport);
		logger.info("===tempSign==加密前==" + tempSign.toString());
		String key = "";
		try {
			key = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("===tempSign==加密后==" + key);
		logger.info("==启动验证=================key.equals(sign):" + key.equals(sign));
		if (key.equals(sign)) {
			ServerInfo serverInfo = serverService.getServerInfo(Integer.parseInt(clientArgs.getString("serverid")));
			if (serverInfo != null) {
				logger.info("==验证通过=================启动游戏服务器");
				String url = serverInfo.getUrl();
				StringBuffer args = new StringBuffer("c=5001");
				args.append("&playerId=" + clientArgs.getString("characterid"));
				args.append("&shopId=" + clientArgs.getString("subjectId"));
				args.append("&order=" + cpOrderId);
				args.append("&price=" + fee);
				args.append("&source=" + clientArgs.getString("type"));
				args.append("&priceUnit=CNY");
				args.append("&goodsCount=0");
				String[] strList = url.split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
				logger.info("发送HTTP请求========================" + args.toString());
				String result = HttpRequest.sendGet(title.toString(), args.toString());
				logger.info("拿到HTTP请求结果========================");
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject.has("errorId")) {
						String errorId = jsonObject.get("errorId").toString();
						if (errorId != null && errorId.equals("0")) {
							logger.info("游戏服务器返回正确结果========================");
							orderInfo.setStatus(3);
							orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
							orderServcie.update(orderInfo);
							logger.info("保存结果========================");
							return "SUCCESS";
						}
						String msg = jsonObject.get("msg").toString();
						logger.info("msg=======" + msg);
						return "游戏服处理异常" + msg;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return "FAIL";
	}

}
