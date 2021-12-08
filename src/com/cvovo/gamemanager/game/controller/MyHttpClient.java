package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(MyHttpClient.class);

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url 发送请求的URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {

			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			String urlNameString = url + "?" + param;

			System.out.println(urlNameString);
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 设置 HttpURLConnection的字符编码
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// // 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error(String.format("url:{%s},param:{%s}getResult :请求出现异常！%s", url, param, e.getMessage()));
			logger.error("", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static byte[] getResult(String url, String param) {
		PrintWriter out = null;
		InputStream in = null;
		try {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/jason");
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			in = conn.getInputStream();
			return getByteArray(in);
		} catch (Exception e) {
			logger.error(String.format("url:{%s},param:{%s},getResult :请求出现异常！", url, param), e);
			logger.error("", e);
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
			} catch (IOException ex) {
				logger.error("", ex);
			}
		}
		return null;
	}

	// /**
	// * 向指定 URL 发送POST方法的请求
	// *
	// * @param url
	// * 发送请求的 URL
	// * @param param
	// * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	// * @return 所代表远程资源的响应结果
	// */
	// public static Object getObjectResult(String url, String param) {
	// PrintWriter out = null;
	// InputStream in = null;
	// ObjectInputStream oi = null;
	// Object returnValue = null;
	// try {
	// if (!url.startsWith("http")) {
	// url = "http://" + url;
	// }
	// URL realUrl = new URL(url);
	// URLConnection conn = realUrl.openConnection();
	// // 发送POST请求必须设置如下两行
	// conn.setDoOutput(true);
	// conn.setDoInput(true);
	// conn.setUseCaches(false);
	// conn.setRequestProperty("Content-Type", "application/octet-stream");
	// out = new PrintWriter(conn.getOutputStream());
	// // 发送请求参数
	// out.print(param);
	// // flush输出流的缓冲
	// out.flush();
	// in = conn.getInputStream();
	// byte[] datas = getByteArray(in);
	// oi = new ObjectInputStream(new ByteArrayInputStream(GZipUtils.decompress(datas)));
	// returnValue = oi.readObject();
	// } catch (Exception e) {
	// logger.error(String.format("url:{%s},param:{%s}getResult :请求出现异常！%s", url, param, e.getMessage()));
	// logger.error("", e);
	// }
	// // 使用finally块来关闭输出流、输入流
	// finally {
	// try {
	// if (out != null) {
	// out.close();
	// }
	// if (in != null) {
	// in.close();
	// }
	// if (oi != null) {
	// oi.close();
	// }
	//
	// } catch (IOException ex) {
	// logger.error("", ex);
	// }
	// }
	// return returnValue;
	// }

	public static byte[] getByteArray(InputStream input) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = input.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
		} catch (IOException e) {
			logger.error("", e);
		}
		return baos.toByteArray();
	}

	public static String sendPost(String url, Map<String, String> param) {
		StringBuffer args = new StringBuffer();
		for (Map.Entry<String, String> entry : param.entrySet()) {
			try {
				args.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String body = args.toString().substring(0, args.toString().length() - 1);
		// 空格和星号转义
		// body = body.replaceAll("\\+", "%20").replaceAll("\\*", "%2A");

		return sendPost(url, body);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url 发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		logger.debug(url + ":" + param);
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置 HttpURLConnection的字符编码
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error(String.format("url:{%s},param:{%s}getResult :请求出现异常！%s", url, param, e.getMessage()));
			logger.error("", e);
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
			} catch (IOException ex) {
				logger.error("", ex);
			}
		}
		return result;
	}
}