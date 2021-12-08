package com.cvovo.gamemanager.game.persist.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wordGuidInfo")
public class WorldGuildInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 45)
	private int serverId;
	@Column(nullable = false, length = 45)
	private int guildId;
	@Column(nullable = true, length = 65)
	private String name;
	@Column(nullable = true, length = 45)
	private int value;
	@Column(nullable = true, length = 45)
	private int type;
	@Column(nullable = true, length = 45)
	private int status; // 0 未参与，1：匹配中。2:匹配结束

	@Column(nullable = true, length = 4)
	private Timestamp valueTimer;
	
	@Column(nullable = true, length = 4)
	private Timestamp matchTimer;

	public WorldGuildInfo() {
		this.status = 0;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
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
	 * @return the valueTimer
	 */
	public Timestamp getValueTimer() {
		return valueTimer;
	}

	/**
	 * @param valueTimer
	 *            the valueTimer to set
	 */
	public void setValueTimer(Timestamp valueTimer) {
		this.valueTimer = valueTimer;
	}

	/**
	 * @return the matchTimer
	 */
	public Timestamp getMatchTimer() {
		return matchTimer;
	}

	/**
	 * @param matchTimer the matchTimer to set
	 */
	public void setMatchTimer(Timestamp matchTimer) {
		this.matchTimer = matchTimer;
	}
	
}
