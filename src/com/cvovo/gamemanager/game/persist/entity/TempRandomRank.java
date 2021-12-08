package com.cvovo.gamemanager.game.persist.entity;

public class TempRandomRank {

	private int serverId;

	private int playerId;

	private String url;
	private String innerUrl;

	private int currentRank;

	/**
	 * @return the serverId
	 */
	public int getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the playerId
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * @param playerId
	 *            the playerId to set
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the innerUrl
	 */
	public String getInnerUrl() {
		return innerUrl;
	}

	/**
	 * @param innerUrl
	 *            the innerUrl to set
	 */
	public void setInnerUrl(String innerUrl) {
		this.innerUrl = innerUrl;
	}

	/**
	 * @return the currentRank
	 */
	public int getCurrentRank() {
		return currentRank;
	}

	/**
	 * @param currentRank the currentRank to set
	 */
	public void setCurrentRank(int currentRank) {
		this.currentRank = currentRank;
	}
	

}
