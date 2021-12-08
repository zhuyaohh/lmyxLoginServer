package com.cvovo.gamemanager.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

class TstoreData {
	public String txid;
	public String appid = "OA00705901";
	public String signdata;
}

@Service
@Transactional
public class TstoreSdk {
	private static final Logger logger = LoggerFactory.getLogger(TstoreSdk.class);
	private static final String goUrl = "https://iap.tstore.co.kr/digitalsignconfirm.iap";
	private static final String goUrl_Temp = " https://iapdev.tstore.co.kr/digitalsignconfirm.iap";

	// https://iap.tstore.co.kr/digitalsignconfirm.iap
	// https://iapdev.tstore.co.kr/digitalsignconfirm.iap

	public static String verTstorePay(String txid, String signdata, String sandbox) throws Exception {
		logger.info("====sandbox========" + sandbox);
		String urlStr = goUrl;
		if (sandbox != null && sandbox.equals("Sandbox")) {
			urlStr = goUrl_Temp;
		}
		try {
			URL url = new URL(urlStr);

			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, null, new SecureRandom());

			httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
			httpsURLConnection.setConnectTimeout(10000);
			httpsURLConnection.setReadTimeout(10000);
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setRequestProperty("Content-Type", "application/json");
			httpsURLConnection.setDoOutput(true);
			httpsURLConnection.setDoInput(true);
			httpsURLConnection.connect();

			Gson gsd = new Gson();
			TstoreData TST = new TstoreData();
			TST.txid = txid;
			TST.signdata = signdata;
			String sendInstent = gsd.toJson(TST);

			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpsURLConnection.getOutputStream());
			bufferedOutputStream.write(sendInstent.getBytes("UTF-8"));
			bufferedOutputStream.flush();
			bufferedOutputStream.close();

			int responseCode = httpsURLConnection.getResponseCode();
			logger.info("进入验证================");
			if (responseCode == HttpURLConnection.HTTP_OK) {
				logger.info("====成功=============");
				return convertStreamToString(httpsURLConnection.getInputStream());
			}
			logger.info("==验证失败================");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	final static String convertStreamToString(InputStream is) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		int i = is.read();
		while (i != -1) {
			bs.write(i);
			i = is.read();
		}
		return bs.toString();
	}
}
