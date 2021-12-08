package com.cvovo.gamemanager.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.util.DigestUtils;

public class MyhttpService {
	private int read_time_out = 10000;
	private static final Logger logger = LoggerFactory.getLogger(MyhttpService.class);

	public MyhttpService() {
		super();
	}

	public MyhttpService(int time_out) {
		read_time_out = time_out;
	}

	public String doPost(String url, Map<String, String> params) {
		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append("&");
			}
			postData.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return service(false, url, postData.toString(), "POST", null);
	}

	public String doPost(String url, Map<String, String> params, Map<String, String> headers) {
		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append("&");
			}
			postData.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return service(false, url, postData.toString(), "POST", headers);
	}

	public String doPost(String url, String body) {
		return service(false, url, body, "POST", null);
	}

	public String doPost(String url, String postData, Map<String, String> headers) {
		return service(false, url, postData, "POST", headers);
	}

	public String doGet(String url, Map<String, String> headers) {
		return service(false, url, null, "GET", headers);
	}

	public String doGet(String url) {
		return service(false, url, null, "GET", null);
	}

	public String doHttpsPost(String url, String postData) {
		return service(true, url, postData, "POST", null);
	}

	public String doHttpsPost(String url, Map<String, String> params) {
		return doHttpsPost(url, params, null);
	}

	public String doHttpsPost(String url, Map<String, String> params, Map<String, String> headers) {
		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> entry : params.entrySet()) {
			if (postData.length() != 0) {
				postData.append("&");
			}
			postData.append(entry.getKey()).append("=").append(entry.getValue());
		}
		return service(true, url, postData.toString(), "POST", headers);
	}

	public String doHttpsGet(String url) {
		return service(true, url, null, "GET", null);
	}

	private String service(boolean isHttps, String url, String postData, String method, Map<String, String> headers) {

		HttpURLConnection conn = null;
		try {
			boolean doOutput = postData != null && postData.equals("");
			conn = isHttps ? createHttpsConn(url, method, doOutput) : createHttpConn(url, method, doOutput);

			fillProperties(conn, headers);

			if (doOutput)
				writeMsg(conn, postData);

			String msg = readMsg(conn);

			logger.debug(msg);

			return msg;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return null;
	}

	private HttpURLConnection createHttpConn(String url, String method, boolean doOutput) throws IOException {
		URL dataUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) dataUrl.openConnection();
		conn.setReadTimeout(read_time_out);
		conn.setRequestMethod(method);
		conn.setDoOutput(doOutput);
		conn.setDoInput(true);
		return conn;
	}

	public static void main(String[] args) {
		System.out.println(DigestUtils.md5DigestAsHex("19a98d31-4652-4b94-b7cd-129e8ddaliji11899CNY68appstoreQY7road-16-WAN-0668ddddSHEN-2535-7ROAD-shenqug-lovedede77".getBytes()));
	}

	private String readMsg(HttpURLConnection conn) throws IOException {
		return readMsg(conn, "UTF-8");
	}

	private String readMsg(HttpURLConnection conn, String charSet) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charSet));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void writeMsg(HttpURLConnection conn, String postData) throws IOException {
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.write(postData.getBytes());
		dos.flush();
		dos.close();
	}

	private void fillProperties(HttpURLConnection conn, Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return;
		}

		for (Entry<String, String> entry : params.entrySet()) {
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}
	}

	public String httpsPost(String url, String postData) {
		HttpURLConnection conn = null;
		try {
			boolean doOutput = (postData != null && postData.equals(""));// !Strings.isNullOrEmpty(postData);
			conn = createHttpsConn(url, "POST", doOutput);
			if (doOutput)
				writeMsg(conn, postData);

			return readMsg(conn);
		} catch (Exception ex) {
			// ingore
			// just print out
			logger.error(ex.getMessage(), ex);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return null;
	}

	private HttpURLConnection createHttpsConn(String url, String method, boolean doOutput) throws Exception {
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};

		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		trustAllHttpsCertificates();

		URL dataUrl = new URL(url);

		HttpURLConnection conn = (HttpURLConnection) dataUrl.openConnection();
		conn.setReadTimeout(read_time_out);
		conn.setRequestMethod(method);
		conn.setDoOutput(doOutput);
		conn.setDoInput(true);
		return conn;
	}

	private static void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:

		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new miTM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	}

	public static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
			return;
		}
	}

	/**
	 * 执行一个HTTP POST请求，返回请求响应的内容
	 * 
	 * @param url
	 *            请求的URL地址
	 * @param params
	 *            请求的查询参数,可以为null
	 * @return 返回请求响应的内容
	 */
	public static String doPostforUC(String url, String body) {
		StringBuffer stringBuffer = new StringBuffer();
		HttpEntity entity = null;
		BufferedReader in = null;
		// HttpResponse response = null;
		// try {
		// DefaultHttpClient httpclient = new DefaultHttpClient();
		// HttpParams params = httpclient.getParams();
		// HttpConnectionParams.setConnectionTimeout(params, 20000);
		// HttpConnectionParams.setSoTimeout(params, 20000);
		// HttpPost httppost = new HttpPost(url);
		// httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		//
		// httppost.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
		// response = httpclient.execute(httppost);
		//
		// entity = response.getEntity();
		// in = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		// String ln;
		// while ((ln = in.readLine()) != null) {
		// stringBuffer.append(ln);
		// stringBuffer.append("\r\n");
		// }
		// httpclient.getConnectionManager().shutdown();
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// } catch (IllegalStateException e2) {
		// e2.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (null != in) {
		// try {
		// in.close();
		// in = null;
		// } catch (IOException e3) {
		// e3.printStackTrace();
		// }
		// }
		// }
		return stringBuffer.toString();
	}

}