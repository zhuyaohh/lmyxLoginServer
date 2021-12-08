package com.cvovo.gamemanager.game.persist.entity;



public class WorldGuildTeam {

	private int type;

	private int myServerId;

	private int myGuildId;

	private String myGuildName;

	private int serverId;

	private int guildId;

	private String guildName;

	public WorldGuildTeam(int type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the myServerId
	 */
	public int getMyServerId() {
		return myServerId;
	}

	/**
	 * @param myServerId
	 *            the myServerId to set
	 */
	public void setMyServerId(int myServerId) {
		this.myServerId = myServerId;
	}

	/**
	 * @return the myGuildId
	 */
	public int getMyGuildId() {
		return myGuildId;
	}

	/**
	 * @param myGuildId
	 *            the myGuildId to set
	 */
	public void setMyGuildId(int myGuildId) {
		this.myGuildId = myGuildId;
	}

	/**
	 * @return the myGuildName
	 */
	public String getMyGuildName() {
		return myGuildName;
	}

	/**
	 * @param myGuildName
	 *            the myGuildName to set
	 */
	public void setMyGuildName(String myGuildName) {
		this.myGuildName = myGuildName;
	}

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
	 * @return the guildId
	 */
	public int getGuildId() {
		return guildId;
	}

	/**
	 * @param guildId
	 *            the guildId to set
	 */
	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}

	/**
	 * @return the guildName
	 */
	public String getGuildName() {
		return guildName;
	}

	/**
	 * @param guildName
	 *            the guildName to set
	 */
	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

}
