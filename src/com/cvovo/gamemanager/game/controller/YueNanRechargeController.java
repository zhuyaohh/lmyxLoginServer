package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

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
import com.cvovo.gamemanager.util.HMAC_SHA256;
import com.cvovo.gamemanager.util.PublicMethod;

@RestController
@RequestMapping("/")
public class YueNanRechargeController {

	private static final Logger logger = LoggerFactory.getLogger(YueNanRechargeController.class);
	@Autowired
	private OrderService orderServcie;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "doOrderInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getYueNanRecharge(String GameOrderID, String KulOrderID, String ServerID, String PackageID, String Time, String Sign, HttpServletResponse response) throws JSONException, IOException {
		logger.info("===============welcome getYueNanRecharge===");
		logger.info("====GameOrderID=====" + GameOrderID);
		logger.info("====KulOrderID=======" + KulOrderID);
		logger.info("====ServerID=========" + ServerID);
		logger.info("====PackageID=======" + PackageID);
		logger.info("====Time===========" + Time);
		logger.info("====Sign============" + Sign);
		String secretKey = "FHSccf39ac96deb5296efc155fe30b1720a";
		StringBuffer oldSign = new StringBuffer(GameOrderID);
		oldSign.append(KulOrderID);
		oldSign.append(ServerID);
		oldSign.append(PackageID);
		oldSign.append(Time);
		HashMap<String, Object> valueMap = new HashMap<String, Object>();
		String newSign = HMAC_SHA256.HMACSHA256(oldSign.toString().getBytes(), secretKey.getBytes());
		int result = -102;
		String value = "SIGN IS ERROR";
		logger.info("====newSign============" + newSign.toString());
		if (newSign.toString().equals(Sign)) {

			OrderInfo orderInfo = new OrderInfo();

			String[] args = ServerID.split("\\|");
			orderInfo.setCpOrderId(GameOrderID);
			orderInfo.setOrderId(KulOrderID);
			orderInfo.setRoleId(args[1]);
			orderInfo.setServerId(Integer.parseInt(args[0]));
			orderInfo.setStatus(2);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setTotalFee("00");
			orderInfo.setSubjectId(PackageID);
			orderInfo.setType(args[2]);
			orderInfo = orderServcie.checkSaveOrCreate(orderInfo);
			if (orderInfo != null && orderInfo.getStatus() != 3) {
				if (doPostOrderRecharge(orderInfo)) {
					result = 0;
					valueMap.put("GameOrderID", GameOrderID);
					JSONObject js = new JSONObject();
					js.put("e", result);
					js.put("r", valueMap);
					PrintWriter writer = response.getWriter();
					response.getWriter().write(js.toString());
					writer.close();
				}
			} else {
				result = -101;
				value = "Duplicate GameOrderID";
			}

		}

		PrintWriter writer = response.getWriter();
		JSONObject js = new JSONObject();
		js.put("e", result);
		js.put("r", value);
		writer.write(js.toString());
		writer.close();

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
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	@RequestMapping(value = "GetCharacter", method = { RequestMethod.GET, RequestMethod.POST })
	public void getGetCharacter(String KulUserID, String ServerID, HttpServletResponse response) throws IOException, JSONException {
		logger.info("======getGetCharacter==============");
		logger.info("======KulUserID======" + KulUserID);
		logger.info("======ServerID=====" + ServerID);
		Account account = accountService.findByUin(KulUserID);
		if (account != null) {
			int id = Integer.parseInt(ServerID);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			String[] strList = serverInfo.getUrl().split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			StringBuffer args = new StringBuffer("c=1010");
			args.append("&platAccount=" + account.getUin());
			args.append("&serverId=" + id);
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			result = result.replace("errorCode", "e");
			result = result.replace("level", "Level");
			result = result.replace("name", "RoleName");
			result = result.replace("roleId", "RoleID");
			JSONObject tempJs = getGetCharacterJasonByStr(result, ServerID);
			PrintWriter writer = response.getWriter();
			writer.append(tempJs.toString());
			writer.close();
		} else {
			org.json.JSONObject jasonObject = new org.json.JSONObject();
			jasonObject.put("e", -100);
			jasonObject.put("r", "Role not exist");
			PrintWriter writer = response.getWriter();
			writer.append(jasonObject.toString());
			writer.close();
		}
	}

