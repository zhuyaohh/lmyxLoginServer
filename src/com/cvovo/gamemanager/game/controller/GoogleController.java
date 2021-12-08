package com.cvovo.gamemanager.game.controller;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.test.GoogleHMVerify;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class GoogleController {
	private static final Logger logger = LoggerFactory.getLogger(GoogleController.class);

	@Autowired
	private GoogleHMVerify googleHMVerify;

	@RequestMapping(value = "googleReceipt", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult GOOGLEVerify(String receipt, String serverId, String roleId, String type,String price) throws Exception {
		logger.info("======GOOGLEVerify================");
		logger.info("======serverId================" + serverId);
		logger.info("======roleId================" + roleId);

		JSONObject job = JSONObject.fromObject(receipt);

		String packageName = job.getString("packageName");
		String productId = job.getString("productId");
		String purchaseToken = job.getString("purchaseToken");
		String puchaseTime = job.getString("purchaseTime");
		googleHMVerify.getInfoFromGooglePlayServer(puchaseTime, packageName, productId, purchaseToken, serverId, roleId, type);
		return RestResult.receipt(receipt);
	}
}
