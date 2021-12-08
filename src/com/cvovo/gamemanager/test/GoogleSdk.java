package com.cvovo.gamemanager.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleSdk {
	private static final Logger logger = LoggerFactory.getLogger(GoogleSdk.class);
	private static final String client_id = "226638591656-4t927es1ggmg4ohlrrc02mtsu9lug2es.apps.googleusercontent.com";
	private static final String client_secret = "SrzEhwnl4HJpur5GUYSSnOdK";
	private static final String refresh_token = "1/62TeV5dQN5uGneJpSzxM7xfWGsk0dDpSf4dC6Fk34dM";
	private static final GoogleSdk instance = new GoogleSdk();

	private static String Access_token = "";

	private static long token_time = 0;

	private static int expires_in = 0;

	private GoogleSdk() {
	}

	public static GoogleSdk getInstance() {
		return instance;
	}

	private boolean doRefreshAccessToken() {
		try {
			URL urlGetToken = new URL("https://accounts.google.com/o/oauth2/token");
			HttpURLConnection connectionGetToken = (HttpURLConnection) urlGetToken.openConnection();
			connectionGetToken.setRequestMethod("POST");
			connectionGetToken.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(connectionGetToken.getOutputStream());
			writer.write("refresh_token=" + refresh_token + "&");
			writer.write("client_id=" + client_id + "&");
			writer.write("client_secret=" + client_secret + "&");
			writer.write("grant_type=refresh_token");
			writer.close();

			if (connectionGetToken.getResponseCode() == HttpURLConnection.HTTP_OK) {
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connectionGetToken.getInputStream(), "utf-8"));
				String strLine = "";
				while ((strLine = reader.readLine()) != null) {
					sb.append(strLine);
				}

				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(sb.toString());
				Access_token = rootNode.path("access_token").textValue();
				expires_in = Integer.parseInt(rootNode.path("expires_in").textValue());
				token_time = System.currentTimeMillis();
				return true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean RefreshAccessToken() {
		if (Access_token.isEmpty()) {
			return doRefreshAccessToken();
		}
		long runTime = System.currentTimeMillis() - token_time;
		if (runTime > (expires_in - 10) * 1000) {
			return doRefreshAccessToken();
		}
		return true;
	}

	public int getPurchaseState(String orderId, String packageName, String productId, String purchaseToken, String serverId, String roleId) throws Exception {
		logger.info("===============getPurchaseState=======");
		RefreshAccessToken();
		try {
			String url = "https://www.googleapis.com/androidpublisher/v2/applications";
			StringBuffer getURL = new StringBuffer();
			getURL.append(url);
			getURL.append("/" + packageName);
			getURL.append("/purchases/products");
			getURL.append("/" + productId);
			getURL.append("/tokens/" + purchaseToken);
			getURL.append("?access_token=" + Access_token);
			URL urlObtainOrder = new URL(getURL.toString());
			HttpURLConnection connectionObtainOrder = (HttpURLConnection) urlObtainOrder.openConnection();
			connectionObtainOrder.setRequestMethod("GET");
			connectionObtainOrder.setDoOutput(true);
			int nCode = connectionObtainOrder.getResponseCode();
			logger.info("===============http=======");
			if (connectionObtainOrder.getResponseMessage() != null || !connectionObtainOrder.getResponseMessage().equals("")) {
				logger.info("===============errs=======" + connectionObtainOrder.getResponseMessage());
			}
			if (nCode == HttpURLConnection.HTTP_OK) {

				logger.info("===============OK=======");
				StringBuilder sbLines = new StringBuilder("");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connectionObtainOrder.getInputStream(), "utf-8"));
				String strLine = "";
				while ((strLine = reader.readLine()) != null) {
					sbLines.append(strLine);
				}
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(sbLines.toString());
				int status = Integer.parseInt(rootNode.path("purchaseState").textValue());

				if (status == 0) {
					int nConsumeStat = Integer.parseInt(rootNode.path("consumptionState").textValue());
					logger.info("===============nConsumeStat=======" + nConsumeStat);
					if (nConsumeStat == 1)
						return 0;
					else if (nConsumeStat == 0)
						return 1;
					else
						return -1;
				} else {
					return -1;
				}
			} else {
				logger.info("===============fail=======");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
