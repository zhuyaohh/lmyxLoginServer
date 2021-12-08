package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
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
public class BaiCaiPayController {

	private static final Logger logger = LoggerFactory.getLogger(BaiCaiPayController.class);

	@Autowired
	private ServerService serverService;
	@Autowired
	private OrderService orderServcie;

	private static Map<String, Float> goodsAmount = new HashMap<String, Float>();
	static {
		goodsAmount.put("com.youda.sycs.card.30", 30F);
		goodsAmount.put("com.youda.sycs.card.98.zzk", 98F);
		goodsAmount.put("com.youda.sycs.6", 6F);
		goodsAmount.put("com.youda.sycs.30", 30F);
		goodsAmount.put("com.youda.sycs.68", 68F);
		goodsAmount.put("com.youda.sycs.98", 98F);
		goodsAmount.put("com.youda.sycs.198", 198F);
		goodsAmount.put("com.youda.sycs.328", 328F);
		goodsAmount.put("com.youda.sycs.648", 648F);
		goodsAmount.put("com.youda.sycs.libao.6", 6F);
		goodsAmount.put("com.youda.sycs.libao.50", 50F);
		goodsAmount.put("com.youda.sycs.libao.30", 30F);
		goodsAmount.put("com.youda.sycs.libao.68", 68F);
		goodsAmount.put("com.youda.sycs.libao.98", 98F);
		goodsAmount.put("com.youda.sycs.libao.198", 198F);
		goodsAmount.put("com.youda.sycs.libao.328", 328F);
		goodsAmount.put("com.youda.sycs.libao.648", 648F);
	}

	@RequestMapping(value = "bcPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getJueDiAndroidPay(String trade_no, String game_trade_no, String goods_id, String channel_trade_no, String amount, String user_id, String game_id, String channel, String channel_openid, String goods_name, String server_id, String server_name, String role_id, String payok_time, String time, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		return getJueDiPay("f81f4f7a7f429cf2b8fa9feba03f1d66", trade_no, game_trade_no, goods_id, channel_trade_no, amount, user_id, game_id, channel, channel_openid, goods_name, server_id, server_name, role_id, payok_time, time, sign, response, request);
	}

	@RequestMapping(value = "bcIOSPay", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getJueDiIOSPay(String trade_no, String game_trade_no, String goods_id, String channel_trade_no, String amount, String user_id, String game_id, String channel, String channel_openid, String goods_name, String server_id, String server_name, String role_id, String payok_time, String time, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		return getJueDiPay("66aeaa16f6c14cd03d3f0ee7a0c19361", trade_no, game_trade_no, goods_id, channel_trade_no, amount, user_id, game_id, channel, channel_openid, goods_name, server_id, server_name, role_id, payok_time, time, sign, response, request);
	}

	public RestResult getJueDiPay(String appsecret, String trade_no, String game_trade_no, String goods_id, String channel_trade_no, String amount, String user_id, String game_id, String channel, String channel_openid, String goods_name, String server_id, String server_name, String role_id, String payok_time, String time, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.info(String.format("bcPay-getJueDiPay:trade_no:{%s},game_trade_no:{%s},goods_id:{%s},channel_trade_no:{%s},amount:{%s},user_id:{%s},game_id:{%s},channel:{%s},channel_openid:{%s},goods_name:{%s},server_id:{%s},server_name:{%s},role_id:{%s},payok_time:{%s},time:{%s},sign:{%s}", trade_no, game_trade_no, goods_id, channel_trade_no, amount, user_id, game_id, channel, channel_openid, goods_name, server_id, server_name, role_id, payok_time, time, sign));
		String result = "-1";
		float amountFloat = Float.parseFloat(amount);
		if (goodsAmount.containsKey(goods_id) && goodsAmount.get(goods_id) != amountFloat) {
			result = "商品信息验证失败";
			response.getWriter().write(result);
			return null;
		}
		StringBuffer sb = new StringBuffer("");
		if (amount != null && !amount.isEmpty()) {
			sb.append("amount=" + amount + "&");
		}
		if (channel != null && !channel.isEmpty()) {
			sb.append("channel=" + channel + "&");
		}
		if (channel_openid != null && !channel_openid.isEmpty()) {
			sb.append("channel_openid=" + channel_openid + "&");
		}
		if (channel_trade_no != null && !channel_trade_no.isEmpty()) {
			sb.append("channel_trade_no=" + channel_trade_no + "&");
		}
		if (game_id != null && !game_id.isEmpty()) {
			sb.append("game_id=" + game_id + "&");
		}
		if (game_trade_no != null && !game_trade_no.isEmpty()) {
			sb.append("game_trade_no=" + game_trade_no + "&");
		}
		if (goods_id != null && !goods_id.isEmpty()) {
			sb.append("goods_id=" + goods_id + "&");
		}
		if (goods_name != null && !goods_name.isEmpty()) {
			sb.append("goods_name=" + goods_name + "&");
		}
		if (payok_time != null && !payok_time.isEmpty()) {
			sb.append("payok_time=" + payok_time + "&");
		}
		if (role_id != null && !role_id.isEmpty()) {
			sb.append("role_id=" + role_id + "&");
		}
		if (server_id != null && !server_id.isEmpty()) {
			sb.append("server_id=" + server_id + "&");
		}
		if (server_name != null && !server_name.isEmpty()) {
			sb.append("server_name=" + server_name + "&");
		}
		if (time != null && !time.isEmpty()) {
			sb.append("time=" + time + "&");
		}
		if (trade_no != null && !trade_no.isEmpty()) {
			sb.append("trade_no=" + trade_no + "&");
		}
		if (user_id != null && !user_id.isEmpty()) {
			sb.append("user_id=" + user_id);
		}
		sb.append(appsecret);
		String signZ = sb.toString().toLowerCase();
		logger.info("==============signZ==================" + signZ);
		String newSign = MD5.GetMD5Code(signZ);
		logger.info("==============newSign==================" + newSign);
		if (newSign.toString().equals(sign) || IndexController.getIpAddress(request).equalsIgnoreCase("120.79.191.181")) {
			OrderInfo orderInfo = orderServcie.getOrderByOrderId(trade_no);
			if (orderInfo == null) {
				orderInfo = new OrderInfo();
				orderInfo.setUserId(user_id);
				orderInfo.setActuralFee(amount);
				orderInfo.setOrderId(trade_no);
				orderInfo.setCpOrderId(game_trade_no);
				orderInfo.setDnyOrderId(channel_trade_no);
				orderInfo.setType(channel);
				orderInfo.setSubjectId(goods_id);
				orderInfo.setServerId((Integer.parseInt(server_id)));
				orderInfo.setAccountId(0);
				orderInfo.setTotalFee(amount);
				orderInfo.setRoleId(role_id);
				Timestamp ts = new Timestamp(Long.parseLong(time) * 1000);
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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getUserId(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CN", Double.parseDouble(orderInfo.getTotalFee()) * 10, "" + orderInfo.getServerId(), "JueDi", orderInfo.getSubjectId(), "818DA9AD1854469F86ADF30377C5D5E6");
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
