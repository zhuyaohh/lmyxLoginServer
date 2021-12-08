package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class LastPayController {
	private static final Logger logger = LoggerFactory.getLogger(NewGNIOSPay.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;
	private static final Map<String, String> shopItem = new HashMap<String, String>();
	static {
		shopItem.put("com.xphl.zsxsj.01", "3000");
		shopItem.put("com.xphl.zsxsj.02", "9800");
		shopItem.put("com.xphl.zsxsj.03", "64800");
		shopItem.put("com.xphl.zsxsj.04", "32800");
		shopItem.put("com.xphl.zsxsj.05", "12800");
		shopItem.put("com.xphl.zsxsj.06", "6800");
		shopItem.put("com.xphl.zsxsj.07", "3000");
		shopItem.put("com.xphl.zsxsj.08", "600");
		shopItem.put("com.xphl.zsxsj.09", "19800");
		/****/
		shopItem.put("com.hai.fei.1", "3000");
		shopItem.put("com.hai.fei.2", "9800");
		shopItem.put("com.hai.fei.3", "64800");
		shopItem.put("com.hai.fei.4", "32800");
		shopItem.put("com.hai.fei.5", "19800");
		shopItem.put("com.hai.fei.6", "12800");
		shopItem.put("com.hai.fei.7", "6800");
		shopItem.put("com.hai.fei.8", "3000");
		shopItem.put("com.hai.fei.9", "600");
		/***/
		shopItem.put("com.zhanshen.qifei.1", "3000");
		shopItem.put("com.zhanshen.qifei.2", "9800");
		shopItem.put("com.zhanshen.qifei.3", "64800");
		shopItem.put("com.zhanshen.qifei.4", "32800");
		shopItem.put("com.zhanshen.qifei.5", "19800");
		shopItem.put("com.zhanshen.qifei.6", "12800");
		shopItem.put("com.zhanshen.qifei.7", "6800");
		shopItem.put("com.zhanshen.qifei.8", "3000");
		shopItem.put("com.zhanshen.qifei.9", "600");
		/**风灵幻想*/
		shopItem.put("com.fl.hx.yueka", "3000");
		shopItem.put("com.fl.hx.nianka", "9800");
		shopItem.put("com.fl.hx.6480", "64800");
		shopItem.put("com.fl.hx.3280", "32800");
		shopItem.put("com.fl.hx.1980", "19800");
		shopItem.put("com.fl.hx.1280", "12800");
		shopItem.put("com.fl.hx.680", "6800");
		shopItem.put("com.fl.hx.300", "3000");
		shopItem.put("com.fl.hx.60", "600");
		/**永恒战场*/
		shopItem.put("com.honour.ios.1", "3000");
		shopItem.put("com.honour.ios.3", "9800");
		shopItem.put("com.honour.ios.4", "64800");
		shopItem.put("com.honour.ios.5", "32800");
		shopItem.put("com.honour.ios.6", "19800");
		shopItem.put("com.honour.ios.7", "12800");
		shopItem.put("com.honour.ios.8", "6800");
		shopItem.put("com.honour.ios.9", "3000");
		shopItem.put("com.honour.ios.10", "600");
	}

	@RequestMapping(value = "zsIosPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblAndroidPay(String trade_no, String cpid, String game_seq_num, String server_seq_num,
			String amount, String user_id, String ext_info, String timestamp, String remark, String verstring,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		logger.info("================welcome to zsIosPay=================");
		logger.info("=trade_no==" + trade_no);
		logger.info("=cpid==" + cpid);
		logger.info("=game_seq_num==" + game_seq_num);
		logger.info("=server_seq_num==" + server_seq_num);
		logger.info("=amount==" + amount);
		logger.info("=user_id==" + user_id);
		logger.info("=ext_info==" + ext_info);
		logger.info("=timestamp==" + timestamp);
		logger.info("=remark==" + remark);
		logger.info("=verstring==" + verstring);

		String result = "fail";
		String GAME_SEQNUM = "1001";
		String SECRETKEY = "YX6Qf1mX";
		if (!GAME_SEQNUM.equals(game_seq_num)) {
			result = "GAME_SEQNUM is error";
			PrintWriter writer = response.getWriter();
			response.getWriter().write(result);
			writer.close();
			return;
		}
		StringBuffer sb = new StringBuffer("trade_no=");
		sb.append(trade_no + "&cpid=");
		sb.append(cpid + "&game_seq_num=");
		sb.append(game_seq_num + "&server_seq_num=");
		sb.append(server_seq_num + "&amount=");
		sb.append(amount + "&user_id=");
		sb.append(user_id + "&ext_info=");
		sb.append(ext_info + "&timestamp=");
		sb.append(timestamp + "&SecretKey=");
		sb.append(SECRETKEY);
		String sign = MD5.GetMD5Code(sb.toString());
		if (sign.equals(verstring)) {
			String[] str = ext_info.split("\\|");
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setRoleId(str[0]);
			orderInfo.setOrderId(trade_no);
			orderInfo.setServerId(Integer.parseInt(server_seq_num));
			orderInfo.setSubjectId(str[2]);
			orderInfo.setCpOrderId(str[3]);
			orderInfo.setActuralFee(amount);
			orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
			orderInfo.setType("M_IOS");
			orderInfo.setTotalFee(amount);
			orderInfo.setStatus(1);
			if (shopItem.containsKey(orderInfo.getSubjectId())&&Integer.parseInt(amount) < Integer.parseInt(shopItem.get(orderInfo.getSubjectId()))) {
				result = "price error";
			} else {
				if (orderServcie.createStatus(orderInfo)) {
					if (doPostOrderRecharge(orderInfo)) {
						result = "success";
					}
				}

			}

			PrintWriter writer = response.getWriter();
			response.getWriter().write(result);
			writer.close();
		} else {
			PrintWriter writer = response.getWriter();
			response.getWriter().write(result);
			writer.close();
		}

	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		String trade_no = "30104110011001zFjqxagt732f";
		String cpid = "1041";
		String game_seq_num = "1001";
		String server_seq_num = "1001";
		String amount = "1";
		String user_id = "1646711";
		String ext_info = "9900502|1001|com.xphl.zsxsj.03|11513022_9645";
		String timestamp = "20190115130035";
		String remark = "支付成功";
		String verstring = "274726f98baf001a9209f37f972c6903";
		String SECRETKEY = "YX6Qf1mX";
		StringBuffer sb = new StringBuffer("trade_no=");
		sb.append(trade_no + "&cpid=");
		sb.append(cpid + "&game_seq_num=");
		sb.append(game_seq_num + "&server_seq_num=");
		sb.append(server_seq_num + "&amount=");
		sb.append(amount + "&user_id=");
		sb.append(user_id + "&ext_info=");
		sb.append(ext_info + "&timestamp=");
		sb.append(timestamp + "&SecretKey=");
		sb.append(SECRETKEY);
		String sign = MD5.GetMD5Code(sb.toString());
		System.out.println(sign);

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
