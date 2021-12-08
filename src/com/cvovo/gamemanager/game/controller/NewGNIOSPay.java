package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

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

@RestController
@RequestMapping("/")
public class NewGNIOSPay {
	private static final Logger logger = LoggerFactory.getLogger(NewGNIOSPay.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "guoNAndroidPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblAndroidPay(String out_trade_no, String price, String pay_status, String extend, String signType,
			String sign, HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("================welcome to guoNAndroidPay=================");
		logger.info("=pay_status==" + pay_status);
		logger.info("=price==" + price);
		logger.info("=out_trade_no==" + out_trade_no);
		logger.info("=extend==" + extend);
		logger.info("=signType==" + signType);
		logger.info("=sign==" + sign);
		String gamekey = "kssbALs34t6";
		String result = "fail";
		String trade_no = out_trade_no;
		String priceM = price;
		String pay_Status = pay_status;
		String extendM = extend;
		String signTypeM = signType;
		String signM = sign;
		StringBuffer ff = new StringBuffer();
		ff.append(trade_no);
		ff.append(priceM);
		ff.append(pay_Status);
		ff.append(extendM);
		ff.append(gamekey);
		String signY = MD5.GetMD5Code(ff.toString());
		logger.info("=signY==" + signY);
		if (signY.equals(signM)) {
			String[] str = extendM.split(",");
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setRoleId(str[0]);
			orderInfo.setOrderId(out_trade_no);
			orderInfo.setServerId(Integer.parseInt(str[1]));
			orderInfo.setSubjectId(str[2]);
			orderInfo.setCpOrderId(str[3]);
			orderInfo.setActuralFee(priceM);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setType("M1_ANDROID");
			orderInfo.setTotalFee(priceM);
			orderInfo.setStatus(1);
			if (orderServcie.createStatus(orderInfo)) {
				if (doPostOrderRecharge(orderInfo)) {
					result = "success";
				}
			}

			PrintWriter writer = response.getWriter();
			response.getWriter().write(result);
			writer.close();
		}
	}
	@RequestMapping(value = "guoNIosPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblPay(String out_trade_no, String price, String pay_status, String extend, String signType,
			String sign, HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("================welcome to getklblPay=================");
		logger.info("=pay_status==" + pay_status);
		logger.info("=price==" + price);
		logger.info("=out_trade_no==" + out_trade_no);
		logger.info("=extend==" + extend);
		logger.info("=signType==" + signType);
		logger.info("=sign==" + sign);
		String gamekey = "Ksxs4353qz";
		String result = "fail";
		String trade_no = out_trade_no;
		String priceM = price;
		String pay_Status = pay_status;
		String extendM = extend;
		String signTypeM = signType;
		String signM = sign;
		StringBuffer ff = new StringBuffer();
		ff.append(trade_no);
		ff.append(priceM);
		ff.append(pay_Status);
		ff.append(extendM);
		ff.append(gamekey);
		String signY = MD5.GetMD5Code(ff.toString());
		logger.info("=signY==" + signY);
		if (signY.equals(signM)) {
			String[] str = extendM.split("\\|");
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setRoleId(str[0]);
			orderInfo.setOrderId(out_trade_no);
			orderInfo.setServerId(Integer.parseInt(str[1]));
			orderInfo.setSubjectId(str[2]);
			orderInfo.setCpOrderId(str[3]);
			orderInfo.setActuralFee(priceM);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setType("IOS");
			orderInfo.setTotalFee(priceM);
			orderInfo.setStatus(1);
			if (orderServcie.createStatus(orderInfo)) {
				if (doPostOrderRecharge(orderInfo)) {
					result = "success";
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
			args.append("&priceUnit=CN");
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
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRemak(),
								orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CN",
								Double.parseDouble(orderInfo.getTotalFee()), "ZS", "GN", "GAME101",
								"C33DA9EEDA0D4A448ACEB264779CF503");
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
