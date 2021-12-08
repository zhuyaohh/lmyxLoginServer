package com.cvovo.gamemanager.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	private static ObjectMapper mapper = new ObjectMapper();

	public static <T> T getObjcet(String content, Class<T> valueType) {
		try {
			return mapper.readValue(content, valueType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getJson(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 將字符串压缩为 gzip 流 * @param content
	 *
	 * @return
	 */
	public static byte[] gzip(String content) {
		ByteArrayOutputStream baos = null;
		GZIPOutputStream out = null;
		byte[] ret = null;
		try {
			baos = new ByteArrayOutputStream();
			out = new GZIPOutputStream(baos);
			out.write(content.getBytes());
			out.close();
			baos.close();
			ret = baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		// String[] strs = { "1", "2", "3", "4", "5" };
		int[] ints = { 1, 2, 3, 4, 5 };
		System.err.println(getJson(ints));
	}
}
