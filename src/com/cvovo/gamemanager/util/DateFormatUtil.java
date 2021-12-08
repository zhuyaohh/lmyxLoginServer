package com.cvovo.gamemanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

	public static String getString() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(date);
	}
}
