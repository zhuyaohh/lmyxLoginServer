package com.cvovo.gamemanager.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.cvovo.gamemanager.game.persist.entity.CheckVersion;
import com.cvovo.gamemanager.game.service.CheckService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class CheckController {
	@Autowired
	private CheckService checkService;

	@RequestMapping(value = "updateCheck", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult saveCheck(String check) {
		CheckVersion check1 = checkService.findCheckByStr(check);
		if (check1 != null) {
			check1.setCheckStr(check);
			checkService.saveCheck(check1);
		} else {
			CheckVersion check2 = new CheckVersion(check);
			checkService.saveCheck(check2);
		}
		return RestResult.result(0);
	}

	@RequestMapping(value = "deleteCheck", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult deleteCheck(String check) {
		checkService.deleteCheck(check);
		return RestResult.result(0);
	}
}
