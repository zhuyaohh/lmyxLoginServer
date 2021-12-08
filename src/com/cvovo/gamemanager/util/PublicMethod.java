package com.cvovo.gamemanager.util;

import java.util.Calendar;
import java.util.Random;

public class PublicMethod {
	public static String getUniqueId() {
		StringBuffer sb = new StringBuffer();
		Calendar c2 = Calendar.getInstance();
		int month = c2.get(Calendar.MONTH) + 1;
		sb.append(month);
		int date = c2.get(Calendar.DATE);
		sb.append(date);
		int hour = c2.get(Calendar.HOUR_OF_DAY);
		sb.append(hour);
		int minute = c2.get(Calendar.MINUTE);
		sb.append(minute);
		int second = c2.get(Calendar.SECOND);
		sb.append(second);
		Random random = new Random();
		String id = String.valueOf(random.nextInt(10000));
		sb.append("_");
		sb.append(id);
		return sb.toString();
	}

	public static String getBillUniqueId() {
		StringBuffer sb = new StringBuffer();
		Calendar c2 = Calendar.getInstance();
		int month = c2.get(Calendar.MONTH) + 1;
		sb.append(month);
		int date = c2.get(Calendar.DATE);
		sb.append(date);
		int hour = c2.get(Calendar.HOUR_OF_DAY);
		sb.append(hour);
		int minute = c2.get(Calendar.MINUTE);
		sb.append(minute);
		int second = c2.get(Calendar.SECOND);
		sb.append(second);
		Random random = new Random();
		String id = String.valueOf(random.nextInt(10000));
		sb.append(id);
		return sb.toString();
	}

}
