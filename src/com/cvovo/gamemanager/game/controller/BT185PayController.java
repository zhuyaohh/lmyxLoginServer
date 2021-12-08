package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
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

import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class BT185PayController {
	private static final Logger logger = LoggerFactory.getLogger(BT185PayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "btBillNo", method = { RequestMethod.GET, RequestMethod.POST })
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

	@RequestMapping(value = "btIOSBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getIOSOrderNo(String username, int amount, int rmb, int serverid, int channel, String gameid, String product_id, String orderno, String sign, String type3, String type4, String type5) throws Exception {
		logger.info("btIOSBillNo:body=========================" + String.format("username:{%s},amount:{%s},rmb:{%s},serverid:{%s},channel:{%s},gameid:{%s},product_id:{%s},orderno:{%s},sign:{%s},type3:{%s},type3:{%s},type5:{%s}", username, amount, rmb, serverid, channel, gameid, product_id, orderno, sign, type3, type4, type5));
		RestResult resutResult = new RestResult();
		Account tempAccount = accountService.findByUin(username);
		if (tempAccount == null) {
			resutResult.put("errcode", 1);
			resutResult.put("msg", "FAIL userId is null!!!");
			return resutResult;
		}
		String type = "BT_" + channel;
		String secretkey = "bb479f96c06da6cdcf2a4f345aea83ba";
		StringBuilder sb = new StringBuilder();
		sb.append(username + amount + rmb + serverid + orderno + product_id + secretkey);
		String md5Code = MD5.GetMD5Code(sb.toString());

		if (!md5Code.equalsIgnoreCase(sign)) {
			resutResult.put("errcode", 2);
			resutResult.put("msg", "FAIL MD5 is ERROR!!!");
			return resutResult;
		}
		String playerId = type3;
		OrderInfo orderInfo = new OrderInfo(orderno, "" + rmb, "" + rmb, serverid, product_id, type, username, orderno, orderno);
		orderInfo.setPriceUnit("" + amount);
		orderInfo.setRoleId(playerId);
		if (!orderServcie.create(orderInfo)) {
			resutResult.put("errcode", 4);
			resutResult.put("msg", "!orderServcie.create(orderInfo)");
			return resutResult;
		}
		ServerInfo serverInfo = serverService.getServerInfo(serverid);
		String msgInfo = "no serverId:" + serverid;
		if (serverInfo != null) {
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=5001");
			args.append("&playerId=" + playerId);
			if (product_id != null && !product_id.equals("")) {
				args.append("&shopId=" + product_id);
			} else {
				args.append("&shopId=" + "0000");
			}
			args.append("&order=" + orderno);
			args.append("&price=" + rmb);
			args.append("&source=" + type);
			args.append("&priceUnit=" + amount);
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
						resutResult.put("errcode", 0);
						return resutResult;
					} else {
						msgInfo = resultData.get("msg").toString();
						logger.info("msg=======" + msgInfo);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		resutResult.put("errcode", 6);
		resutResult.put("msg", msgInfo);
		return resutResult;
	}
}
