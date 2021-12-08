package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
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

@RestController
@RequestMapping("/")
public class QQController {
	private static final Logger logger = LoggerFactory.getLogger(QQController.class);

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "yybBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void billNo(String a, String b, String c, String d, String e, String f, String g, String h, String i, String j, String k, String l, String m, String n, String o, String p, HttpServletResponse response) throws ParseException, IOException {
		String str = "errorSign";

		try {
			e = URLDecoder.decode(e, "UTF-8");
			j = URLDecoder.decode(j, "UTF-8");
			n = URLDecoder.decode(n, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String appKey = "974eec315b1d73457f2fa7eaca2b8a8e";
		StringBuffer tempSign = new StringBuffer("a=" + a);
		tempSign.append("&b=" + b);
		tempSign.append("&c=" + c);
		tempSign.append("&d=" + d);
		tempSign.append("&e=" + e);
		tempSign.append("&f=" + f);
		tempSign.append("&g=" + g);
		tempSign.append("&h=" + h);
		tempSign.append("&i=" + i);
		tempSign.append("&j=" + j);
		tempSign.append("&k=" + k);
		tempSign.append("&l=" + l);
		tempSign.append("&m=" + m);
		tempSign.append("&n=" + n);
		tempSign.append("&appkey=" + appKey);
		String sign = o;
		logger.info("========混淆前sign====" + tempSign.toString());
		logger.info("========sign====" + sign);
		logger.info("========o====" + o);
		logger.info("========p====" + p);
		logger.info("========进入sign===============");
		if (sign.equals(o)) {
			logger.info("========signOK=============");
			OrderInfo orderInfo = new OrderInfo();
			orderInfo.setServerId(Integer.parseInt(c));
			orderInfo.setRoleId(d);

			orderInfo.setRoleName(e);
			orderInfo.setDnyOrderId(f);
			orderInfo.setStatus(Integer.parseInt(g));
			orderInfo.setType(h);
			orderInfo.setTotalFee(i);
			orderInfo.setOrderId(f);
			orderInfo.setRemak(j);
			orderInfo.setCpOrderId(k);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			Date date = sdf.parse(l);
			Date finishDate = sdf.parse(m);
			orderInfo.setDate(new Timestamp(date.getTime()));
			orderInfo.setFinishDate(new Timestamp(finishDate.getTime()));
			orderInfo.setSubjectId(n);
			logger.info("========start db===============");
			if (orderServcie.create(orderInfo)) {
				if (g.equalsIgnoreCase("1") || g.equalsIgnoreCase("201")) {
					int id = Integer.parseInt(c);
					ServerInfo serverInfo = serverService.getServerInfo(id);
					if (serverInfo != null) {
						logger.info("成功获取服务器地址==============================");
						String url = serverInfo.getUrl();
						StringBuffer args = new StringBuffer("c=1004");
						args.append("&playerId=" + d);
						args.append("&shopId=" + k);
						args.append("&order=" + f);
						args.append("&price=" + i);
						args.append("&source=" + h);
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
									orderInfo.setStatus(-99);
									orderServcie.update(orderInfo);
									str = "success";
								}
							}
						} catch (Exception ex) {

						}

					}

				}
			}
		} else {
			logger.info("========已经有此订单===============");
			str = "success";
		}

		response.getWriter().write(str);
	}

}