	public JSONObject getGetCharacterJasonByStr(String result, String serverId) throws JSONException {
		JSONObject js = new JSONObject(result);
		JSONObject t = null;
		String roleId = "-11";
		if (js.has("role")) {
			JSONArray tt = ((JSONArray) js.get("role"));
			for (int i = 0; i < tt.length(); i++) {
				t = (JSONObject) tt.get(i);
				roleId = t.getString("RoleID");
				OrderInfo CardDayMonthOrder = orderServcie.getOrderInfoByInfo(serverId, roleId, "com.cmn.5hiepsi.p1");
				if (CardDayMonthOrder != null && t != null) {
					t.put("CardDayMonth", CardDayMonthOrder.getDate());
				}
				OrderInfo CardDayYear = orderServcie.getOrderInfoByInfo(serverId, roleId, "com.cmn.5hiepsi.p2");
				if (CardDayYear != null) {
					t.put("CardDayYear", CardDayYear.getDate());
				}
			}

		}

		return js;
	}

	@RequestMapping(value = "Topup", method = { RequestMethod.GET, RequestMethod.POST })
	public void getOrderInfo(String RoleID, String ServerID, String KulOrderID, String Package, String Time, String Sign, HttpServletResponse response) throws IOException, JSONException {
		logger.info("======Topup==getOrderInfo================");
		logger.info("======RoleID======" + RoleID);
		logger.info("======ServerID=====" + ServerID);
		logger.info("======KulOrderID===" + KulOrderID);
		logger.info("======Package=====" + Package);
		logger.info("======Time=======" + Time);
		logger.info("======Sign=======" + Sign);
		String secretKey = "0be0aee18ae663219e4ea2ef2de3f176";
		String result = "{\"e\":\"-100\",\"r\":\"GameOrderID Not Exist\"}";
		PrintWriter writer = response.getWriter();
		StringBuffer newSign = new StringBuffer(RoleID);
		newSign.append(ServerID);
		newSign.append(KulOrderID);
		newSign.append(Package);
		newSign.append(Time);
		logger.info("======加密前=======" + newSign.toString());
		String sign1 = HMAC_SHA256.HMACSHA256(newSign.toString().getBytes(), secretKey.getBytes());

		logger.info("======s加密后======" + sign1);
		if (sign1.equals(Sign)) {
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setCpOrderId(PublicMethod.getBillUniqueId());
			orderInfo.setOrderId(KulOrderID);
			orderInfo.setRoleId(RoleID);
			orderInfo.setServerId(Integer.parseInt(ServerID));
			orderInfo.setStatus(2);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setTotalFee("00");
			orderInfo.setSubjectId(Package);
			orderInfo.setType("WAP");
			orderInfo = orderServcie.checkSaveOrCreate(orderInfo);

			if (orderInfo != null && orderInfo.getStatus() != 3) {
				if (doPostOrderRecharge(orderInfo)) {
					JSONObject jasonObject = new JSONObject();
					JSONObject jasonObjectSon = new JSONObject();
					jasonObjectSon.put("GameOrderID", orderInfo.getCpOrderId());
					jasonObject.put("e", "0");
					jasonObject.put("r", jasonObjectSon);
					writer.append(jasonObject.toString());
					logger.info("================" + jasonObject.toString() + "==============");
					writer.close();
				}
			} else {
				result = "{\"e\":\"-100\",\"r\":\"KulOrderID IS ALREADY \"}";
			}
			writer.append(result);
			writer.close();

		}
	}

	public static void main(String[] args) throws JSONException {

		// /,\"gold\":48397,\"RoleID\":100406,\"CardDayMonth\":\"2017-01-03 10:59:00\",\"CardDayYear\":\"2017-01-01 03:30:00\"
		String result = "{\"e\":0,\"role\":[{\"Level\":2,\"RoleName\":\"daika\",\"gold\":2325,\"RoleID\":100424}]}";
		JSONObject js = new JSONObject(result);
		if (js.has("role")) {
			JSONObject t = (JSONObject) ((JSONArray) js.get("role")).get(0);
			System.out.println(t.get("RoleID"));

		}

	}
}
