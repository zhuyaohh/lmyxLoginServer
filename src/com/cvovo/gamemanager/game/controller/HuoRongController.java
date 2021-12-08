package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class HuoRongController {

	public static final String APPID = "1000000027";
	public static final String KEY = "de17b0057a75670de69096d3583e5c0c";

	@Autowired
	private OrderService orderServcie;
	@Autowired
	public ServerService serverService;

	@RequestMapping(value = "getHRServer", method = { RequestMethod.GET, RequestMethod.POST })
	public void getAllServerList(String time, String sign, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sign1 = MD5.GetMD5Code(time + "|" + KEY);
		org.json.JSONObject jsonObject = new JSONObject();
		if (sign1.equals(sign)) {
			org.json.JSONArray data = new JSONArray();
			java.util.List<ServerInfo> list = serverService.getServerListBySign(1);
			for (int i = 0; i < list.size(); i++) {
				org.json.JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("id", list.get(i).getId());
				jsonObject1.put("name", list.get(i).getName());
				data.put(jsonObject1);
			}
			data.put(serverService.getAllServerList());
			jsonObject.put("code", "0");
			jsonObject.put("msg", "");
			jsonObject.put("data", data);
		} else {
			jsonObject.put("code", "-1");
			jsonObject.put("msg", "验证失败");
		}
		PrintWriter writer = response.getWriter();
		response.getWriter().write(jsonObject.toString());
		writer.close();
	}

	@RequestMapping(value = "getHRRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getRoleInfo(String uid, String server_id, String time, String sign, HttpServletRequest request, HttpServletResponse response) {
		JSONObject jsonObject = new JSONObject();
		try {
			String sign1 = MD5.GetMD5Code(server_id + "|" + uid + "|" + time + "|" + KEY);
			if (sign1.equals(sign)) {
				jsonObject.put("code", "0");
				jsonObject.put("msg", "");
				ServerInfo serverInfo = serverService.getServerInfo(Integer.parseInt(server_id));
				if (serverInfo != null) {
					JSONArray data = new JSONArray();
					StringBuffer title = new StringBuffer("http://");
					String ip = serverInfo.getUrl().split(":")[0];
					title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
					StringBuffer args = new StringBuffer("c=1010");
					args.append("&platAccount=" + uid);
					args.append("&serverId=" + server_id);
					JSONObject result = new JSONObject(HttpRequest.sendGet(title.toString(), args.toString()));
					if (result.getInt("errorCode") == 0) {
						JSONArray roles = jsonObject.getJSONArray("role");
						for (int i = 0; i < roles.length(); i++) {
							JSONObject role = roles.getJSONObject(i);
							JSONObject returnRole = new JSONObject();
							returnRole.put("name", role.getString("name"));
							returnRole.put("id", role.getInt("roleId"));
							returnRole.put("level", role.getInt("level"));
							returnRole.put("gold", role.getInt("gold"));
							data.put(returnRole);
						}
						jsonObject.put("data", data);
					} else {
						jsonObject.put("code", "-4");
						jsonObject.put("msg", "获取服务器数据异常:" + result.getInt("errorCode"));
					}
				} else {
					jsonObject.put("code", "-2");
					jsonObject.put("msg", "没有数据");
				}
			} else {
				jsonObject.put("code", "-1");
				jsonObject.put("msg", "验证失败");
			}
		} catch (Exception e) {
			try {
				jsonObject.put("code", "-3");
				jsonObject.put("msg", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		try {
			PrintWriter writer = response.getWriter();
			response.getWriter().write(jsonObject.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "huorongfbGift", method = { RequestMethod.GET, RequestMethod.POST })
	public void payCallBack(String server_id, String user_id, String role_id, String config, String sign, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sign1 = MD5.GetMD5Code(server_id + "|" + user_id + "|" + role_id + "|" + config + "|" + KEY);
		org.json.JSONObject jsonObject = new JSONObject();
		if (sign1.equals(sign)) {
			JSONObject rewardJson = new JSONObject(config);
			int reward = Integer.parseInt(rewardJson.getString("awardId"));
			int num = Integer.parseInt(rewardJson.getString("num"));
			StringBuffer args = new StringBuffer("c=1011");
			args.append("&playerId=" + role_id);
			args.append("&reward=" + reward);
			args.append("&num=" + num);
			ServerInfo serverInfo = serverService.getServerInfo(Integer.parseInt(server_id));
			String[] strList = serverInfo.getUrl().split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			JSONObject result = new JSONObject(HttpRequest.sendGet(title.toString(), args.toString()));
			jsonObject.put("code", result.getInt("errorCode"));
			jsonObject.put("msg", result.getString("errorMessage"));
		} else {
			jsonObject.put("code", "-1");
			jsonObject.put("msg", "验证失败");
		}
		PrintWriter writer = response.getWriter();
		response.getWriter().write(jsonObject.toString());
		writer.close();
	}

	@RequestMapping(value = "huorongPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void payCallBack(String order_id, String uid, String role_id, String goods_id, String money, String game_money, String src, String server_id, String time, String cp_order_id, String sign, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sign1 = MD5.GetMD5Code(order_id + "|" + uid + "|" + role_id + "|" + goods_id + "|" + money + "|" + game_money + "|" + src + "|" + server_id + "|" + time + "|" + cp_order_id + "|" + KEY);
		org.json.JSONObject jsonObject = new JSONObject();
		if (sign1.equals(sign)) {
			OrderInfo orderInfo = null;
			if (cp_order_id.equals("-1")) {// WEB支付
				orderInfo = orderServcie.getOrderByOrderId(order_id);
			} else {
				orderInfo = orderServcie.getOrderByCpOrderId(cp_order_id);
			}
			if (orderInfo == null) {
				orderInfo = new OrderInfo(order_id, money, money, Integer.parseInt(server_id), goods_id, src, uid, cp_order_id, "");
				orderInfo.setRoleId(role_id);
				orderInfo.setStatus(2);
				orderServcie.update(orderInfo);
			} else if (orderInfo.getStatus() == 1) {
				orderInfo.setStatus(2);
				orderInfo.setDnyOrderId(order_id);
				orderInfo.setPriceUnit("US");
				orderServcie.update(orderInfo);
			}
			if (orderInfo.getStatus() == 2) {
				sendPayInfo2GameServer(orderInfo, serverService.getServerInfo(Integer.parseInt(server_id)), role_id, goods_id, cp_order_id, money, src, jsonObject);
			}
		} else {
			jsonObject.put("code", "-1");
			jsonObject.put("order_id", cp_order_id);
			jsonObject.put("msg", "验证失败");
		}
		PrintWriter writer = response.getWriter();
		response.getWriter().write(jsonObject.toString());
		writer.close();
	}

	public void sendPayInfo2GameServer(OrderInfo orderInfo, ServerInfo serverInfo, String roleId, String subjectId, String cpOrderId, String price, String type, JSONObject returnJSON) {
		if (serverInfo != null) {
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=5001");
			args.append("&playerId=" + roleId);
			if (subjectId != null && !subjectId.equals("")) {
				args.append("&shopId=" + subjectId);
			} else {
				args.append("&shopId=" + "0000");
			}
			args.append("&order=" + cpOrderId);
			args.append("&price=" + price);
			args.append("&source=" + type);
			args.append("&priceUnit=" + "US");
			args.append("&goodsCount=0");
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result);
				if (jsonObject.has("errorId")) {
					String errorId = jsonObject.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						orderInfo.setStatus(3);
						orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
						orderServcie.update(orderInfo);
						double goodsCount = 0D;
						if (jsonObject.has("goodsCount")) {
							goodsCount = Double.parseDouble(jsonObject.get("goodsCount").toString());
						}
						YinNiPayController.talkingDataCharge(type, roleId, cpOrderId, Double.parseDouble(price), "USD", goodsCount, serverInfo.getId().toString(), "HuoRong", subjectId, "C33DA9EEDA0D4A448ACEB264779CF503");
						returnJSON.put("code", "0");
						returnJSON.put("order_id", cpOrderId);
					} else {
						String msg = jsonObject.get("msg").toString();
						returnJSON.put("code", "-1");
						returnJSON.put("order_id", cpOrderId);
						returnJSON.put("msg", msg);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}
}
