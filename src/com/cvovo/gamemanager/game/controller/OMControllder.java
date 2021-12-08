package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

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

@RestController
@RequestMapping("/")
public class OMControllder {
	private static final Logger logger = LoggerFactory.getLogger(OMControllder.class);

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "omBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getOrderr(String gameId, String serverId, String orderId, String userName, String diamond, String roleId, String productId, String price, String paytype, String sign, HttpServletResponse response) throws IOException {
		String result = "-1";
		logger.info("================welcome to omBillNo=================");
		logger.info("=gameId==" + gameId);
		logger.info("=serverId==" + serverId);
		logger.info("=orderId==" + orderId);
		logger.info("=userName==" + userName);
		logger.info("=diamond==" + diamond);
		logger.info("=roleId==" + roleId);
		logger.info("=productId==" + productId);
		logger.info("=pirce==" + price);
		logger.info("=payType==" + paytype);
		logger.info("=sign==" + sign);
		String key = "w9i1e211l042b4qcjbax9hmyx3xxs6n3";
		StringBuffer sb = new StringBuffer(gameId);
		sb.append(serverId);
		sb.append(orderId);
		sb.append(userName);
		sb.append(diamond);
		sb.append(roleId);
		sb.append(productId);
		sb.append(price);
		sb.append(paytype);
		sb.append(key);
		logger.info("=sb==" + sb.toString());
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("=sign1==" + sign1);
		if (sign1.equals(sign)) {
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setRemak(userName);
			orderInfo.setRoleId(roleId);
			orderInfo.setOrderId(orderId);
			orderInfo.setServerId(Integer.parseInt(serverId));
			orderInfo.setSubjectId(productId);
			orderInfo.setActuralFee(diamond);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setType(paytype);
			orderInfo.setTotalFee(price);
			orderInfo.setStatus(1);
			if (orderServcie.createStatus(orderInfo)) {
				if (doPostOrderRecharge(orderInfo)) {
					result = "1";
				}
			}

			PrintWriter writer = response.getWriter();
			response.getWriter().write(result);
			writer.close();
		}

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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRemak(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "US", Double.parseDouble(orderInfo.getTotalFee()) * 65, "FC", "GS", "GAME101", "B707307CCF6046F487A7F9B95BA26A91");
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
}
