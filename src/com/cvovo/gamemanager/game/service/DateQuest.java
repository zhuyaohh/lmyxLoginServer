package com.cvovo.gamemanager.game.service;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DateQuest {

	private static final Logger logger = LoggerFactory.getLogger(DateQuest.class);
	@Autowired
	private TeamService teamService;
	@Autowired
	private NewTeamService newTeamService;
	@Autowired
	private RandomRankConfigService randomRankConfigSercive;

	private TimerTask tt;
	private TimerTask newTt;

	public void stopTast() {
		if (tt != null) {
			logger.info("stop=================");
			// tt.cancel();
			// teamService.deleteAll();
			// randomRankConfigSercive.stopRandomRankConfig();
		}
	}

	public void timer() {
		// logger.info("timer===定时器启动==============");
		// if (randomRankConfigSercive.getRandomRankConfig() != null && randomRankConfigSercive.getRandomRankConfig().getStatus() == 1) {
		// int t = teamService.addDate();
		// logger.info("=====时间节点===========" + t);
		// Calendar calendar = Calendar.getInstance();
		// calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
		// calendar.set(Calendar.MINUTE, 0); // 控制分
		// calendar.set(Calendar.SECOND, 0); // 控制秒
		// calendar.set(Calendar.MILLISECOND, 0);
		// calendar.roll(Calendar.DAY_OF_YEAR, true);
		//
		// Timer timer = new Timer();
		// tt = new TimerTask() {
		// public void run() {
		// int timeDate = teamService.addDate();
		// if (timeDate >= 7) {
		// logger.info("=======timeDate >= 7=====" + timeDate);
		// return;
		//
		// }
		// if (timeDate == 0) {
		// logger.info("=======timeDate == 0=====" + timeDate);
		// teamService.deleteAll();
		// teamService.newTeams();
		//
		// } else if (timeDate >= 1) {
		// logger.info("======timeDate >= 1====" + timeDate);
		// teamService.updateTeam();
		// }
		//
		// }
		//
		// };
		// timer.schedule(tt, calendar.getTime(), 1000 * 60 * 60 * 24);
		// // timer.schedule(tt, 0, 30);
		// }

	}

	public void todoNewTeamInfo() {
		int timeDate = newTeamService.addDate();
		if (timeDate >= 7) {
			logger.info("=======timeDate >= 7=====" + timeDate);
			return;

		}
		if (timeDate == 0) {
			logger.info("=======timeDate == 0=====" + timeDate);

			for (int i = 1; i < 4; i++) {
				newTeamService.newTeams(String.valueOf(i));
			}

		} else if (timeDate >= 1) {
			logger.info("======timeDate >====" + timeDate);
			for (int i = 1; i < 4; i++) {
				newTeamService.updateTeam(String.valueOf(i));
			}
		}
	}

	public void newTimer() {
		logger.info("timer===定时器启动==============");
		if (randomRankConfigSercive.getRandomRankConfig() != null && randomRankConfigSercive.getRandomRankConfig().getStatus() == 1) {
			logger.info("========GO GO GO========");
			int t = newTeamService.addDate();
			newTeamService.deleteAllStatus();

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
			calendar.set(Calendar.MINUTE, 0); // 控制分
			calendar.set(Calendar.SECOND, 0); // 控制秒
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.roll(Calendar.DAY_OF_YEAR, true);
			this.todoNewTeamInfo();
			Timer timer = new Timer();
			newTt = new TimerTask() {
				public void run() {
					int timeDate = newTeamService.addDate();
					logger.info("=====时间节点===========" + timeDate);
					if (timeDate <= -1 || timeDate >= 7) {
						newTeamService.updateAllStatus();
						return;
					}

					if (timeDate == 0) {
						logger.info("=======timeDate == 0=====" + timeDate);
						for (int i = 1; i < 4; i++) {
							newTeamService.newTeams(String.valueOf(i));
						}

					} else if (timeDate >= 1) {
						logger.info("======timeDate >====" + timeDate);
						for (int i = 1; i < 4; i++) {
							newTeamService.updateTeam(String.valueOf(i));
						}
					}
				}

			};
			timer.schedule(newTt, calendar.getTime(), 1000 * 60 * 60 * 24);
		}

	}

	public void testG() {
		int timeDate = newTeamService.addDate();
		if (timeDate == 0) {
			for (int i = 1; i < 4; i++) {
				newTeamService.newTeams(String.valueOf(i));
			}
		} else if (timeDate >= 1) {
			logger.info("======timeDate >= 1====" + timeDate);
			for (int i = 1; i < 4; i++) {
				newTeamService.updateTeam(String.valueOf(i));
			}
		}

	}
}
