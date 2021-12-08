package com.cvovo.gamemanager.game.persist.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "newTeamInfo")
public class NewTeamInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 45)
	private int attackId;

	@Column(nullable = false, length = 45)
	private int defenseId;

	@Column(nullable = true)
	private int betWinCount;

	@Column(nullable = true)
	private int betFailCount;

	@Column(nullable = true)
	private String battleResult;

	@Column(nullable = false, length = 45)
	private String type;

	@Column(nullable = true)
	private int date;
	@Column(nullable = true, length = 4)
	private int status;

	@Column(nullable = true, length = 4)
	private Timestamp createTime;

	public NewTeamInfo() {
		this.status = 0;
	}

	/**
	 * @return the battleResult
	 */
	public String getBattleResult() {
		return battleResult;
	}

	public NewTeamInfo(int attackId, int defenseId, String type) {
		this.type = type;
		this.attackId = attackId;
		this.defenseId = defenseId;
		this.date = 1;
		this.status = 0;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public NewTeamInfo(int attackId, int defenseId, int date, String type) {
		this.type = type;
		this.attackId = attackId;
		this.defenseId = defenseId;
		this.date = date;
		this.status = 0;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the attackId
	 */
	public int getAttackId() {
		return attackId;
	}

	/**
	 * @param attackId
	 *            the attackId to set
	 */
	public void setAttackId(int attackId) {
		this.attackId = attackId;
	}

	/**
	 * @return the defenseId
	 */
	public int getDefenseId() {
		return defenseId;
	}

	/**
	 * @param defenseId
	 *            the defenseId to set
	 */
	public void setDefenseId(int defenseId) {
		this.defenseId = defenseId;
	}

	/**
	 * @return the betWinCount
	 */
	public int getBetWinCount() {
		return betWinCount;
	}

	/**
	 * @param betWinCount
	 *            the betWinCount to set
	 */
	public void setBetWinCount(int betWinCount) {
		this.betWinCount = betWinCount;
	}

	/**
	 * @return the betFailCount
	 */
	public int getBetFailCount() {
		return betFailCount;
	}

	/**
	 * @param betFailCount
	 *            the betFailCount to set
	 */
	public void setBetFailCount(int betFailCount) {
		this.betFailCount = betFailCount;
	}

	/**
	 * @param battleResult
	 *            the battleResult to set
	 */
	public void setBattleResult(String battleResult) {
		this.battleResult = battleResult;
	}

	/**
	 * @return the date
	 */
	public int getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(int date) {
		this.date = date;
	}

	public void addBetWinCount() {
		this.betWinCount++;
	}

	public void addBetFailCount() {
		this.betFailCount++;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the createTime
	 */
	public Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
