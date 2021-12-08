package com.cvovo.gamemanager.game.persist.entity;

import java.util.ArrayList;
import java.util.List;

public class TeamInfoTemp {

	private int attackId;

	private int defenseId;

	private int betWinCount;

	private int betFailCount;

	private List<BattleResult> battleResult = new ArrayList<BattleResult>();

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
	 * @return the battleResult
	 */
	public List<BattleResult> getBattleResult() {
		return battleResult;
	}

	/**
	 * @param battleResult
	 *            the battleResult to set
	 */
	public void setBattleResult(List<BattleResult> battleResult) {
		this.battleResult = battleResult;
	}



}
