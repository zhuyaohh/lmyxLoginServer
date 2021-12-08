package com.cvovo.gamemanager.game.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "server_type")
public class ServerType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 2)
	private int channel;
	@Column(nullable = false, length = 45)
	private String login;
	@Column(nullable = false, length = 60)
	private String name;
	@Column(nullable = false, length = 45)
	private String url;
	@Column(nullable = false, length = 45)
	private String nextLogin;
	@Column(nullable = true, length = 100)
	private String notice;
	@Column(nullable = true, length = 100)
	private String nextNotice;

	/**
	 * @return the payUrl
	 */
	public String getPayUrl() {
		return payUrl;
	}

	/**
	 * @param payUrl
	 *            the payUrl to set
	 */
	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	@Column(nullable = false, length = 40)
	private String payUrl;
	@Column(nullable = false, length = 40)
	private String version;
	@Column(nullable = false, length = 40)
	private String nextPayUrl;
	@Column(nullable = false, length = 40)
	private String updateURL;
	@Column(nullable = false, length = 40)
	private String netxVersion;

	/**
	 * @return the id
	 */
	public long getId() {
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
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the nextLogin
	 */
	public String getNextLogin() {
		return nextLogin;
	}

	/**
	 * @param nextLogin
	 *            the nextLogin to set
	 */
	public void setNextLogin(String nextLogin) {
		this.nextLogin = nextLogin;
	}

	/**
	 * @return the nextPayUrl
	 */
	public String getNextPayUrl() {
		return nextPayUrl;
	}

	/**
	 * @param nextPayUrl
	 *            the nextPayUrl to set
	 */
	public void setNextPayUrl(String nextPayUrl) {
		this.nextPayUrl = nextPayUrl;
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
	 * @return the notice
	 */
	public String getNotice() {
		return notice;
	}

	/**
	 * @param notice
	 *            the notice to set
	 */
	public void setNotice(String notice) {
		this.notice = notice;
	}

	/**
	 * @return the nextNotice
	 */
	public String getNextNotice() {
		return nextNotice;
	}

	/**
	 * @param nextNotice
	 *            the nextNotice to set
	 */
	public void setNextNotice(String nextNotice) {
		this.nextNotice = nextNotice;
	}

	/**
	 * @return the updateURL
	 */
	public String getUpdateURL() {
		return updateURL;
	}

	/**
	 * @param updateURL
	 *            the updateURL to set
	 */
	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	/**
	 * @return the netxVersion
	 */
	public String getNetxVersion() {
		return netxVersion;
	}

	/**
	 * @param netxVersion
	 *            the netxVersion to set
	 */
	public void setNetxVersion(String netxVersion) {
		this.netxVersion = netxVersion;
	}

}
