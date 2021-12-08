package com.cvovo.gamemanager.game.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.WorldGuildInfoDao;
import com.cvovo.gamemanager.game.persist.entity.WorldGuildInfo;
import com.cvovo.gamemanager.game.persist.entity.WorldGuildTeam;
import com.cvovo.gamemanager.game.persist.entity.WorldGuildTemp;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class WorldGuildInfoService {
	private static final Logger logger = LoggerFactory.getLogger(WorldGuildInfoService.class);
	@Autowired
	private WorldGuildInfoDao worldGuildInfoDao;

	// public void saveOrUpdate(int serverId, int guildId, int value, String guildName, int type, int status) {
	// WorldGuildInfo tempInfo = worldGuildInfoDao.findByServerIdAndGuildId(serverId, guildId);
	// if (tempInfo != null) {
	// tempInfo.setValue(value);
	// tempInfo.setType(type);
	// tempInfo.setStatus(1);
	// tempInfo.setValueTimer(new Timestamp(System.currentTimeMillis()));
	// worldGuildInfoDao.save(tempInfo);
	// } else {
	// WorldGuildInfo newInfo = new WorldGuildInfo();
	// newInfo.setGuildId(guildId);
	// newInfo.setServerId(serverId);
	// newInfo.setValue(value);
	// newInfo.setType(type);
	// newInfo.setStatus(1);
	// newInfo.setValueTimer(new Timestamp(System.currentTimeMillis()));
	// worldGuildInfoDao.save(newInfo);
	//
	// }
	// }

	public void save(WorldGuildInfo wgi) {
		worldGuildInfoDao.save(wgi);
	}

	public RestResult saveOrUpdate(int serverId, int guildId, int value) {
		WorldGuildInfo tempInfo = worldGuildInfoDao.findByServerIdAndGuildId(serverId, guildId);
		if (tempInfo != null) {
			tempInfo.setValue(value);
			tempInfo.setValueTimer(new Timestamp(System.currentTimeMillis()));
			tempInfo.setStatus(0);
		}
		return RestResult.groupBuy();
	}

	public WorldGuildInfo findWorldGuildInfoByServerIdAndGuildId(int serverId, int guildId) {
		return worldGuildInfoDao.findByServerIdAndGuildId(serverId, guildId);
	}

	public Calendar getEndTime() {
		Date dNow = new Date(); // 当前时间
		Calendar calendar1 = Calendar.getInstance(); // 得到日历
		calendar1.setTime(dNow);// 把当前时间赋给日历
		calendar1.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		calendar1.set(Calendar.HOUR_OF_DAY, 23); // 控制时
		calendar1.set(Calendar.SECOND, 59); // 控制时
		calendar1.set(Calendar.MINUTE, 59); // 控制时
		return calendar1;
	}

	public Calendar getEndTime3() {
		Date dNow = new Date(); // 当前时间
		Calendar calendar1 = Calendar.getInstance(); // 得到日历
		calendar1.setTime(dNow);// 把当前时间赋给日历
		calendar1.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		calendar1.set(Calendar.HOUR_OF_DAY, 10); // 控制时
		calendar1.set(Calendar.SECOND, 59); // 控制时
		calendar1.set(Calendar.MINUTE, 59); // 控制时
		return calendar1;
	}

	public Calendar getEndTime1() {
		Date dNow = new Date(); // 当前时间
		Calendar calendar1 = Calendar.getInstance(); // 得到日历
		calendar1.setTime(dNow);// 把当前时间赋给日历
		calendar1.set(Calendar.HOUR_OF_DAY, 23); // 控制时
		calendar1.set(Calendar.SECOND, 59); // 控制时
		calendar1.set(Calendar.MINUTE, 59); // 控制时
		return calendar1;
	}

	public Calendar getStrartTime1() {
		Date dNow = new Date(); // 当前时间
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(dNow);// 把当前时间赋给日历

		calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
		calendar.set(Calendar.SECOND, 0); // 控制时
		calendar.set(Calendar.MINUTE, 0); // 控制时
		return calendar;
	}

	public Calendar getStrartTime() {
		Date dNow = new Date(); // 当前时间
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.setTime(dNow);// 把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
		calendar.set(Calendar.SECOND, 0); // 控制时
		calendar.set(Calendar.MINUTE, 0); // 控制时
		return calendar;
	}

	public List<WorldGuildInfo> getMatchAllList(int type) {
		Timestamp startTime = new Timestamp(this.getStrartTime().getTime().getTime());
		Timestamp endTime = new Timestamp(this.getEndTime().getTime().getTime());
		List<String> list = new ArrayList<String>();
		list.add("value");
		list.add("valueTimer");
		PageRequest page = new PageRequest(0, 10000, new Sort(Direction.DESC, list));
		List<WorldGuildInfo> worldList = worldGuildInfoDao.getAllMatchWorldInfo(startTime, endTime, type, page);
		return worldList;
	}

	public List<WorldGuildInfo> getMatchAllListT(int type) {
		Timestamp startTime = new Timestamp(this.getStrartTime1().getTime().getTime());
		Timestamp endTime = new Timestamp(this.getEndTime1().getTime().getTime());
		List<String> list = new ArrayList<String>();
		list.add("value");
		list.add("valueTimer");
		PageRequest page = new PageRequest(0, 10000, new Sort(Direction.DESC, list));
		List<WorldGuildInfo> worldList = worldGuildInfoDao.getAllMatchWorldInfo(startTime, endTime, type, page);
		return worldList;
	}

	public List<WorldGuildInfo> getAllTopList() {
		List<String> list = new ArrayList<String>();
		list.add("value");
		list.add("valueTimer");
		PageRequest page = new PageRequest(0, 10000, new Sort(Direction.DESC, list));
		List<WorldGuildInfo> worldList = worldGuildInfoDao.getTopAllWorldInfo(page);
		return worldList;
	}

	public void worldGuildMatchingFlush(int serverId) {
		List<WorldGuildInfo> list = this.getMatchAllListT(1);
		for (WorldGuildInfo wgi : list) {
			wgi.setMatchTimer(new Timestamp(this.getEndTime3().getTime().getTime()));
			worldGuildInfoDao.save(wgi);
		}
	}

	public RestResult worldGuildMatching(int serverId) {
		List<WorldGuildTeam> list = new ArrayList<WorldGuildTeam>();
		for (int j = 0; j < 3; j++) {
			List<WorldGuildInfo> temp = this.getMatchAllList(j);
			if (temp.size() % 2 != 0) {
				WorldGuildInfo info = temp.get(temp.size() - 1);
				info.setStatus(0);
				worldGuildInfoDao.save(info);
				temp.remove(temp.size() - 1);
			}

			for (int i = 0; i < temp.size(); i += 2) {
				if (i > temp.size() - 2) {
					break;
				}
				WorldGuildInfo wgi = temp.get(i);
				WorldGuildInfo wgi2 = temp.get(i + 1);

				if (wgi.getServerId() == serverId || wgi2.getServerId() == serverId) {
					WorldGuildTeam wgt = new WorldGuildTeam(wgi.getType());
					wgt.setMyServerId(wgi.getServerId());
					wgt.setMyGuildId(wgi.getGuildId());
					wgt.setMyGuildName(wgi.getName());
					wgt.setServerId(wgi2.getServerId());
					wgt.setGuildId(wgi2.getGuildId());
					wgt.setGuildName(wgi2.getName());
					list.add(wgt);
				}
			}
		}

		if (list != null && list.size() > 0) {
			logger.info("匹配成功=========================");
			return RestResult.successWorldGuildInfo(list);
		}
		return RestResult.successWorldGuildInfo();
	}

	public RestResult getTopList() {
		List<WorldGuildInfo> w = this.getAllTopList();
		List<WorldGuildTemp> list = new ArrayList<WorldGuildTemp>();
		for (WorldGuildInfo wgi : w) {
			WorldGuildTemp wgt = new WorldGuildTemp();
			wgt.setGuildId(wgi.getGuildId());
			wgt.setServerId(wgi.getServerId());
			wgt.setGuildName(wgi.getName());
			wgt.setValue(wgi.getValue());
			list.add(wgt);
		}
		if (list != null && list.size() > 0) {
			return RestResult.successWorldGuildTemp(list);
		}
		return RestResult.successWorldGuildInfo();
	}
}
