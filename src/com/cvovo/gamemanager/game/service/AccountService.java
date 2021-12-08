package com.cvovo.gamemanager.game.service;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.AccountDao;
import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class AccountService {
	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	@Autowired
	private AccountDao accountDao;

	@Autowired
	private ServerService serverService;

	public RestResult getResultByVisitor() {
		return null;
	}

	public Account findByFbUserId(String fbUserId) {
		Account account = accountDao.findByFbUserId(fbUserId);
		return account;
	}

	public void create(Account account) {
		Account exist = accountDao.findByUserId(account.getUserId());
		logger.info("exist==========" + exist);
		if (exist == null) {
			logger.info("======strat db==========");
			accountDao.save(account);
			logger.info("======db over==========");
		} else {
			exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountDao.save(exist);
		}
	}

	public void createTaiGuo(Account account) {
		Account exist = accountDao.findBysUid(account.getsUid());
		logger.info("exist==========" + exist);
		if (exist == null) {
			logger.info("======strat db==========");
			accountDao.save(account);
			logger.info("======db over==========");
		} else {
			exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountDao.save(exist);
		}
	}

	public void createYYB(Account account) {
		logger.info("================DB=======");
		Account exist = accountDao.findByUin(account.getUin());
		if (exist == null) {
			logger.info("================DB初始化 =======");
			accountDao.save(account);
		} else {
			logger.info("=========已有玩家更新时间======");
			exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountDao.save(exist);
		}
	}

	public Account findByUin(String uin) {
		return accountDao.findByUin(uin);
	}

	public Account createVisitor(Account account) {
		Account exist = accountDao.findByNick(account.getNick());
		if (exist == null) {
			accountDao.save(account);
			return account;
		} else {
			exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountDao.save(exist);
		}
		return exist;
	}

	public void createFB(Account account) {
		Account exist = accountDao.findByFbUserId(account.getFbUserId());
		if (exist == null) {
			accountDao.save(account);
		} else {
			exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountDao.save(exist);
		}
	}

	public boolean bindFB(Account account) {
		logger.info("===bindFbaccount===" + account);
		Account exist = accountDao.findByFbUserId(account.getFbUserId());
		if (exist == null) {
			logger.info("===启动DB====");
			accountDao.save(account);
			logger.info("==保存成功=========");
			return true;
		} else {
			logger.info("==is成功=========");
			accountDao.save(account);
			return true;
		}
	}

	public String getsUserId() {
		StringBuffer sb = new StringBuffer("mengyu_");
		String timer = PublicMethod.getUniqueId();
		sb.append(timer);

		Account tempAccount = this.getAccountBySuid(sb.toString());
		if (tempAccount != null) {
			sb = new StringBuffer("mengyu_");
			timer = PublicMethod.getUniqueId();
			sb.append(timer);
		}
		return sb.toString();
	}

	public boolean register(Account account) {
		Account exist = accountDao.findBysUid(account.getsUid());
		if (exist == null) {
			accountDao.save(account);
			return true;
		}
		return false;
	}

	public boolean registerJapn(Account account) {
		Account exist = accountDao.findByNick(account.getNick());
		if (exist == null) {
			accountDao.save(account);
			return true;
		}
		return false;
	}

	public boolean registerT(Account account) {
		Account exist = accountDao.findByNick(account.getNick());
		if (exist == null) {
			accountDao.save(account);
			return true;
		}
		return false;
	}

	public RestResult loginLy(Account account, int flag, byte isBind) {
		Account exist = accountDao.findByNick(account.getNick());
		if (exist != null) {
			if(account.getPassword() != null && account.getPassword() != ""){
				if (exist.getPassword().equals(account.getPassword())) {
					exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
					accountDao.save(exist);
					return serverService.getServerListLY(exist.getsUid(), flag, isBind);
				}
			}
		}
		return RestResult.USE_OR_PWD_ERROR;
	}

	public RestResult login(Account account, int flag) {
		Account exist = accountDao.findByNick(account.getNick());
		if (exist != null) {
			if (exist.getPassword().equals(account.getPassword())) {
				exist.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountDao.save(exist);
				return serverService.getServerList(exist.getsUid(), flag);
			}
		}
		return RestResult.USE_OR_PWD_ERROR;
	}

	public RestResult visitorLogin(Account account, int flag) {
		return serverService.getServerList(account.getsUid(), flag);
	}

	public Account findByNick(String nick) {
		return accountDao.findByNick(nick);
	}

	public void deleteAccount(Long id) {
		accountDao.delete(id);
	}

	public void deleteAccount(Account account) {
		accountDao.delete(account);
	}

	public Boolean deleteAccounts(List<Long> ids) {
		try {
			for (Long id : ids)
				accountDao.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void updateAccount(Account account) {
		accountDao.save(account);
	}

	public Account findAccountById(Long id) {
		return accountDao.findOne(id);
	}

	public RestResult findAccountByUserId(int userId) {
		Account exit = accountDao.findByUserId(userId);
		if (exit != null) {
			return RestResult.checkUserIdSuc(exit.getType());
		} else {
			return RestResult.checkUserIdError();
		}

	}

	public Account getAccountByUserId(int userId) {
		return accountDao.findByUserId(userId);
	}

	public Account getAccountBySuid(String sUserId) {
		return accountDao.findBysUid(sUserId);
	}

	public Account getAccountByDevice(String device) {
		return accountDao.findByDevice(device);
	}
}
