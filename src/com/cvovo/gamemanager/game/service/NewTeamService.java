package com.cvovo.gamemanager.game.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.NewRandomRankDao;
import com.cvovo.gamemanager.game.persist.dao.NewTeamInfoDao;
import com.cvovo.gamemanager.game.persist.entity.BattleResult;
import com.cvovo.gamemanager.game.persist.entity.NewRandomRank;
import com.cvovo.gamemanager.game.persist.entity.NewTeamInfo;
import com.cvovo.gamemanager.game.persist.entity.TeamInfoTemp;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class NewTeamService {

	@Autowired
	private NewRandomRankDao newRandomRankDao;

	// @Autowired
	// private DateQuest dq;

	@Autowired
	private NewRandomRankService newRandomRankService;

	@Autowired
	private RandomRankConfigService randomRankConfig;
	@Autowired
	private NewTeamInfoDao newTeamInfoDao;
	private static final Logger logger = LoggerFactory.getLogger(NewTeamService.class);

	public List<NewTeamInfo> newTeams(String type) {
		if (this.addDate() < 0) {
			return null;
		}
		List<NewTeamInfo> returnList = newTeamInfoDao.findByDateAndStatusAndType(this.addDate() + 1, 0, type);
		if (returnList != null && returnList.size() > 0) {
			return returnList;
		} else {
			returnList = new ArrayList<NewTeamInfo>();
		}

		List<NewRandomRank> list = newRandomRankService.getTop64ByRank(type);
		HashMap<Integer, Integer> hashMap = getNewTeamPosition(list.size());
		for (java.util.Map.Entry<Integer, Integer> arg : hashMap.entrySet()) {
			NewTeamInfo newTeamInfo = new NewTeamInfo(arg.getKey(), arg.getValue(), this.addDate() + 1, type);
			newTeamInfoDao.save(newTeamInfo);
			returnList.add(newTeamInfo);
		}
		return returnList;
	}

	public void deleteAll() {
		newTeamInfoDao.deleteAll();
	}

	public void updateAllStatus() {
		// System.err.println("11111111111111");
		newTeamInfoDao.updateTeamInfo();
	}

	public void deleteAllStatus() {
		newTeamInfoDao.deleteTeamInfo();
	}

	public List<NewTeamInfo> updateTeam(String type) {
		int d = addDate();
		List<NewTeamInfo> returnList = new ArrayList<NewTeamInfo>();
		List<NewTeamInfo> list = newTeamInfoDao.findByDateAndStatusAndType(d, 0, type);
		List<NewTeamInfo> tempList = newTeamInfoDao.findByDateAndStatusAndType(d + 1, 0, type);
		if (tempList != null && tempList.size() > 0) {
			return tempList;
		}
		for (int i = 0; i < list.size(); i = i + 2) {
			NewTeamInfo attInfo = list.get(i);
			if (i + 1 < list.size()) {
				NewTeamInfo defInfo = list.get(i + 1);
				NewTeamInfo teamInfo = new NewTeamInfo(getWinner(attInfo), getWinner(defInfo), d + 1, type);
				newTeamInfoDao.save(teamInfo);
				returnList.add(teamInfo);
			} else {
				NewTeamInfo teamInfo = new NewTeamInfo(getWinner(attInfo), getWinner(attInfo), d + 1, type);
				newTeamInfoDao.save(teamInfo);
				// dq.stopTast();
			}

		}
		return returnList;
	}

	public int getWinner(NewTeamInfo info) {
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

	public List<NewTeamInfo> getTeamData(int date, String type) {
		return newTeamInfoDao.findByDateAndStatusAndType(date, 0, type);

	}

	public RestResult save(NewTeamInfo teamInfo) {
		newTeamInfoDao.save(teamInfo);
		return RestResult.groupBuy();

	}

	public RestResult getTeamDataByDate(int date, String type) {
		List<TeamInfoTemp> list = this.getNewTeamData(this.getTeamData(date, type));
		return RestResult.successTeamInfoTemp(list);
	}

	public boolean timePK() {
		Calendar ca = Calendar.getInstance();

		ca.set(Calendar.HOUR_OF_DAY, 9); // 控制时
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

	public List<TeamInfoTemp> getNewTeamData(List<NewTeamInfo> data) {
		List<TeamInfoTemp> list = new ArrayList<TeamInfoTemp>();
		for (NewTeamInfo ti : data) {
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
		return list;
		// return RestResult.successTeamInfoTemp(list);
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
