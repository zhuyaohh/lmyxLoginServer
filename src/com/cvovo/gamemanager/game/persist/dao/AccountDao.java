package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.Account;

public interface AccountDao extends JpaRepository<Account, Long> {

	Account findByUserId(int UserId);

	Account findByNick(String nick);

	Account findBysUid(String sUid);

	Account findByFbUserId(String fbUserId);

	Account findByDevice(String device);

	Account findByUin(String uin);

}
