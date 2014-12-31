package com.suda.msgcenter.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormat {

	public static String DateToString(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		
		try {
			Date dt = (Date) sdf.parse(date);
			String formatStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
			return formatStr;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";

	}
}
