package com.cvovo.gamemanager.game.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server_info")
public class ServerInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, length = 100)
	private String url;

	@Column(nullable = false, length = 100)
	private String innerUrl;

	@Column(nullable = false, length = 2)
	private int state;

	@Column(nullable = false, length = 65)
	private int port;

	@Column(nullable = false, length = 2)
	private int sign;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public void setInnerUrl(String innerUrl) {
		this.innerUrl = innerUrl;
	}

	public String getInnerUrl() {
		return this.innerUrl;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public int getSign() {
		return this.sign;
	}
}
