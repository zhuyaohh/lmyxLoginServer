package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.io.IOException;
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
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class TGPayController {

	private static final Logger logger = LoggerFactory.getLogger(TGPayController.class);

	@Autowired
	private ServerService serverService;
	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "tgPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getTaiGuoAndroidPay(String appId, String uid, String orderId, String cpOrderId, String amount, String paytype, String ext, String einfo, String cardId, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		return Pay("66aeaa16f6c14cd03d3f0ee7a0c19361", appId, uid, orderId, cpOrderId, amount, paytype, ext, einfo, cardId, sign, response, request);
	}

	@RequestMapping(value = "tgmjPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getTaiGuoMJPay(HttpServletResponse response, HttpServletRequest request) throws IOException {
		BufferedReader br = request.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		logger.info("getTaiGuoMJPay:body=========================" + body);
		try {
			JSONObject jsonObject = new JSONObject(body);
			String appId = jsonObject.getString("appId");
			String uid = jsonObject.getString("uid");
			String orderId = jsonObject.getString("orderId");
			String cpOrderId = jsonObject.getString("cpOrderId");
			String amount = jsonObject.getString("amount");
			String paytype = jsonObject.getString("paytype");
			String ext = !jsonObject.isNull("ext") ? jsonObject.getString("ext") : "";
			String einfo = !jsonObject.isNull("einfo") ? jsonObject.getString("einfo") : "";
			String cardId = !jsonObject.isNull("cardId") ? jsonObject.getString("cardId") : "";
			String sign = jsonObject.getString("sign");
			return Pay("8f2b4e58a13c7e3e34a751", appId, uid, orderId, cpOrderId, amount, paytype, ext, einfo, cardId, sign, response, request);
		} catch (JSONException e) {
			e.printStackTrace();
			response.getWriter().write("JSON 解析失败");
		}
		return null;

	}

	@RequestMapping(value = "tgIOSPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getTaiGuoIOSPay(HttpServletResponse response, HttpServletRequest request) throws IOException {
		BufferedReader br = request.getReader();
		String str, body = "";
		while ((str = br.readLine()) != null) {
			body += str;
		}
		logger.info("tgIOSPay:body=========================" + body);
		try {
			JSONObject jsonObject = new JSONObject(body);
			String appId = jsonObject.getString("appId");
			String uid = jsonObject.getString("uid");
			String orderId = jsonObject.getString("orderId");
			String cpOrderId = jsonObject.getString("cpOrderId");
			String amount = jsonObject.getString("amount");
			String paytype = jsonObject.getString("paytype");
			String ext = !jsonObject.isNull("ext") ? jsonObject.getString("ext") : "";
			String einfo = !jsonObject.isNull("einfo") ? jsonObject.getString("einfo") : "";
			String cardId = !jsonObject.isNull("cardId") ? jsonObject.getString("cardId") : "";
			String sign = jsonObject.getString("sign");
			return Pay("29a49eec9ad1281a2e799e", appId, uid, orderId, cpOrderId, amount, paytype, ext, einfo, cardId, sign, response, request);
		} catch (JSONException e) {
			e.printStackTrace();
			response.getWriter().write("JSON 解析失败");
		}
		return null;
	}

	public RestResult Pay(String appsecret, String appId, String uid, String orderId, String cpOrderId, String amount, String paytype, String ext, String einfo, String cardId, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.info(String.format("TGPayController:appsecret:{%s},appId:{%s},uid:{%s},orderId:{%s},cpOrderId:{%s},amount:{%s},paytype:{%s},ext:{%s},einfo:{%s},cardId:{%s},sign:{%s},", appsecret, appId, uid, orderId, cpOrderId, amount, paytype, ext, einfo, cardId, sign));
		String info = String.format("appId=%s&uid=%s&orderId=%s&cpOrderId=%s&amount=%s&paytype=%s&ext=%s||%s", appId, uid, orderId, cpOrderId, amount, paytype, ext, appsecret);
		String newSign = MD5.GetMD5Code(info);
		String result = "-1";
		logger.info("==============newSign==================" + newSign);
		if (newSign.toString().equals(sign)) {
			OrderInfo orderInfo = orderServcie.getOrderByOrderId(orderId);
			if (orderInfo == null) {
				orderInfo = new OrderInfo();
				orderInfo.setUserId(uid);
				orderInfo.setActuralFee(amount);
				orderInfo.setOrderId(orderId);
				orderInfo.setCpOrderId(cpOrderId);
				orderInfo.setDnyOrderId(orderId);
				orderInfo.setAccountId(0);
				orderInfo.setTotalFee(amount);
				String[] args = ext.split(",");
				if (args.length < 4) {
					orderInfo.setType(paytype + "-Three");
					orderInfo.setSubjectId("0");
					orderInfo.setServerId((Integer.parseInt(einfo.split("\\|")[0])));
					orderInfo.setRoleId(einfo.split("\\|")[1]);
				} else {
					orderInfo.setType(paytype + (args.length > 4 ? args[4] : ""));
					orderInfo.setSubjectId(args[2]);
					orderInfo.setServerId((Integer.parseInt(args[1])));
					orderInfo.setRoleId(args[0]);
				}

				Timestamp ts = new Timestamp(System.currentTimeMillis());
				orderInfo.setDate(ts);
				orderServcie.create(orderInfo);
			}
			if (doPostOrderRecharge(orderInfo)) {
				result = "0";
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
			args.append("&priceUnit=USD");
			args.append("&source=" + orderInfo.getType());
			args.append("&amount=" + orderInfo.getActuralFee());
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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getUserId(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()) / 100, "USD", jsonObject.getInt("goodsCount"), "" + orderInfo.getServerId(), "TG", orderInfo.getSubjectId(), "D1BA61E117B945BF8B7C3C2221875762");
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
