package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.cvovo.gamemanager.game.persist.entity.ShopItem;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.game.service.ShopItemService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class HanKongPayController {
	private static final Logger logger = LoggerFactory.getLogger(HanKongPayController.class);

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private ServerService serverService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ShopItemService shopItemService;

	@RequestMapping(value = "koreaPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getGroupInfoByType(String order_no, String user_id, String server_no, String money, String gold, String time, String sign, String pay_product_type, String callback_info, HttpServletResponse response) throws IOException {
		String key = "cgaTelR9heFYvDYTNOlUORQtKFNPl25o";
		String result = "-99";
		logger.info("================ welcome koreaPay==================");

		logger.info("==============order_no==================" + order_no);
		logger.info("==============user_id==================" + user_id);
		logger.info("==============server_no==================" + server_no);
		logger.info("==============money==================" + money);
		logger.info("==============gold==================" + gold);
		logger.info("==============time==================" + time);
		logger.info("==============sign==================" + sign);
		logger.info("==============pay_product_type==================" + pay_product_type);
		logger.info("==============callback_info==================" + callback_info);
		// $sign = md5($order_no + $user_id + $money + $gold + $time + 密钥)
		StringBuffer sb = new StringBuffer(order_no);
		sb.append(user_id);
		sb.append(money);
		sb.append(gold);
		sb.append(time);
		sb.append(callback_info);
		sb.append(key);
		String newSign = MD5.GetMD5Code(sb.toString());
		logger.info("==============newSign==================" + newSign);
		if (newSign.toString().equals(sign)) {
			OrderInfo oi = new OrderInfo();
			String[] str = callback_info.split("_");
			oi.setRemak(user_id);
			oi.setRoleId(str[0]);
			oi.setActuralFee(order_no);
			oi.setDnyOrderId(order_no);
			oi.setOrderId(order_no);
			oi.setPriceUnit("HB");
			oi.setServerId(Integer.parseInt(server_no));
			oi.setSubjectId(pay_product_type);
			oi.setStatus(1);
			oi.setTotalFee(money);
			oi.setActuralFee(gold);
			oi.setType(str[1]);
			Timestamp ts = new Timestamp(Long.parseLong(time) * 1000);
			oi.setDate(ts);
			if (orderServcie.createStatus(oi)) {
				oi.setStatus(2);
				if (doPostOrderRecharge(oi)) {
					result = "1";
				} else {
					result = "-3";
				}
			} else {
				result = "2";
			}
		} else {
			result = "-2";
		}

		PrintWriter writer = response.getWriter();
		response.getWriter().write(result);
		writer.close();
	}

	public boolean doPostOrderRecharge(OrderInfo orderInfo) {
		logger.info("======koreaPay================");
		int id = orderInfo.getServerId();
		ServerInfo serverInfo = serverService.getServerInfo(id);
		/**
		 * StringBuffer args = new StringBuffer("c=4001"); args.append("&playerId=" + roleId); if (goodsId != null && !goodsId.equals("")) { args.append("&shopId=" + goodsId); } else { args.append("&shopId=" + "0000"); } args.append("&order=" + orderId); args.append("&price=" + price); args.append("&source=" + type); args.append("&priceUnit=" + priceUnit); args.append("&goodsCount=" + goodsCount);
		 * 
		 * **/
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			int goodsCount = 0;
			ShopItem item = shopItemService.checkPriceAndShopItem(orderInfo.getSubjectId());
			if (item != null) {
				goodsCount = Integer.parseInt(item.getPrice());
			}
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=5001");
			args.append("&playerId=" + orderInfo.getRoleId());
			args.append("&shopId=" + orderInfo.getSubjectId());
			args.append("&order=" + orderInfo.getOrderId());
			args.append("&price=" + orderInfo.getTotalFee());
			args.append("&source=" + orderInfo.getType());
			args.append("&priceUnit=" + "KRW");
			args.append("&goodsCount=" + goodsCount);
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

	public static void main(String[] args) {
		// 1495622617

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = new Timestamp(Long.parseLong("1495622617") * 1000);
		System.out.println(ts);
		// Timestamp ts = new Timestamp(Long.parseLong(timerNow));
		// System.out.println(ts.toString());
	}
}
