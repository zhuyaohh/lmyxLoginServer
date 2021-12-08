package com.cvovo.gamemanager.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartupService {

	@Autowired
	public void startup(DateQuest da) {
		// da.timer();
		da.newTimer();
	}

}
