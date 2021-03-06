package com.cvovo.gamemanager.game.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.cvovo.gamemanager.util.JsonUtils;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class YinNiPayController {
	private static final Logger logger = LoggerFactory.getLogger(YinNiPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "yinNiBill", method = { RequestMethod.GET, RequestMethod.POST })
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
		String token1 = table.get("token");
		String type = table.get("type");
		String serverid = table.get("serverid");
		String subjectId = table.get("subjectId");
		String subject = table.get("subject");
		String acturalFee = table.get("acturalFee");
		String totalFee = table.get("acturalFee");
		String characterid = table.get("characterid");
		String playerid = table.get("uin");
		String ulevel = table.get("ulevel");
		String rolename = table.get("rolename");
		int accountId = (int) Float.parseFloat(table.get("accountId"));

		if (type == null || type.equals("")) {
			type = "??????";
		}
		logger.info("===================??????======");
		String billNo = PublicMethod.getUniqueId();
		Account tempAccount = accountService.getAccountByUserId(accountId);
		String guid = "";
		if (tempAccount != null) {
			guid = tempAccount.getsUid();
		}
		OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, (int) Float.parseFloat(serverid), subjectId, type, accountId, billNo, String.valueOf(billNo));
		logger.info("=========????????????=========");
		if (orderServcie.create(orderInfo)) {
			return RestResult.getBillSuc("1", String.valueOf(billNo), guid);
		} else {
			return RestResult.getBillSuc("0", String.valueOf(billNo), guid);
		}
	}

	public RestResult getOrderInfo(String agent, String passport, String server, String order, String money, String sign, String time, String param, String goodid, String key2) {
		logger.info("=========time????????????=========" + time);
		logger.info("=========agent????????????=========" + agent);
		logger.info("=========passport????????????==========" + passport);
		logger.info("=========param????????????=========" + param);

		logger.info("=========server????????????=======" + server);
		logger.info("=========order????????????===========" + order);
		logger.info("=========money????????????===========" + money);
		logger.info("=========sign????????????============" + sign);
		logger.info("=========goodid????????????==========" + goodid);

		String[] limit = param.split(",");
		String cpOrderId = limit[0];
		String type = limit[1];
		String roleId = limit[2];
		String serverId = limit[3];
		String googId = limit[4];
		OrderInfo orderInfo = null;
		int positon = 1;
		if (cpOrderId != null && !cpOrderId.equals("")) {
			positon = 0;
			orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
			if (orderInfo == null) {
				return RestResult.billYinNi("-5", "?????????????????????");
			} else if (orderInfo != null && orderInfo.getStatus() == 3) {
				orderInfo.setDnyOrderId(order);
				orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
				orderInfo.setPriceUnit("US");
				orderServcie.update(orderInfo);
				return RestResult.billYinNi("-4", "??????????????????");
			} else if (orderInfo != null && orderInfo.getStatus() == 1) {
				orderInfo.setStatus(2);
				orderInfo.setDnyOrderId(order);
				orderInfo.setPriceUnit("US");
				orderServcie.update(orderInfo);
			}
		} else {
			if (cpOrderId != null && !cpOrderId.equals("")) {
				orderInfo = orderServcie.getOrderByOrderId(cpOrderId);
			}
		}

		logger.info("==??????????????????====================");
		StringBuffer tempSign = new StringBuffer(passport);
		if (order != null && !order.equals("")) {
			tempSign.append(order);
		}
		if (key2 != null && !key2.equals("")) {
			tempSign.append(key2);
		}
		if (server != null && !server.equals("")) {
			tempSign.append(server);
		}
		if (time != null && !time.equals("")) {
			tempSign.append(time);
		}
		if (agent != null && !agent.equals("")) {
			tempSign.append(agent);
		}
		if (money != null && !money.equals("")) {
			tempSign.append(money);
		}

		logger.info("===tempSign==?????????==" + tempSign.toString());
		String key = "";
		try {
			key = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("===tempSign==?????????==" + key);
		logger.info("===sign==??????=" + sign);
		if (orderInfo == null && positon == 1) {
			String newPrice = money;
			orderInfo = new OrderInfo(order, newPrice, newPrice, (int) Float.parseFloat(server), goodid, type, Integer.parseInt(roleId), order, passport);
			orderInfo.setStatus(2);
			orderServcie.update(orderInfo);
		}
		logger.info("==????????????=================key.equals(sign):" + key.equals(sign));
		if (key.equals(sign)) {
			int id = Integer.parseInt(server);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			if (serverInfo != null) {
				logger.info("==????????????=================?????????????????????");
				String url = serverInfo.getUrl();
				StringBuffer args = new StringBuffer("c=5001");
				args.append("&playerId=" + roleId);
				if (goodid != null && !goodid.equals("")) {
					args.append("&shopId=" + googId);
				} else {
					args.append("&shopId=" + "0000");
				}
				args.append("&order=" + cpOrderId);
				args.append("&price=" + money);
				args.append("&source=" + type);
				args.append("&priceUnit=" + "US");
				args.append("&goodsCount=0");
				String[] strList = url.split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
				logger.info("??????HTTP??????========================" + args.toString());
				String result = HttpRequest.sendGet(title.toString(), args.toString());
				logger.info("??????HTTP????????????========================");
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject.has("errorId")) {
						String errorId = jsonObject.get("errorId").toString();
						if (errorId != null && errorId.equals("0")) {
							logger.info("?????????????????????????????????========================");
							orderInfo.setStatus(3);
							orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
							orderServcie.update(orderInfo);
							logger.info("????????????========================");

							double goodsCount = 0D;
							if (jsonObject.has("goodsCount")) {
								goodsCount = Double.parseDouble(jsonObject.get("goodsCount").toString());
							}
							talkingDataCharge(type, passport, cpOrderId, Double.parseDouble(money), "USD", goodsCount, serverId, type, googId, "");
							return RestResult.billYinNi("0", "????????????");
						} else {
							String msg = jsonObject.get("msg").toString();
							logger.info("msg=======" + msg);
							return RestResult.billYinNi("-8", "?????????????????????");
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return RestResult.billYinNi("-255", "????????????");
	}

	public static void talkingDataCharge(String OS, String accountID, String orderID, double currencyAmount, String currencyType,
			double virtualCurrencyAmount, String gameServer, String partner, String iapID, String talkingDateKey) {
		try {
			JSONObject json = new JSONObject();
			json.put("msgID", orderID);
			json.put("status", "success");
			if (OS == null || OS.isEmpty() || OS.toLowerCase().endsWith("ios") || OS.toLowerCase().endsWith("_i")) {
				OS = "ios";
			} else {
				OS = "android";
			}
			json.put("OS", OS);
			json.put("accountID", accountID);
			json.put("orderID", orderID);
			json.put("currencyAmount", currencyAmount);
			json.put("currencyType", currencyType);
			json.put("chargeTime", 0D);
			json.put("gameServer", gameServer);
			json.put("partner", partner);
			json.put("virtualCurrencyAmount", virtualCurrencyAmount);
			json.put("iapID", iapID);
			JSONArray array = new JSONArray();
			array.put(json);
			byte[] param = JsonUtils.gzip(array.toString());
			StringBuffer sb = new StringBuffer("http://api.talkinggame.com/api/charge/");
			sb.append(talkingDateKey);
			// ?????? B707307CCF6046F487A7F9B95BA26A91
			String info = HttpRequest.sendPost(sb.toString(), param);

			JSONObject returnJSON = new JSONObject(info);
			if (returnJSON.getInt("code") != 100) {
				logger.info(info);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * http://{????????????}? agent={???????????????}& passport ={?????????}&server={???????????????}&order={?????????}&money={????????????}&sign={????????????}&time={?????????}&param={????????????}&goodid={??????id *
	 */
	@RequestMapping(value = "yinNiBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getOrderNo1(String agent, String passport, String server, String order, String money, String sign, String time, String param, String goodid) {
		return getOrderInfo(agent, passport, server, order, money, sign, time, param, goodid, "9ty*i&m@chHiU$0FlGo*");
	}

	@RequestMapping(value = "yinNiYBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getOrderNo2(String agent, String passport, String server, String order, String money, String sign, String time, String param, String goodid) {
		return getOrderInfo(agent, passport, server, order, money, sign, time, param, goodid, "C18GPKdDO0urOzTQeTTp");
	}

	@RequestMapping(value = "lunplayerGATBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLunplayerOrder(String agent, String passport, String server, String order, String money, String sign, String time, String param, String goodid) {
		RestResult restResult = getOrderInfo(agent, passport, server, order, money, sign, time, param, goodid, "Tkyfa1us0Gd$s9uwB1ce");
		return restResult;
	}
}
