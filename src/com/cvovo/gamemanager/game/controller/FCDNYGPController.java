package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;
import com.cvovo.gamemanager.test.DnyGoogleHMVerify;
import com.cvovo.gamemanager.util.RestResult;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/")
public class FCDNYGPController {
	private static final Logger logger = LoggerFactory.getLogger(FCDNYGPController.class);

	@Autowired
	private DnyGoogleHMVerify DnyGoogleHMVerify;

	@RequestMapping(value = "dnyGoogleReceipt", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult GOOGLEVerify(String receipt, String serverId, String roleId, String type) throws Exception {
		logger.info("======FCDNYGP === GOOGLEVerify================");
		logger.info("======serverId================" + serverId);
		logger.info("======roleId================" + roleId);

		JSONObject job = JSONObject.fromObject(receipt);

		String packageName = job.getString("packageName");
		String productId = job.getString("productId");
		String purchaseToken = job.getString("purchaseToken");
		String puchaseTime = job.getString("purchaseTime");
		IosOrderInfo orderInfo = DnyGoogleHMVerify.getInfoFromGooglePlayServer(puchaseTime, packageName, productId,
				purchaseToken, serverId, roleId, type);
		if (!orderInfo.getStatus().equals("3")) {
			return RestResult.payError("1");
		}
		 return RestResult.payError("-1");
	}
}
