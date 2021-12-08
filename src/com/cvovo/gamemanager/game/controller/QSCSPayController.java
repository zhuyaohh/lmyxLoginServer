package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class QSCSPayController {

	private static final Logger logger = LoggerFactory.getLogger(QSCSPayController.class);

	@Autowired
	private ServerService serverService;
	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "qscsPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getPay(String nt_data, String nt_data_json, String sign, String md5Sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		// String nt_data = request.getParameter("nt_data");
		// String nt_data_json = request.getParameter("nt_data_json");
		// String sign = request.getParameter("sign");
		// String md5Sign = request.getParameter("md5Sign");
		logger.info(String.format("getPay %s,%s,%s,%s", nt_data, nt_data_json, sign, md5Sign));
		String newSign = MD5.GetMD5Code(nt_data + sign + "1f3f3d240d274705b1c0ebe20151f20d");
		if (!md5Sign.equals(newSign)) {
			response.getWriter().write("sign Fail");
			return;
		}
		JSONObject data = JSONObject.parseObject(nt_data_json);
		boolean is_test = data.getBooleanValue("is_test");
		String order_no = data.getString("order_no");
		String cpOrderId = data.getString("extras_params");
		double price = data.getDoubleValue("amount");
		OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
		if (orderInfo == null) {
			response.getWriter().write("Faill orderInfo = null");
			return;
		}
		switch (orderInfo.getStatus()) {
		case 3:
			response.getWriter().write("SUCCESS");
			break;
		case 2:
			if (doPostOrderRecharge(orderInfo)) {
				response.getWriter().write("SUCCESS");
			} else {
				response.getWriter().write("发钻失败");
			}
			break;
		case 1:
			if (Double.parseDouble(orderInfo.getActuralFee()) != price * 100) {
				response.getWriter().write("商品与支付金额不符");
			} else {
				orderInfo.setStatus(2);
				orderInfo.setDnyOrderId(order_no);
				orderInfo.setPriceUnit("CNY");
				orderServcie.update(orderInfo);
				if (doPostOrderRecharge(orderInfo)) {
					response.getWriter().write("SUCCESS");
				} else {
					response.getWriter().write("发钻失败");
				}
			}
			break;
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
			args.append("&priceUnit=CNY");
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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getUserId(), orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CN", Double.parseDouble(orderInfo.getTotalFee()) * 10, "" + orderInfo.getServerId(), "QSCS", orderInfo.getSubjectId(), "202EFB9E681047A8961F9771093EAD11");
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
