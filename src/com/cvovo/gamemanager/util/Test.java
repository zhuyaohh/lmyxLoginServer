package com.cvovo.gamemanager.util;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvovo.gamemanager.game.controller.MyHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		StringBuffer sb = new StringBuffer();
//		// sb.append("{\"packageName\":\"com.icehp.my.mw\",\"productId\":\"my02\",\"purchaseTime\":1473751758494,\"purchaseState\":0,\"purchaseToken\":\"ojjjdkdknlafdpmahmbhkdif.AO-J1OzDY_LMW7JksmC_Zh_HqyQFqhVdKOfCaPNi5_jHKzrfAhZ4WCVFLAthrqteJkQxw9LsbZS5zYoUnyltqY1TAquZXNFWibRhBVCbCAW7aw0kXJ4XFzg\"}");
//		sb.append("{\"product\":[{\"appid\":\"OA00705901\",\"bp_info\":\"\",\"charge_amount\":1270,\"detail_pname\":\"가격\",\"log_time\":\"20160919210555\",\"product_id\":\"0910060692\",\"tid\":\"\"}],\"message\":\"정상검증완료.\",\"detail\":\"0000\",\"count\":1,\"status\":0}");
//
//		JSONObject job = JSONObject.fromObject(sb.toString());
//		String packageName = job.getString("packageName");
//		String productId = job.getString("productId");
//		String purchaseToken = job.getString("purchaseToken");
//		String puchaseTime = job.getString("purchaseTime");
//		logger.info("======packageName================" + packageName);
//		logger.info("======productId================" + productId);
//		logger.info("======purchaseToken================" + purchaseToken);
//		ObjectMapper ObjMapper = new ObjectMapper();
//		JsonNode rootNode = null;
//		try {
//			rootNode = ObjMapper.readTree(sb.toString());
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String nCode = rootNode.findValue("status").asText();
//		System.out.println(nCode);
		MyHttpClient.sendPost("http://localhost:8081/loginserver/chuangKuPay", "{\"gameId\":\"1501\",\"uid\":\"go#106512452861510577629\",\"productId\":\"7\",\"create_time\":1537856786000,\"orderId\":\"kf18092500004617\",\"sign\":\"3a08a3a9e96be1f064d9a64503f912ab\",\"attach\":\"1:100107\",\"prices\":\"4000\",\"productName\":\"月卡\",\"channelId\":\"151\",\"subAppID\":\"1\"}");
	}
}
