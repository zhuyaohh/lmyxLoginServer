package com.cvovo.gamemanager.game.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Timestamp;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class BT996SDKPayController {
	private static final Logger logger = LoggerFactory.getLogger(BT996SDKPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	private static String getMD5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {

		}
		return "";
	}

	protected String GetEncode(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (Exception e) {
		}
		return "";
	}

	// 签名成功处理函数
	protected String DoSdkNotify(String amount, String appid, String charid, String cporderid, String extinfo, String gold, String orderid, String serverid, String time, String uid, String sign) {
		String success = "SUCCESS";
		try {
			extinfo = URLDecoder.decode(extinfo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String[] extendArray = extinfo.split(",");
		String cpOrderId = extendArray[0];
		int serverId = Integer.parseInt(extendArray[1]);
		String playerId = extendArray[2];
		String shopId = extendArray[3];
		String type = extendArray[4];
		if (cpOrderId == null || cpOrderId.isEmpty()) {
			return "Faill cpOrderId = null";
		}
		OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
		if (orderInfo == null) {
			return "Faill orderInfo = null";
		}
		switch (orderInfo.getStatus()) {
		case 3:
			return success;
		case 1:
			orderInfo.setStatus(2);
			orderInfo.setDnyOrderId(orderid);
			orderInfo.setPriceUnit("CNY");
			orderServcie.update(orderInfo);
			break;
		default:
			break;
		}
		int payPrice = (int) Double.parseDouble(amount);
		ServerInfo serverInfo = serverService.getServerInfo(serverId);
		if (serverInfo != null) {
			logger.info("==验证通过=================启动游戏服务器");
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=5001");
			args.append("&playerId=" + playerId);
			args.append("&shopId=" + shopId);
			args.append("&order=" + cpOrderId);
			args.append("&price=" + payPrice);
			args.append("&source=" + type);
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
						return success;
					}
					String msg = jsonObject.get("msg").toString();
					logger.info("msg=======" + msg);
					return msg;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return success;
	}

	@RequestMapping(value = "btzsrybillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public String pay_notify(@RequestParam("amount") String amount, @RequestParam("appid") String appid, @RequestParam("charid") String charid, @RequestParam("cporderid") String cporderid, @RequestParam("extinfo") String extinfo, @RequestParam("gold") String gold, @RequestParam("orderid") String orderid, @RequestParam("serverid") String serverid, @RequestParam("time") String time, @RequestParam("uid") String uid, @RequestParam("sign") String sign) {
		logger.info(String.format("btzsrybillNo:amount:{%s},appid:{%s},charid:{%s},cporderid:{%s},extinfo:{%s},gold:{%s},orderid:{%s},serverid:{%s},time:{%s},uid:{%s},sign:{%s}", amount, appid, charid, cporderid, extinfo, gold, orderid, serverid, time, uid, sign));
		String Pay_Key = "NULL";
		if (appid.equals("783")) {
			Pay_Key = "736a94a736b1fb7fb1d72a17d2479d97";
		} else if (appid.equals("784")) {
			Pay_Key = "53f6d5d69b7b2cec58a8881ffcde6be7";
		}
		String code = String.format("amount=%s&appid=%s&charid=%s&cporderid=%s&extinfo=%s&gold=%s&orderid=%s&serverid=%s&time=%s&uid=%s%s", GetEncode(amount), GetEncode(appid), GetEncode(charid), GetEncode(cporderid), GetEncode(extinfo), GetEncode(gold), GetEncode(orderid), GetEncode(serverid), time, GetEncode(uid), Pay_Key);
		String md5 = getMD5(code);
		// 签名失败
		if (!md5.equals(sign)) {
			return "ERROR";
		}
		// 处理签名成功
		return DoSdkNotify(amount, appid, charid, cporderid, extinfo, gold, orderid, serverid, time, uid, sign);
	}

	@RequestMapping(value = "bt996iosbillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public int getOrderInfo(int game_id, String out_trade_no, String price, String extend, String sign) throws Exception {
		logger.info(String.format("game_id:{%s},out_trade_no:{%s},price:{%s},extend:{%s},sign:{%s}", game_id, out_trade_no, price, extend, sign));
		String passport = "62b8bfc7ca3de150129c1e60e6b82058";

		int payPrice = (int) Double.parseDouble(price);
		String[] extendArray = extend.split(",");
		String cpOrderId = extendArray[0];
		int serverId = Integer.parseInt(extendArray[1]);
		String playerId = extendArray[2];
		String shopId = extendArray[3];
		String type = extendArray[4];
		if (cpOrderId == null || cpOrderId.isEmpty()) {
			return 0;
		}

		OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
		if (orderInfo == null) {
			return 0;
		}
		switch (orderInfo.getStatus()) {
		case 3:
			return 1;
		case 1:
			orderInfo.setStatus(2);
			orderInfo.setDnyOrderId(out_trade_no);
			orderInfo.setPriceUnit("CNY");
			orderServcie.update(orderInfo);
			break;
		default:
			break;
		}
		logger.info("==获取订单信息====================");
		StringBuffer tempSign = new StringBuffer("");
		tempSign.append(game_id);
		tempSign.append(out_trade_no);
		tempSign.append(price);
		tempSign.append(extend);
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
			ServerInfo serverInfo = serverService.getServerInfo(serverId);
			if (serverInfo != null) {
				logger.info("==验证通过=================启动游戏服务器");
				String url = serverInfo.getUrl();
				StringBuffer args = new StringBuffer("c=5001");
				args.append("&playerId=" + playerId);
				args.append("&shopId=" + shopId);
				args.append("&order=" + cpOrderId);
				args.append("&price=" + payPrice);
				args.append("&source=" + type);
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
							return 1;
						}
						String msg = jsonObject.get("msg").toString();
						logger.info("msg=======" + msg);
						return 0;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return 0;
	}

}
