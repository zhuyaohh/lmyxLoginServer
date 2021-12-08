package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.alipay.Base64;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5Util;

@RestController
@RequestMapping("/")
public class ChangXiaoPayController {
	private static final Logger logger = LoggerFactory.getLogger(ChangXiaoPayController.class);
	@Autowired
	private OrderService orderServcie;
	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "changxiaoPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void cxPayNext(String orderno, String fee, String sign, String pay_type, String app_id, String attach, HttpServletResponse response) throws Exception {
		logger.info("===orderno===" + orderno);
		logger.info("===fee===" + fee);
		logger.info("===sign===" + sign);
		logger.info("===pay_type===" + pay_type);
		logger.info("===app_id===" + app_id);
		logger.info("===attach===" + attach);
		PrintWriter writer = response.getWriter();
		OrderInfo order = orderServcie.getOrderByCpOrderId(orderno);
		if (order == null || (order != null && order.getCpOrderId() != orderno)) {
			order = new OrderInfo();
			if (attach != null && !attach.equals("")) {
				String[] str = attach.split("_");
				order.setCpOrderId(orderno);
				order.setServerId(Integer.parseInt(str[1]));
				order.setRoleId(str[2]);
				order.setSubjectId(str[0]);
				order.setDate(new Timestamp(System.currentTimeMillis()));
				order.setStatus(2);
				order.setActuralFee(fee);
				order.setType(pay_type);
				orderServcie.update(order);
				if (dotRecharge(order)) {
					writer.write("ok");
				}
			}
			writer.write("fail");
		}
		writer.write("fail");
	}

	public boolean dotRecharge(OrderInfo orderInfo) {
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
			args.append("&source=" + orderInfo.getType());
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
					logger.info("===errorId======" + errorId);
					if (errorId != null && errorId.equals("0")) {
						logger.info("===orderInfo status setting======");
						orderInfo.setStatus(3);
						orderServcie.update(orderInfo);
						logger.info("===db is ok======");
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	@RequestMapping(value = "newChangxiaoPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void cxNewPayNext(HttpServletResponse response, HttpServletRequest request) throws Exception {
		logger.info("======cxNewPayNext=================");
		String key = "5dc978113444b9c8010ced2856093550";
		StringBuffer bundleID = new StringBuffer("cn.jlsj.universal.appleuser.com");
		PrintWriter writer = response.getWriter();
		BufferedReader br = request.getReader();
		String result = "0";
		String reason = "";
		String str, wholeStr = "";
		while ((str = br.readLine()) != null) {
			wholeStr += str;
		}
		logger.info("===wholeStr======" + wholeStr);

		String[] roleId_serverId = null;
		String notification = null, bid, signature = null, pid, tid = null, type = null, timestamp, extraData = null, version = null, newJason = null;
		JSONObject jsonObject = null;
		try {
			logger.info("=======wholeStr开始转移JASON对象=================");
			jsonObject = new JSONObject(wholeStr);
			logger.info("===转换成果======" + jsonObject.toString());
		} catch (Exception ex) {
			logger.info("==============转换异常=========");
			org.json.JSONObject jasonObject = new org.json.JSONObject();
			jasonObject.put("result", result);
			jasonObject.put("reason", "args is error");
			jasonObject.put("extraData", extraData);
			String responseStr = jasonObject.toString();
			String bsResponseStr = Base64.encode(responseStr.getBytes()).toString();
			org.json.JSONObject resultJason = new org.json.JSONObject();
			resultJason.put("response", bsResponseStr);
			resultJason.put("version", version);
			writer.write(resultJason.toString());
			writer.close();
			return;
		}
		if (jsonObject.has("notification")) {
			notification = jsonObject.getString("notification");
			logger.info("===notification======" + notification);
			newJason = new String(Base64.decode(notification));
			logger.info("===newJason======" + newJason);
			JSONObject wholeObject = new JSONObject(newJason);
			if (wholeObject.has("bid")) {
				bid = wholeObject.getString("bid");
				logger.info("===bid======" + bid);
			}
			if (wholeObject.has("pid")) {
				pid = wholeObject.getString("pid");
				logger.info("===pid======" + pid);
			}
			if (wholeObject.has("tid")) {
				tid = wholeObject.getString("tid");
				logger.info("===tid======" + tid);
			}
			if (wholeObject.has("type")) {
				type = wholeObject.getString("type");
				logger.info("===type======" + type);
			}
			if (wholeObject.has("timestamp")) {
				timestamp = wholeObject.getString("timestamp");
				logger.info("===timestamp======" + timestamp);
			}
			if (wholeObject.has("extraData")) {
				extraData = wholeObject.getString("extraData");
				logger.info("===extraData======" + extraData);
				roleId_serverId = extraData.split("_");

			}
		}
		if (jsonObject.has("signature")) {
			signature = jsonObject.getString("signature");
			logger.info("===signature======" + signature);
		}
		if (jsonObject.has("version")) {
			version = jsonObject.getString("version");
			logger.info("===version======" + version);
		}
		String newArgs = bundleID.append(Base64.encode(notification.getBytes())).append(key).toString();
		String strTG = MD5Util.MD5Encode(newArgs, "utf-8");
		logger.info("===newSignature======" + strTG);
		if (strTG.equals(signature)) {
			if (roleId_serverId != null && roleId_serverId.length == 3) {
				OrderInfo order = orderServcie.getOrderByCpOrderId(tid);
				if (order == null || (order != null && order.getCpOrderId() != tid)) {
					order = new OrderInfo();
					order.setCpOrderId(tid);
					order.setOrderId(tid);
					order.setServerId(Integer.parseInt(roleId_serverId[1]));
					order.setRoleId(roleId_serverId[2]);
					order.setSubjectId(roleId_serverId[0]);
					order.setFinishDate(new Timestamp(System.currentTimeMillis()));
					order.setStatus(2);
					order.setActuralFee("0");
					order.setType(type);
					order = orderServcie.checkSaveOrCreate(order);
					logger.info("===order======" + order);
					if (order != null && dotRecharge(order)) {
						result = "1";
					}

				} else {
					reason = "tid has already";
					extraData = "";
				}

			} else {
				reason = "extraData  is error";
				extraData = "";
			}
		} else {
			reason = "signature is error ";
			extraData = "";
		}

		org.json.JSONObject jasonObject = new org.json.JSONObject();
		jasonObject.put("result", result);
		jasonObject.put("reason", reason);
		jasonObject.put("extraData", extraData);
		String responseStr = jasonObject.toString();
		String bsResponseStr = Base64.encode(responseStr.getBytes()).toString();
		org.json.JSONObject resultJason = new org.json.JSONObject();
		resultJason.put("response", bsResponseStr);
		resultJason.put("version", version);
		writer.write(resultJason.toString());
		writer.close();
	}

	public static String bytes2hex03(byte[] bytes) {
		final String HEX = "0123456789abcdef";
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			// 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt((b >> 4) & 0x0f));
			// 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
			sb.append(HEX.charAt(b & 0x0f));
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		StringBuffer bundleID = new StringBuffer("cn.jlsj.universal.appleuser.com");
		String key = "5dc978113444b9c8010ced2856093550";
		String sdf = "eyJiaWQiOiJjbi5qbHNqLnVuaXZlcnNhbC5hcHBsZXVzZXIuY29tIiwicGlkIjoiY24uamxzai51bml2ZXJzYWwuYXBwbGV1c2VyLmNvbS5wMDEiLCJ0aWQiOiIyMDE3MDMwMzIwMzMyNTc0NyIsInR5cGUiOjAsInRpbWVzdGFtcCI6MTQ4ODU0NDQ1NSwiZXh0cmFEYXRhIjoiY24uamxzai51bml2ZXJzYWwuYXBwbGV1c2VyLmNvbS5wMDFfMTAwM18xMDMyNzMifQ==";
		StringBuffer newJason = new StringBuffer("{\"bid\":\"cn.jlsj.universal.appleuser.com\",");
		newJason.append("\"pid\":\"cn.jlsj.universal.appleuser.com.p01\",");
		newJason.append("\"tid\":\"20170303203325747\",");
		newJason.append("\"type\":0,\"");
		newJason.append("\"timestamp\":1488544455,");
		newJason.append("\"extraData\":\"cn.jlsj.universal.appleuser.com.p01_1003_103273\"}");
		System.out.println(newJason);
		String t = bundleID.append(Base64.encode(sdf.getBytes())).append(key).toString();
		String t2 = MD5Util.MD5Encode(t, "utf-8");

		System.out.println(t2);

	}
}
