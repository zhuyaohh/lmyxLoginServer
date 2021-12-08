package com.cvovo.gamemanager.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class IOS_Verify {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IOS_Verify.class);

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}
	};

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private static final String url_sandbox = "https://sandbox.itunes.apple.com/verifyReceipt";
	private static final String url_verify = "https://buy.itunes.apple.com/verifyReceipt";

	/**
	 * 苹果服务器验证
	 * 
	 * @param receipt 账单
	 * @url 要验证的地址
	 * @return null 或返回结果 沙盒 https://sandbox.itunes.apple.com/verifyReceipt
	 * @throws Exception
	 * 
	 */
	public static String buyAppVerify(String receipt, String verifyState) throws Exception {
		logger.info("====verifyState========" + verifyState);
		String url = url_verify;
		if (verifyState != null && verifyState.equals("Sandbox")) {
			url = url_sandbox;
		}
		logger.info("====url========" + url);

		org.json.JSONObject json = new org.json.JSONObject();
		json.put("receipt-data", receipt);

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {

			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 设置 HttpURLConnection的字符编码
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(json.toString());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error(String.format("url:{%s},param:{%s}getResult :请求出现异常！%s", url, receipt, e.getMessage()));
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 根据原始收据返回苹果的验证地址: * 沙箱 https://sandbox.itunes.apple.com/verifyReceipt 真正的地址 https://buy.itunes.apple.com/verifyReceipt
	 * 
	 * @param receipt
	 * @return Sandbox 测试单 Real 正式单
	 */
	public static String getEnvironment(String receipt) {
		try {
			JSONObject job = JSONObject.fromObject(receipt);
			if (job.containsKey("environment")) {
				String evvironment = job.getString("environment");
				return evvironment;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "Real";
	}

}
