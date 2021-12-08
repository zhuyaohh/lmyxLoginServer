package com.cvovo.gamemanager.game.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.RandomRankDao;
import com.cvovo.gamemanager.game.persist.dao.TeamInfoDao;
import com.cvovo.gamemanager.game.persist.entity.BattleResult;
import com.cvovo.gamemanager.game.persist.entity.RandomRank;
import com.cvovo.gamemanager.game.persist.entity.TeamInfo;
import com.cvovo.gamemanager.game.persist.entity.TeamInfoTemp;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class TeamService {
	@Autowired
	private RandomRankDao randomRankDao;

	@Autowired
	private DateQuest dq;

	@Autowired
	private RandomRankService randomRankService;

	@Autowired
	private RandomRankConfigService randomRankConfig;
	@Autowired
	private TeamInfoDao teamInfoDao;

	public List<TeamInfo> newTeams() {
		if (this.addDate() < 0) {
			return null;
		}
		List<TeamInfo> returnList = teamInfoDao.findByDateAndStatus(this.addDate() + 1, 0);
		if (returnList != null && returnList.size() > 0) {
			return returnList;
		} else {
			returnList = new ArrayList<TeamInfo>();
		}

		List<RandomRank> list = randomRankService.getTop64ByRank();
		HashMap<Integer, Integer> hashMap = getNewTeamPosition(list.size());
		for (java.util.Map.Entry<Integer, Integer> arg : hashMap.entrySet()) {
			TeamInfo teamInfo = new TeamInfo(arg.getKey(), arg.getValue(), this.addDate() + 1);
			teamInfoDao.save(teamInfo);
			returnList.add(teamInfo);
		}
		return returnList;
	}

	public void deleteAll() {
		teamInfoDao.deleteAll();
	}

	public void updateAllStatus() {
		teamInfoDao.updateTeamInfo();
	}

	public List<TeamInfo> updateTeam() {
		int d = addDate();
		List<TeamInfo> returnList = new ArrayList<TeamInfo>();
		List<TeamInfo> list = teamInfoDao.findByDateAndStatus(d, 0);
		List<TeamInfo> tempList = teamInfoDao.findByDateAndStatus(d + 1, 0);
		if (tempList != null && tempList.size() > 0) {
			return tempList;
		}
		for (int i = 0; i < list.size(); i = i + 2) {
			TeamInfo attInfo = list.get(i);
			if (i + 1 < list.size()) {
				TeamInfo defInfo = list.get(i + 1);
				TeamInfo teamInfo = new TeamInfo(getWinner(attInfo), getWinner(defInfo), d + 1);
				teamInfoDao.save(teamInfo);
				returnList.add(teamInfo);
			} else {
				TeamInfo teamInfo = new TeamInfo(getWinner(attInfo), getWinner(attInfo), d + 1);
				teamInfoDao.save(teamInfo);
				dq.stopTast();
			}

		}
		return returnList;
	}

	public int getWinner(TeamInfo info) {
		String br = info.getBattleResult();
		if (br == null || br.equals("")) {
			return info.getAttackId();
		}
		String[] resultList = br.split(",");
		int j = 0;
		for (String str : resultList) {
			String[] tempStr = str.split(":");
			String winResult = tempStr[0];
			if (winResult != null && winResult.equals("1")) {
				j++;
			}
		}
		if (j >= 3) {
			return info.getAttackId();
		} else {
			return info.getDefenseId();
		}
	}

	public List<TeamInfo> getTeamData(int date) {
		return teamInfoDao.findByDateAndStatus(date, 0);

	}

	public RestResult save(TeamInfo teamInfo) {
		teamInfoDao.save(teamInfo);
		return RestResult.groupBuy();

	}

	public RestResult getTeamDataByDate(int date) {
		return getTeamData(this.getTeamData(date));
	}

	public boolean timePK() {
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.HOUR_OF_DAY, 15); // 控制时
		ca.set(Calendar.SECOND, 0); // 控制时
		ca.set(Calendar.MINUTE, 0); // 控制时

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(ca.getTime());
		Date now = new Date();
		if (now.getTime() - ca.getTime().getTime() >= 0) {
			return true;
		}
		return false;

	}

	public boolean startTime() {
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.HOUR_OF_DAY, 9); // 控制时
		ca.set(Calendar.SECOND, 0); // 控制时
		ca.set(Calendar.MINUTE, 0); // 控制时

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(ca.getTime());
		System.out.println(str);
		Date now = new Date();
		if (now.getTime() - ca.getTime().getTime() >= 0) {
			return true;
		}
		return false;

	}

	public RestResult getTeamData(List<TeamInfo> data) {
		List<TeamInfoTemp> list = new ArrayList<TeamInfoTemp>();
		for (TeamInfo ti : data) {
			TeamInfoTemp tit = new TeamInfoTemp();
			tit.setAttackId(ti.getAttackId());
			tit.setDefenseId(ti.getDefenseId());
			tit.setBetFailCount(ti.getBetFailCount());
			tit.setBetWinCount(ti.getBetWinCount());
			String str = ti.getBattleResult();
			if (str != null) {
				String[] result = str.split(",");
				for (String win : result) {
					String[] winObject = win.split(":");
					if (winObject.length == 2) {
						BattleResult br = new BattleResult(winObject[0], winObject[1]);
						tit.getBattleResult().add(br);
					}

				}
			}
			list.add(tit);
		}
		return RestResult.successTeamInfoTemp(list);
	}

	public HashMap<Integer, Integer> getNewTeamPosition(int size) {
		HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
		LinkedList<Integer> list = new LinkedList<Integer>();
		List<Integer> arrayList = new ArrayList<Integer>();
		for (int i = 0; i < size; i++)
			list.add(i + 1);
		Collections.shuffle(list);
		for (int i : list) {
			arrayList.add(i);
		}
		for (int i = 0; i < arrayList.size(); i = i + 2) {
			if (i >= (arrayList.size() - 1)) {
				hashMap.put(arrayList.get(i) - 1, arrayList.get(i) - 1);
			} else {
				hashMap.put(arrayList.get(i) - 1, arrayList.get(i + 1) - 1);
			}
		}
		return hashMap;
	}

	public int addDate() {
		if (randomRankConfig.getRandomRankConfig() == null || randomRankConfig.getRandomRankConfig().getStatus() != 1) {
			return 1000;
		}
		String dateString = randomRankConfig.getRandomRankConfig().getLastLoginTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.get(Calendar.DAY_OF_YEAR);

		Calendar newCalendar = Calendar.getInstance();
		newCalendar.get(Calendar.DAY_OF_YEAR);
		int to = newCalendar.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR) - randomRankConfig.getRandomRankConfig().getIntervalDay();

		return to;
	}
}
