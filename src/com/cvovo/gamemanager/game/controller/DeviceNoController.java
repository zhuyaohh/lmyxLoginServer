package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.service.DeviceNoService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class DeviceNoController {
	private static final Logger logger = LoggerFactory.getLogger(DeviceNoController.class);
	@Autowired
	private DeviceNoService deveiceNoService;

	@RequestMapping(value = "stopAccountId", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult prohibitDeveiceByAccountId(String accountId, String key) {
		logger.info("stopAccountId======================="+accountId);
		if (accountId != null && !accountId.equals("")) {
			return deveiceNoService.protectedDeviceByAccountId(accountId);
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "stopDeveiceNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult prohibitDeveiceByDeveiceNo(String deveiceNo, String key) {
		logger.info("stopDeveiceNo======================="+deveiceNo);
		if (deveiceNo != null && !deveiceNo.equals("")) {
			return deveiceNoService.protectedDeviceByDeviceNo(deveiceNo);
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "openAccountId", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult openDeveiceByAccountId(String accountId, String key) {
		if (accountId != null && !accountId.equals("")) {
			return deveiceNoService.openDeviceByAccountId(accountId);
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "openDeviceNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult opentDeveiceByDeveiceNo(String deveiceNo, String key) {
		if (deveiceNo != null && !deveiceNo.equals("")) {
			return deveiceNoService.openDeviceByDeviceNo(deveiceNo);
		}
		return RestResult.result(1);
	}
}
