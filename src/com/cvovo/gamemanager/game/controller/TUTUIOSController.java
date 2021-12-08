package com.cvovo.gamemanager.game.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.config.AppConfig;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.CheckService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class TUTUIOSController {
	private static final Logger logger = LoggerFactory.getLogger(TUTUIOSController.class);
	private AppConfig appConfig;

	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CheckService checkService;

	@RequestMapping(value = "tutuController", method = { RequestMethod.GET, RequestMethod.POST })
	public void TUTUIOSVerify(String open_id, String cp_order_no, String serial_number, String amount, String verfy, HttpServletResponse response) throws Exception {
		logger.info("=========tutuController====================");
		logger.info("=========open_id回调数据============" + open_id);
		logger.info("=========cp_order_no回调数据=========" + cp_order_no);
		logger.info("=========serial_number回调数据=========" + serial_number);
		logger.info("=========amount回调数据==========" + amount);
		logger.info("=========verfy回调数据=========" + verfy);
		String message = "failure";
		PrintWriter writer = response.getWriter();
		OrderInfo orderInfo = null;
		int positon = 1;
		if (cp_order_no != null && !cp_order_no.equals("")) {
			positon = 0;
			orderInfo = orderServcie.getOrderByCpOrderId(cp_order_no);
			if (orderInfo == null) {
				response.getWriter().write(message);
				writer.close();
				return;
			} else if (orderInfo != null && orderInfo.getStatus() == 3) {
				orderInfo.setDnyOrderId(cp_order_no);
				orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
				orderInfo.setPriceUnit("US");
				orderServcie.update(orderInfo);
				response.getWriter().write("success");
				writer.close();
				return;
			} else if (orderInfo != null && orderInfo.getStatus() == 1) {
				orderInfo.setStatus(2);
				orderInfo.setDnyOrderId(cp_order_no);
				orderInfo.setPriceUnit("US");
				orderServcie.update(orderInfo);
			}
		}
		String skey = "39ef1c3b167264312bb95317";
		String app_key = "58d39aa1ac43e9";
		String verfyTest = MD5.GetMD5Code(app_key + skey + open_id + cp_order_no + serial_number);
		if (verfy.equals(verfyTest)) {
			int id = orderInfo.getServerId();
			ServerInfo serverInfo = serverService.getServerInfo(id);
			if (serverInfo != null) {
				logger.info("==验证通过=================启动游戏服务器");
				String url = serverInfo.getUrl();
				StringBuffer args = new StringBuffer("c=1004");
				args.append("&playerId=" + orderInfo.getRoleId());
				args.append("&shopId=" + orderInfo.getSubjectId());
				args.append("&order=" + orderInfo.getCpOrderId());
				args.append("&source=" + orderInfo.getType());
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
							response.getWriter().write("success");
							writer.close();
							return;
						} else {
							String msg = jsonObject.get("msg").toString();
							logger.info("msg=======" + msg);
							response.getWriter().write(message);
							writer.close();
							return;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
		response.getWriter().write(message);
		writer.close();
	}

	@RequestMapping(value = "tutuBill", method = { RequestMethod.GET, RequestMethod.POST })
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
		String playerid = table.get("accountId");
		String ulevel = table.get("ulevel");
		String rolename = table.get("rolename");

		if (type == null || type.equals("")) {
			type = "未知";
		}
		if (type.equalsIgnoreCase("OM2_IOS")) {

			String billNo = PublicMethod.getUniqueId();
			OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, (int) Float.parseFloat(serverid), subjectId, type, playerid, billNo, String.valueOf(billNo));
			orderInfo.setRoleId(characterid);
			logger.info("=========对象存储=========");
			String Url = "http://fcgame.poppace.com:8681/lmyxLoginServer/tutuController";
			if (checkService.findCheckByStr("mengyu") != null) {
				Url = "http://114.112.86.2:8081/lmyxLoginServer/tutuController";
			}
			if (orderServcie.create(orderInfo)) {
				return RestResult.getBillSuc("1", String.valueOf(billNo), Url);
			} else {
				return RestResult.getBillSuc("0", String.valueOf(billNo), Url);
			}
		}

		return RestResult.getBillSuc("0", "", "", "", "");
	}
}
