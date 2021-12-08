package com.cvovo.gamemanager.game.persist.entity;

public class BattleResult {
	private String win;
	private String battleId;

	public BattleResult(String win, String battleId) {
		this.win = win;
		this.battleId = battleId;

	}

	/**
	 * @return the win
	 */
	public String getWin() {
		return win;
	}

	/**
	 * @param win
	 *            the win to set
	 */
	public void setWin(String win) {
		this.win = win;
	}

	/**
	 * @return the battleId
	 */
	public String getBattleId() {
		return battleId;
	}

	/**
	 * @param battleId
	 *            the battleId to set
	 */
	public void setBattleId(String battleId) {
		this.battleId = battleId;
	}

}
