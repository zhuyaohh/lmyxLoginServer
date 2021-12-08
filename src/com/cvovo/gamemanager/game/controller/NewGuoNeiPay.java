package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import com.cvovo.gamemanager.util.IOSDesUtil;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.TestDom4j;

@RestController
@RequestMapping("/")
public class NewGuoNeiPay {
	private static final Logger logger = LoggerFactory.getLogger(NewGuoNeiPay.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "klblPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblPay(String nt_data, String sign, String md5Sign, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.info("========klblPay=======");
		String callbackKey = "46539374597138429217802120338452";
		String Md5Key = "8bz3ukv6ffiz3mq22xku213j2rgrtg81";
		String resultReturn = "SignError";
		logger.info("=nt_data=" + nt_data);
		logger.info("&sign=" + sign);
		logger.info("&md5Sign=" + md5Sign);

		try {

			logger.info("========" + nt_data + "=======");
			logger.info("========" + sign + "=======");
			logger.info("========" + md5Sign + "=======");
			StringBuffer sb = new StringBuffer(nt_data);
			sb.append(sign);
			sb.append(Md5Key);
			String md5Go = MD5.GetMD5Code(sb.toString());
			if (md5Go.equals(md5Sign)) {
				String xml = IOSDesUtil.decode(nt_data, callbackKey);
				logger.info("==XML=====" + xml + "=======");
				Map<?, ?> map = TestDom4j.readStringXmlOut(xml);
				String[] strExt = map.get("extras_params").toString().split(",");
				String serverId = strExt[0];
				String roleId = strExt[1];
				String subjectId = strExt[2];
				String orderInfoId = map.get("game_order").toString();
				OrderInfo oi = orderServcie.getOrderByOrderId(orderInfoId);
				if (oi != null) {
					String payTime = map.get("pay_time").toString();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Long payDate = sdf.parse(payTime).getTime();
					oi.setCpOrderId(map.get("order_no").toString());
					oi.setRemak(map.get("channel").toString());
					oi.setUserId(map.get("channel_uid").toString());
					oi.setFinishDate(new Timestamp(payDate));
					oi.setActuralFee(map.get("amount").toString());
					oi.setTotalFee(map.get("amount").toString());
					oi.setStatus(Integer.parseInt(map.get("status").toString()));
					oi.setRoleId(roleId);
					oi.setServerId(Integer.parseInt(serverId));
					oi.setSubjectId(subjectId);
					orderServcie.createStatusByGuoN(oi);
					if (doPostOrderRecharge(oi)) {
						resultReturn = "SUCCESS";
					} else {
						resultReturn = "sendPostFail";
					}
				} else {
					resultReturn = "Illegal order";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("======resultReturn=========" + resultReturn);
		PrintWriter writer = response.getWriter();
		writer.write(resultReturn);
		writer.close();
	}

	public boolean doPostOrderRecharge(OrderInfo orderInfo) {
		logger.info("======doPostIOSRecharge================");
		int id = orderInfo.getServerId();
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=1004");
			args.append("&playerId=" + orderInfo.getRoleId());
			args.append("&shopId=" + orderInfo.getSubjectId());
			args.append("&order=" + orderInfo.getOrderId());
			args.append("&price=" + orderInfo.getTotalFee());
			args.append("&priceUnit=RMB");
			args.append("&source=" + orderInfo.getType());
			args.append("&goodsCount=1");
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			logger.info("http=========" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("http============" + result);
			org.json.JSONObject jsonObject;
			try {
				jsonObject = new org.json.JSONObject(result);
				if (jsonObject.has("errorId")) {
					String errorId = jsonObject.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						YinNiPayController.talkingDataCharge("android", orderInfo.getUserId(), orderInfo.getOrderId(),
								Double.parseDouble(orderInfo.getTotalFee()), "RMB",
								Double.parseDouble(orderInfo.getTotalFee()), "LKLL", "FC", "GAME101",
								"C33DA9EEDA0D4A448ACEB264779CF503");
						if (orderInfo.getStatus() != 3) {
							orderInfo.setStatus(3);
							orderServcie.update(orderInfo);
						}
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}
}
